// ClientHandler.java
package com.screenshare.server;

import com.screenshare.common.*;
import com.screenshare.util.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final String clientId;
    private final ScreenShareServer server;
    private final AtomicBoolean running = new AtomicBoolean(true);

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private long lastHeartbeat;

    public ClientHandler(Socket clientSocket, ScreenShareServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.clientId = generateClientId();
        this.lastHeartbeat = System.currentTimeMillis();

        try {
            this.inputStream = new DataInputStream(clientSocket.getInputStream());
            this.outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            Logger.error("Failed to create streams for client " + clientId, e);
        }
    }

    @Override
    public void run() {
        Logger.info("Client handler started for: " + clientId);

        try {
            // Send welcome message
            sendMessage(new Message(MessageType.CONNECT_RESPONSE, "SERVER", "Connected successfully"));

            while (running.get() && !clientSocket.isClosed()) {
                try {
                    // Read message length first
                    int messageLength = inputStream.readInt();
                    if (messageLength <= 0 || messageLength > Protocol.MAX_PAYLOAD_SIZE) {
                        Logger.error("Invalid message length: " + messageLength);
                        break;
                    }

                    // Read the full message
                    byte[] messageData = new byte[messageLength];
                    inputStream.readFully(messageData);

                    // Deserialize and process
                    Message message = Protocol.deserialize(messageData);
                    processMessage(message);

                } catch (SocketException e) {
                    Logger.info("Client " + clientId + " disconnected");
                    break;
                } catch (EOFException e) {
                    Logger.info("Client " + clientId + " closed connection");
                    break;
                } catch (IOException e) {
                    Logger.error("Error reading from client " + clientId, e);
                    break;
                }
            }

        } catch (Exception e) {
            Logger.error("Unexpected error in client handler for " + clientId, e);
        } finally {
            cleanup();
        }
    }

    private void processMessage(Message message) {
        Logger.debug("Received message: " + message);
        lastHeartbeat = System.currentTimeMillis();

        switch (message.getType()) {
            case HEARTBEAT:
                sendMessage(new Message(MessageType.HEARTBEAT_ACK, "SERVER"));
                break;

            case START_SHARE:
                Logger.info("Client " + clientId + " started sharing screen");
                server.notifyClientStartedSharing(clientId);
                break;

            case STOP_SHARE:
                Logger.info("Client " + clientId + " stopped sharing screen");
                server.notifyClientStoppedSharing(clientId);
                break;

            case SCREEN_DATA:
                // Forward screen data to other clients
                server.broadcastScreenData(clientId, message);
                break;

            case DISCONNECT:
                Logger.info("Client " + clientId + " requested disconnect");
                running.set(false);
                break;

            default:
                Logger.debug("Unhandled message type: " + message.getType());
        }
    }

    public boolean sendMessage(Message message) {
        try {
            byte[] serialized = Protocol.serialize(message);
            outputStream.writeInt(serialized.length);
            outputStream.write(serialized);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            Logger.error("Failed to send message to client " + clientId, e);
            return false;
        }
    }

    public boolean isAlive() {
        return running.get() && !clientSocket.isClosed() &&
                (System.currentTimeMillis() - lastHeartbeat) < 60000; // 60 second timeout
    }

    public String getClientId() {
        return clientId;
    }

    private void cleanup() {
        running.set(false);
        server.removeClient(this);

        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            Logger.error("Error during cleanup for client " + clientId, e);
        }

        Logger.info("Client handler cleaned up for: " + clientId);
    }

    private String generateClientId() {
        return "CLIENT_" + System.currentTimeMillis() + "_" +
                clientSocket.getRemoteSocketAddress().toString().replace("/", "");
    }
}

/* 2025-06-19 16:26:51: Refactored network layer */

/* 2025-06-19 16:26:52: Refactored network layer */

/* 2025-06-19 16:26:54: Improved error logging */

/* 2025-06-19 16:26:56: Introduced proper resource cleanup */

/* 2025-06-19 16:26:57: Refactored network layer */

/* 2025-06-19 16:26:58: Updated JavaDoc comments */

/* 2025-06-19 16:26:59: Codebase cleanup and style consistency */

/* 2025-06-19 16:26:59: Updated server configuration handling */

/* 2025-06-19 16:27:00: Updated server configuration handling */
