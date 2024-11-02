package com.clientchat.services;

import com.clientchat.protocol.CommandType;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Service {
    protected final Socket socket;
    protected final BufferedReader in;
    protected final DataOutputStream out;
    protected final Scanner keyboard;
    protected static boolean isRunning = true;

    public Service(Socket socket) throws IOException {
        this.socket = socket;
        this.keyboard = new Scanner(System.in);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new DataOutputStream(socket.getOutputStream());
    }

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

    protected void sendReq(CommandType command) throws IOException {
        out.writeBytes(command + System.lineSeparator());
    }

    protected boolean isSuccess(CommandType res) {
        return res == CommandType.OK;
    }

    protected void handleExit() throws IOException {
        sendReq(CommandType.EXIT);
        Service.isRunning = false;
        System.out.println("Thank you for having trusted us!");
    }
}
