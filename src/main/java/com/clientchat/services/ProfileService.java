package com.clientchat.services;

import java.io.IOException;
import java.net.Socket;
import com.clientchat.auth.AuthManager;

public class ProfileService extends Service {
    // attributes
    private static ProfileService instance;
    private String choice;

    // constructors
    private ProfileService(Socket socket) throws IOException {
        super(socket);
    }

    // only one instance can exists at a time
    public static ProfileService getInstance(Socket socket) throws IOException {
        if (instance == null) {
            instance = new ProfileService(socket);
        }
        return instance;
    }

    // main body --> fn to run
    public boolean run() throws IOException {
        do {
            printProfileMenu();
            choice = super.keyboard.nextLine();

            switch (choice) {
                case "1":
                    handleLogout();
                    return true;
                    case "2":
                    handleBack();
                    break;
                case "0":
                    super.handleExit();
                    break;
                default:
                    System.out.println("Unknown option. Please try again.");
            }
        } while (!choice.equals("0"));
        
        return false;
    }

    // private methods --> can only be seen inside this class
    private void printProfileMenu() {
        System.out.println(super.newLine() + "- - - " + AuthManager.getInstance().getUsername() + "'s" + " PROFILE - - -");
        System.out.println("1) Logout");
        System.out.println("2) Back");
        System.out.println("0) Exit");
        System.out.print(": ");
    }

    private void handleLogout() throws IOException {
        // super.sendReq(CommandType.LOGOUT.toString());
        // CommandType res = super.catchCommandRes();

        // if (super.isSuccess(res)) {
        //     // set choice to "0" to leave this loop, without exiting the app
        //     // this send the user to the auth menu
        //     choice = "0";
        // } else {
        //     System.out.println(res.getDescription());
        // }

        AuthManager.getInstance().logout();
        choice = "0";
    }

    private void handleBack() throws IOException {
        // set choice to "0" to leave this loop, without exiting the app
        // this send the user to the chat service menu
        choice = "0";
    }
}
