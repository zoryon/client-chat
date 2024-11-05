package com.clientchat.services;

import com.clientchat.lib.SynchronizedBufferedReader;
import com.clientchat.protocol.CommandType;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Service {
    // attributes
    protected final Socket socket;
    protected final BufferedReader tmp;
    protected final SynchronizedBufferedReader in;
    protected final DataOutputStream out;
    protected final Scanner keyboard;
    protected static boolean isRunning = true;
    protected final Gson gson;

    // constructors
    public Service(Socket socket) throws IOException {
        this.socket = socket;
        this.keyboard = new Scanner(System.in);
        tmp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        in = new SynchronizedBufferedReader(tmp);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.gson = new Gson();
    }

    // protected methods --> can only be seen inside services package
    protected String newLine() {
        return System.lineSeparator();
    }

    protected String catchRes() throws IOException {
        return in.readLine();
    }

    protected CommandType catchCommandRes() throws IOException {
        // return the CommandType which value correspond to the catched string
        return CommandType.valueOf(catchRes());
    }

    protected void sendReq(String req) throws IOException {
        out.writeBytes(req + newLine());
    }

    protected void sendJsonReq(Object req) throws IOException {
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
}
