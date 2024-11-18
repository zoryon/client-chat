package com.clientchat.protocol;

public enum Color {
    RED("\u001B[31m"),
    BRIGHT_RED("\u001B[91m"),
    GREEN("\u001B[32m"),
    BRIGHT_GREEN("\u001B[92m"),
    YELLOW("\u001B[33m"),
    BRIGHT_YELLOW("\u001B[93m"),
    BLUE("\u001B[34m"),
    BRIGHT_BLUE("\u001B[94m"),
    MAGENTA("\u001B[35m"),
    BRIGHT_MAGENTA("\u001B[95m"),
    CYAN("\u001B[36m"),
    BRIGHT_CYAN("\u001B[96m"),
    WHITE("\u001B[37m");

    // attributes
    private final String ansi;

    // constructors
    private Color(String ansi) {
        this.ansi = ansi;
    }

    public String getANSI() {
        return ansi;
    }
}
