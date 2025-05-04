package gui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import models.Workout;
import models.User;

public class ProgressPanel extends JPanel {
    private final User currentUser;
    private JLabel totalWorkoutsLabel;
    private JLabel totalCaloriesLabel;
    private JLabel totalDurationLabel;
    
    public ProgressPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "Today's Progress",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(70, 130, 180)));
        
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statsPanel.setOpaque(false);
        
        totalWorkoutsLabel = new JLabel("Total Workouts: 0");
        totalWorkoutsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        totalCaloriesLabel = new JLabel("Total Calories: 0");
        totalCaloriesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        totalDurationLabel = new JLabel("Total Duration: 0 mins");
        totalDurationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        statsPanel.add(totalWorkoutsLabel);
        statsPanel.add(totalCaloriesLabel);
        statsPanel.add(totalDurationLabel);
        
        add(statsPanel, BorderLayout.CENTER);
        
        updateStats();
    }
    
    public void updateStats() {
        List<Workout> allWorkouts = Workout.getUserWorkouts(currentUser.getUserId());
        
        // Filter for today's workouts
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        
        List<Workout> todaysWorkouts = new ArrayList<>();
        for (Workout workout : allWorkouts) {
            String workoutDate = sdf.format(workout.getWorkoutDate());
            if (workoutDate.equals(today)) {
                todaysWorkouts.add(workout);
            }
        }
        
        // Calculate stats
        int totalWorkouts = todaysWorkouts.size();
        int totalCalories = 0;
        int totalDuration = 0;
        
        for (Workout workout : todaysWorkouts) {
            totalCalories += workout.getCalories();
            totalDuration += workout.getDuration();
        }
        
        // Update labels
        totalWorkoutsLabel.setText("Total Workouts: " + totalWorkouts);
        totalCaloriesLabel.setText("Total Calories: " + totalCalories);
        totalDurationLabel.setText("Total Duration: " + totalDuration + " mins");
    }
}