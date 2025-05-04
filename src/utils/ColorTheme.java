package utils;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class ColorTheme {
    // Main colors
    public static final Color PRIMARY = new Color(70, 130, 180);  // Steel Blue
    public static final Color SECONDARY = new Color(240, 248, 255);  // Alice Blue
    public static final Color ACCENT = new Color(135, 206, 250);  // Light Sky Blue
    public static final Color TEXT_DARK = new Color(50, 50, 50);
    public static final Color TEXT_LIGHT = Color.WHITE;
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 12);
    
    // Apply styles to components
    public static void styleButton(JButton button, boolean isPrimary) {
        if (isPrimary) {
            button.setBackground(PRIMARY);
            button.setForeground(TEXT_LIGHT);
        } else {
            button.setBackground(SECONDARY);
            button.setForeground(PRIMARY);
            button.setBorderPainted(false);
        }
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
    }
    
    public static Border createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY, 2), 
                title, 
                TitledBorder.CENTER, 
                TitledBorder.TOP,
                TITLE_FONT,
                PRIMARY);
    }
    
    public static void applyPanelStyle(JComponent panel, String title) {
        panel.setBackground(SECONDARY);
        if (title != null) {
            panel.setBorder(createTitledBorder(title));
        }
    }
}