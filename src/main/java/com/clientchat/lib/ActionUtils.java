package com.clientchat.lib;

import java.io.IOException;

public class ActionUtils {
    /*
     * interface defining a single abstract method 'run'
     * that can throw both IOException and InterruptedException.
     * this is necessary 'cause when we add a MenuOption we
     * pass the constructors the function as a Runnable object.
     * that Runnable obj could throw Exceptions and we
     * need to handle them correctly. 
     */
    @FunctionalInterface
    public interface ActionWithException {
        void run() throws IOException, InterruptedException;
    }

    public static Runnable wrapAction(ActionWithException action) {
        return () -> { // lambda expression
            try {
                // attempt to run the provided action
                action.run();
            } catch (IOException e) {
                // handle IOException specifically and log the error message
                System.out.println("I/O Error: " + e.getMessage());
            } catch (InterruptedException e) {
                // handle InterruptedException, log it, and re-interrupt the thread to maintain
                // its interrupted state
                System.out.println("Thread was interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // catch any other unexpected exceptions and log their messages
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        };
    }
}