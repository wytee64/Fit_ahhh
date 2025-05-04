package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import models.User;

public class MainFrame extends JFrame {
    private WorkoutPanel workoutPanel;
    private JToggleButton darkModeToggle;
    
    ProgressPanel progressPanel; // This should be declared here, not inside the constructor
    private User currentUser;
    
    public MainFrame(User user) {
        this.currentUser = user;
        
        setTitle("Fitness Tracker - Welcome " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Set background color
        getContentPane().setBackground(new Color(255, 248, 240)); // Warm light background
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel(user);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create and add the workout panel
        workoutPanel = new WorkoutPanel(user);
        mainPanel.add(workoutPanel, BorderLayout.CENTER);
        
        // ERROR: You cannot declare fields inside a method
        // private ProgressPanel progressPanel; <- Remove this line
        
        // Just initialize the field that was declared at class level
        progressPanel = new ProgressPanel(user);
        mainPanel.add(progressPanel, BorderLayout.EAST);
        
        // Add the main panel to the frame
        add(mainPanel);
        
        // Add window listener to clean up resources when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (workoutPanel != null) {
                    workoutPanel.stopBackgroundRefresh();
                }
            }
        });
    }
    
    private JPanel createHeaderPanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(70, 130, 180));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        panel.add(welcomeLabel, BorderLayout.WEST);
        
        // Controls panel (right side)
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlsPanel.setOpaque(false);
        
        // Dark mode toggle
        darkModeToggle = new JToggleButton("Dark Mode");
        darkModeToggle.setFocusPainted(false);
        darkModeToggle.addActionListener(e -> toggleDarkMode(darkModeToggle.isSelected()));
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        
        controlsPanel.add(darkModeToggle);
        controlsPanel.add(logoutButton);
        
        panel.add(controlsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void toggleDarkMode(boolean darkMode) {
        // Implement dark mode toggle functionality
        if (darkMode) {
            // Apply dark theme
            applyDarkTheme();
        } else {
            // Apply light theme
            applyLightTheme();
        }
    }
    
    private void applyDarkTheme() {
        // Set dark theme colors
        UIManager.put("Panel.background", new Color(50, 50, 50));
        UIManager.put("Table.background", new Color(60, 60, 60));
        UIManager.put("Table.foreground", Color.WHITE);
        UIManager.put("Table.selectionBackground", new Color(80, 80, 80));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(100, 100, 100));
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("TextField.background", new Color(70, 70, 70));
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("ComboBox.background", new Color(70, 70, 70));
        UIManager.put("ComboBox.foreground", Color.WHITE);
        
        // Refresh UI
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    private void applyLightTheme() {
        // Reset to default light theme
        UIManager.put("Panel.background", new Color(240, 248, 255));
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", Color.BLACK);
        UIManager.put("Table.selectionBackground", new Color(135, 206, 250));
        UIManager.put("Table.selectionForeground", Color.BLACK);
        UIManager.put("Table.gridColor", new Color(200, 220, 240));
        UIManager.put("Label.foreground", Color.BLACK);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.foreground", Color.BLACK);
        
        // Refresh UI
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    private void logout() {
        // Stop background threads
        if (workoutPanel != null) {
            workoutPanel.stopBackgroundRefresh();
        }
        
        // Show login frame
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
    }
}