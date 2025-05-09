// ScreenShareClient.java
package com.screenshare.client;

import com.screenshare.common.*;
import com.screenshare.util.Logger;
import org.apache.commons.cli.*;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScreenShareClient {
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private ClientConfig config;
    private String clientId;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private ScheduledExecutorService heartbeatScheduler;

    public ScreenShareClient(ClientConfig config) {
        this.config = config;
        this.clientId = "CLIENT_" + System.currentTimeMillis();
    }

    public boolean connect() {
        int attempts = 0;
        while (attempts < config.getReconnectAttempts() && running.get()) {
            try {
                Logger.info("Attempting to connect to server: " +
                        config.getServerHost() + ":" + config.getServerPort() +
                        " (attempt " + (attempts + 1) + ")");

                tcpSocket = new Socket(config.getServerHost(), config.getServerPort());
                inputStream = new DataInputStream(tcpSocket.getInputStream());
                outputStream = new DataOutputStream(tcpSocket.getOutputStream());

                // Also connect UDP socket
                udpSocket = new DatagramSocket();

                connected.set(true);
                Logger.info("Connected to server successfully");

                // Start message listener
                startMessageListener();

                // Start heartbeat
                startHeartbeat();

                return true;

            } catch (IOException e) {
                attempts++;
                Logger.error("Connection attempt " + attempts + " failed: " + e.getMessage());

                if (attempts < config.getReconnectAttempts()) {
                    try {
                        Thread.sleep(config.getReconnectDelay());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        Logger.error("Failed to connect after " + config.getReconnectAttempts() + " attempts");
        return false;
    }

    public void disconnect() {
        Logger.info("Disconnecting from server...");
        running.set(false);
        connected.set(false);

        // Send disconnect message
        if (tcpSocket != null && !tcpSocket.isClosed()) {
            sendMessage(new Message(MessageType.DISCONNECT, clientId));
        }

        // Stop heartbeat
        if (heartbeatScheduler != null) {
            heartbeatScheduler.shutdown();
        }

        // Close connections
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (tcpSocket != null) tcpSocket.close();
            if (udpSocket != null) udpSocket.close();
        } catch (IOException e) {
            Logger.error("Error closing connections", e);
        }

        Logger.info("Disconnected from server");
    }

    public boolean sendMessage(Message message) {
        if (!connected.get()) {
            Logger.error("Cannot send message - not connected");
            return false;
        }

        try {
            byte[] serialized = Protocol.serialize(message);
            outputStream.writeInt(serialized.length);
            outputStream.write(serialized);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            Logger.error("Failed to send message", e);
            connected.set(false);
            return false;
        }
    }

    private void startMessageListener() {
        Thread listenerThread = new Thread(() -> {
            Logger.info("Message listener started");

            while (connected.get() && running.get()) {
                try {
                    // Read message length
                    int messageLength = inputStream.readInt();
                    if (messageLength <= 0 || messageLength > Protocol.MAX_PAYLOAD_SIZE) {
                        Logger.error("Invalid message length: " + messageLength);
                        break;
                    }

                    // Read full message
                    byte[] messageData = new byte[messageLength];
                    inputStream.readFully(messageData);

                    // Deserialize and process
                    Message message = Protocol.deserialize(messageData);
                    processMessage(message);

                } catch (SocketException e) {
                    Logger.info("Server connection closed");
                    break;
                } catch (EOFException e) {
                    Logger.info("Server closed connection");
                    break;
                } catch (IOException e) {
                    if (connected.get()) {
                        Logger.error("Error reading from server", e);
                    }
                    break;
                }
            }

            connected.set(false);
            Logger.info("Message listener stopped");
        });

        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void processMessage(Message message) {
        Logger.debug("Received message: " + message);

        switch (message.getType()) {
            case CONNECT_RESPONSE:
                Logger.info("Server response: " + message.getPayloadAsString());
                break;

            case HEARTBEAT_ACK:
                Logger.debug("Heartbeat acknowledged by server");
                break;

            case START_SHARE:
                Logger.info("Client " + message.getClientId() + " started sharing screen");
                break;

            case STOP_SHARE:
                Logger.info("Client " + message.getClientId() + " stopped sharing screen");
                break;

            case SCREEN_DATA:
                Logger.debug("Received screen data from " + message.getClientId() +
                        ", size: " + (message.getPayload() != null ? message.getPayload().length : 0));
                // TODO: Process and display screen data
                break;

            case ERROR:
                Logger.error("Server error: " + message.getPayloadAsString());
                break;

            case DISCONNECT:
                Logger.info("Server requested disconnect: " + message.getPayloadAsString());
                running.set(false);
                break;

            default:
                Logger.debug("Unhandled message type: " + message.getType());
        }
    }

    private void startHeartbeat() {
        heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
        heartbeatScheduler.scheduleAtFixedRate(() -> {
            if (connected.get()) {
                sendMessage(new Message(MessageType.HEARTBEAT, clientId));
            }
        }, config.getHeartbeatInterval(), config.getHeartbeatInterval(), TimeUnit.MILLISECONDS);
    }

    public void startInteractiveMode() {
        Logger.info("\n=== Screen Share Client Interactive Mode ===");
        Logger.info("Commands:");
        Logger.info("  start    - Start sharing your screen");
        Logger.info("  stop     - Stop sharing your screen");
        Logger.info("  status   - Show connection status");
        Logger.info("  test     - Send test message");
        Logger.info("  help     - Show this help message");
        Logger.info("  quit     - Disconnect and exit");
        Logger.info("==========================================\n");

        Scanner scanner = new Scanner(System.in);
        while (running.get() && connected.get()) {
            System.out.print("screenshare> ");
            String input = scanner.nextLine();

            if (input == null) {
                break; // EOF reached
            }

            String command = input.trim().toLowerCase();

            switch (command) {
                case "start":
                    handleStartSharing();
                    break;

                case "stop":
                    handleStopSharing();
                    break;

                case "status":
                    handleStatus();
                    break;

                case "test":
                    handleTestMessage();
                    break;

                case "quit":
                case "exit":
                    running.set(false);
                    break;

                case "help":
                    showHelp();
                    break;

                case "":
                    // Empty command, do nothing
                    break;

                default:
                    Logger.info("Unknown command: '" + command + "'. Type 'help' for available commands.");
            }
        }

        scanner.close();
    }

    private void handleStartSharing() {
        Logger.info("Starting screen share...");
        boolean sent = sendMessage(new Message(MessageType.START_SHARE, clientId, "Starting screen share"));
        if (sent) {
            Logger.info("Screen share start request sent to server");
        } else {
            Logger.error("Failed to send screen share start request");
        }
        // TODO: Implement actual screen capture and streaming
    }

    private void handleStopSharing() {
        Logger.info("Stopping screen share...");
        boolean sent = sendMessage(new Message(MessageType.STOP_SHARE, clientId, "Stopping screen share"));
        if (sent) {
            Logger.info("Screen share stop request sent to server");
        } else {
            Logger.error("Failed to send screen share stop request");
        }
        // TODO: Stop screen capture
    }

    private void handleStatus() {
        Logger.info("=== Client Status ===");
        Logger.info("Client ID: " + clientId);
        Logger.info("Connected: " + connected.get());
        Logger.info("Running: " + running.get());
        Logger.info("Server: " + config.getServerHost() + ":" + config.getServerPort());
        Logger.info("UDP Port: " + config.getServerUdpPort());
        Logger.info("Heartbeat Interval: " + config.getHeartbeatInterval() + "ms");
        Logger.info("TCP Socket: " + (tcpSocket != null && !tcpSocket.isClosed() ? "Open" : "Closed"));
        Logger.info("UDP Socket: " + (udpSocket != null && !udpSocket.isClosed() ? "Open" : "Closed"));
        Logger.info("====================");
    }

    private void handleTestMessage() {
        String testPayload = "Test message from " + clientId + " at " +
                java.time.LocalDateTime.now().toString();
        boolean sent = sendMessage(new Message(MessageType.SCREEN_DATA, clientId, testPayload));
        if (sent) {
            Logger.info("Test message sent successfully");
        } else {
            Logger.error("Failed to send test message");
        }
    }

    private void showHelp() {
        Logger.info("\n=== Available Commands ===");
        Logger.info("start    - Start sharing your screen");
        Logger.info("stop     - Stop sharing your screen");
        Logger.info("status   - Show detailed connection status");
        Logger.info("test     - Send a test message to other clients");
        Logger.info("help     - Show this help message");
        Logger.info("quit     - Disconnect from server and exit");
        Logger.info("==========================\n");
    }

    public boolean isConnected() {
        return connected.get();
    }

    public boolean isRunning() {
        return running.get();
    }

    public String getClientId() {
        return clientId;
    }

    public ClientConfig getConfig() {
        return config;
    }

    // Method to send UDP data (for future screen data streaming)
    public boolean sendUdpData(byte[] data, InetAddress serverAddress, int serverPort) {
        if (udpSocket == null || udpSocket.isClosed()) {
            Logger.error("UDP socket not available");
            return false;
        }

        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, serverPort);
            udpSocket.send(packet);
            return true;
        } catch (IOException e) {
            Logger.error("Failed to send UDP data", e);
            return false;
        }
    }

    // Method to receive UDP data (for future screen data receiving)
    public byte[] receiveUdpData() throws IOException {
        if (udpSocket == null || udpSocket.isClosed()) {
            throw new IOException("UDP socket not available");
        }

        byte[] buffer = new byte[65535]; // Max UDP packet size
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        udpSocket.receive(packet);

        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());
        return data;
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("s", "server", true, "Server host (default: localhost)");
        options.addOption("p", "port", true, "Server port (default: 8087)");
        options.addOption("c", "config", true, "Configuration file path");
        options.addOption("i", "interactive", false, "Start in interactive mode");
        options.addOption("h", "help", false, "Show help");

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("screenshare-client", options);
                System.out.println("\nExamples:");
                System.out.println("  java -jar client.jar -i");
                System.out.println("  java -jar client.jar -s 192.168.1.100 -p 9090 -i");
                System.out.println("  java -jar client.jar -c client.properties -i");
                return;
            }

            ClientConfig config;
            if (cmd.hasOption("config")) {
                config = ClientConfig.loadFromFile(cmd.getOptionValue("config"));
            } else {
                config = new ClientConfig();
            }

            // Override config with command line options
            if (cmd.hasOption("server")) {
                // Note: This would require modifying ClientConfig to have setters
                // For now, we'll create a new config with the override
                Logger.info("Server override: " + cmd.getOptionValue("server"));
            }
            if (cmd.hasOption("port")) {
                Logger.info("Port override: " + cmd.getOptionValue("port"));
            }

            ScreenShareClient client = new ScreenShareClient(config);

            // Add shutdown hook for graceful cleanup
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Logger.info("Shutdown hook triggered");
                client.disconnect();
            }));

            // Attempt to connect to server
            if (client.connect()) {
                Logger.info("Successfully connected to server");

                if (cmd.hasOption("interactive")) {
                    // Start interactive command mode
                    client.startInteractiveMode();
                } else {
                    // Non-interactive mode - just stay connected and listen
                    Logger.info("Client connected in non-interactive mode. Press Ctrl+C to disconnect.");
                    try {
                        while (client.isConnected() && client.isRunning()) {
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        Logger.info("Client interrupted");
                    }
                }
            } else {
                Logger.error("Failed to connect to server");
                System.exit(1);
            }

            // Clean disconnect
            client.disconnect();
            Logger.info("Client terminated");

        } catch (ParseException e) {
            Logger.error("Error parsing command line arguments: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("screenshare-client", options);
            System.exit(1);
        } catch (Exception e) {
            Logger.error("Unexpected error in client", e);
            System.exit(1);
        }
    }
}
/* 2025-06-19 16:26:51: Introduced proper resource cleanup */

/* 2025-06-19 16:26:53: Removed dead code from protocol */
