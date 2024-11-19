package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import com.clientchat.auth.AuthManager;
import com.clientchat.lib.ActionUtils;
import com.clientchat.lib.Console;
import com.clientchat.lib.MenuBuilder;
import com.clientchat.lib.MenuOption;
import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonUser;

public class ProfileService extends Service {
    // attributes
    private static ProfileService instance;
    private String choice;
    private CommandType res;

    // constructors
    private ProfileService(Socket socket) throws IOException {
        super(socket);

        // initialize menu
        super.initializeMenuOptions(
            new MenuBuilder()
                .addOption("0", "Exit", ActionUtils.wrapAction(super::handleExit))
                .addOption("1", "Back", ActionUtils.wrapAction(this::handleBack))
                .addOption("2", "Change username", ActionUtils.wrapAction(this::handleChangeUsername))
                .addOption("3", "Logout", ActionUtils.wrapAction(this::handleLogout))
                .addOption("4", "Delete", ActionUtils.wrapAction(this::handleDeleteUser))
                .build()
        );
    }

    // ISTANCE --> only one instance can exists at a time
    public static ProfileService getInstance(Socket socket) throws IOException {
        if (instance == null) {
            instance = new ProfileService(socket);
        }
        return instance;
    }

    /*
     * main body (NOT A THREAD) --> fn to run.
     * it's just to divide code in many files, instead of a single one
     */
    public boolean run() throws IOException, InterruptedException {
        do {
            MenuOption.printMenu("- - - " + AuthManager.getInstance().getUsername() + "'s" + " PROFILE - - -", super.menuOptions);

            // get the choice from the user
            choice = super.keyboard.nextLine().trim();

            // get the action to perform based on the user's choice
            MenuOption selectedOption = super.menuOptions.getOrDefault(choice, new MenuOption("Unknown option", super::handleUnknownOption));

            // manage logout
            if (choice.equals("3")) return handleLogout();
            if (choice.equals("4")) return handleDeleteUser();

            // run the passed fn related to the user's choice
            selectedOption.getAction().run();
        } while (Service.isRunning && !choice.equals("0"));

        return false;
    }

    // private methods --> can only be seen inside this class
    private void handleChangeUsername() throws IOException, InterruptedException {
        // get new username from string and send it to the server
        System.out.println("Enter the new username");
        String newUsername = super.keyboard.nextLine().trim();

        res = reqWithSecurityConfirmation(CommandType.UPD_NAME, newUsername);

        if (super.isSuccess(res)) {
            AuthManager.getInstance().authenticate(newUsername);
            
            Console.clear();
            System.out.println("Successfully changed username to: " + newUsername);
        } else {
            System.out.println(res.getDescription());
        }
    }

    private boolean handleLogout() throws IOException, InterruptedException {
        super.sendReq(CommandType.LOGOUT);
        res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            /*
             * set choice to "0" to leave this loop, without exiting the app.
             * this sends the user to the auth menu
             */
            AuthManager.getInstance().logout();
            return true;
        } 
            
        System.out.println(res.getDescription());
        return false;
    }

    private void handleBack() throws IOException {
        /*
         * set choice to "0" to leave this loop, without exiting the app.
         * this send the user to the service menu
         */
        Console.clear();
        choice = "0";
    }

    private boolean handleDeleteUser() throws IOException, InterruptedException {
        res = reqWithSecurityConfirmation(CommandType.DEL_USER, null);
        if (super.isSuccess(res)) {
            AuthManager.getInstance().logout();
            choice = "0";
            return true;
        }

        System.out.println("Error: " + res.getDescription());
        return false;
    }

    /*
     * The goal of this fn is to make sure the user who want
     * to make the action is the owner of the account.
     * "<T>" is a generic type of "toSend".
     * It's something like "Object".
     * This way "RequestData" object will have
     * the attribute "data" of the correct type
     */
    private <T> CommandType reqWithSecurityConfirmation(CommandType command, T toSend) throws IOException, InterruptedException {
        // send delete user request
        super.sendReq(command);

        // BEFORE CONTINUING, SAFETY MEASURES MUST BE TAKEN
        // get password from user
        System.out.print("To confirm this action, enter your password: ");
        String password = super.keyboard.nextLine().trim();

        JsonUser tmp;
        // send json password
        if (toSend == null) {
            tmp = new JsonUser(AuthManager.getInstance().getUsername(), password);
        } else {
            tmp = new JsonUser(toSend.toString(), password);
        }
        super.sendJsonReq(tmp);

        // get and the return the response
        return super.catchCommandRes();
    }
}
