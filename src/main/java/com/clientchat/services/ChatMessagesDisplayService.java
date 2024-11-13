package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import com.clientchat.protocol.JsonMessage;
import com.google.gson.Gson;

public class ChatMessagesDisplayService extends Thread {
    // attributes
    private ArrayList<JsonMessage> prevMessages;
    private EventListenerService eventListener;
    private int chatId;
    private boolean isActive;

    // constructors
    public ChatMessagesDisplayService(Socket socket, int chatId, EventListenerService eventListener) throws IOException {
        this.prevMessages = new ArrayList<>();
        this.chatId = chatId;
        this.eventListener = eventListener;
        this.isActive = true;
    }

    // main body --> runned fn when thread starts
    @Override
    public void run() {
        // continuously check for new messages until the user leaves the chat
        do {
            if (eventListener.hasUpdated) {
                JsonMessage msg = new Gson().fromJson(eventListener.getDataQueue().take());
                System.out.println("[" + msg.getSenderName() + "] " + ": " + msg.getContent());
                eventListener.hasUpdated = false;
            }
        } while (isActive);
    }

    private ArrayList<JsonMessage> fetchMessages() {
        return eventListener.getChatMessages(chatId);
    }

    private ArrayList<JsonMessage> findNewMessages(ArrayList<JsonMessage> newMessages) {
        ArrayList<JsonMessage> ans = new ArrayList<>();

        for (int i = 0; i < prevMessages.size(); i++) {
            if (!prevMessages.get(i).equals(newMessages.get(i))) {
                ans.add(newMessages.get(i));
            }
        }
        
        return ans;
    }

    private void displayMessages(ArrayList<JsonMessage> messages) {
        System.out.println("New messages in chat " + chatId + ":");
        for (JsonMessage message : messages) {
            System.out.println("[" + message.getSenderName() + "] " + ": " + message.getContent());
        }
    }

    public void stopDisplay() {
        isActive = false;
    }
}
