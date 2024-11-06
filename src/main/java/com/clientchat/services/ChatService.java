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
    private final EventListenerService eventListener;
    private String choice;

    // constructors
    private ChatService(Socket socket) throws IOException {
        super(socket);
        this.eventListener = EventListenerService.getInstance(socket);

        // start the listener service to get up-to-date data
        new Thread(eventListener).start();

        // initialize menu
        super.initializeMenuOptions(
            new MenuBuilder()
            .addOption("1", "View your chats", this::handleViewUserChats)
            .addOption("2", "Connect to a chat", ActionUtils.wrapAction(this::handleConnectToChat))
            .addOption("3", "See own profile", ActionUtils.wrapAction(this::handleViewProfile))
            .addOption("0", "Exit", ActionUtils.wrapAction(super::handleExit))
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

    // main body --> fn to run
    public void run() throws IOException {
        do {
            MenuOption.printMenu("- - - MENU - - -", super.menuOptions);
            choice = super.keyboard.nextLine().trim();

            MenuOption selectedOption = super.menuOptions.getOrDefault(choice, new MenuOption("Unknown option", super::handleUnknownOption));
            selectedOption.getAction().run();
        } while (!choice.equals("0"));
    }

    // private methods --> can only be seen inside this class
    private void handleViewUserChats() {
        System.out.println("Your chats:");
        eventListener.printAllChats();
    }

    private void handleViewProfile() throws IOException {
        if (ProfileService.getInstance(socket).run())
            choice = "0";
    }

    private void handleConnectToChat() throws IOException {
        // get chat identifier
        System.out.print("Insert chat identifier (chatName#chatId): ");
        String chatToSend = super.keyboard.nextLine().trim();

        String tmp;
        do {
            // get the user text message
            tmp = super.keyboard.nextLine();
            switch (tmp) {
                case "/exit":
                    break;
                default:
                    super.sendReq(CommandType.SEND_MSG.toString());
                    super.sendJsonReq(new JsonMessage(Integer.parseInt(chatToSend.split("#")[1]), tmp));
                    CommandType ok = catchCommandRes();

                    if (super.isSuccess(ok)) {
                        System.out.print("- Successfully sent");
                    } else {
                        System.out.print("- Error: " + ok.getDescription());
                    }

                    super.cleanBuffer();
            }
        } while (!tmp.equals("/exit"));
    }
}
