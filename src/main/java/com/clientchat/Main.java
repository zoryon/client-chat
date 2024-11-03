package com.clientchat;

import com.clientchat.services.AuthService;
import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            Socket socket = new Socket("localhost", 3000);

            new Thread(AuthService.getInstance(socket)).start();
        } catch (IOException e) {
            System.err.println("Error during client execution: " + e.getMessage());
        }
    }
}