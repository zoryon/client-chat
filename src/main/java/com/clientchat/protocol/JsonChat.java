package com.clientchat.protocol;

import java.util.ArrayList;

public class JsonChat {
    // attributes
    private int id;
    private String username1;
    private String username2;
    private ArrayList<String> messages;

    // constructors
    public JsonChat(int id, String username1, String username2, ArrayList<String> messages) {
        this.id = id;
        this.username1 = username1;
        this.username2 = username2;
        this.messages = messages;
    }

    // methods
    public int getId() {
        return id;
    }
    
    public ArrayList<String> getMessages() {
        return messages;
    }
    
    public ArrayList<String> getParticipants() {
        ArrayList<String> ans = new ArrayList<>();
        ans.add(username1);
        ans.add(username2);
        return ans;
    }
}
