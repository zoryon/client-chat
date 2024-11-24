package com.client.chat.swing.lib;

import javax.swing.JOptionPane;
import java.awt.Component;

public class DialogUtils {
    private DialogUtils() {} // Prevent instantiation
    
    public static void showErrorDialog(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }
}
