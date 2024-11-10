package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import com.clientchat.protocol.JsonMessage;

public class ChatMessagesDisplayService extends Thread {
    // attributes
    ArrayList<JsonMessage> prevMessages;

    // constructors
    public ChatMessagesDisplayService(Socket socket, int chatId) throws IOException {
        prevMessages = EventListenerService.getInstance(socket).getChatMessages(chatId);
    }

    // main body --> runned fn when thread starts
    @Override
    public void run() {
        // TODO: dowhile which print messages untill the user leave the connected chat
        // the messages will be printed ONLY IF the messages array got updated
    }
}
