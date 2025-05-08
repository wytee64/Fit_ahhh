package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DatabaseConnection;

public class Workout {
    private int workoutId;
    private int userId;
    private String workoutType;
    private int duration;
    private int calories;
    private int sets;
    private int reps;
    private Timestamp workoutDate;

    public Workout(int userId, String workoutType, int duration, int calories, int sets, int reps) {
        System.out.println("Workout - fitness track ahhh");
        this.userId = userId;
        this.workoutType = workoutType;
        this.duration = duration;
        this.calories = calories;
        this.sets = sets;
        this.reps = reps;
        this.workoutDate = new java.sql.Timestamp(System.currentTimeMillis());
    }

    public int getWorkoutId() {
        return workoutId;
    }
    public String getWorkoutType() {
        return workoutType;
    }
    public int getDuration() {
        return duration;
    }
    public int getCalories() {
        return calories;
    }
    public void setCalories(int calories) {
        this.calories = calories;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }
    public int getSets() {
        return sets;
    }
    public void setSets(int sets) {
        this.sets = sets;
    }
    public int getReps() {
        return reps;
    }
    public void setReps(int reps) {
        this.reps = reps;
    }
    public Timestamp getWorkoutDate() {
        return workoutDate;
    }

    public boolean saveWorkout() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Workouts (user_id, workout_type, duration, calories, sets, reps) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
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

    public boolean deleteWorkout() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Workouts WHERE workout_id = ?")) {
            pstmt.setInt(1, workoutId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateWorkout() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE Workouts SET workout_type = ?, duration = ?, calories = ?, sets = ?, reps = ? WHERE workout_id = ?")) {
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

    public static List<Workout> getUserWorkouts(int userId) {
        List<Workout> workoutsArraylist = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Workouts WHERE user_id = ? ORDER BY workout_date DESC")) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Workout workout = new Workout(rs.getInt("user_id"), rs.getString("workout_type"), rs.getInt("duration"), rs.getInt("calories"), rs.getInt("sets"), rs.getInt("reps"));
                    workout.workoutId = rs.getInt("workout_id");
                    workout.workoutDate = rs.getTimestamp("workout_date");
                    workoutsArraylist.add(workout);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workoutsArraylist;
    }
    
    public static List<Workout> getAllTheWorkoutsForSpecificDate(int userId, Date date) {
        List<Workout> workoutsArraylist = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Workouts WHERE user_id = ? AND DATE(workout_date) = ? ORDER BY workout_date DESC")) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, date);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Workout workout = new Workout(rs.getInt("user_id"), rs.getString("workout_type"), rs.getInt("duration"), rs.getInt("calories"), rs.getInt("sets"), rs.getInt("reps"));
                    workout.workoutId = rs.getInt("workout_id");
                    workout.workoutDate = rs.getTimestamp("workout_date");
                    workoutsArraylist.add(workout);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workoutsArraylist;
    }
}