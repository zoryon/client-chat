package com.clientchat.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.clientchat.lib.SynchronizedBufferedReader;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonChat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EventListenerService extends Thread {
    private static EventListenerService instance;
    private final SynchronizedBufferedReader in;
    private final BlockingQueue<CommandType> commandQueue;
    private final BlockingQueue<String> dataQueue;
    private ArrayList<JsonChat> chatList;
    private boolean running;

    private EventListenerService(SynchronizedBufferedReader in) {
        this.in = in;
        this.commandQueue = new LinkedBlockingQueue<>();
        this.dataQueue = new LinkedBlockingQueue<>();
        this.chatList = new ArrayList<>();
        this.running = true;
    }

    public static EventListenerService getInstance(SynchronizedBufferedReader in) {
        if (instance == null) {
            instance = new EventListenerService(in);
        }
        return instance;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // get the command type
                CommandType command = CommandType.valueOf(in.readLine());
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
                        break;
                    default:
                        break;
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Error in Event Listener Service: " + e.getMessage());
                running = false;
            }
        }
    }

    public ArrayList<JsonChat> getChatList() {
        return chatList;
    }

    public void stopListener() {
        running = false;
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
}