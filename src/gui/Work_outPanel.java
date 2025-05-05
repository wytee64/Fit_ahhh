package gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import models.User;
import models.Workout;

public class Work_outPanel extends JPanel {
    private final User currentUser;
    private JTable workoutTable;
    private DefaultTableModel tableMdl;
    private JTextField durationFld;
    private JTextField caloriesFld;
    private JTextField setsFld;
    private JTextField repsField;
    private JComboBox<String> workoutTypeCombo;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JDateChooser dateChooser;
    private ScheduledExecutorService scheduler;

    private final String[] workoutTypes = {"Running", "Walking", "Cycling", "Swimming", "Weight Training", "Yoga", "Pilates", "Basketball", "Football", "Tennis", "Other"};
    
    public Work_outPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        createFormPanel();
        createTablePanel();
        createButtonPanel();
        startBackgroundRefresh();
    }
    
    private void createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 80, 20), 2), "Add Workout", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), new Color(220, 80, 20)));
        formPanel.setBackground(new Color(255, 248, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Workout Type:"), gbc);
        
        gbc.gridx = 1;
        workoutTypeCombo = new JComboBox<>(workoutTypes);
        formPanel.add(workoutTypeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Duration (mins):"), gbc);
        
        gbc.gridx = 1;
        durationFld = new JTextField(10);
        formPanel.add(durationFld, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Calories:"), gbc);
        
        gbc.gridx = 1;
        caloriesFld = new JTextField(10);
        formPanel.add(caloriesFld, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Sets:"), gbc);
        
        gbc.gridx = 1;
        setsFld = new JTextField(10);
        formPanel.add(setsFld, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Reps:"), gbc);
        
        gbc.gridx = 1;
        repsField = new JTextField(10);
        formPanel.add(repsField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Filter by Date:"), gbc);
        
        gbc.gridx = 1;
        dateChooser = new JDateChooser();
        dateChooser.setDate(new java.util.Date());
        formPanel.add(dateChooser, gbc);
        add(formPanel, BorderLayout.WEST);
    }
    
    private void createTablePanel() {
        // Create table model with columns
        String[] columns = {"ID", "Type", "Duration", "Calories", "Sets", "Reps", "Date"};
        tableMdl = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        // Create table
        workoutTable = new JTable(tableMdl);
        workoutTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workoutTable.getTableHeader().setReorderingAllowed(false);
        
        // Add selection listener
        workoutTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = workoutTable.getSelectedRow();
                if (selectedRow != -1) {
                    populateFormFromTable(selectedRow);
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                } else {
                    clearForm();
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(workoutTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 80, 20), 2),
                "Your Workouts",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(220, 80, 20)));
        
        // Add scroll pane to the main panel
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(255, 248, 240));
        
        // Add button
        addButton = new JButton("Add Workout");
        addButton.setBackground(new Color(220, 80, 20)); // PRIMARY color
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Georgia", Font.BOLD, 12));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> handleAddWorkout());
        
        // Edit button
        editButton = new JButton("Update Selected");
        editButton.setBackground(new Color(220, 80, 20)); // PRIMARY color
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Georgia", Font.BOLD, 12));
        editButton.setFocusPainted(false);
        editButton.setEnabled(false);
        editButton.addActionListener(e -> handleEditWorkout());
        
        // Delete button
        deleteButton = new JButton("Delete Selected");
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Georgia", Font.BOLD, 12));
        deleteButton.setFocusPainted(false);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> handleDeleteWorkout());
        
        // Refresh button
        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(255, 140, 0)); // SECONDARY button color
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshWorkouts());
        
        // Add buttons to panel
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Add button panel to the main panel
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void handleAddWorkout() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }
            
            // Get values from form
            String workoutType = (String) workoutTypeCombo.getSelectedItem();
            int duration = Integer.parseInt(durationFld.getText());
            int calories = Integer.parseInt(caloriesFld.getText());
            int sets = Integer.parseInt(setsFld.getText());
            int reps = Integer.parseInt(repsField.getText());
            
            // Create and save workout
            Workout workout = new Workout(currentUser.getUserId(), workoutType, duration, calories, sets, reps);
            if (workout.saveWorkout()) {
                JOptionPane.showMessageDialog(this, "Workout added successfully!");
                clearForm();
                refreshWorkouts();
                updateProgressPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add workout", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleEditWorkout() {
        int selectedRow = workoutTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }
            
            // Get workout ID from table
            int workoutId = Integer.parseInt(tableMdl.getValueAt(selectedRow, 0).toString());
            
            // Get values from form
            String workoutType = (String) workoutTypeCombo.getSelectedItem();
            int duration = Integer.parseInt(durationFld.getText());
            int calories = Integer.parseInt(caloriesFld.getText());
            int sets = Integer.parseInt(setsFld.getText());
            int reps = Integer.parseInt(repsField.getText());
            
            // Get all workouts to find the one to update
            List<Workout> workouts = Workout.getUserWorkouts(currentUser.getUserId());
            Workout workoutToUpdate = null;
            
            for (Workout w : workouts) {
                if (w.getWorkoutId() == workoutId) {
                    workoutToUpdate = w;
                    break;
                }
            }
            
            if (workoutToUpdate != null) {
                // Update workout properties
                workoutToUpdate.setWorkoutType(workoutType);
                workoutToUpdate.setDuration(duration);
                workoutToUpdate.setCalories(calories);
                workoutToUpdate.setSets(sets);
                workoutToUpdate.setReps(reps);
                
                // Save changes
                if (workoutToUpdate.updateWorkout()) {
                    JOptionPane.showMessageDialog(this, "Workout updated successfully!");
                    clearForm();
                    refreshWorkouts();
                    updateProgressPanel();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update workout", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleDeleteWorkout() {
        int selectedRow = workoutTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this workout?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Get workout ID from table
        int workoutId = Integer.parseInt(tableMdl.getValueAt(selectedRow, 0).toString());
        
        // Get all workouts to find the one to delete
        List<Workout> workouts = Workout.getUserWorkouts(currentUser.getUserId());
        Workout workoutToDelete = null;
        
        for (Workout w : workouts) {
            if (w.getWorkoutId() == workoutId) {
                workoutToDelete = w;
                break;
            }
        }
        
        if (workoutToDelete != null) {
            // Delete workout
            if (workoutToDelete.deleteWorkout()) {
                JOptionPane.showMessageDialog(this, "Workout deleted successfully!");
                clearForm();
                refreshWorkouts();
                updateProgressPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete workout", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateInput() {
        // Check if fields are empty
        if (durationFld.getText().isEmpty() || caloriesFld.getText().isEmpty() ||
                setsFld.getText().isEmpty() || repsField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            // Validate numeric fields
            int duration = Integer.parseInt(durationFld.getText());
            int calories = Integer.parseInt(caloriesFld.getText());
            int sets = Integer.parseInt(setsFld.getText());
            int reps = Integer.parseInt(repsField.getText());
            
            // Check for negative values
            if (duration <= 0 || calories < 0 || sets <= 0 || reps <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter positive values", "Input Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void populateFormFromTable(int row) {
        // Get values from table
        String workoutType = tableMdl.getValueAt(row, 1).toString();
        String duration = tableMdl.getValueAt(row, 2).toString();
        String calories = tableMdl.getValueAt(row, 3).toString();
        String sets = tableMdl.getValueAt(row, 4).toString();
        String reps = tableMdl.getValueAt(row, 5).toString();
        
        // Set values in form
        workoutTypeCombo.setSelectedItem(workoutType);
        durationFld.setText(duration);
        caloriesFld.setText(calories);
        setsFld.setText(sets);
        repsField.setText(reps);
    }
    
    private void clearForm() {
        workoutTypeCombo.setSelectedIndex(0);
        durationFld.setText("");
        caloriesFld.setText("");
        setsFld.setText("");
        repsField.setText("");
        workoutTable.clearSelection();
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    public void refreshWorkouts() {
        // Clear table
        tableMdl.setRowCount(0);
        
        // Get date from date chooser
        java.util.Date selectedDate = dateChooser.getDate();
        List<Workout> workouts;
        
        if (selectedDate != null) {
            // Get workouts for selected date
            Date sqlDate = new Date(selectedDate.getTime());
            workouts = Workout.getUserWorkoutsForSpecificDate(currentUser.getUserId(), sqlDate);
        } else {
            // Get all workouts
            workouts = Workout.getUserWorkouts(currentUser.getUserId());
        }
        
        // Format for displaying date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        // Add workouts to table
        for (Workout workout : workouts) {
            Object[] row = {
                workout.getWorkoutId(),
                workout.getWorkoutType(),
                workout.getDuration(),
                workout.getCalories(),
                workout.getSets(),
                workout.getReps(),
                dateFormat.format(workout.getWorkoutDate())
            };
            tableMdl.addRow(row);
        }
    }
    
    public void startBackgroundRefresh() {
        // Create scheduler for background refresh
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::refreshWorkouts, 0, 30, TimeUnit.SECONDS);
    }
    
    public void stopBackgroundRefresh() {
        // Shutdown scheduler
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void updateProgressPanel() {
        // find the parent HomePage
        Container parent = getParent();
        while (parent != null && !(parent instanceof HomePage)) {
            parent = parent.getParent();
        }
        
        if (parent instanceof HomePage) {
            HomePage homePage = (HomePage) parent;
            // get the progress panel and update it
            if (homePage.progress_Pnl != null) {
                homePage.progress_Pnl.updateStats();
            }
        }
    }
    
    // Inner class for date chooser
    private static class JDateChooser extends JPanel {
        private final JTextField dateField;
        private final JButton dateButton;
        private java.util.Date date;
        
        public JDateChooser() {
            setLayout(new BorderLayout(5, 0));
            
            dateField = new JTextField(10);
            dateField.setEditable(false);
            
            dateButton = new JButton("...");
            dateButton.setPreferredSize(new Dimension(30, 25));
            
            add(dateField, BorderLayout.CENTER);
            add(dateButton, BorderLayout.EAST);
            
            dateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JDialog dialog = new JDialog();
                    dialog.setTitle("Select Date");
                    dialog.setModal(true);
                    
                    JPanel panel = new JPanel(new BorderLayout());
                    JCalendar calendar = new JCalendar();
                    
                    JButton okButton = new JButton("OK");
                    okButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            setDate(calendar.getDate());
                            dialog.dispose();
                        }
                    });
                    
                    panel.add(calendar, BorderLayout.CENTER);
                    panel.add(okButton, BorderLayout.SOUTH);
                    
                    dialog.setContentPane(panel);
                    dialog.pack();
                    dialog.setLocationRelativeTo(JDateChooser.this);
                    dialog.setVisible(true);
                }
            });
        }
        
        public void setDate(java.util.Date date) {
            this.date = date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateField.setText(date != null ? sdf.format(date) : "");
        }
        
        public java.util.Date getDate() {
            return date;
        }
    }
    
    private static class JCalendar extends JPanel {
        private final JComboBox<String> monthCombo;
        private final JSpinner yearSpinner;
        private final JPanel daysPanel;
        private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        private int selectedDay = 1;
        private int selectedMonth = 0;
        private int selectedYear = 2023;
        
        public JCalendar() {
            setLayout(new BorderLayout());
            JPanel headerPanel = new JPanel(new FlowLayout());
            monthCombo = new JComboBox<>(months);
            yearSpinner = new JSpinner(new SpinnerNumberModel(2023, 1900, 2100, 1));
            headerPanel.add(monthCombo);
            headerPanel.add(yearSpinner);
            daysPanel = new JPanel(new GridLayout(0, 7));
            String[] dayLabels = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
            for (String label : dayLabels) {
                JLabel dayLabel = new JLabel(label, SwingConstants.CENTER);
                daysPanel.add(dayLabel);
            }
            java.util.Calendar cal = java.util.Calendar.getInstance();
            selectedDay = cal.get(java.util.Calendar.DAY_OF_MONTH);
            selectedMonth = cal.get(java.util.Calendar.MONTH);
            selectedYear = cal.get(java.util.Calendar.YEAR);
            monthCombo.setSelectedIndex(selectedMonth);
            yearSpinner.setValue(selectedYear);
            monthCombo.addActionListener(e -> updateCalendar());
            yearSpinner.addChangeListener(e -> updateCalendar());
            add(headerPanel, BorderLayout.NORTH);
            add(daysPanel, BorderLayout.CENTER);
            updateCalendar();
        }
        
        private void updateCalendar() {
            selectedMonth = monthCombo.getSelectedIndex();
            selectedYear = (int) yearSpinner.getValue();
            daysPanel.removeAll();
            String[] dayLabels = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
            for (String label : dayLabels) {
                JLabel dayLabel = new JLabel(label, SwingConstants.CENTER);
                daysPanel.add(dayLabel);
            }
            
            // Get first day of month and number of days
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(selectedYear, selectedMonth, 1);
            
            int firstDayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK) - 1;
            int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            
            // Add empty cells for days before first day of month
            for (int i = 0; i < firstDayOfWeek; i++) {
                daysPanel.add(new JLabel(""));
            }
            
            for (int day = 1; day <= daysInMonth; day++) {
                final int currentDay = day;
                JButton dayButton = new JButton(String.valueOf(day));
                dayButton.addActionListener(e -> {
                    selectedDay = currentDay;
                    updateCalendar();
                });
                // Highlight selected day
                if (day == selectedDay) dayButton.setBackground(new Color(135, 206, 250));
                daysPanel.add(dayButton);
            }
            daysPanel.revalidate();
            daysPanel.repaint();
        }
        
        public java.util.Date getDate() {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            return cal.getTime();
        }
    }
}
