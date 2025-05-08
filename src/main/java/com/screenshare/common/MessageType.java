// MessageType.java
package com.screenshare.common;

import java.io.Serializable;

public enum MessageType {
    CONNECT_REQUEST(1),
    CONNECT_RESPONSE(2),
    HEARTBEAT(3),
    HEARTBEAT_ACK(4),
    START_SHARE(5),
    STOP_SHARE(6),
    SCREEN_DATA(7),
    CLIENT_LIST(8),
    ERROR(9),
    DISCONNECT(10);

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MessageType fromValue(int value) {
        for (MessageType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message type: " + value);
    }
}





/* 2025-06-19 16:26:51: Updated JavaDoc comments */

/* 2025-06-19 16:26:52: Codebase cleanup and style consistency */
