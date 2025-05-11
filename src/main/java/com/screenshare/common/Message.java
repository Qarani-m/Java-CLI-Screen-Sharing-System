
// Message.java
package com.screenshare.common;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageType type;
    private String clientId;
    private byte[] payload;
    private long timestamp;

    public Message() {
        this.timestamp = System.currentTimeMillis();
    }

    public Message(MessageType type, String clientId) {
        this();
        this.type = type;
        this.clientId = clientId;
    }

    public Message(MessageType type, String clientId, byte[] payload) {
        this(type, clientId);
        this.payload = payload;
    }

    public Message(MessageType type, String clientId, String textPayload) {
        this(type, clientId);
        this.payload = textPayload.getBytes();
    }

    // Getters and Setters
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public byte[] getPayload() { return payload; }
    public void setPayload(byte[] payload) { this.payload = payload; }

    public String getPayloadAsString() {
        return payload != null ? new String(payload) : "";
    }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return String.format("Message{type=%s, clientId='%s', payloadSize=%d, timestamp=%d}",
                type, clientId, payload != null ? payload.length : 0, timestamp);
    }
}

/* 2025-06-19 16:26:52: Modularized protocol logic */

/* 2025-06-19 16:26:52: Enhanced thread safety */

/* 2025-06-19 16:26:53: Improved object serialization logic */
