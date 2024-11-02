package com.clientchat;

import com.clientchat.auth.AuthManager;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonUser;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class ClientManager {
    private final AuthManager authManager;
    private final BufferedReader in;
    private final DataOutputStream out;
    private final Scanner keyboard;
    private boolean isRunning;

    public ClientManager(BufferedReader in, DataOutputStream out, Scanner keyboard) {
        this.authManager = AuthManager.getInstance();
        this.in = in;
        this.out = out;
        this.keyboard = keyboard;
        this.isRunning = true;
    }

    public void run() throws IOException {
        while (isRunning) {
            if (!authManager.isAuthenticated()) {
                handleAuthenticationMenu();
            } else {
                System.out.println("\nLogged in as: " + authManager.getUsername());
                // TODO: handle main interface logic (with ChatManager.java class)
            }
        }
    }

    private void handleAuthenticationMenu() throws IOException {
        printAuthMenu();
        String choice = keyboard.nextLine();
        
        switch (choice) {
            case "1":
                handleSignUp();
                break;
            case "2":
                handleSignIn();
                break;
            case "0":
                handleExit();
                break;
            default:
                System.out.println("Unknown option. Please try again.");
        }
    }

    private void handleSignUp() throws IOException {
        JsonUser user = getCredentialsFromUser();
        sendAuthReq(CommandType.NEW_USER, user);
        CommandType res = catchCommandRes();
        
        if (isSuccess(res)) {
            System.out.println("Successfully registered!");
            System.out.println("You'll automatically be signed in");
            authManager.authenticate(user.getUsername());
        } else {
            System.out.println("Error" + res.getDescription());
        }
    }

    private void handleSignIn() throws IOException {
        JsonUser user = getCredentialsFromUser();
        sendAuthReq(CommandType.OLD_USER, user);
        CommandType res = catchCommandRes();
        
        if (isSuccess(res)) {
            System.out.println("Successfully signed in!");
            authManager.authenticate(user.getUsername());
        } else {
            System.out.println("Error" + res.getDescription());
        }
    }

    private JsonUser getCredentialsFromUser() {
        System.out.print("\nInsert username: ");
        String username = keyboard.nextLine().trim();

        System.out.print("Insert password: ");
        String password = keyboard.nextLine().trim();

        return new JsonUser(username, password);
    }

    private void handleExit() throws IOException {
        sendReq(CommandType.EXIT);
        if (authManager.isAuthenticated()) {
            authManager.logout();
        }
        isRunning = false;
    }

    private void printAuthMenu() {
        System.out.println(newLine() + "- - - MENU - - -");
        System.out.println("1) Sign Up");
        System.out.println("2) Sign In");
        System.out.println("0) Exit");
        System.out.print(": ");
    }

    private String newLine() {
        return System.lineSeparator();
    }

    private String catchRes() throws IOException {
        return in.readLine();
    }

    private CommandType catchCommandRes() throws IOException {
        return CommandType.valueOf(catchRes());
    }

    private void sendReq(CommandType command) throws IOException {
        out.writeBytes(command + newLine());
    }

    private void sendAuthReq(CommandType command, JsonUser user) throws IOException {
        sendReq(command);
        out.writeBytes(new Gson().toJson(user) + newLine());
    }

    private boolean isSuccess(CommandType res) {
        return res == CommandType.OK;
    }
}