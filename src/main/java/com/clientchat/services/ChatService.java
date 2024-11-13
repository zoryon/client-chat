package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import com.clientchat.lib.ActionUtils;
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
                .addOption("1", "View your chats", this::handleViewUserChats)
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
            MenuOption.printMenu("- - - MENU - - -", super.menuOptions);

            // get the choice from the user
            choice = super.keyboard.nextLine().trim();

            // get the action to perform based on the user's choice
            MenuOption selectedOption = super.menuOptions.getOrDefault(choice,
                    new MenuOption("Unknown option", super::handleUnknownOption));

            // run the passed fn related to the user's choice
            selectedOption.getAction().run();
        } while (!choice.equals("0"));
    }

    // private methods --> can only be seen inside this class
    private void handleViewUserChats() {
        System.out.println("Your chats:");
    }

    private void handleViewProfile() throws IOException {
        // starting the loop with user's profile menu
        // if method return true it means the user wants to logout (NOT EXIT)
        if (ProfileService.getInstance(socket).run()) // equal to "new ProfileService(socket).run()"
            choice = "0";
    }

    private void handleCreateChat() throws IOException {
        CreateChatService.getInstance(socket).run();
    }

    private void handleConnectToChat() throws IOException, InterruptedException {
        // can call this version of the fn, if there's no chat identifier to send
        handleConnectToChat(null);
    }

    // protected methods --> can only be seen inside this package
    protected void handleConnectToChat(String chatToSend) throws IOException, InterruptedException {
        if (chatToSend == null) {
            // get chat identifier from user
            System.out.print("\nEnter chat identifier (chatName#chatId): ");
            chatToSend = super.keyboard.nextLine().trim();
        }

        System.out.println("\n- - - " + chatToSend + " - - -");
        // making sure the user has rights to access the chat
        sendReq(CommandType.NAV_CHAT);
        sendJsonReq(chatToSend);
        res = catchCommandRes();

        if (super.isSuccess(res)) {
            // TODO: ADD ADMIN RELATED COMMANDS

            // thread which displays the up-to-date messages of a certain chat
            new ChatMessagesDisplayService(socket, Integer.parseInt(chatToSend.split("#")[1]), super.eventListener).start();
            String tmp;
            do {
                /*
                 * get the user text message in loop.
                 * commands starting with "/" CAN be used as commands
                 * and, as such, should not be sent as text messages to the server
                 */
                tmp = super.keyboard.nextLine();
                
                // case: "/help"
                if (tmp.equals("/help")) {
                    System.out.println("/back --> back to chats");
                    System.out.println("/remove #messageId --> delete last message sent");
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
                    String msgId = tmp.split(" #")[1];

                    super.sendReq(CommandType.RM_MSG);
                    super.sendJsonReq(msgId);
                    res = super.catchCommandRes();

                    if (!super.isSuccess(res)) System.out.println("Error: "  + res.getDescription());

                    continue;
                }

                // case: send text message
                super.sendReq(CommandType.SEND_MSG);

                /*
                 * Integer.parseInt(chatToSend.split("#")[1] -->
                 * chatToSend should have this format: chatName#chatId,
                 * with the split fn we are diving chatName from chatId into an array.
                 * with [1] we are getting the second element (chatId).
                 * after we have the chatId we transform it into an Integer.
                 */
                super.sendJsonReq(new JsonMessage(Integer.parseInt(chatToSend.split("#")[1]), tmp));
                res = super.catchCommandRes();

                if (super.isSuccess(res)) {
                    System.out.print("- *");
                } else {
                    System.out.println("- Error " + res.getDescription());
                }
            } while (!tmp.equals("/back"));
        } else {
            System.out.println("Error: " + res.getDescription());
        }
    }
}
