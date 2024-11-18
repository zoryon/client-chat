package com.clientchat.services;

import com.clientchat.lib.Character;
import com.clientchat.lib.Console;
import com.clientchat.lib.MenuOption;
import com.clientchat.protocol.CommandType;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class Service {
    // attributes
    protected final Socket socket;
    protected final BufferedReader in;
    protected final DataOutputStream out;
    protected final Scanner keyboard;
    protected final Gson gson;
    protected static boolean isRunning = true;
    protected EventListenerService eventListener;

    /*
     * map is an interface.
     * we are using it to get the HasMap structure.
     * HashMap are similar to ArrayLists,
     * BUT, instead of an index, it
     * binds a KEY (of generic type) to an object (of generic type).
     * in this case we bind the menu's number (0, 1, 2, 3..) of type STRING
     * to it's related MenuOption 
     * (which contains the function to use and its own description)
     */
    protected Map<String, MenuOption> menuOptions;

    // constructors
    public Service(Socket socket) throws IOException {
        this.socket = socket;
        this.keyboard = new Scanner(System.in);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.gson = new Gson();
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        this.eventListener = EventListenerService.getInstance(in);
    }

    // protected methods --> can only be seen inside services package
    protected synchronized String catchRes() throws InterruptedException {
        return eventListener.getDataQueue().take(); // wait for a socket stream input
    }

    protected synchronized CommandType catchCommandRes() throws InterruptedException {
        // return the CommandType which value correspond to the catched string
        return eventListener.getCommandQueue().take(); // wait for a socket stream input
    }

    protected <T> T catchJsonRes(Type type) {
        try {
            String json = catchRes();
            return gson.fromJson(json, type);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    protected <T> T catchJsonReq(Type type) {
        return catchJsonRes(type);
    }

    protected void sendReq(CommandType req) throws IOException {
        // send the String request in the socket output stream
        out.writeBytes(req.toString() + Character.NEW_LINE.getValue());
    }

    protected void sendJsonReq(Object req) throws IOException {
        // send the Object to socket output stream in JSON FORMAT
        out.writeBytes(gson.toJson(req) + Character.NEW_LINE.getValue());
    }

    protected void sendRes(CommandType res) throws IOException {
        // same concept as sendReq, but different naming to be more precise
        // send the String response in the socket output stream
        sendReq(res);
    }

    protected void sendJsonRes(Object res) throws IOException {
        // same concept as sendJsonReq, but different naming to be more precise
        // send the Object to socket output stream in JSON FORMAT
        sendJsonReq(res);
    }

    protected boolean isSuccess(CommandType res) {
        return res == CommandType.OK;
    }

    protected void handleExit() throws IOException, InterruptedException {
        // stop the listening service, as it would still be listening while the socket get closed
        Service.isRunning = false;
        EventListenerService.getInstance(in).interrupt();

        sendReq(CommandType.EXIT);
        
        Console.clear();
        System.out.println("Thank you for having trusted us!");
    }

    protected void initializeMenuOptions(Map<String, MenuOption> menuOptions) {
        // this is a SET fn
        this.menuOptions = menuOptions;
    };

    protected void handleUnknownOption() {
        System.out.println("Unknown option. Please try again.");
    }
}
