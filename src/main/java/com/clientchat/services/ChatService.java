package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import com.clientchat.lib.ActionUtils;
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
                .build()
        );
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
            MenuOption.printMenu(super.newLine() + "- - - MENU - - -", super.menuOptions);

            // get the choice from the user
            choice = super.keyboard.nextLine().trim();

            // get the action to perform based on the user's choice
            MenuOption selectedOption = super.menuOptions.getOrDefault(choice, new MenuOption("Unknown option", super::handleUnknownOption));

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
        if (chatToSend == null || chatToSend.trim().equals("") || !chatToSend.contains("#")) {
            // get chat identifier from user
            System.out.print(super.newLine() + "Enter chat identifier (chatName#chatId): ");
            chatToSend = super.keyboard.nextLine().trim();

            if (chatToSend == null || chatToSend.equals("") || !chatToSend.contains("#")) {
                System.out.println("Invalid input! Please use the format chatName#chatId");
                return;
            }
        }

        // making sure the user has rights to access the chat
        super.sendReq(CommandType.NAV_CHAT);

        String[] parts = chatToSend.split("#");
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
            System.out.println("- - - " + chatToSend + " - - -");

            // thread which displays the up-to-date messages of a certain chat
            new ChatMessagesDisplayService(socket, Integer.parseInt(chatToSend.split("#")[1]), super.eventListener).start();
            ChatMessagesDisplayService.startDisplay();
            String tmp;
            do {
                delay();
                /*
                 * get the user text message in loop.
                 * commands starting with "/" CAN be used as commands
                 * and, as such, should not be sent as text messages to the server.
                 * the message should not be "trimmed", cause the user
                 * can decide completely, BUT a message
                 * containing only spaces is not allowed
                 */
                tmp = super.keyboard.nextLine();

                if (tmp == null || tmp.trim().equals("")) continue;
                
                // case: "/help"
                if (tmp.equals("/help")) {
                    System.out.println("/back --> go back to menu");
                    System.out.println("/remove #messageId --> delete a message");
                    continue;
                }

                // case "/back" --> do nothing and just exit this chat loop
                if (tmp.equals("/back"))
                    break;

                // case: "/remove #messageId"
                if (tmp.startsWith("/remove")) {
                    /*
                     * tmp.split(" #")[1] --> get message id from the user
                     * which is after the " #"
                     */
                    parts = tmp.split(" #");

                    if (parts.length != 2) {
                        System.out.println("Invalid input! Please use the format /remove #messageId");
                        continue;
                    }

                    String msgId = parts[1];

                    super.sendReq(CommandType.RM_MSG);

                    // send JsonMessage with (chatId, id)
                    super.sendJsonReq(new JsonMessage(Integer.parseInt(chatId), Integer.parseInt(msgId)));
                    res = super.catchCommandRes();

                    if (!super.isSuccess(res)) System.out.println("Error: "  + res.getDescription());
                    
                    continue;
                }

                // case: send text message
                super.sendReq(CommandType.SEND_MSG);

                // send JsonMessage with (chatId, content)
                super.sendJsonReq(new JsonMessage(Integer.parseInt(chatId), tmp));
                res = super.catchCommandRes();

                // catch res message
                JsonMessage msg = super.catchJsonRes(JsonMessage.class);
                eventListener.addMessage(msg);

                if (!super.isSuccess(res)) System.out.println("Error " + res.getDescription());
            } while (!tmp.equals("/back"));
            Console.clear();
        } else {
            System.out.println("Error: " + res.getDescription());
        }

        ChatMessagesDisplayService.stopDisplay();
    }

    private void delay() throws InterruptedException {
        // wait 800ms before allowing to send other messages
        Thread.sleep(300);
    }
}
