package com.clientchat.services;

import com.clientchat.auth.AuthManager;
import com.clientchat.lib.ActionUtils;
import com.clientchat.lib.Console;
import com.clientchat.lib.MenuBuilder;
import com.clientchat.lib.MenuOption;
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
        this.authManager = AuthManager.getInstance(); // equal to "new AuthManager()"

        // start thread
        super.eventListener.start();

        // initialize menu
        super.initializeMenuOptions(
            new MenuBuilder()
            .addOption("0", "Exit", ActionUtils.wrapAction(super::handleExit))
            .addOption("1", "Sign up", ActionUtils.wrapAction(this::handleSignUp))
            .addOption("2", "Sign in", ActionUtils.wrapAction(this::handleSignIn))
            .build()
        );
    }

    // only one instance can exists at a time
    public static AuthService getInstance(Socket socket) throws IOException {
        if (instance == null) {
            instance = new AuthService(socket);
        }
        return instance;
    }

    /*  
        main body (NOT A THREAD) --> fn to run.
        it's just to divide code in many files, instead of a single one
    */
    public void run() {
        Console.clear();
        try {
            // everything will be run inside this loop
            while (Service.isRunning) {
                if (!authManager.isAuthenticated()) {
                    handleAuthenticationMenu();
                } else {
                    // print a welcome message for the user
                    System.out.println(super.newLine() + "Welcome, " + authManager.getUsername() + "!");

                    /* 
                        ChatService.run() is not a thread, it simulates a main from another class.
                        basically, this thread will be waiting ChatService to finish
                        before continuing with the loop 
                    */
                    ChatService.getInstance(super.socket).run();
                }
            }

            // closing resources on connection closed
            super.out.close();
            super.in.close();
        } catch (IOException e) {
            System.out.println("Unexpected error: " + e);
        }
    }

    // private methods --> can only be seen inside this class
    private void handleAuthenticationMenu() throws IOException {
        MenuOption.printMenu("- - - AUTH MENU - - -", super.menuOptions);

        // get the choice from the user
        String choice = super.keyboard.nextLine().trim();

        // get the action to perform based on the user's choice
        MenuOption selectedOption = super.menuOptions.getOrDefault(choice, new MenuOption("Unknown option", super::handleUnknownOption));

        // run the passed fn related to the user's choice
        selectedOption.getAction().run();
    }

    private void handleSignUp() throws IOException, InterruptedException {
        JsonUser user = getCredentialsFromUser();
        sendAuthReq(CommandType.NEW_USER, user);
        CommandType res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            Console.clear();
            System.out.println(newLine() + "Successfully signed up!");
            System.out.println("You'll automatically be signed in");
            authManager.authenticate(user.getUsername());
        } else {
            Console.clear();
            System.out.println("Sign up error: " + res.getDescription());
        }
    }

    private void handleSignIn() throws IOException, InterruptedException {
        JsonUser user = getCredentialsFromUser();
        sendAuthReq(CommandType.OLD_USER, user);
        CommandType res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            Console.clear();
            System.out.println("Successfully signed in!");
            authManager.authenticate(user.getUsername());
        } else {
            Console.clear();
            System.out.println("Sign in error: " + res.getDescription());
        }
    }

    private JsonUser getCredentialsFromUser() {
        System.out.print(super.newLine() + "Enter username: ");
        String username = super.keyboard.nextLine().trim();

        System.out.print("Enter password: ");
        String password = super.keyboard.nextLine().trim();

        return new JsonUser(username, password);
    }

    private void sendAuthReq(CommandType command, JsonUser user) throws IOException {
        // send the command to let the server know what to expect
        super.sendReq(command);

        // send the user in json format
        sendJsonReq(user);
    }
}