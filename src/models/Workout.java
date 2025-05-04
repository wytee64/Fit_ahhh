package models;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DatabaseConnection;

public class Workout implements Serializable {
    private static final long serialVersionUID = 1L;
    private int workoutId;
    private int userId;
    private String workoutType;
    private int duration;
    private int calories;
    private int sets;
    private int reps;
    private Timestamp workoutDate;

    // Constructor
    public Workout(int userId, String workoutType, int duration, int calories, int sets, int reps) {
        this.userId = userId;
        this.workoutType = workoutType;
        this.duration = duration;
        this.calories = calories;
        this.sets = sets;
        this.reps = reps;
        this.workoutDate = new java.sql.Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getWorkoutId() { return workoutId; }
    public int getUserId() { return userId; }
    public String getWorkoutType() { return workoutType; }
    public int getDuration() { return duration; }
    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }
    // Remove these duplicate methods
    // public int getDuration() { return duration; }
    // public void setDuration(int duration) { this.duration = duration; }
    // public String getWorkoutType() { return workoutType; }
    // public void setWorkoutType(String workoutType) { this.workoutType = workoutType; }
    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }
    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }
    public Timestamp getWorkoutDate() { return workoutDate; }
    
    // Add missing setter methods
    public void setDuration(int duration) { this.duration = duration; }
    public void setWorkoutType(String workoutType) { this.workoutType = workoutType; }

    // Database operations
    public boolean save() {
        String sql = "INSERT INTO Workouts (user_id, workout_type, duration, calories, sets, reps) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, workoutType);
            pstmt.setInt(3, duration);
            pstmt.setInt(4, calories);
            pstmt.setInt(5, sets);
            pstmt.setInt(6, reps);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.workoutId = rs.getInt(1);
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete method
    public boolean delete() {
        String sql = "DELETE FROM Workouts WHERE workout_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, workoutId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update method for editing workouts
    public boolean update() {
        String sql = "UPDATE Workouts SET workout_type = ?, duration = ?, calories = ?, sets = ?, reps = ? WHERE workout_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, workoutType);
            pstmt.setInt(2, duration);
            pstmt.setInt(3, calories);
            pstmt.setInt(4, sets);
            pstmt.setInt(5, reps);
            pstmt.setInt(6, workoutId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Static method to get user's workouts (ArrayList usage)
    public static List<Workout> getUserWorkouts(int userId) {
        List<Workout> workouts = new ArrayList<>();
        String sql = "SELECT * FROM Workouts WHERE user_id = ? ORDER BY workout_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Workout workout = new Workout(
                        rs.getInt("user_id"),
                        rs.getString("workout_type"),
                        rs.getInt("duration"),
                        rs.getInt("calories"),
                        rs.getInt("sets"),
                        rs.getInt("reps")
                    );
                    workout.workoutId = rs.getInt("workout_id");
                    workout.workoutDate = rs.getTimestamp("workout_date");
                    workouts.add(workout);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workouts;
    }
    
    // Get workouts for a specific date
    public static List<Workout> getUserWorkoutsByDate(int userId, Date date) {
        List<Workout> workouts = new ArrayList<>();
        String sql = "SELECT * FROM Workouts WHERE user_id = ? AND DATE(workout_date) = ? ORDER BY workout_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, date);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Workout workout = new Workout(
                        rs.getInt("user_id"),
                        rs.getString("workout_type"),
                        rs.getInt("duration"),
                        rs.getInt("calories"),
                        rs.getInt("sets"),
                        rs.getInt("reps")
                    );
                    workout.workoutId = rs.getInt("workout_id");
                    workout.workoutDate = rs.getTimestamp("workout_date");
                    workouts.add(workout);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workouts;
    }
}