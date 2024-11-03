package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonChat;
import com.clientchat.protocol.JsonUser;

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
                    System.out.println("Hai scelto 1!");
                    List<JsonChat> tmp = eventListener.getAllChats();
                    for (JsonChat chat : tmp) {
                        System.out.println(chat.getId());
                    }
                    break;
                case "2":
                    System.out.println("Hai scelto 2!");
                    break;
                case "3":
                    handleViewLoggedUser();
                    break;
                case "4":
                    handleLogout();
                    break;
                case "0":
                    super.handleExit();
                    break;
                default:
                    System.out.println("Unknown option. Please try again.");
            }
        } while(!choice.equals("0"));
    }

    // private methods --> can only be seen inside this class
    private void printMenu() {
        System.out.println(super.newLine() + "- - - MENU - - -");
        System.out.println("1) View all chats");
        System.out.println("2) Connect to a chat");
        System.out.println("3) See own profile");
        System.out.println("4) Logout");
        System.out.println("0) Exit");
        System.out.print(": ");
    }

    private void handleLogout() throws IOException {
        super.sendReq(CommandType.LOGOUT);
        CommandType res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            // set choice to "0" to leave this loop, without exiting the app
            // this send the user to the auth menu
            choice = "0";
        } else {
            System.out.println(res.getDescription());
        }
    }

    private void handleViewLoggedUser() throws IOException {
        super.sendReq(CommandType.REQ_LOGGED_USER);
        CommandType res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            // TODO: the SERVER should SEND a JsonUser with PASSWORD set to NULL
            JsonUser user = super.gson.fromJson(catchRes(), JsonUser.class);
            System.out.println("You are logged in as: " + user.getUsername());
        } else {
            System.out.println(res.getDescription());
        }
    }
}
