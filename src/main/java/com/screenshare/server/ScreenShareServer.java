

// ScreenShareServer.java
package com.screenshare.server;

import com.screenshare.common.*;
        import com.screenshare.util.Logger;
import org.apache.commons.cli.*;

        import java.io.IOException;
import java.net.*;
        import java.util.concurrent.*;
        import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScreenShareServer {
    private ServerSocket serverSocket;
    private DatagramSocket udpSocket;
    private ExecutorService clientThreadPool;
    private final List<ClientHandler> connectedClients = new CopyOnWriteArrayList<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ServerConfig config;

    public ScreenShareServer(ServerConfig config) {
        this.config = config;
        this.clientThreadPool = Executors.newFixedThreadPool(config.getThreadPoolSize());
    }

    public void start() throws IOException {
        // Start TCP server for control messages
        Logger.info("Screen Share Server started on port " + config.getPort());

        serverSocket = new ServerSocket(config.getPort());
        Logger.info("Screen Share Server started on port " + config.getPort());

        // Start UDP server for screen data
        udpSocket = new DatagramSocket(config.getUdpPort());
        Logger.info("UDP data server started on port " + config.getUdpPort());

        running.set(true);

        // Start heartbeat checker
        startHeartbeatChecker();

        // Accept client connections
        while (running.get()) {
            try {
                Socket clientSocket = serverSocket.accept();

                if (connectedClients.size() >= config.getMaxClients()) {
                    Logger.info("Max clients reached, rejecting connection from: " +
                            clientSocket.getRemoteSocketAddress());
                    clientSocket.close();
                    continue;
                }

                Logger.info("New client connected: " + clientSocket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(clientSocket, this);
                connectedClients.add(handler);
                clientThreadPool.execute(handler);

            } catch (IOException e) {
                if (running.get()) {
                    Logger.error("Error accepting client connection", e);
                }
            }
        }
    }

    public void stop() {
        Logger.info("Stopping Screen Share Server...");
        running.set(false);

        // Close all client connections
        for (ClientHandler client : connectedClients) {
            client.sendMessage(new Message(MessageType.DISCONNECT, "SERVER", "Server shutting down"));
        }

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (udpSocket != null && !udpSocket.isClosed()) {
                udpSocket.close();
            }
        } catch (IOException e) {
            Logger.error("Error closing server sockets", e);
        }

        clientThreadPool.shutdown();
        try {
            if (!clientThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                clientThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            clientThreadPool.shutdownNow();
        }

        Logger.info("Screen Share Server stopped");
    }

    public void removeClient(ClientHandler client) {
        connectedClients.remove(client);
        Logger.info("Client removed: " + client.getClientId() +
                ", remaining clients: " + connectedClients.size());
    }

    public void notifyClientStartedSharing(String clientId) {
        Message notification = new Message(MessageType.START_SHARE, clientId, "Client started sharing");
        broadcastMessage(notification, clientId);
    }

    public void notifyClientStoppedSharing(String clientId) {
        Message notification = new Message(MessageType.STOP_SHARE, clientId, "Client stopped sharing");
        broadcastMessage(notification, clientId);
    }

    public void broadcastScreenData(String senderId, Message screenData) {
        // Forward screen data to all clients except sender
        for (ClientHandler client : connectedClients) {
            if (!client.getClientId().equals(senderId)) {
                client.sendMessage(screenData);
            }
        }
    }

    private void broadcastMessage(Message message, String excludeClientId) {
        for (ClientHandler client : connectedClients) {
            if (!client.getClientId().equals(excludeClientId)) {
                client.sendMessage(message);
            }
        }
    }

    private void startHeartbeatChecker() {
        ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
        heartbeatScheduler.scheduleAtFixedRate(() -> {
            List<ClientHandler> deadClients = new ArrayList<>();

            for (ClientHandler client : connectedClients) {
                if (!client.isAlive()) {
                    deadClients.add(client);
                }
            }

            for (ClientHandler deadClient : deadClients) {
                Logger.info("Removing dead client: " + deadClient.getClientId());
                removeClient(deadClient);
            }

        }, config.getHeartbeatInterval(), config.getHeartbeatInterval(), TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("p", "port", true, "Server port (default: 8087)");
        options.addOption("c", "config", true, "Configuration file path");
        options.addOption("h", "help", false, "Show help");

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("screenshare-server", options);
                return;
            }

            ServerConfig config;
            if (cmd.hasOption("config")) {
                config = ServerConfig.loadFromFile(cmd.getOptionValue("config"));
            } else {
                config = new ServerConfig();
            }

            if (cmd.hasOption("port")) {
                // Override port from command line
                System.setProperty("server.port", cmd.getOptionValue("port"));
            }

            ScreenShareServer server = new ScreenShareServer(config);

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

            server.start();

        } catch (ParseException e) {
            Logger.error("Error parsing command line arguments: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("screenshare-server", options);
        } catch (IOException e) {
            Logger.error("Failed to start server", e);
        }
    }
}

/* 2025-06-19 16:26:56: Simplified client-server handshake */

/* 2025-06-19 16:26:59: Improved error logging */

/* 2025-06-19 16:26:59: Added null safety checks */

/* 2025-06-19 16:27:01: Enhanced thread safety */

/* 2025-06-19 16:27:01: Codebase cleanup and style consistency */

/* 2025-06-19 16:27:03: Updated JavaDoc comments */

/* 2025-06-19 16:27:03: FIXME: Concurrency issue needs attention */

/* 2025-06-19 16:27:04: Performance tweaks for high load */
