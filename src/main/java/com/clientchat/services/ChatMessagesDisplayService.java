package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonMessage;

public class ChatMessagesDisplayService extends Thread {
    // attributes
    private ArrayList<JsonMessage> messagesCache;
    private EventListenerService eventListener;
    private int chatId;
    private static volatile boolean isActive;

    // constructors
    public ChatMessagesDisplayService(Socket socket, int chatId, EventListenerService eventListener) throws IOException {
        this.chatId = chatId;
        this.eventListener = eventListener;
        this.messagesCache = new ArrayList<>(eventListener.getChatMessages(chatId));

        ChatMessagesDisplayService.isActive = false;
    }

    // main body --> runned fn when thread starts
    @Override
    public void run() {
        // continuously check for new messages until the user leaves the chat
        for (JsonMessage msg : messagesCache) {
            System.out.println("[" + msg.getSenderName() + "]: " + msg.getContent());
        }

        do {
            try {
                CommandType command = eventListener.getUpdateType();
                if (command == null) continue;

                messagesCache = eventListener.getChatMessages(chatId);
                if (messagesCache.isEmpty()) continue;

                switch (command) {
                    case SEND_MSG:
                        JsonMessage msg = messagesCache.get(messagesCache.size() - 1);
                        
                        System.out.println("[" + msg.getSenderName() + "]: " + msg.getContent());
                        break;
                    default:
                        break;
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
}
