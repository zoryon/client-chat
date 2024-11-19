package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import com.clientchat.lib.ActionUtils;
import com.clientchat.lib.Character;
import com.clientchat.lib.Console;
import com.clientchat.lib.MenuBuilder;
import com.clientchat.lib.MenuOption;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonMessage;

public class ChatService extends Service {
    // attributes
    private static ChatService instance;
    private String choice;
    private CommandType res;

    // constructors
    private ChatService(Socket socket) throws IOException {
        super(socket);

        // initialize menu
        super.initializeMenuOptions(
                new MenuBuilder()
                        .addOption("0", "Exit", ActionUtils.wrapAction(super::handleExit))
                        .addOption("1", "View your chats", this::handleViewUserChatList)
                        .addOption("2", "Create a chat", ActionUtils.wrapAction(this::handleCreateChat))
                        .addOption("3", "Connect to a chat", ActionUtils.wrapAction(this::handleConnectToChat))
                        .addOption("4", "Manage profile", ActionUtils.wrapAction(this::handleViewProfile))
                        .build());
    }

    // only one instance can exists at a time
    public static ChatService getInstance(Socket socket) throws IOException {
        if (instance == null) {
            instance = new ChatService(socket);
        }
        return instance;
    }

    /*
     * main body (NOT A THREAD) --> fn to run.
     * it's just to divide code in many files, instead of a single one
     */
    public void run() throws IOException {
        do {
            MenuOption.printMenu(Character.NEW_LINE.getValue() + "- - - MENU - - -", super.menuOptions);

            // get the choice from the user
            choice = super.keyboard.nextLine().trim();

            // get the action to perform based on the user's choice
            MenuOption selectedOption = super.menuOptions.getOrDefault(choice,
                    new MenuOption("Unknown option", super::handleUnknownOption));

            Console.clear();
            // run the passed fn related to the user's choice
            selectedOption.getAction().run();
        } while (Service.isRunning && !choice.equals("0"));
    }

    // private methods --> can only be seen inside this class
    private void handleViewUserChatList() {
        System.out.println("Your chats:");
        super.eventListener.printUserChatList();
    }

    private void handleViewProfile() throws IOException, InterruptedException {
        // starting the loop with user's profile menu
        // if method return true it means the user wants to logout (NOT EXIT)
        if (ProfileService.getInstance(socket).run()) { // equal to "new ProfileService(socket).run()"
            Console.clear();
            choice = "0";
        }
    }

    private void handleCreateChat() throws IOException {
        CreateChatService.getInstance(socket).run();
    }

    private void handleConnectToChat() throws IOException, InterruptedException {
        // show the user their chatlist
        handleViewUserChatList();

        // can call this version of the fn, if there's no chat identifier to send
        handleConnectToChat(null);
    }

    // protected methods --> can only be seen inside this package
    protected void handleConnectToChat(String chatToSend) throws IOException, InterruptedException {
        if (chatToSend == null || chatToSend.trim().isEmpty() || !chatToSend.contains("#")) {
            // get chat identifier from user
            System.out.print(Character.NEW_LINE.getValue() + "Enter chat identifier (chatName#chatId): ");
            chatToSend = super.keyboard.nextLine().trim();

            if (chatToSend == null || chatToSend.isEmpty() || !chatToSend.contains("#")) {
                System.out.println("Invalid input! Please use the format chatName#chatId");
                return;
            }
        }

        // making sure the user has rights to access the chat
        super.sendReq(CommandType.NAV_CHAT);

        String[] parts = chatToSend.split("#", 2);
        if (parts.length != 2) {
            System.out.println("Invalid input! Please use the format chatName#chatId");
            return;
        }

        String chatId = parts[1];
        super.sendJsonReq(chatId);
        res = catchCommandRes();

        if (super.isSuccess(res)) {
            Console.clear();
            System.out.println("Connected successfully...");
            System.out.println(Character.NEW_LINE.getValue() + "- - - " + chatToSend + " - - -");

            // thread which displays the up-to-date messages of a certain chat
            ChatMessagesDisplayService displayService = new ChatMessagesDisplayService(socket, Integer.parseInt(chatId), chatToSend, super.eventListener);
            displayService.start();
            ChatMessagesDisplayService.startDisplay();

            String tmp;
            do {
                // add small delay to avoid congestion
                delay();

                // get and print the user msg OR command
                tmp = super.keyboard.nextLine();

                // if message is null or empty, just ignore it
                if (tmp == null || tmp.trim().isEmpty())
                    continue;

                // if
                processCommand(tmp, chatId, displayService);
            } while (!tmp.equals("/back"));

            Console.clear();
        } else {
            System.out.println("Error: " + res.getDescription());
        }

        ChatMessagesDisplayService.stopDisplay();
    }

    private void processCommand(String command, String chatId, ChatMessagesDisplayService displayService) throws IOException, InterruptedException {
        if (command.equals("/back"))
            return; // do nothing

        // case: /help newContent
        if (command.equals("/help")) {
            displayHelp();
            return;
        }

        // case: /remove #messageId
        if (command.startsWith("/remove")) {
            handleRemoveCommand(command, chatId, displayService);
            return;
        }

        // case: /update #messageId: newContent
        if (command.startsWith("/update")) {
            handleUpdateCommand(command, chatId, displayService);
            return;
        }

        // default case: send a text message
        sendMessage(command, chatId);
    }

    private void displayHelp() {
        System.out.println("/back --> go back to menu");
        System.out.println("/remove #messageId --> delete a message");
        System.out.println("/update #messageId --> update the content of a message" + Character.NEW_LINE.getValue());
    }

    private void handleRemoveCommand(String command, String chatId, ChatMessagesDisplayService displayService) throws IOException, InterruptedException {
        String[] parts = command.split("#");
        if (parts.length != 2) {
            System.out.println("Invalid input! Please use the format /remove #messageId");
            return;
        }

        String msgId = parts[1];
        try {
            // attempt to parse chatId and msgId as integers
            int chatIdInt = Integer.parseInt(chatId);
            int msgIdInt = Integer.parseInt(msgId);
            
            // send request
            super.sendReq(CommandType.RM_MSG);
            super.sendJsonReq(new JsonMessage(chatIdInt, msgIdInt));
            res = super.catchCommandRes();
    
            // handle response
            if (super.isSuccess(res)) {
                ArrayList<JsonMessage> chatMessages = super.eventListener.getChatMessages(chatIdInt);
                for (int i = 0; i < chatMessages.size(); i++) {
                    if (chatMessages.get(i).getId() == msgIdInt) {
                        chatMessages.remove(i);
                        break;
                    }
                }

                displayService.reloadChat();
            } else {
                System.out.println("Error: " + res.getDescription());
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format. Please ensure that chatId and messageId are numeric.");
        }
    }

    private void handleUpdateCommand(String command, String chatId, ChatMessagesDisplayService displayService) throws IOException, InterruptedException {
        String[] parts = command.split("#");
        if (parts.length != 2) {
            System.out.println("Invalid input! Please use the format /update #messageId: message");
            return;
        }
    
        String[] newParts = parts[1].split(":" + Character.SPACE.getValue(), 2);
        if (newParts.length != 2) {
            System.out.println("Invalid input! Please use the format /update #messageId: message");
            return;
        }
    
        String msgId = newParts[0];
        String newContent = newParts[1];
    
        try {
            // Attempt to parse chatId and msgId as integers
            int chatIdInt = Integer.parseInt(chatId);
            int msgIdInt = Integer.parseInt(msgId);
    
            super.sendReq(CommandType.UPD_MSG);
            super.sendJsonReq(new JsonMessage(chatIdInt, msgIdInt, newContent));
            res = super.catchCommandRes();
    
            if (super.isSuccess(res)) {
                ArrayList<JsonMessage> chatMessages = super.eventListener.getChatMessages(chatIdInt);
                for (int i = 0; i < chatMessages.size(); i++) {
                    if (chatMessages.get(i).getId() == msgIdInt) {
                        chatMessages.get(i).setContent(newContent);
                        break;
                    }
                }
                displayService.reloadChat();
            } else {
                System.out.println("Error: " + res.getDescription());
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format. Please ensure that chatId and messageId are numeric.");
        }
    }

    private void sendMessage(String message, String chatId) throws IOException, InterruptedException {
        super.sendReq(CommandType.SEND_MSG);
        super.sendJsonReq(new JsonMessage(Integer.parseInt(chatId), message));
        res = super.catchCommandRes();

        JsonMessage msg = super.catchJsonRes(JsonMessage.class);
        eventListener.addMessage(msg);

        System.out.print("[#" + msg.getId() + "]" + Character.NEW_LINE.getValue() + Character.NEW_LINE.getValue());

        if (!super.isSuccess(res)) {
            System.out.println(Character.NEW_LINE.getValue() + "Error " + res.getDescription());
        }
    }

    private void delay() throws InterruptedException {
        // wait 800ms before allowing to send other messages
        Thread.sleep(300);
    }
}
