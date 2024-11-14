package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.clientchat.auth.AuthManager;
import com.clientchat.lib.ActionUtils;
import com.clientchat.lib.Console;
import com.clientchat.lib.MenuBuilder;
import com.clientchat.lib.MenuOption;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonChat;
import com.clientchat.protocol.JsonGroup;

public class CreateChatService extends Service {
    // attributes
    private static CreateChatService instance;
    private String choice;
    private CommandType res;

    // constructors
    private CreateChatService(Socket socket) throws IOException {
        super(socket);

        // initialize menu
        super.initializeMenuOptions(
            new MenuBuilder()
                .addOption("0", "Exit", ActionUtils.wrapAction(super::handleExit))
                .addOption("1", "Back", ActionUtils.wrapAction(this::handleBack))
                .addOption("2", "Create direct chat", ActionUtils.wrapAction(this::createPrivateDirectChat))
                .addOption("3", "Create group chat", ActionUtils.wrapAction(this::createPrivateGroupChat))
                .build()
        );
    }

    // only one instance can exists at a time
    public static CreateChatService getInstance(Socket socket) throws IOException {
        if (instance == null) {
            instance = new CreateChatService(socket);
        }
        return instance;
    }

    /*
     * main body (NOT A THREAD) --> fn to run.
     * it's just to divide code in many files, instead of a single one
     */
    public void run() throws IOException {
        do {
            MenuOption.printMenu("- - - CREATE CHAT MENU - - -", super.menuOptions);

            // get the choice from the user
            choice = super.keyboard.nextLine().trim();

            // get the action to perform based on the user's choice
            MenuOption selectedOption = super.menuOptions.getOrDefault(choice, new MenuOption("Unknown option", super::handleUnknownOption));

            // run the passed fn related to the user's choice
            selectedOption.getAction().run();
        } while (Service.isRunning && !choice.equals("0"));

        Console.clear();
    }

    // private methods --> can only be seen inside this class
    private void createPrivateDirectChat() throws IOException, InterruptedException {
        // get the username with whom the user wants to create a new chat
        System.out.print("Enter the username with whom you want to start a chat: ");
        String username = super.keyboard.nextLine().trim();

        // processing request
        sendReq(CommandType.NEW_CHAT);
        sendJsonReq(username);
        res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            String chatTmp = catchJsonReq(String.class);

            // [0] is the chat name
            // [1] is the actual chatId
            String[] chatId = chatTmp.split("#");

            JsonChat newChat = new JsonChat(Integer.parseInt(chatId[1]), chatId[0]);
            super.eventListener.addChat(newChat);

            Console.clear();
            System.out.println("Private direct chat created successfully.");
            System.out.println("Automatically connecting..");

            // get the identifier (chatName#chatId) and automatically connect
            ChatService.getInstance(socket).handleConnectToChat(chatTmp);
        } else {
            System.out.println("Error: " + res.getDescription());
        }
    }

    private void createPrivateGroupChat() throws IOException, InterruptedException {
        System.out.print("Enter the group name: ");
        String groupName = super.keyboard.nextLine().trim();
        
        // add participants
        ArrayList<String> usernameList = new ArrayList<>();
        usernameList.add(AuthManager.getInstance().getUsername());

        System.out.println("Enter username (/stop to continue): ");
        String tmp;
        do {
            System.out.print(": ");
            tmp = super.keyboard.nextLine().trim();
            if (!tmp.equals("/stop")) usernameList.add(tmp);
        } while (!tmp.equals("/stop"));
        
        sendReq(CommandType.NEW_GROUP);
        sendJsonReq(new JsonGroup(groupName, usernameList));
        CommandType res = super.catchCommandRes();
        
        if (super.isSuccess(res)) {
            String groupTmp = catchJsonReq(String.class);

            // [0] is the chat name
            // [1] is the actual chatId
            String[] chatId = groupTmp.split("#");

            JsonChat newChat = new JsonChat(Integer.parseInt(chatId[1]), chatId[0]);
            super.eventListener.addChat(newChat);

            Console.clear();
            System.out.println("Private group chat created successfully.");
            System.out.println("Automatically connecting..");

            // get the identifier (chatName#chatId) and automatically connect
            ChatService.getInstance(socket).handleConnectToChat(groupTmp);
        } else {
            System.out.println("Error: " + res.getDescription());
        }
    }

    private void handleBack() throws IOException {
        /*
         * set choice to "0" to leave this loop, without exiting the app.
         * this send the user to the service menu
         */
        choice = "0";
    }
}
