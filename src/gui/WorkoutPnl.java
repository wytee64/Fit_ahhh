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

public class WorkoutPnl extends JPanel {
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

    private final String[] TypesofWorkouts = {"push ups","Running", "Walking", "weight lifting", "Swimming", "jumping rope", "Yoga", "Pilates", "Basketball", "Football", "Tennis", "pull up","other"};

    public WorkoutPnl(User user) {
        System.out.println("WorkoutPnl - fitness track ahhh");
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(254, 243, 245));
        makeFormPnl();
        makeTablePnl();
        makeButtonPnl();
        startBackgroundRefresh();
    }

    private void makeFormPnl() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 80, 20), 2), "Add Workout", TitledBorder.CENTER, TitledBorder.TOP, new Font("Georgia", Font.BOLD, 16), new Color(220, 80, 20)));
        formPanel.setBackground(new Color(254, 248, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Workout Type:"), gbc);
        gbc.gridx = 1;
        workoutTypeCombo = new JComboBox<>(TypesofWorkouts);
        formPanel.add(workoutTypeCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("duration (mins):"), gbc);
        gbc.gridx = 1;
        durationFld = new JTextField(10);
        formPanel.add(durationFld, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("calories:"), gbc);
        gbc.gridx = 1;
        caloriesFld = new JTextField(10);
        formPanel.add(caloriesFld, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("sets:"), gbc);
        gbc.gridx = 1;
        setsFld = new JTextField(10);
        formPanel.add(setsFld, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("reps:"), gbc);
        gbc.gridx = 1;
        repsFld = new JTextField(10);
        formPanel.add(repsFld, gbc);
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("filt by Date:"), gbc);
        gbc.gridx = 1;
        dateChooser = new JDateChooser();
        dateChooser.setDate(new java.util.Date());
        formPanel.add(dateChooser, gbc);
        add(formPanel, BorderLayout.WEST);
    }
    
    private void makeTablePnl() {
        String[] columns = {"ID", "type", "duration", "calories", "sets", "reps", "date"};
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
                    fromTableToForm(selectedRow);
                    editBtn.setEnabled(true);
                    deleteBtn.setEnabled(true);
                } else {
                    workoutTypeCombo.setSelectedIndex(0);
                    durationFld.setText("");
                    caloriesFld.setText("");
                    setsFld.setText("");
                    repsFld.setText("");
                    workoutTable.clearSelection();
                    editBtn.setEnabled(false);
                    deleteBtn.setEnabled(false);
                    editBtn.setEnabled(false);
                    deleteBtn.setEnabled(false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(workoutTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 80, 20), 2),"your Workouts", TitledBorder.CENTER, TitledBorder.TOP, new Font("Georgia", Font.BOLD, 16), new Color(221, 80, 22)));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void makeButtonPnl() {
        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPnl.setBackground(new Color(254, 243, 245));
        
        addBtn = new JButton("add workout");
        addBtn.setBackground(new Color(220, 80, 20));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Georgia", Font.BOLD, 12));
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> addWorkout());
        
        editBtn = new JButton("update selected");
        editBtn.setBackground(new Color(221, 80, 22));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFont(new Font("Georgia", Font.BOLD, 12));
        editBtn.setFocusPainted(false);
        editBtn.setEnabled(false);
        editBtn.addActionListener(e -> handleEditWorkout());
        
        deleteBtn = new JButton("delete selected");
        deleteBtn.setBackground(new Color(220, 53, 69));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Georgia", Font.BOLD, 12));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(e -> handleDeleteWorkout());
        
        refreshBtn = new JButton("refresh");
        refreshBtn.setBackground(new Color(255, 140, 133));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Georgia", Font.BOLD, 12));
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
            if (!validateInput()) return;
            
            String workoutType = (String) workoutTypeCombo.getSelectedItem();
            int duration = Integer.parseInt(durationFld.getText());
            int calories = Integer.parseInt(caloriesFld.getText());
            int sets = Integer.parseInt(setsFld.getText());
            int reps = Integer.parseInt(repsFld.getText());
            
            Workout workout = new Workout(currentUser.getUserId(), workoutType, duration, calories, sets, reps);
            if (workout.saveWorkout()) {
                JOptionPane.showMessageDialog(this, "workout added");
                workoutTypeCombo.setSelectedIndex(0);
                durationFld.setText("");
                caloriesFld.setText("");
                setsFld.setText("");
                repsFld.setText("");
                workoutTable.clearSelection();
                editBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
                refreshWorkouts();
                updateProgressPanel();
            } else JOptionPane.showMessageDialog(this, "failed to add workout", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "enter valid numbers", "error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleEditWorkout() {
        int selectedRow = workoutTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        try {
            if (!validateInput()) return;
            
            int workoutId = Integer.parseInt(tableMdl.getValueAt(selectedRow, 0).toString());

            String workoutType = (String) workoutTypeCombo.getSelectedItem();
            int duration = Integer.parseInt(durationFld.getText());
            int calories = Integer.parseInt(caloriesFld.getText());
            int sets = Integer.parseInt(setsFld.getText());
            int reps = Integer.parseInt(repsFld.getText());
            
            List<Workout> workouts = Workout.getUserWorkouts(currentUser.getUserId());
            Workout workoutselected = null;
            
            for (Workout w : workouts) {
                if (w.getWorkoutId() == workoutId) {
                    workoutselected = w;
                    break;
                }
            }
            
            if (workoutselected != null) {
                workoutselected.setWorkoutType(workoutType);
                workoutselected.setDuration(duration);
                workoutselected.setCalories(calories);
                workoutselected.setSets(sets);
                workoutselected.setReps(reps);
                
                if (workoutselected.updateWorkout()) {
                    JOptionPane.showMessageDialog(this, "workout updated");
                    workoutTypeCombo.setSelectedIndex(0);
                    durationFld.setText("");
                    caloriesFld.setText("");
                    setsFld.setText("");
                    repsFld.setText("");
                    workoutTable.clearSelection();
                    editBtn.setEnabled(false);
                    deleteBtn.setEnabled(false);
                    refreshWorkouts();
                    updateProgressPanel();
                } else {
                    JOptionPane.showMessageDialog(this, "failed to update workout", "error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "enter valid numbers", "error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleDeleteWorkout() {
        int selectedRow = workoutTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "delete workout?", "deleting", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        int workoutId = Integer.parseInt(tableMdl.getValueAt(selectedRow, 0).toString());
        
        List<Workout> workouts = Workout.getUserWorkouts(currentUser.getUserId());
        Workout workoutToDelete = null;
        
        for (Workout w : workouts) {
            if (w.getWorkoutId() == workoutId) {
                workoutToDelete = w;
                break;
            }
        }
        
        if (workoutToDelete != null) {
            if (workoutToDelete.deleteWorkout()) {
                JOptionPane.showMessageDialog(this, "workout deleted");
                workoutTypeCombo.setSelectedIndex(0);
                durationFld.setText("");
                caloriesFld.setText("");
                setsFld.setText("");
                repsFld.setText("");
                workoutTable.clearSelection();
                editBtn.setEnabled(false);
                deleteBtn.setEnabled(false);

                refreshWorkouts();
                updateProgressPanel();
            } else {
                JOptionPane.showMessageDialog(this, "failed to delete workout", "error", JOptionPane.ERROR_MESSAGE);
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
    
    private void fromTableToForm(int row) {
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

    public void refreshWorkouts() {
        tableMdl.setRowCount(0);
        
        java.util.Date selectedDate = dateChooser.getDate();
        List<Workout> workouts;
        
        if (selectedDate != null) {
            Date sqlDate = new Date(selectedDate.getTime());
            workouts = Workout.getAllTheWorkoutsForSpecificDate(currentUser.getUserId(), sqlDate);
        }
        else workouts = Workout.getUserWorkouts(currentUser.getUserId());

        
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
            if (homePage.progress_Pnl != null) {
                homePage.progress_Pnl.updateTodaysProgress();
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
                    dialog.setTitle("date");
                    dialog.setModal(true);
                    
                    JPanel pnl = new JPanel(new BorderLayout());
                    JCalendar calendar = new JCalendar();
                    
                    JButton okBtn = new JButton("ok");
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
        private final String[] months = {"january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"};
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
            String[] dayLabels = {"su", "mo", "tu", "we", "th", "fr", "sa"};
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
            String[] dayLabels = {"su", "mo", "tu", "we", "th", "fr", "sa"};
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
