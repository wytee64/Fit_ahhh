package gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
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
    private JTextField repsFld;
    private JComboBox<String> workoutTypeCombo;
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton refreshBtn;
    private JDateChooser dateChooser;
    private ScheduledExecutorService scheduler;

    private final String[] TypesofWorkouts = {"Running", "Walking", "Cycling", "Swimming", "Weight Training", "Yoga", "Pilates", "Basketball", "Football", "Tennis", "Other"};

    public Work_outPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(254, 243, 245));
        // create and start these
        createFormPnl();
        createTablePnl();
        createButtonPnl();
        startBackgroundRefresh();
    }
    private void createFormPnl() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 80, 20), 2), "Add Workout", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), new Color(220, 80, 20)));
        formPanel.setBackground(new Color(255, 248, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Workout Type:"), gbc);
        gbc.gridx = 1;
        workoutTypeCombo = new JComboBox<>(TypesofWorkouts);
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
        repsFld = new JTextField(10);
        formPanel.add(repsFld, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Filter by Date:"), gbc);

        gbc.gridx = 1;
        dateChooser = new JDateChooser();
        dateChooser.setDate(new java.util.Date());
        formPanel.add(dateChooser, gbc);
        add(formPanel, BorderLayout.WEST);
    }
    
    private void createTablePnl() {
        String[] columns = {"ID", "Type", "Duration", "Calories", "Sets", "Reps", "Date"};
        tableMdl = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        workoutTable = new JTable(tableMdl);
        workoutTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workoutTable.getTableHeader().setReorderingAllowed(false);
        
        workoutTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = workoutTable.getSelectedRow();
                if (selectedRow != -1) {
                    populateFormFromTable(selectedRow);
                    editBtn.setEnabled(true);
                    deleteBtn.setEnabled(true);
                } else {
                    clearTheFormFields();
                    editBtn.setEnabled(false);
                    deleteBtn.setEnabled(false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(workoutTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 80, 20), 2),
                "Your Workouts",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(220, 80, 20)));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createButtonPnl() {
        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPnl.setBackground(new Color(255, 248, 240));
        
        addBtn = new JButton("Add Workout");
        addBtn.setBackground(new Color(220, 80, 20)); // PRIMARY color
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Georgia", Font.BOLD, 12));
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> addWorkout());
        
        editBtn = new JButton("Update Selected");
        editBtn.setBackground(new Color(220, 80, 20)); // PRIMARY color
        editBtn.setForeground(Color.WHITE);
        editBtn.setFont(new Font("Georgia", Font.BOLD, 12));
        editBtn.setFocusPainted(false);
        editBtn.setEnabled(false);
        editBtn.addActionListener(e -> handleEditWorkout());
        
        deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(new Color(220, 53, 69));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Georgia", Font.BOLD, 12));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(e -> handleDeleteWorkout());
        
        refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(255, 140, 0)); // SECONDARY button color
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refreshWorkouts());
        
        btnPnl.add(addBtn);
        btnPnl.add(editBtn);
        btnPnl.add(deleteBtn);
        btnPnl.add(refreshBtn);
        
        add(btnPnl, BorderLayout.SOUTH);
    }
    
    private void addWorkout() {
        try {
            if (!validateInput()) {
                return;
            }
            
            String workoutType = (String) workoutTypeCombo.getSelectedItem();
            int duration = Integer.parseInt(durationFld.getText());
            int calories = Integer.parseInt(caloriesFld.getText());
            int sets = Integer.parseInt(setsFld.getText());
            int reps = Integer.parseInt(repsFld.getText());
            
            Workout workout = new Workout(currentUser.getUserId(), workoutType, duration, calories, sets, reps);
            if (workout.saveWorkout()) {
                JOptionPane.showMessageDialog(this, "Workout added successfully!");
                clearTheFormFields();
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
            int reps = Integer.parseInt(repsFld.getText());
            
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
                    clearTheFormFields();
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
                clearTheFormFields();
                refreshWorkouts();
                updateProgressPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete workout", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateInput() {
        if (durationFld.getText().isEmpty() || caloriesFld.getText().isEmpty() || setsFld.getText().isEmpty() || repsFld.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "fill in all fields", "error with form commpletion", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int duration = Integer.parseInt(durationFld.getText());
            int calories = Integer.parseInt(caloriesFld.getText());
            int sets = Integer.parseInt(setsFld.getText());
            int reps = Integer.parseInt(repsFld.getText());
            
            if (duration <= 0 || calories < 0 || sets <= 0 || reps <= 0) {
                JOptionPane.showMessageDialog(this, "values cannot be negative", "error with form commpletion", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "enter positive numbers only", "error with form commpletion", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void populateFormFromTable(int row) {
        //this moves the data from the table to the form to be edited or deleted
        String workoutType = tableMdl.getValueAt(row, 1).toString();
        String duration = tableMdl.getValueAt(row, 2).toString();
        String calories = tableMdl.getValueAt(row, 3).toString();
        String sets = tableMdl.getValueAt(row, 4).toString();
        String reps = tableMdl.getValueAt(row, 5).toString();
        
        workoutTypeCombo.setSelectedItem(workoutType);
        durationFld.setText(duration);
        caloriesFld.setText(calories);
        setsFld.setText(sets);
        repsFld.setText(reps);
    }
    
    private void clearTheFormFields() {
        workoutTypeCombo.setSelectedIndex(0);
        durationFld.setText("");
        caloriesFld.setText("");
        setsFld.setText("");
        repsFld.setText("");
        workoutTable.clearSelection();
        editBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
    }
    
    public void refreshWorkouts() {
        tableMdl.setRowCount(0);
        
        java.util.Date selectedDate = dateChooser.getDate();
        List<Workout> workouts;
        
        if (selectedDate != null) {

            Date sqlDate = new Date(selectedDate.getTime());
            workouts = Workout.getAllTheWorkoutsForSpecificDate(currentUser.getUserId(), sqlDate);
        } else {
            //gets all dates
            workouts = Workout.getUserWorkouts(currentUser.getUserId());
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (Workout workout : workouts) {
            Object[] row = {workout.getWorkoutId(), workout.getWorkoutType(), workout.getDuration(), workout.getCalories(), workout.getSets(), workout.getReps(), dateFormat.format(workout.getWorkoutDate())};
            tableMdl.addRow(row);
        }
    }
    
    public void startBackgroundRefresh() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::refreshWorkouts, 0, 30, TimeUnit.SECONDS);
    }
    
    public void stopBackgroundRefresh() {
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
        Container parent = getParent();
        while (parent != null && !(parent instanceof HomePage)) {
            parent = parent.getParent();
        }
        
        if (parent instanceof HomePage) {
            HomePage homePage = (HomePage) parent;
            // gets the progress pnl and updates it
            if (homePage.progress_Pnl != null) {
                homePage.progress_Pnl.updateStats();
            }
        }
    }
    
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
                    
                    JPanel pnl = new JPanel(new BorderLayout());
                    JCalendar calendar = new JCalendar();
                    
                    JButton okBtn = new JButton("OK");
                    okBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            setDate(calendar.getDate());
                            dialog.dispose();
                        }
                    });
                    
                    pnl.add(calendar, BorderLayout.CENTER);
                    pnl.add(okBtn, BorderLayout.SOUTH);
                    
                    dialog.setContentPane(pnl);
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
        private int selectedDay;
        private int selectedMonth;
        private int selectedYear;
        
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
            
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(selectedYear, selectedMonth, 1);
            
            int firstDayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK) - 1;
            int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            
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
