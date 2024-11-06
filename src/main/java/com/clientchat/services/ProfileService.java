package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import com.clientchat.auth.AuthManager;
import com.clientchat.lib.ActionUtils;
import com.clientchat.lib.MenuBuilder;
import com.clientchat.lib.MenuOption;
import com.clientchat.protocol.CommandType;

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
                .addOption("1", "Change username", ActionUtils.wrapAction(this::handleChangeUsername))
                .addOption("2", "Logout", ActionUtils.wrapAction(this::handleLogout))
                .addOption("3", "Back", ActionUtils.wrapAction(this::handleBack))
                .addOption("0", "Exit", ActionUtils.wrapAction(super::handleExit))
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
    public boolean run() throws IOException {
        do {
            MenuOption.printMenu("- - - " + AuthManager.getInstance().getUsername() + "'s" + " PROFILE - - -",
                    super.menuOptions);

            // get the choice from the user
            choice = super.keyboard.nextLine().trim();

            // get the action to perform based on the user's choice
            MenuOption selectedOption = super.menuOptions.getOrDefault(choice, new MenuOption("Unknown option", super::handleUnknownOption));

            // run the passed fn related to the user's choice
            selectedOption.getAction().run();
        } while (!choice.equals("0"));

        return false;
    }

    // private methods --> can only be seen inside this class
    private void handleChangeUsername() throws IOException {
        super.sendReq(CommandType.UPD_NAME.toString());

        // get new username from string and send it to the server
        String newUsername = super.keyboard.nextLine().trim();
        super.sendJsonReq(newUsername);

        res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            System.out.println("Successfully changed username to: " + newUsername);
        }

        // as server sends NULL here, we clear the input stream
        super.cleanBuffer();
    }

    private void handleLogout() throws IOException {
        super.sendReq(CommandType.LOGOUT.toString());
        res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            /*
             * set choice to "0" to leave this loop, without exiting the app.
             * this send the user to the auth menu
             */
            AuthManager.getInstance().logout();
            choice = "0";
        } else {
            System.out.println(res.getDescription());
        }

        super.cleanBuffer();
    }

    private void handleBack() throws IOException {
        /*
         * set choice to "0" to leave this loop, without exiting the app.
         * this send the user to the service menu
         */
        choice = "0";
    }
}
