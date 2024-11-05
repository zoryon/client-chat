package com.clientchat;

import com.clientchat.services.AuthService;
import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            // "10.22.9.10"
            Socket socket = new Socket("10.22.9.10", 3000);

            // auth service + chat service
            AuthService.getInstance(socket).run();
        } catch (IOException e) {
            System.err.println("Error during client execution: " + e.getMessage());
        }
    }
}