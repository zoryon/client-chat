package com.clientchat.auth;

public class AuthManager {
    private static AuthManager instance;
    private boolean isAuthenticated;
    private String username;
    
    private AuthManager() {
        this.isAuthenticated = false;
        this.username = null;
    }
    
    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }
    
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
    
    public void authenticate(String username) {
        this.username = username;
        this.isAuthenticated = true;
    }
    
    public void logout() {
        this.username = null;
        this.isAuthenticated = false;
    }
    
    public String getUsername() {
        return username;
    }
}