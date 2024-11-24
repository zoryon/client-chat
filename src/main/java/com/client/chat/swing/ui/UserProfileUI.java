package com.client.chat.swing.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import com.client.chat.swing.auth.AuthManager;
import com.client.chat.swing.protocol.CustomColors;
import com.client.chat.swing.ui.CustomFrame.CustomFrame;
import java.awt.*;
import java.awt.event.MouseAdapter;

public class UserProfileUI extends CustomFrame {
    private static UserProfileUI instance;
    private final String username;

    private JButton logoutButton;
    private JButton deleteUserButton;
    private JButton changeUsernameButton;
    private JButton backButton;
    
    private UserProfileUI() {
        super(false);

        username = AuthManager.getInstance().getUsername();
        
        setBackground(CustomColors.BACKGROUND.getColor());
        setSize(380, 550);
        setLocationRelativeTo(null);

        initComponents();
    }

    public static UserProfileUI getInstance() {
        if (instance == null) {
            instance = new UserProfileUI();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    private void initComponents() {
        // Main panel with modern background
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(CustomColors.BACKGROUND.getColor());

        // Back button panel
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.setBackground(CustomColors.BACKGROUND.getColor());
        backButton = createStyledButton("←", CustomColors.BORDER.getColor(), 50, 30);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        backButtonPanel.add(backButton);

        // Username Panel with enhanced styling
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        usernamePanel.setBackground(CustomColors.BACKGROUND.getColor());

        // Enhanced username label
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        usernameLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());

        usernamePanel.add(usernameLabel);

        // Styled change username button
        changeUsernameButton = createStyledButton("Edit Username", CustomColors.PRIMARY.getColor());
        changeUsernameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeUsernameButton.setMaximumSize(new Dimension(200, 40));

        // Styled logout button
        logoutButton = createStyledButton("Logout", CustomColors.PRIMARY.getColor());
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(200, 40));

        // Styled delete button with warning colors
        deleteUserButton = createStyledButton("Delete Account", CustomColors.DANGER.getColor());
        deleteUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteUserButton.setMaximumSize(new Dimension(200, 40));
        
        // Add components with more spacing
        panel.add(backButtonPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(usernamePanel);
        panel.add(Box.createVerticalStrut(40));
        panel.add(changeUsernameButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(logoutButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(deleteUserButton);
        panel.add(Box.createVerticalStrut(20));

        add(panel);
    }

    private JButton createStyledButton(String text, Color mainColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        button.setBackground(mainColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        button.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(mainColor, 1, true),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        
        // Make the button size more substantial
        button.setPreferredSize(new Dimension(150, 40));
        
        return button;
    }

    private JButton createStyledButton(String text, Color mainColor, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        button.setBackground(mainColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        button.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(mainColor, 1, true),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        
        // Make the button size more substantial
        button.setPreferredSize(new Dimension(width, height));
        
        return button;
    }

    // event listeners
    public void addLogoutListener(MouseAdapter listener) {
        logoutButton.addMouseListener(listener);
    }

    public void addDeleteUserListener(MouseAdapter listener) {
        deleteUserButton.addMouseListener(listener);
    }

    public void addChangeUsernameListener(MouseAdapter listener) {
        changeUsernameButton.addMouseListener(listener);
    }

    public void addBackButtonListener(MouseAdapter listener) {
        backButton.addMouseListener(listener);
    }
}