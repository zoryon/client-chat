package com.clientchat;

import com.clientchat.auth.AuthManager;
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
        try (
            Socket socket = new Socket("localhost", 3000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner keyboard = new Scanner(System.in)
        ) {
            ClientManager clientManager = new ClientManager(in, out, keyboard);
            clientManager.run();
        } catch (IOException e) {
            System.err.println("Error during client execution: " + e.getMessage());
        }
    }
}