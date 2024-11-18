package com.clientchat.lib;

public enum Character {
    NEW_LINE(System.lineSeparator()),
    SPACE(" "),
    HASHTAG("#");

    // attributes
    private final String value;

    // constructors
    private Character(String value) {
        this.value = value;
    }

    // getters
    public String getValue() {
        return value;
    }
}
