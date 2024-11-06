package com.clientchat;

import com.clientchat.services.AuthService;
import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            // LAB PC's IP: 10.22.9.10
            Socket socket = new Socket("localhost", 3000);

            /*
             * AuthService.run() is not a thread, it simulates a main from another class.
             * basically, main will be waiting AuthService to finish
             * before continuing with the normal execution of the code.
             */
            AuthService.getInstance(socket).run(); // equal to "new AuthService(socket).run()"
        } catch (IOException e) {
            System.err.println("Error during client execution: " + e.getMessage());
        }
    }
}