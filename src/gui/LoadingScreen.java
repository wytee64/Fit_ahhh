package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


// something we thought would be cool to add

public class LoadingScreen extends JWindow {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoadingScreen loadingScreen = new LoadingScreen();
            loadingScreen.setVisible(true);
        });
    }

    private JProgressBar pb;
    private JLabel statusLbl;
    private Timer timer;
    private int progress = 0;
    public LoadingScreen() {
        System.out.println("watermelon - fitness track ahhh");
        JPanel mainPnl = new JPanel(new BorderLayout());
        mainPnl.setBorder(BorderFactory.createLineBorder(new Color(220, 80, 20), 3));
        mainPnl.setBackground(new Color(254, 243, 245));
        JPanel titlePnl = new JPanel(new BorderLayout());
        titlePnl.setBackground(new Color(254, 243, 245));
        JLabel titleLbl = new JLabel("Fitness Track Ahhh", JLabel.CENTER);
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 24));
        titleLbl.setForeground(new Color(220, 80, 20));
        titlePnl.add(titleLbl, BorderLayout.CENTER);
        JPanel progressPnl = new JPanel(new BorderLayout(0, 10));
        progressPnl.setBackground(new Color(254, 243, 245));
        progressPnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pb = new JProgressBar(0, 100);
        pb.setStringPainted(true);
        pb.setForeground(new Color(220, 80, 20));
        pb.setBackground(new Color(254, 243, 245));
        pb.setFont(new Font("Georgia", Font.BOLD, 12));
        statusLbl = new JLabel("......Loading application......", JLabel.CENTER);
        statusLbl.setFont(new Font("Georgia", Font.PLAIN, 12));
        progressPnl.add(pb, BorderLayout.CENTER);
        progressPnl.add(statusLbl, BorderLayout.SOUTH);
        mainPnl.add(titlePnl, BorderLayout.NORTH);
        mainPnl.add(progressPnl, BorderLayout.CENTER);
        JLabel companyNameLbl = new JLabel("watermelon inc", JLabel.RIGHT);
        companyNameLbl.setFont(new Font("Georgia", Font.ITALIC, 10));
        companyNameLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 10));
        mainPnl.add(companyNameLbl, BorderLayout.SOUTH);
        companyNameLbl.setVisible(true);

        setContentPane(mainPnl);
        setSize(400, 200);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);

        timer = new Timer(60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += 1;
                pb.setValue(progress);
                if (progress < 20) statusLbl.setText(".......Initializing application.......");
                else if (progress < 40) statusLbl.setText(".......Loading user interface.......");
                else if (progress < 60) statusLbl.setText(".......Connecting to database.......");
                else if (progress < 80) statusLbl.setText(".......Loading workout data.......");
                else statusLbl.setText(".......Starting application.......");
                if (progress >= 100) {
                    timer.stop();
                    //go to login page
                    dispose();
                    LoginPage loginPage = new LoginPage();
                    loginPage.setVisible(true);
                }
            }
        });
        timer.start();
    }

    private void openLoginPage() {

    }

}