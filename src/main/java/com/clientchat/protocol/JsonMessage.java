package com.clientchat.protocol;

public class JsonMessage {
    // attributes
    private int chatId;
    private int senderId;
    private String senderName;
    private String content;
    
    // constructors
    public JsonMessage(int chatId, int senderId, String senderName, String content) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
    }

    // getters and setters
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
}
