package utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ColorTheme {
    // Main colors
    public static final Color PRIMARY = new Color(220, 80, 20);    // Orange-red
    public static final Color SECONDARY = new Color(255, 248, 240); // Warm light background
    public static final Color ACCENT = new Color(255, 180, 0);     // Yellow-orange
    public static final Color TEXT_PRIMARY = new Color(50, 50, 50); // Dark text
    public static final Color TEXT_SECONDARY = Color.WHITE;        // White text
    
    // Button colors
    public static final Color BUTTON_PRIMARY = new Color(220, 80, 20);  // Orange-red
    public static final Color BUTTON_SECONDARY = new Color(255, 140, 0); // Darker orange
    public static final Color BUTTON_DANGER = new Color(220, 53, 69);   // Red
    
    // Font settings
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 12);
    public static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 12);
    
    // Apply styles to components
    public static void styleButton(JButton button, boolean isPrimary) {
        button.setBackground(isPrimary ? BUTTON_PRIMARY : BUTTON_SECONDARY);
        button.setForeground(TEXT_SECONDARY);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
    }
    
    public static void applyPanelStyle(JPanel panel, String title) {
        panel.setBackground(SECONDARY);
        if (title != null) {
            panel.setBorder(createTitledBorder(title));
        }
    }
    
    public static Border createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY, 2),
                title,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                HEADER_FONT,
                PRIMARY);
    }
}