package com.clientchat;

public class Menu {
    private static final String MENU_HEADER = "\n- - - MENU - - -";
    private static final String MENU_FOOTER = "0) Exit";

    public static void printAuthMenu() {
        System.out.println(MENU_HEADER);
        System.out.println("1) Sign Up");
        System.out.println("2) Sign In");
        System.out.println(MENU_FOOTER);
    }

    public static void printHomeMenu() {
        System.out.println(MENU_HEADER);
        System.out.println("1) See chat list");
        System.out.println("2) See group list");
        System.out.println("3) Create chat");
        System.out.println("4) Create group");
        System.out.println(MENU_FOOTER);
    }
}
