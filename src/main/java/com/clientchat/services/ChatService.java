package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;

public class ChatService extends Service {
    private static ChatService instance;

    private ChatService(Socket socket) throws IOException {
        super(socket);
    }

    public static ChatService getInstance(Socket socket) throws IOException {
        if (instance == null) {
            instance = new ChatService(socket);
        }
        return instance;
    }

    // main body --> fn to run
    public void run() {
        while (Service.isRunning) {
            System.out.println("Chat service is going...");
            
        }
    }
}
