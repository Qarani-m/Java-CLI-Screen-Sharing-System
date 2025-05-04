
// NetworkBuffer.java
package com.screenshare.common;

import java.nio.ByteBuffer;

public class NetworkBuffer {
    private ByteBuffer buffer;
    private static final int DEFAULT_SIZE = 64 * 1024; // 64KB

    public NetworkBuffer() {
        this(DEFAULT_SIZE);
    }

    public NetworkBuffer(int size) {
        this.buffer = ByteBuffer.allocate(size);
    }

    public void clear() {
        buffer.clear();
    }

    public void flip() {
        buffer.flip();
    }

    public boolean hasRemaining() {
        return buffer.hasRemaining();
    }

    public int remaining() {
        return buffer.remaining();
    }

    public byte[] getBytes() {
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return data;
    }

    public void putBytes(byte[] data) {
        if (data.length > buffer.remaining()) {
            // Expand buffer if needed
            ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() + data.length);
            buffer.flip();
            newBuffer.put(buffer);
            buffer = newBuffer;
        }
        buffer.put(data);
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}

/* 2025-06-19 16:26:51: Enhanced thread safety */
