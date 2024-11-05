package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonMessage;

public class ChatService extends Service {
    // attributes
    private static ChatService instance;
    private final EventListenerService eventListener;
    private String choice;

    // constructors
    private ChatService(Socket socket) throws IOException {
        super(socket);
        this.eventListener = EventListenerService.getInstance(socket);

        // start the listener service to get up-to-date data
        new Thread(eventListener).start();
    }

    // only one instance can exists at a time
    public static ChatService getInstance(Socket socket) throws IOException {
        if (instance == null) {
            instance = new ChatService(socket);
        }
        return instance;
    }

    // main body --> fn to run
    public void run() throws IOException {
        do {
            printMenu();
            choice = super.keyboard.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("Your chats:");
                    eventListener.printAllChats();
                    break;
                case "2":
                    handleConnectToChat();
                    break;
                case "3":
                    handleViewProfile();
                    break;
                case "0":
                    super.handleExit();
                    break;
                default:
                    System.out.println("Unknown option. Please try again.");
            }
        } while (!choice.equals("0"));
    }

    // private methods --> can only be seen inside this class
    private void printMenu() {
        System.out.println(super.newLine() + "- - - MENU - - -");
        System.out.println("1) View all chats");
        System.out.println("2) Connect to a chat");
        System.out.println("3) See own profile");
        System.out.println("0) Exit");
        System.out.print(": ");
    }

    private void handleViewProfile() throws IOException {
        if (ProfileService.getInstance(socket).run()) choice = "0";
    }

    private void handleConnectToChat() throws IOException {
        // get chat identifier
        System.out.print("Insert chat identifier (chatName#chatId): ");
        String chatToSend = super.keyboard.nextLine();

        // move to a specific chat
        super.sendReq(CommandType.NAV_CHAT.toString());

        // send chat identifier to server
        super.sendJsonReq(chatToSend);

        CommandType res = super.catchCommandRes();
        if (isSuccess(res)) {
            String tmp;
            do {
                tmp = super.keyboard.nextLine();
                switch (tmp) {
                    case "/exit":
                    break;
                    default:
                        super.sendReq(CommandType.SEND_MSG.toString());
                        super.sendJsonReq(
                            new JsonMessage(
                                Integer.parseInt(chatToSend.split("#")[1]), 
                                tmp
                            )
                        );
                        CommandType ok = catchCommandRes();
                        if (isSuccess(ok)) {
                            System.out.print("- Successfully sent");
                        } else {
                            System.out.print("- Error: " + ok.getDescription());
                        }
                }
            } while (!tmp.equals("/exit"));
        } else {
            System.out.println("Error: " + res);
        }
    }
}
