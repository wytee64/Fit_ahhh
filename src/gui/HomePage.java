package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import models.User;

public class HomePage extends JFrame {
    private WorkoutPnl workoutPnl;
    ProgressPnl progress_Pnl;
    private User currentUser;
    public HomePage(User user) {
        System.out.println("HomePage - fitness track ahhh");
        this.currentUser = user;
        setTitle("Fitness track Ahhh....Welcome " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(255, 248, 240));
        JPanel mainPnl = new JPanel(new BorderLayout());
        mainPnl.setBackground(new Color(240, 248, 255));
        JPanel headerPnl = createHeaderPnl(user);
        mainPnl.add(headerPnl, BorderLayout.NORTH);
        workoutPnl = new WorkoutPnl(user);
        mainPnl.add(workoutPnl, BorderLayout.CENTER);
        progress_Pnl = new ProgressPnl(user);
        mainPnl.add(progress_Pnl, BorderLayout.EAST);

        add(mainPnl);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (workoutPnl != null) workoutPnl.stopBackgroundRefresh();
            }
        });
    }

    private JPanel createHeaderPnl(User user) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(new Color(70, 130, 180));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel welcomeLbl = new JLabel("Welcome, " + user.getName() + "!");
        welcomeLbl.setFont(new Font("Georgia", Font.BOLD, 18));
        welcomeLbl.setForeground(Color.WHITE);
        pnl.add(welcomeLbl, BorderLayout.WEST);
        JPanel controlsPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlsPnl.setOpaque(false);

        JButton log_outBtn = new JButton("log out");
        log_outBtn.setBackground(new Color(220, 53, 69));
        log_outBtn.setForeground(Color.WHITE);
        log_outBtn.setFocusPainted(false);
        log_outBtn.addActionListener(e -> logout());

        controlsPnl.add(log_outBtn);
        pnl.add(controlsPnl, BorderLayout.EAST);
        return pnl;
    }

    private void logout() {
        if (workoutPnl != null) workoutPnl.stopBackgroundRefresh();

        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });
    }
}