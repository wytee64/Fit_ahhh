package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import models.User;

public class MainFrame extends JFrame {
    private WorkoutPanel workoutPanel;
    private JToggleButton darkModeToggle;
    ProgressPanel progressPanel;
    private User currentUser;
    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("Fitness track Ahhh....Welcome " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(255, 248, 240));
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        JPanel headerPanel = createHeaderPanel(user);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        workoutPanel = new WorkoutPanel(user);
        mainPanel.add(workoutPanel, BorderLayout.CENTER);
        progressPanel = new ProgressPanel(user);
        mainPanel.add(progressPanel, BorderLayout.EAST);
        
        // addin the main panel to the frame
        add(mainPanel);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (workoutPanel != null) workoutPanel.stopBackgroundRefresh();
            }
        });
    }
    
    private JPanel createHeaderPanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(70, 130, 180));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Welcome label ting
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName() + "!");
        welcomeLabel.setFont(new Font("Georgia", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        panel.add(welcomeLabel, BorderLayout.WEST);
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlsPanel.setOpaque(false);
        
        // Dark mode button creations
        darkModeToggle = new JToggleButton("Dark Mode");
        darkModeToggle.setFocusPainted(false);
        darkModeToggle.addActionListener(e -> switchToDarkMode(darkModeToggle.isSelected()));

        // creating the logout button
        JButton logoutButton = new JButton("log out");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        controlsPanel.add(darkModeToggle);
        controlsPanel.add(logoutButton);
        panel.add(controlsPanel, BorderLayout.EAST);
        return panel;
    }


    private void switchToDarkMode(boolean darkMode) {
        if (darkMode) {
            applyDarkTheme();
        } else {
            applyLightTheme();
        }
    }
    
    private void applyDarkTheme() {
        // TODO: make the light mode work

    }
    
    private void applyLightTheme() {
        // TODO: make the dark mode work too
    }
    
    private void logout() {
        // Stop background threads first before login out
        if (workoutPanel != null) {
            workoutPanel.stopBackgroundRefresh();
        }
        
        // then go to the log in page
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
    }
}