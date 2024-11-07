package com.clientchat.services;

import com.clientchat.lib.MenuOption;
import com.clientchat.lib.SynchronizedBufferedReader;
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
    protected final BufferedReader tmp;
    protected final SynchronizedBufferedReader in;
    protected final DataOutputStream out;
    protected final Scanner keyboard;
    protected final Gson gson;
    protected static boolean isRunning = true;

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

        this.tmp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.in = new SynchronizedBufferedReader(tmp);
    }

    // protected methods --> can only be seen inside services package
    protected String newLine() {
        return System.lineSeparator(); // lineSeparator --> "\n"
    }

    protected String catchRes() throws IOException {
        return in.readLine(); // wait for a socket stream input
    }

    protected CommandType catchCommandRes() throws IOException {
        // return the CommandType which value correspond to the catched string
        return CommandType.valueOf(catchRes());
    }

    protected <T> T catchJsonReq(Type type) {
        try {
            String json = catchRes();
            return gson.fromJson(json, type);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    protected void sendReq(String req) throws IOException {
        // send the String request in the socket output stream
        out.writeBytes(req + newLine());
    }

    protected void sendJsonReq(Object req) throws IOException {
        // send the Object to socket output stream in JSON FORMAT
        out.writeBytes(gson.toJson(req) + newLine());
    }

    protected boolean isSuccess(CommandType res) {
        return res == CommandType.OK;
    }

    protected void handleExit() throws IOException {
        sendReq(CommandType.EXIT.toString());
        Service.isRunning = false;
        System.out.println("Thank you for having trusted us!");
    }

    protected void cleanBuffer() throws IOException {
        // clearing buffered reader from useless NULL data
        in.readLine();
    }

    protected void initializeMenuOptions(Map<String, MenuOption> menuOptions) {
        // this is a SET fn
        this.menuOptions = menuOptions;
    };

    protected void handleUnknownOption() {
        System.out.println("Unknown option. Please try again.");
    }
}
