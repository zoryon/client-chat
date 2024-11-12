package com.clientchat.protocol;

public class JsonMessage {
    // attributes
    private int id;
    private int chatId;
    private int senderId;
    private String senderName;
    private String content;
    
    // constructors
    public JsonMessage(int chatId, String content) {
        this.chatId = chatId;
        this.content = content;

        // the server will modify these
        this.id = 0;
        this.senderId = 0;
        this.senderName = null;
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean equals(JsonMessage msg) {
        // compare the relevant fields for equality
        return 
            (this.getId() == msg.getId()) &&
            (this.getChatId() == msg.getChatId()) &&
            (this.getSenderId() == msg.getSenderId()) &&
            (this.getSenderName().equals(msg.getSenderName())) &&
            (this.getContent().equals(msg.getContent()));
    }
}
