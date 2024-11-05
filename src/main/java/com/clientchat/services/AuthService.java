package com.clientchat.services;

import com.clientchat.auth.AuthManager;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonUser;
import java.io.IOException;
import java.net.Socket;

public class AuthService extends Service {
    // attributes
    private static AuthService instance;
    private final AuthManager authManager;

    // constructors
    private AuthService(Socket socket) throws IOException {
        super(socket);
        this.authManager = AuthManager.getInstance();
    }

    // only one instance can exists at a time
    public static AuthService getInstance(Socket socket) throws IOException {
        if (instance == null) {
            instance = new AuthService(socket);
        }
        return instance;
    }

    // main body --> fn to run
    public void run() {
        try {
            while (Service.isRunning) {
                if (!authManager.isAuthenticated()) {
                    handleAuthenticationMenu();
                } else {
                    System.out.println("\nLogged in as: " + authManager.getUsername());

                    /* 
                        ChatService.run() is not a thread, it simulates a main from another class.
                        basically, this thread will be waiting ChatService to finish
                        before continuing with the loop 
                    */
                    ChatService.getInstance(super.socket).run();
                }
            }

            // closing resources on connection closed
            super.keyboard.close();
            super.out.close();
            super.in.close();
        } catch (IOException e) {
            System.out.println("Unexpected error: " + e);
        }
    }

    // private methods --> can only be seen inside this class
    private void handleAuthenticationMenu() throws IOException {
        printAuthMenu();
        String choice = super.keyboard.nextLine();

        switch (choice) {
            case "1":
                handleSignUp();
                break;
            case "2":
                handleSignIn();
                break;
            case "0":
                super.handleExit();
                break;
            default:
                System.out.println("Unknown option. Please try again.");
        }
    }

    private void handleSignUp() throws IOException {
        JsonUser user = getCredentialsFromUser();
        sendAuthReq(CommandType.NEW_USER, user);
        CommandType res = super.catchCommandRes();

        if (isSuccess(res)) {
            System.out.println(newLine() + "Successfully registered!");
            System.out.println("You'll automatically be signed in");
            authManager.authenticate(user.getUsername());
        } else {
            System.out.println("Error: " + res.getDescription());
        }
    }

    private void handleSignIn() throws IOException {
        JsonUser user = getCredentialsFromUser();
        sendAuthReq(CommandType.OLD_USER, user);
        CommandType res = super.catchCommandRes();

        if (isSuccess(res)) {
            System.out.println("Successfully signed in!");
            authManager.authenticate(user.getUsername());
        } else {
            System.out.println("Error: " + res.getDescription());
        }
    }

    private JsonUser getCredentialsFromUser() {
        System.out.print("\nInsert username: ");
        String username = super.keyboard.nextLine().trim();

        System.out.print("Insert password: ");
        String password = super.keyboard.nextLine().trim();

        return new JsonUser(username, password);
    }

    private void printAuthMenu() {
        System.out.println(super.newLine() + "- - - AUTH MENU - - -");
        System.out.println("1) Sign Up");
        System.out.println("2) Sign In");
        System.out.println("0) Exit");
        System.out.print(": ");
    }

    private void sendAuthReq(CommandType command, JsonUser user) throws IOException {
        // send the command to let the server know what to expect
        super.sendReq(command.toString());
        // send the user in json format
        sendJsonReq(user);
    }
}