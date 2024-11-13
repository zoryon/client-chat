package com.clientchat.services;

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
     * Basically an ArrayList,
     * BUT, instead of an index, MAP
     * binds a KEY to an object.
     * In this case we bind the menu's number (0, 1, 2, 3) in STRING FORMAT
     * to it's related MenuOption
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
    protected String newLine() {
        return System.lineSeparator(); // lineSeparator --> "\n"
    }

    protected synchronized String catchRes() throws InterruptedException {
        return eventListener.getDataQueue().take(); // wait for a socket stream input
    }

    protected synchronized CommandType catchCommandRes() throws InterruptedException {
        // return the CommandType which value correspond to the catched string
        return eventListener.getCommandQueue().take(); // wait for a socket stream input
    }

    protected <T> T catchJsonReq(Type type) {
        try {
            String json = catchRes();
            return gson.fromJson(json, type);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    protected void sendReq(CommandType req) throws IOException {
        // send the String request in the socket output stream
        out.writeBytes(req.toString() + newLine());
    }

    protected void sendJsonReq(Object req) throws IOException {
        // send the Object to socket output stream in JSON FORMAT
        out.writeBytes(gson.toJson(req) + newLine());
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
        sendReq(CommandType.EXIT);
        stopService();
        Service.isRunning = false;
        System.out.println("Thank you for having trusted us!");
    }

    protected void initializeMenuOptions(Map<String, MenuOption> menuOptions) {
        // this is a SET fn
        this.menuOptions = menuOptions;
    };

    protected void handleUnknownOption() {
        System.out.println("Unknown option. Please try again.");
    }

    public void stopService() throws InterruptedException {
        eventListener.stopListener();
        eventListener.join();
    }
}
