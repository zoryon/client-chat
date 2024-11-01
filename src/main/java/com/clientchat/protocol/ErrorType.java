package com.clientchat.protocol;

public enum ErrorType {
    ERR_GEN("General server error"),
    ERR_NOT_FOUND("User not found"),
    ERR_CHAT_EXISTS("Private chat already exists"),
    ERR_USER_EXISTS("Username already in use"),
    ERR_DISCONNECT("Client has disconnected");

    private final String description;

    ErrorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}