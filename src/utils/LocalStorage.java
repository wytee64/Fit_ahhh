package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import models.Workout;

public class LocalStorage {
    private static final String STORAGE_DIR = System.getProperty("user.home") + File.separator + ".fitness_tracker";
    
    public static void saveWorkouts(int userId, List<Workout> workouts) {
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        File file = new File(STORAGE_DIR + File.separator + "workouts_" + userId + ".dat");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(workouts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<Workout> loadWorkouts(int userId) {
        File file = new File(STORAGE_DIR + File.separator + "workouts_" + userId + ".dat");
        
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Workout>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}