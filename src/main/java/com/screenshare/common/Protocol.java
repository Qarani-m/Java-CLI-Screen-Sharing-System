
// Protocol.java
package com.screenshare.common;

import java.io.*;
import java.nio.ByteBuffer;

public class Protocol {
    public static final int HEADER_SIZE = 12; // 4 bytes length + 4 bytes type + 4 bytes clientId length
    public static final int MAX_PAYLOAD_SIZE = 1024 * 1024; // 1MB max payload
    public static final int MAGIC_NUMBER = 0xABCDEF00;

    public static byte[] serialize(Message message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Write magic number
        dos.writeInt(MAGIC_NUMBER);

        // Write message type
        dos.writeInt(message.getType().getValue());

        // Write client ID
        byte[] clientIdBytes = message.getClientId().getBytes();
        dos.writeInt(clientIdBytes.length);
        dos.write(clientIdBytes);

        // Write timestamp
        dos.writeLong(message.getTimestamp());

        // Write payload
        byte[] payload = message.getPayload();
        if (payload != null) {
            dos.writeInt(payload.length);
            dos.write(payload);
        } else {
            dos.writeInt(0);
        }

        return baos.toByteArray();
    }

    public static Message deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);

        // Read and verify magic number
        int magic = dis.readInt();
        if (magic != MAGIC_NUMBER) {
            throw new IOException("Invalid magic number: " + Integer.toHexString(magic));
        }

        // Read message type
        MessageType type = MessageType.fromValue(dis.readInt());

        // Read client ID
        int clientIdLength = dis.readInt();
        byte[] clientIdBytes = new byte[clientIdLength];
        dis.readFully(clientIdBytes);
        String clientId = new String(clientIdBytes);

        // Read timestamp
        long timestamp = dis.readLong();

        // Read payload
        int payloadLength = dis.readInt();
        byte[] payload = null;
        if (payloadLength > 0) {
            payload = new byte[payloadLength];
            dis.readFully(payload);
        }

        Message message = new Message(type, clientId, payload);
        message.setTimestamp(timestamp);
        return message;
    }
}

/* 2025-06-19 16:26:52: Added null safety checks */

/* 2025-06-19 16:26:53: Updated server configuration handling */

/* 2025-06-19 16:26:54: Modularized protocol logic */

/* 2025-06-19 16:26:54: Added null safety checks */
