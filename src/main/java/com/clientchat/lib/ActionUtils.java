package com.clientchat.lib;

import java.io.IOException;

public class ActionUtils {
    @FunctionalInterface
    public interface ActionWithException {
        void run() throws IOException;
    }

    public static Runnable wrapAction(ActionWithException action) {
        return () -> {
            try {
                action.run();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        };
    }
}
