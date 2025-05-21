// ================================
// ServerConfig.java
// ================================
package com.screenshare.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerConfig {
    private int port = 8085;
    private int udpPort = 8081;
    private int maxClients = 50;
    private int threadPoolSize = 20;
    private int heartbeatInterval = 30000; // 30 seconds
    private int clientTimeout = 60000; // 60 seconds
    private String logLevel = "INFO";
    private boolean enableUdp = true;
    private int bufferSize = 64 * 1024; // 64KB
    private int maxPayloadSize = 1024 * 1024; // 1MB

    public ServerConfig() {
        // Default constructor with default values
    }

    public static ServerConfig loadFromFile(String filename) {
        ServerConfig config = new ServerConfig();
        Properties props = new Properties();

        try (InputStream input = new FileInputStream(filename)) {
            props.load(input);

            config.port = Integer.parseInt(props.getProperty("server.port", "8089"));
            config.udpPort = Integer.parseInt(props.getProperty("server.udp.port", "8081"));
            config.maxClients = Integer.parseInt(props.getProperty("server.max.clients", "50"));
            config.threadPoolSize = Integer.parseInt(props.getProperty("server.thread.pool.size", "20"));
            config.heartbeatInterval = Integer.parseInt(props.getProperty("server.heartbeat.interval", "30000"));
            config.clientTimeout = Integer.parseInt(props.getProperty("server.client.timeout", "60000"));
            config.logLevel = props.getProperty("server.log.level", "INFO");
            config.enableUdp = Boolean.parseBoolean(props.getProperty("server.enable.udp", "true"));
            config.bufferSize = Integer.parseInt(props.getProperty("server.buffer.size", String.valueOf(64 * 1024)));
            config.maxPayloadSize = Integer.parseInt(props.getProperty("server.max.payload.size", String.valueOf(1024 * 1024)));

            System.out.println("Loaded server configuration from: " + filename);

        } catch (IOException e) {
            System.err.println("Error loading server config file '" + filename + "': " + e.getMessage());
            System.err.println("Using default configuration values");
        } catch (NumberFormatException e) {
            System.err.println("Error parsing numeric values in config file '" + filename + "': " + e.getMessage());
            System.err.println("Using default configuration values");
        }

        return config;
    }

    public static ServerConfig loadFromResources(String resourcePath) {
        ServerConfig config = new ServerConfig();
        Properties props = new Properties();

        try (InputStream input = ServerConfig.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                System.err.println("Unable to find config resource: " + resourcePath);
                return config;
            }

            props.load(input);

            config.port = Integer.parseInt(props.getProperty("server.port", "8088"));
            config.udpPort = Integer.parseInt(props.getProperty("server.udp.port", "8081"));
            config.maxClients = Integer.parseInt(props.getProperty("server.max.clients", "50"));
            config.threadPoolSize = Integer.parseInt(props.getProperty("server.thread.pool.size", "20"));
            config.heartbeatInterval = Integer.parseInt(props.getProperty("server.heartbeat.interval", "30000"));
            config.clientTimeout = Integer.parseInt(props.getProperty("server.client.timeout", "60000"));
            config.logLevel = props.getProperty("server.log.level", "INFO");
            config.enableUdp = Boolean.parseBoolean(props.getProperty("server.enable.udp", "true"));
            config.bufferSize = Integer.parseInt(props.getProperty("server.buffer.size", String.valueOf(64 * 1024)));
            config.maxPayloadSize = Integer.parseInt(props.getProperty("server.max.payload.size", String.valueOf(1024 * 1024)));

            System.out.println("Loaded server configuration from resources: " + resourcePath);

        } catch (IOException e) {
            System.err.println("Error loading server config resource '" + resourcePath + "': " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing numeric values in config resource '" + resourcePath + "': " + e.getMessage());
        }

        return config;
    }

    public void printConfiguration() {
        System.out.println("=== Server Configuration ===");
        System.out.println("Port: " + port);
        System.out.println("UDP Port: " + udpPort);
        System.out.println("Max Clients: " + maxClients);
        System.out.println("Thread Pool Size: " + threadPoolSize);
        System.out.println("Heartbeat Interval: " + heartbeatInterval + "ms");
        System.out.println("Client Timeout: " + clientTimeout + "ms");
        System.out.println("Log Level: " + logLevel);
        System.out.println("UDP Enabled: " + enableUdp);
        System.out.println("Buffer Size: " + bufferSize + " bytes");
        System.out.println("Max Payload Size: " + maxPayloadSize + " bytes");
        System.out.println("============================");
    }

    // Getters
    public int getPort() { return port; }
    public int getUdpPort() { return udpPort; }
    public int getMaxClients() { return maxClients; }
    public int getThreadPoolSize() { return threadPoolSize; }
    public int getHeartbeatInterval() { return heartbeatInterval; }
    public int getClientTimeout() { return clientTimeout; }
    public String getLogLevel() { return logLevel; }
    public boolean isUdpEnabled() { return enableUdp; }
    public int getBufferSize() { return bufferSize; }
    public int getMaxPayloadSize() { return maxPayloadSize; }

    // Setters (for command line overrides)
    public void setPort(int port) { this.port = port; }
    public void setUdpPort(int udpPort) { this.udpPort = udpPort; }
    public void setMaxClients(int maxClients) { this.maxClients = maxClients; }
    public void setThreadPoolSize(int threadPoolSize) { this.threadPoolSize = threadPoolSize; }
    public void setHeartbeatInterval(int heartbeatInterval) { this.heartbeatInterval = heartbeatInterval; }
    public void setClientTimeout(int clientTimeout) { this.clientTimeout = clientTimeout; }
    public void setLogLevel(String logLevel) { this.logLevel = logLevel; }
    public void setEnableUdp(boolean enableUdp) { this.enableUdp = enableUdp; }
    public void setBufferSize(int bufferSize) { this.bufferSize = bufferSize; }
    public void setMaxPayloadSize(int maxPayloadSize) { this.maxPayloadSize = maxPayloadSize; }
}

/* 2025-06-19 16:26:50: Refactored network layer */

/* 2025-06-19 16:26:54: Modularized protocol logic */

/* 2025-06-19 16:26:54: Logging mechanism refactored */

/* 2025-06-19 16:26:55: Codebase cleanup and style consistency */

/* 2025-06-19 16:26:55: FIXME: Concurrency issue needs attention */

/* 2025-06-19 16:26:55: FIXME: Concurrency issue needs attention */

/* 2025-06-19 16:26:58: Updated JavaDoc comments */
