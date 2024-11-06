package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonChat;
import com.google.gson.reflect.TypeToken;

public class EventListenerService extends Service implements Runnable {
    // attributes
    private static EventListenerService instance;
    private ArrayList<JsonChat> chatList;

    // constructors
    private EventListenerService(Socket socket) throws IOException {
        super(socket);
        this.chatList = new ArrayList<>();
    }

    // only one instance can exists at a time
    public static EventListenerService getInstance(Socket socket) throws IOException {
        if (instance == null) {
            instance = new EventListenerService(socket);
        }
        return instance;
    }

    // main body --> runned fn when thread starts
    @Override
    public void run() {
        try {
            catchInitialChats();

            // then start listening for updates
            while (Service.isRunning) {
                CommandType command = super.catchCommandRes();
                switch (command) {
                    default:
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public methods
    public ArrayList<JsonChat> getAllChats() {
        return new ArrayList<>(chatList);
    }

    public void printAllChats() {
        for (JsonChat chat : chatList) {
            System.out.println(chat.getChatName() + "#" + chat.getId());
        }
    }

    // private methods --> can only be seen inside this class
    private void catchInitialChats() throws IOException {
        String jsonChatList = super.catchRes();

        chatList = super.gson.fromJson(
            jsonChatList, 
            new TypeToken<ArrayList<JsonChat>>() {}.getType()
        );
    }
}
