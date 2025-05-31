// Logger.java
package com.screenshare.util;

public class Logger {
    private static final boolean DEBUG = true;

    public static void info(String message) {
        System.out.println("[INFO] " + timestamp() + " " + message);
    }

    public static void debug(String message) {
        if (DEBUG) {
            System.out.println("[DEBUG] " + timestamp() + " " + message);
        }
    }

    public static void error(String message) {
        System.err.println("[ERROR] " + timestamp() + " " + message);
    }

    public static void error(String message, Throwable t) {
        System.err.println("[ERROR] " + timestamp() + " " + message);
        t.printStackTrace();
    }

    private static String timestamp() {
        return java.time.LocalDateTime.now().toString();
    }
}
/* 2025-06-19 16:26:51: Improved error logging */

/* 2025-06-19 16:26:53: Removed dead code from protocol */

/* 2025-06-19 16:26:55: Logging mechanism refactored */

/* 2025-06-19 16:26:57: TODO: Add unit tests */

/* 2025-06-19 16:27:02: Simplified client-server handshake */
