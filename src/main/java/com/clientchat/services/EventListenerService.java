package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonChat;
import com.clientchat.protocol.JsonMessage;
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
                    case SEND_MSG: 
                        JsonMessage msg = catchJsonReq(JsonMessage.class);

                        if (msg != null) {
                            // loop through the chatList
                            chatList.forEach(chat -> {
                                // stop when the match is found
                                if (chat.getId() == msg.getChatId())
                                    chat.addMessage(msg);
                            });
                        } else {
                            sendRes(CommandType.ERR_GEN);
                        }
                        break;
                    default:
                        super.sendRes(CommandType.ERR_WRONG_DATA);
                        continue;
                }

                // default OK response
                super.sendRes(CommandType.OK);
                super.sendJsonRes(null);

                // add a delay of 500 milliseconds
                Thread.sleep(500);
            }
        } catch (IOException | InterruptedException e) {
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

    public ArrayList<JsonMessage> getChatMessages(int chatId) {
        for (JsonChat chat : chatList) {
            if (chat.getId() == chatId) {
                return chat.getMessages();
            }
        }
    
        return new ArrayList<>();
    }

    public void printChatMessages(int chatId) {
        ArrayList<JsonMessage> messages = getChatMessages(chatId);

        messages.forEach(message -> {
            System.out.println(message.getSenderName() + "#" + message.getId() + ": " + message.getContent());
        });
    }

    // private methods --> can only be seen inside this class
    private void catchInitialChats() throws IOException {
        chatList = catchJsonReq(new TypeToken<ArrayList<JsonChat>>() {}.getType());
        System.out.println(chatList);
    }
}
