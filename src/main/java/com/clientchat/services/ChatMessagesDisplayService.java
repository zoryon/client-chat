package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.clientchat.auth.AuthManager;
import com.clientchat.lib.Console;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonMessage;

public class ChatMessagesDisplayService extends Thread {
    // attributes
    private int lastDisplayedMessageId;
    private String chatToSend;
    private int chatId;
    private EventListenerService eventListener;
    private ArrayList<JsonMessage> messagesCache;
    private static volatile boolean isActive;

    // constructors
    public ChatMessagesDisplayService(Socket socket, int chatId, String chatToSend, EventListenerService eventListener) throws IOException {
        lastDisplayedMessageId = -1;
        this.chatToSend = chatToSend;
        this.chatId = chatId;
        this.eventListener = eventListener;

        // before initializing the messages cache, the init of both the event listener and chat id is needed
        this.messagesCache = new ArrayList<>(eventListener.getChatMessages(chatId));

        // display is up only when needed
        ChatMessagesDisplayService.isActive = false;
    }

    // main body --> runned fn when thread starts
    @Override
    public void run() {
        // on successful connection
        displayAllChatMessages();
        
        // continuously check for new messages until the user leaves the chat
        do {
            try {
                CommandType command = eventListener.getUpdateType();
                if (command == null) continue;

                messagesCache = eventListener.getChatMessages(chatId);
                if (messagesCache.isEmpty()) continue;

                switch (command) {
                    case SEND_MSG:
                        JsonMessage msg = messagesCache.get(messagesCache.size() - 1);

                        if (!hasSameIdAsLastMessageDisplayed(msg)) {
                            System.out.println("[" + msg.getSenderName() + "]: " + msg.getContent());
                        }
                        break;
                    case RM_MSG:
                        reloadChat();
                        break;
                    case UPD_MSG:
                        reloadChat();
                        break;
                    default:
                }

                // set eventListener.hasUpdated to false
                eventListener.readUpdate();
                
                // reduce weight on machine
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (Service.isRunning && isActive);
    }

    public static void startDisplay() {
        ChatMessagesDisplayService.isActive = true;
    }

    public static void stopDisplay() {
        ChatMessagesDisplayService.isActive = false;
    }

    // private methods --> can only be seen inside this class
    private void reloadChat() {
        Console.clear();
        displayTitle();
        displayAllChatMessages();
    }

    private void displayAllChatMessages() {
        for (JsonMessage msg : messagesCache) {
            if (!hasSameIdAsLastMessageDisplayed(msg)) {
                if (msg.getSenderName().equals(AuthManager.getInstance().getUsername())) {
                    System.out.println(msg.getContent());
                    System.out.println("[#" + msg.getId() + "]" + "\n");
                } else {
                    System.out.println("[" + msg.getSenderName() + "]: " + msg.getContent());
                }
            }
        }
    }

    private boolean hasSameIdAsLastMessageDisplayed(JsonMessage msg) {
        boolean hasSameId = msg.getId() == lastDisplayedMessageId;

        if (!hasSameId) lastDisplayedMessageId = msg.getId();

        return hasSameId;
    }

    private void displayTitle() {
        System.out.println("- - - " + chatToSend + " - - -");
    }
}
