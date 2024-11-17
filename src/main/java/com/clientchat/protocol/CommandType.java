package com.clientchat.protocol;

public enum CommandType {
    // general commands
    NEW_USER("Register new user and initialize user session"),
    OLD_USER("Initialize user session"),
    REQ_ALL_CHATS("Request information about user chats (userId)"),
    DEL_USER("Request to delete user information"),
    MENU_BACK("Return to the main menu"),
    NAV_CHAT("Navigate to specified chat or group"),
    SEND_MSG("Send a new message"),
    RM_MSG("Remove a message"),
    UPD_MSG("Update the content of a message"),
    UPD_NAME("Change username"),
    REQ_LOGGED_USER("View information regarding the logged in user"),
    REQ_ALL_USERS("View all users in the system"),
    REQ_ALL_GROUPS("View all groups in the system"),
    INIT("Send initial user's chat array"),
    LOGOUT("Logout from the current account"),
    OK("The previous command was executed successfully"),
    EXIT("Exit from the app and close the connection"),

    // chat management
    NEW_CHAT("Initiate a new private chat"),
    LEAVE_GROUP("Leave a group chat"),
    DEL_GROUP("Delete a group"),
    ADD_ADMIN("Add a new admin to a group"),
    RM_ADMIN("Remove admin privileges from a user"),
    UPD_GROUP_NAME("Change the name of a group"),
    NEW_GROUP("Create a new group"),
    REQ_GROUP_USER("View users in a specific group"),

    // error messages
    ERR_GEN("General server error"),
    ERR_NOT_FOUND("User not found"),
    ERR_CHAT_EXISTS("Private chat already exists"),
    ERR_USER_EXISTS("Username already in use"),
    ERR_DISCONNECT("Client has disconnected"),
    ERR_WRONG_DATA("The data entered is incorrect");

    private final String description;

    CommandType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}