package gui;

import javax.swing.*;
import java.awt.*;
import models.User;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField regEmailField;  // Add this field
    private JPasswordField regPasswordField;  // Add this field
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public LoginFrame() {
        setTitle("Fitness Tracker - Login/Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        // Set the frame background
        getContentPane().setBackground(new Color(240, 248, 255));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(240, 248, 255));

        // Login Panel
        JPanel loginPanel = createLoginPanel();
        cardPanel.add(loginPanel, "login");

        // Register Panel
        JPanel registerPanel = createRegisterPanel();
        cardPanel.add(registerPanel, "register");

        add(cardPanel);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2), 
                "Login", 
                javax.swing.border.TitledBorder.CENTER, 
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(70, 130, 180)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setFocusPainted(false);
        
        JButton switchToRegisterButton = new JButton("New User? Register");
        switchToRegisterButton.setBackground(new Color(240, 248, 255));
        switchToRegisterButton.setForeground(new Color(70, 130, 180));
        switchToRegisterButton.setBorderPainted(false);
        switchToRegisterButton.setFocusPainted(false);

        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(loginButton, gbc);

        gbc.gridy = 3;
        panel.add(switchToRegisterButton, gbc);

        loginButton.addActionListener(e -> handleLogin());
        switchToRegisterButton.addActionListener(e -> cardLayout.show(cardPanel, "register"));

        return panel;
    }

    private JPanel createRegisterPanel() {
        // Apply similar styling to the register panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2), 
                "Register", 
                javax.swing.border.TitledBorder.CENTER, 
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(70, 130, 180)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        nameField = new JTextField(20);
        regEmailField = new JTextField(20);  // Changed from local variable to class field
        regPasswordField = new JPasswordField(20);  // Changed from local variable to class field
        JButton registerButton = new JButton("Register");
        JButton switchToLoginButton = new JButton("Already have account? Login");

        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(regEmailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(regPasswordField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(registerButton, gbc);

        gbc.gridy = 4;
        panel.add(switchToLoginButton, gbc);

        registerButton.addActionListener(e -> handleRegister());
        switchToLoginButton.addActionListener(e -> cardLayout.show(cardPanel, "login"));

        return panel;
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        User user = new User("", email, password);
        if (user.login(email, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            // Open main application window
            openMainApplication(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials");
        }
    }

    private void handleRegister() {
        String name = nameField.getText();
        String email = regEmailField.getText();  // Changed from emailField
        String password = new String(regPasswordField.getPassword());  // Changed from passwordField

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        User user = new User(name, email, password);
        if (user.register()) {
            JOptionPane.showMessageDialog(this, "Registration successful!");
            cardLayout.show(cardPanel, "login");
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed");
        }
    }

    private void openMainApplication(User user) {
        // Create and show main application window
        MainFrame mainFrame = new MainFrame(user);
        mainFrame.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}