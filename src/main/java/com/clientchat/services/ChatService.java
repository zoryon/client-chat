package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;

import com.clientchat.protocol.CommandType;

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
    public void run() throws IOException {
        while (Service.isRunning) {
            String choice;
            do {
                printMenu();
                choice = super.keyboard.nextLine();

                switch (choice) {
                    case "1":
                        break;
                    case "2":
                        break;
                    case "0":
                        super.handleExit();
                        break;
                    default:
                        System.out.println("Unknown option. Please try again.");
                }
            } while (choice.equals(""));
        }
    }

    public void printMenu() {
        System.out.println(super.newLine() + "- - - MENU - - -");
        System.out.println("1) View all chats");
        System.out.println("2) Connect to a chat");
        System.out.println("0) Exit");
        System.out.print(":");
    }
}
