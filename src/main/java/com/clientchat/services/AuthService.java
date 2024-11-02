package com.clientchat.services;

import com.clientchat.auth.AuthManager;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonUser;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.Socket;

public class AuthService extends Service implements Runnable {
    // attributes
    private static AuthService instance;
    private final AuthManager authManager;

    // constructors
    private AuthService(Socket socket) throws IOException {
        super(socket);
        this.authManager = AuthManager.getInstance();
    }

    public static AuthService getInstance(Socket socket) throws IOException {
        if (instance == null) {
            instance = new AuthService(socket);
        }
        return instance;
    }

    // main body --> runned fn when thread starts
    @Override
    public void run() {
        try {
            while (Service.isRunning) {
                if (!authManager.isAuthenticated()) {
                    handleAuthenticationMenu();
                } else {
                    System.out.println("\nLogged in as: " + authManager.getUsername());

                    /* 
                        ChatService.run() is not a thread, it simulates a main from another class
                        basically, this thread will be waiting ChatService to finish
                        before continuing with the loop 
                    */
                    ChatService.getInstance(super.socket).run();
                }
            }
        } catch (IOException e) {
            System.out.println("Unexpected error: " + e);
        }

        // closing resources when the user exit
        try {
            super.keyboard.close();
            super.in.close();
            super.out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // functions
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
                handleExit();
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
            System.out.println("Error" + res.getDescription());
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
            System.out.println("Error" + res.getDescription());
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
        super.sendReq(command);
        // send the user in json format
        super.out.writeBytes(new Gson().toJson(user) + super.newLine());
    }

    protected void delay() {
        // add a small delay to prevent excessive CPU usage
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}