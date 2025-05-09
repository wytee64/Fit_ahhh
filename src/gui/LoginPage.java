package gui;

import javax.swing.*;
import java.awt.*;

import models.User;

public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField registerEmailField;
    private JPasswordField registerPasswordField;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public LoginPage() {
        System.out.println("LoadingPage - fitness track ahhh");
        setTitle("Fitness track Ahhh");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        // frame background
        getContentPane().setBackground(new Color(254, 243, 245));
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(254, 243, 245));
        JPanel loginPanel = createLoginPanel();
        cardPanel.add(loginPanel, "login");

        // making th register panel
        JPanel registerPanel = createRegisterPanel();
        cardPanel.add(registerPanel, "register");
        add(cardPanel);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 248, 240));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 80, 20), 3), "Login", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new Font("Georgia", Font.BOLD, 16), new Color(220, 80, 20)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(220, 80, 20));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Georgia", Font.BOLD, 12));
        loginBtn.setFocusPainted(false);

        JButton switchToRegisterBtn = new JButton("register or sign up");
        switchToRegisterBtn.setBackground(new Color(254, 243, 245));
        switchToRegisterBtn.setForeground(new Color(220, 80, 20));
        switchToRegisterBtn.setBorderPainted(false);
        switchToRegisterBtn.setFocusPainted(false);

        // addin components to screen
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(loginBtn, gbc);
        gbc.gridy = 3;
        panel.add(switchToRegisterBtn, gbc);
        loginBtn.addActionListener(e -> login());
        switchToRegisterBtn.addActionListener(e -> cardLayout.show(cardPanel, "register"));
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(254, 243, 245));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 80, 20), 2), "Fitness track Ahhh", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new Font("Georgia", Font.BOLD, 16), new Color(221, 80, 20)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        nameField = new JTextField(20);
        registerEmailField = new JTextField(20);
        registerPasswordField = new JPasswordField(20);
        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(220, 80, 20));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Georgia", Font.BOLD, 12));
        registerBtn.setFocusPainted(false);

        JButton switchToLoginBtn = new JButton("login");
        switchToLoginBtn.setBackground(new Color(254, 243, 245));
        switchToLoginBtn.setForeground(new Color(220, 80, 20));
        switchToLoginBtn.setBorderPainted(false);
        switchToLoginBtn.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("email:"), gbc);
        gbc.gridx = 1;
        panel.add(registerEmailField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("password:"), gbc);
        gbc.gridx = 1;
        panel.add(registerPasswordField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(registerBtn, gbc);
        gbc.gridy = 4;
        panel.add(switchToLoginBtn, gbc);
        registerBtn.addActionListener(e -> signUp());
        switchToLoginBtn.addActionListener(e -> cardLayout.show(cardPanel, "login"));
        return panel;
    }


    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "some fields not filled");
            return;
        }
        User user = new User("", email, password);
        if (user.login(email, password)) {
            JOptionPane.showMessageDialog(this, "login succesful");

            //go to home page
            HomePage homePage = new HomePage(user);
            homePage.setVisible(true);
            this.dispose();
        } else JOptionPane.showMessageDialog(this, "invalid login details");
    }

    private void signUp() {
        String name = nameField.getText();
        String email = registerEmailField.getText();
        String password = new String(registerPasswordField.getPassword());
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "fill in all fields please");
            return;
        }
        User user = new User(name, email, password);
        if (user.register()) {
            JOptionPane.showMessageDialog(this, "successfully registered");
            cardLayout.show(cardPanel, "login");
        } else {
            JOptionPane.showMessageDialog(this, "failed to register");
        }
    }

    private void openHomePage(User user) {
        HomePage homePage = new HomePage(user);
        homePage.setVisible(true);
        this.dispose();
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new LoginPage().setVisible(true);
//        });
//    }
}