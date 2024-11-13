package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import com.clientchat.protocol.JsonMessage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
                JsonMessage msg;
                try {
                    msg = new Gson().fromJson(eventListener.getDataQueue().take(), JsonMessage.class);
                    System.out.println("[" + msg.getSenderName() + "] " + ": " + msg.getContent());
                    eventListener.readUpdate();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (isActive);
    }

    public void stopDisplay() {
        isActive = false;
    }
}
