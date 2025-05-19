
// ================================
// ClientConfig.java
// ================================
package com.screenshare.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientConfig {
    private String serverHost = "localhost";
    private int serverPort = 8085;
    private int serverUdpPort = 8081;
    private int reconnectAttempts = 3;
    private int reconnectDelay = 5000; // 5 seconds
    private int heartbeatInterval = 30000; // 30 seconds
    private int connectionTimeout = 10000; // 10 seconds
    private String logLevel = "INFO";
    private boolean enableUdp = true;
    private int bufferSize = 64 * 1024; // 64KB
    private String clientName = "";

    public ClientConfig() {
        // Default constructor with default values
    }

    public static ClientConfig loadFromFile(String filename) {
        ClientConfig config = new ClientConfig();
        Properties props = new Properties();

        try (InputStream input = new FileInputStream(filename)) {
            props.load(input);

            config.serverHost = props.getProperty("server.host", "localhost");
            config.serverPort = Integer.parseInt(props.getProperty("server.port", "8080"));
            config.serverUdpPort = Integer.parseInt(props.getProperty("server.udp.port", "8081"));
            config.reconnectAttempts = Integer.parseInt(props.getProperty("client.reconnect.attempts", "3"));
            config.reconnectDelay = Integer.parseInt(props.getProperty("client.reconnect.delay", "5000"));
            config.heartbeatInterval = Integer.parseInt(props.getProperty("client.heartbeat.interval", "30000"));
            config.connectionTimeout = Integer.parseInt(props.getProperty("client.connection.timeout", "10000"));
            config.logLevel = props.getProperty("client.log.level", "INFO");
            config.enableUdp = Boolean.parseBoolean(props.getProperty("client.enable.udp", "true"));
            config.bufferSize = Integer.parseInt(props.getProperty("client.buffer.size", String.valueOf(64 * 1024)));
            config.clientName = props.getProperty("client.name", "");

            System.out.println("Loaded client configuration from: " + filename);

        } catch (IOException e) {
            System.err.println("Error loading client config file '" + filename + "': " + e.getMessage());
            System.err.println("Using default configuration values");
        } catch (NumberFormatException e) {
            System.err.println("Error parsing numeric values in config file '" + filename + "': " + e.getMessage());
            System.err.println("Using default configuration values");
        }

        return config;
    }

    public static ClientConfig loadFromResources(String resourcePath) {
        ClientConfig config = new ClientConfig();
        Properties props = new Properties();

        try (InputStream input = ClientConfig.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                System.err.println("Unable to find config resource: " + resourcePath);
                return config;
            }

            props.load(input);

            config.serverHost = props.getProperty("server.host", "localhost");
            config.serverPort = Integer.parseInt(props.getProperty("server.port", "8080"));
            config.serverUdpPort = Integer.parseInt(props.getProperty("server.udp.port", "8081"));
            config.reconnectAttempts = Integer.parseInt(props.getProperty("client.reconnect.attempts", "3"));
            config.reconnectDelay = Integer.parseInt(props.getProperty("client.reconnect.delay", "5000"));
            config.heartbeatInterval = Integer.parseInt(props.getProperty("client.heartbeat.interval", "30000"));
            config.connectionTimeout = Integer.parseInt(props.getProperty("client.connection.timeout", "10000"));
            config.logLevel = props.getProperty("client.log.level", "INFO");
            config.enableUdp = Boolean.parseBoolean(props.getProperty("client.enable.udp", "true"));
            config.bufferSize = Integer.parseInt(props.getProperty("client.buffer.size", String.valueOf(64 * 1024)));
            config.clientName = props.getProperty("client.name", "");

            System.out.println("Loaded client configuration from resources: " + resourcePath);

        } catch (IOException e) {
            System.err.println("Error loading client config resource '" + resourcePath + "': " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing numeric values in config resource '" + resourcePath + "': " + e.getMessage());
        }

        return config;
    }

    public void printConfiguration() {
        System.out.println("=== Client Configuration ===");
        System.out.println("Server Host: " + serverHost);
        System.out.println("Server Port: " + serverPort);
        System.out.println("Server UDP Port: " + serverUdpPort);
        System.out.println("Reconnect Attempts: " + reconnectAttempts);
        System.out.println("Reconnect Delay: " + reconnectDelay + "ms");
        System.out.println("Heartbeat Interval: " + heartbeatInterval + "ms");
        System.out.println("Connection Timeout: " + connectionTimeout + "ms");
        System.out.println("Log Level: " + logLevel);
        System.out.println("UDP Enabled: " + enableUdp);
        System.out.println("Buffer Size: " + bufferSize + " bytes");
        System.out.println("Client Name: " + (clientName.isEmpty() ? "Not set" : clientName));
        System.out.println("=============================");
    }

    // Getters
    public String getServerHost() { return serverHost; }
    public int getServerPort() { return serverPort; }
    public int getServerUdpPort() { return serverUdpPort; }
    public int getReconnectAttempts() { return reconnectAttempts; }
    public int getReconnectDelay() { return reconnectDelay; }
    public int getHeartbeatInterval() { return heartbeatInterval; }
    public int getConnectionTimeout() { return connectionTimeout; }
    public String getLogLevel() { return logLevel; }
    public boolean isUdpEnabled() { return enableUdp; }
    public int getBufferSize() { return bufferSize; }
    public String getClientName() { return clientName; }

    // Setters (for command line overrides)
    public void setServerHost(String serverHost) { this.serverHost = serverHost; }
    public void setServerPort(int serverPort) { this.serverPort = serverPort; }
    public void setServerUdpPort(int serverUdpPort) { this.serverUdpPort = serverUdpPort; }
    public void setReconnectAttempts(int reconnectAttempts) { this.reconnectAttempts = reconnectAttempts; }
    public void setReconnectDelay(int reconnectDelay) { this.reconnectDelay = reconnectDelay; }
    public void setHeartbeatInterval(int heartbeatInterval) { this.heartbeatInterval = heartbeatInterval; }
    public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }
    public void setLogLevel(String logLevel) { this.logLevel = logLevel; }
    public void setEnableUdp(boolean enableUdp) { this.enableUdp = enableUdp; }
    public void setBufferSize(int bufferSize) { this.bufferSize = bufferSize; }
    public void setClientName(String clientName) { this.clientName = clientName; }
}

/* 2025-06-19 16:26:53: NOTE: Code modularity improved */

/* 2025-06-19 16:26:57: Removed dead code from protocol */

/* 2025-06-19 16:26:58: Updated JavaDoc comments */
