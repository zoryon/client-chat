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
    public void run() throws IOException {
        String choice;
        do {
            printMenu();
            choice = super.keyboard.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("Hai scelto 1!");
                    break;
                case "2":
                    System.out.println("Hai scelto 2!");
                    break;
                case "3":
                    // TODO: request the name from the server
                    break;
                case "4":
                    // TODO: before logging out from the client, request the logout from server

                    // set choice to "0" to leave this loop, without exiting the app
                    // this send the user to the auth menu
                    choice = "0";
                    break;
                case "0":
                    super.handleExit();
                    break;
                default:
                    System.out.println("Unknown option. Please try again.");
            }
        } while(!choice.equals("0"));
    }

    public void printMenu() {
        System.out.println(super.newLine() + "- - - MENU - - -");
        System.out.println("1) View all chats");
        System.out.println("2) Connect to a chat");
        System.out.println("3) See profile");
        System.out.println("4) Logout");
        System.out.println("0) Exit");
        System.out.print(": ");
    }
}
