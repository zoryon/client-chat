package com.clientchat;

import com.clientchat.protocol.CommandType;
import com.clientchat.protocol.JsonUser;
import com.google.gson.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws UnknownHostException, IOException {
        @SuppressWarnings("resource") // suppressing --> never closed socket warning
        // client socket creation
        Socket socket = new Socket("localhost", 3000);

        // working variables
        // read inputs from the server
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream())
        );

        // write outputs to the server
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // get user input from keyboard
        Scanner keyboard = new Scanner(System.in);

        // store command res
        CommandType res;

        // main loop
        String choice;
        do {
            printAuthMenu(); 
            // wating --> user choice
            choice = keyboard.nextLine(); 

            switch (choice) {
                case "1":
                    // sending --> signup a new user req
                    sendAuthReq(out, keyboard, CommandType.NEW_USER);

                    // waiting --> server validation
                    res = catchCommandRes(in);

                    if (res == CommandType.OK) {
                        // TODO: show success message
                        // TODO: automatically signin the user
                    } else {
                        // TODO: show error message (sout --> res)
                        // TODO: show the menu again (loop)
                    }
                    break;
                case "2":
                    // sending --> signin an existing user req
                    sendAuthReq(out, keyboard, CommandType.OLD_USER);

                    // waiting --> user validation
                    res = catchCommandRes(in);
                    if (res == CommandType.OK) {
                        // TODO: show success message
                    } else {
                        // TODO: show error message (sout --> res)
                        // TODO: show the menu again (loop)
                    }
                    break;
                case "0":
                    // sending --> exit req
                    sendReq(out, CommandType.EXIT);
                    break;
                default:
                    break;
            }
        } while (!choice.equals("0"));

        // closing the resources
        keyboard.close();
        in.close();
        out.close();
    }

    public static String newLine() {
        return "\n";
    }

    public static void printAuthMenu() {
        System.out.println(newLine() + "- - - MENU - - -");
        System.out.println("1) Sign Up");
        System.out.println("2) Sign In");
        System.out.println("0) Esci");
        System.out.print(": ");
    }

    public static String catchRes(BufferedReader in) throws IOException {
        return in.readLine();
    }

    public static CommandType catchCommandRes(BufferedReader in) throws IOException {
        return CommandType.valueOf(catchRes(in));
    }

    public static void sendReq(DataOutputStream out, CommandType command) throws IOException {
        out.writeBytes(command.toString() + newLine());
    }

    public static void sendAuthReq(DataOutputStream out, Scanner keyboard, CommandType command) throws IOException {
        sendReq(out, command);

        JsonUser user = new JsonUser(null, null);

        System.out.print("\nInsert username: ");
        user.setUsername(keyboard.nextLine());

        System.out.print("Insert password: ");
        user.setPassword(keyboard.nextLine());

        out.writeBytes(new Gson().toJson(user).toString() + newLine());
    }
}