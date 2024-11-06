package com.clientchat.lib;

import java.util.Map;

public class MenuOption {
    // attributes
    private String description;
    private Runnable action;

    // constructors
    public MenuOption(String description, Runnable action) {
        this.description = description;
        this.action = action;
    }

    // getters and setters
    public String getDescription() {
        return description;
    }

    public Runnable getAction() {
        return action;
    }

    // methods
    public static void printMenu(String title, Map<String, MenuOption> options) {
        System.out.println("\n" + title);
        options.forEach((key, option) -> System.out.println(key + ") " + option.getDescription()));
        System.out.print(": ");
    }
}
