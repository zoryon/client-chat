package com.clientchat.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonChat;
import com.clientchat.protocol.JsonMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EventListenerService extends Thread {
    // attributes
    private static EventListenerService instance;
    private final BufferedReader in;
    private final BlockingQueue<CommandType> commandQueue;
    private final BlockingQueue<String> dataQueue;
    private ArrayList<JsonChat> chatList;
    
    private CommandType updateType;

    // constructors
    private EventListenerService(BufferedReader in) {
        this.in = in;
        this.commandQueue = new LinkedBlockingQueue<>();
        this.dataQueue = new LinkedBlockingQueue<>();
        this.chatList = new ArrayList<>();
        this.updateType = null;
    }

    // only one instance can exists at a time
    public static EventListenerService getInstance(BufferedReader in) {
        if (instance == null) {
            instance = new EventListenerService(in);
        }
        return instance;
    }

    // main body --> runned fn when thread starts
    @Override
    public void run() {
        while (Service.isRunning) {
            try {
                // get the command type
                String commandStr = in.readLine();
                if (commandStr == null || commandStr.equals("null")) continue;
                
                CommandType command = CommandType.valueOf(commandStr);
                commandQueue.put(command);

                switch (command) {
                    case OK:
                        // get the general data and it to the blocking queue
                        String data = in.readLine();
                        if (!data.equals("null")) dataQueue.put(data);
                        break;
                    case INIT:
                        // catch the initial array when INIT CommandType is sent
                        chatList = new Gson().fromJson(in.readLine(), new TypeToken<ArrayList<JsonChat>>() {}.getType());
                        commandQueue.take();
                        break;
                    case SEND_MSG:
                        // add new msg to the array
                        JsonMessage msg = new Gson().fromJson(in.readLine(), JsonMessage.class);
                        chatList.forEach(chat -> {
                            if (chat.getId() == msg.getChatId()) chat.addMessage(msg);
                        });
                        
                        notifyUpdate(commandQueue.take());
                        break;
                    case RM_MSG:
                        int msgId = Integer.parseInt(new Gson().fromJson(in.readLine(), String.class));
                        
                        for (JsonChat chat : chatList) {
                            ArrayList<JsonMessage> chatMessages = chat.getMessages();
                            for (int i = 0; i < chatMessages.size(); i++) {
                                if (chatMessages.get(i).getId() == msgId) {
                                    chat.getMessages().remove(i);
                                }
                            }
                        }

                        notifyUpdate(commandQueue.take());
                        break;
                    case NEW_CHAT:
                        chatList.add(new Gson().fromJson(in.readLine(), JsonChat.class));
                        commandQueue.take();
                        break;
                    case NEW_GROUP:
                        chatList.add(new Gson().fromJson(in.readLine(), JsonChat.class));
                        commandQueue.take();
                        break;
                    default:
                        break;
                }
            } catch (IOException | InterruptedException e) { }
        }
    }

    // public methods
    public void printUserChatList() {
        for (JsonChat chat : chatList) {
            System.out.println(chat.getChatName() + "#" + chat.getId());
        }

        if (chatList.isEmpty()) {
            System.out.println("Nessuna chat presente..");
        }
    }

    public BlockingQueue<CommandType> getCommandQueue() {
        return commandQueue;
    }

    public BlockingQueue<String> getDataQueue() {
        return dataQueue;
    }

    public void printCommandAndData() {
        CommandType command = commandQueue.peek();
        System.out.println("Command: " + command);

        String data = dataQueue.peek();
        System.out.println("Data: " + data);
    }

    // public chat-related methods
    public ArrayList<JsonMessage> getChatMessages(int chatId) {
        for (JsonChat chat : chatList) {
            if (chat.getId() == chatId) return chat.getMessages();
        }

        return new ArrayList<JsonMessage>();
    }

    public boolean addChat(JsonChat newChat) {
        return chatList.add(newChat);
    }

    public void notifyUpdate(CommandType command) {
        updateType = command;
    }

    public void readUpdate() {
        updateType = null;
    }

    public CommandType getUpdateType() {
        return updateType;
    }
}
