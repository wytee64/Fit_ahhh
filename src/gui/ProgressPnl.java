package gui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import models.Workout;
import models.User;

public class ProgressPnl extends JPanel {
    private User currentUser;
    private JLabel totalWorkoutsLbl;
    private JLabel totalCaloriesLbl;
    private JLabel totalDurationLbl;
    
    public ProgressPnl(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(71, 129, 184), 3),
                "Today's Progress",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Georgia", Font.BOLD, 14),
                new Color(70, 130, 181)));

        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statsPanel.setOpaque(false);
        
        totalWorkoutsLbl = new JLabel("Total Workouts: 0");
        totalWorkoutsLbl.setFont(new Font("Georgia", Font.BOLD, 14));
        
        totalCaloriesLbl = new JLabel("Total Calories: 0");
        totalCaloriesLbl.setFont(new Font("Georgia", Font.BOLD, 14));
        
        totalDurationLbl = new JLabel("Total Duration: 0 mins");
        totalDurationLbl.setFont(new Font("Georgia", Font.BOLD, 14));
        
        statsPanel.add(totalWorkoutsLbl);
        statsPanel.add(totalCaloriesLbl);
        statsPanel.add(totalDurationLbl);
        
        add(statsPanel, BorderLayout.CENTER);
        
        updateStats();
    }

    public void updateStats() {
        List<Workout> allWorkouts = Workout.getUserWorkouts(currentUser.getUserId());
        
        // filter for todays workout
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        List<Workout> todaysWorkouts = new ArrayList<>();
        for (Workout workout : allWorkouts) {
            String workoutDate = sdf.format(workout.getWorkoutDate());
            if (workoutDate.equals(today)) todaysWorkouts.add(workout);
        }
        
        // Calculate satistics
        int totalWorkouts = todaysWorkouts.size();
        int totalCalories = 0;
        int totalDuration = 0;
        for (Workout workout : todaysWorkouts) {
            totalCalories += workout.getCalories();
            totalDuration += workout.getDuration();
        }
        
        // display calaculated values on the gui
        totalWorkoutsLbl.setText("Total workouts: " + totalWorkouts);
        totalCaloriesLbl.setText("Total calories: " + totalCalories);
        totalDurationLbl.setText("Total duration: " + totalDuration + " mins");
    }
}