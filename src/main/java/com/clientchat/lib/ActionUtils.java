package com.clientchat.lib;

import java.io.IOException;

public class ActionUtils {
    @FunctionalInterface
    public interface ActionWithException {
        void run() throws IOException, InterruptedException;
    }

    public static Runnable wrapAction(ActionWithException action) {
        return () -> {
            try {
                action.run();
            } catch (IOException e) {
                System.out.println("I/O Error: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();  // Ristabilisce lo stato di interruzione
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        };
    }
}