package Systems.Consultation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Systems.Database.DatabaseConnection;
import Systems.Consultation.Doctor;

import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.Timer;
import java.util.logging.*;


public class DoctorManager {
    private static final Logger LOGGER = Logger.getLogger(DoctorManager.class.getName());
    private final DatabaseConnection dbConnection;
    private DefaultTableModel model;
    private Doctor doctor;

    // Define constants for error and success messages
    private static final String ERR_DOCTOR_EXISTS = "Doctor already exists.";
    private static final String ERR_VALIDATION = "Validation Error";
    private static final String ERR_DB_ERROR = "Database Error";
    private static final String SUCCESS_DOCTOR_ADDED = "Doctor added successfully.";
    private static final String SUCCESS_DOCTOR_UPDATED = "Doctor updated successfully.";

    public DoctorManager(DatabaseConnection dbConnection, DefaultTableModel model) {
        this.dbConnection = dbConnection;
        this.model = model;
    }

    // Utility method to add labels and components to the panel
    public void addLabelAndComponent(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = x + 1;
        panel.add(component, gbc);
    }

  

    
    // Create basic info panel for a doctor
    private JPanel createBasicInfoPanel(Doctor doctor) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(20);
        nameField.setName("Doctor Name");
        JComboBox<String> specialistCombo = new JComboBox<>(getSpecialistList());
        specialistCombo.setName("Specialty");

        JSpinner maxPatientsPerDaySpinner = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
        maxPatientsPerDaySpinner.setName("Max Patients Per Day");
        JSpinner maxPatientsPerSlotSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        maxPatientsPerSlotSpinner.setName("Max Patients Per Slot");
        JSpinner slotDurationSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 120, 5));
        slotDurationSpinner.setName("Slot Duration (minutes)");

        if (doctor != null) {
            nameField.setText(doctor.getName());
            specialistCombo.setSelectedItem(doctor.getSpecialist());
            maxPatientsPerDaySpinner.setValue(doctor.getMaxPatientsPerDay());
            maxPatientsPerSlotSpinner.setValue(doctor.getMaxPatientsPerSlot());
            slotDurationSpinner.setValue(doctor.getSlotDuration());
        }

        addLabelAndComponent(panel, gbc, "Doctor Name:", nameField, 0, 0);
        addLabelAndComponent(panel, gbc, "Specialty:", specialistCombo, 0, 1);
        addLabelAndComponent(panel, gbc, "Max Patients Per Day:", maxPatientsPerDaySpinner, 0, 2);
        addLabelAndComponent(panel, gbc, "Max Patients Per Slot:", maxPatientsPerSlotSpinner, 0, 3);
        addLabelAndComponent(panel, gbc, "Slot Duration (minutes):", slotDurationSpinner, 0, 4);

        return panel;
    }

    private String[] getSpecialistList() {
        // Dummy list for demonstration purposes
        return new String[]{
            "Allergist/Immunologist",
            "Anesthesiologist",
            "Cardiologist",
            "Dermatologist",
            "Endocrinologist",
            "Gastroenterologist",
            "Geriatrician",
            "Hematologist",
            "Infectious Disease Specialist",
            "Internist",
            "Nephrologist",
            "Neurologist",
            "Obstetrician/Gynecologist",
            "Oncologist",
            "Orthopedic Surgeon",
            "Otolaryngologist (ENT)",
            "Pediatrician",
            "Physiatrist",
            "Plastic Surgeon",
            "Podiatrist",
            "Pulmonologist",
            "Rheumatologist",
            "Surgeon",
            "Urologist",
            "Vascular Surgeon"
        };
    }


    public Doctor getDoctorByName(String doctorName) throws SQLException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM doctors WHERE name = ?")) {
    
            stmt.setString(1, doctorName);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                return new Doctor(
                    rs.getString("name"),
                    rs.getString("specialist"),
                    rs.getString("room"),
                    rs.getString("patient_consult"),
                    rs.getString("availability"),
                    rs.getInt("max_patients_per_day"),
                    rs.getInt("max_patients_per_slot"),
                    rs.getInt("slot_duration")
                );
            }
        }
        return null; // If no doctor is found with the given name
    }

    // Sample method to return the list of specialties (modify as per your requirement)

 

 

    public String[] generateHours() {
        String[] hours = new String[12];
        for (int i = 0; i < 12; i++) {
            hours[i] = String.format("%02d", i == 0 ? 12 : i);
        }
        return hours;
    }

    // Refactored validateAndSaveDoctor
public boolean validateAndSaveDoctor(JDialog dialog, JPanel basicInfoPanel, JPanel availabilityPanel, JPanel additionalOptionsPanel, DefaultTableModel model) {
    try {
        JTextField nameField = (JTextField) findComponentByName(basicInfoPanel, "Doctor Name");
        JComboBox<?> specialtyCombo = (JComboBox<?>) findComponentByName(basicInfoPanel, "Specialty");

        // Reuse helper validation logic
        if (!validateField(nameField, "Doctor name is required.") || specialtyCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(dialog, "Doctor name and specialty must be provided.", ERR_VALIDATION, JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String doctorName = nameField.getText().trim();
        String specialist = (String) specialtyCombo.getSelectedItem();

        if (isDoctorExists(doctorName)) {
            JOptionPane.showMessageDialog(dialog, ERR_DOCTOR_EXISTS, ERR_VALIDATION, JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Get additional details
        int maxPatientsPerDay = (int) ((JSpinner) findComponentByName(basicInfoPanel, "Max Patients Per Day")).getValue();
        int maxPatientsPerSlot = (int) ((JSpinner) findComponentByName(basicInfoPanel, "Max Patients Per Slot")).getValue();
        int slotDuration = (int) ((JSpinner) findComponentByName(basicInfoPanel, "Slot Duration (minutes)")).getValue();
        String availability = "Available"; // Mock availability for now
        String room = "Room " + (model.getRowCount() + 1);

        // Save the doctor
        saveDoctorToDatabase(doctorName, specialist, room, "0 / " + maxPatientsPerDay,
                availability, maxPatientsPerDay, maxPatientsPerSlot, slotDuration, "", "", false, false);

        // Update the table
        model.addRow(new Object[]{doctorName, specialist, room, "0/" + maxPatientsPerDay, availability});
        JOptionPane.showMessageDialog(dialog, SUCCESS_DOCTOR_ADDED, "Success", JOptionPane.INFORMATION_MESSAGE);
        return true;
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error saving doctor", e);
        JOptionPane.showMessageDialog(dialog, ERR_DB_ERROR + ": " + e.getMessage(), ERR_DB_ERROR, JOptionPane.ERROR_MESSAGE);
        return false;
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Unexpected error", e);
        JOptionPane.showMessageDialog(dialog, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}


    public boolean validateAndUpdateDoctor(JDialog dialog, JPanel basicInfoPanel, JPanel availabilityPanel, JPanel additionalOptionsPanel, DefaultTableModel model, int row) {
        try {
            // Extract and validate basic information
            JTextField nameField = (JTextField) findComponentByName(basicInfoPanel, "Doctor Name");
            JComboBox<?> specialistCombo = (JComboBox<?>) findComponentByName(basicInfoPanel, "Specialty");
            JSpinner maxPatientsSpinner = (JSpinner) findComponentByName(basicInfoPanel, "Max Patients Per Day");
            
            if (nameField == null || specialistCombo == null || maxPatientsSpinner == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid form structure. Please check the panel components.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
    
            String doctorName = nameField.getText().trim();
            if (doctorName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Doctor name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
    
            String specialist = (String) specialistCombo.getSelectedItem();
            int maxPatientsPerDay = (Integer) maxPatientsSpinner.getValue();
    
            // Extract availability details
            JTextField availabilityField = (JTextField) findComponentByName(availabilityPanel, "Availability");
            if (availabilityField == null) {
                JOptionPane.showMessageDialog(dialog, "Availability information is missing.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            String availability = availabilityField.getText().trim();
    
            // Extract additional options
            JTextField roomField = (JTextField) findComponentByName(additionalOptionsPanel, "Room Number");
            JTextField patientConsultField = (JTextField) findComponentByName(additionalOptionsPanel, "Consultation Limit");
            
            if (roomField == null || patientConsultField == null) {
                JOptionPane.showMessageDialog(dialog, "Additional options are incomplete.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
    
            String room = roomField.getText().trim();
            int patientConsult;
            try {
                patientConsult = Integer.parseInt(patientConsultField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(dialog, "Consultation limit must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
    
            // Update doctor's information in the database
            String updateQuery = "UPDATE doctors SET name = ?, specialty = ?, max_patients_per_day = ?, availability = ?, room = ?, consultation_limit = ? WHERE id = ?";
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
    
                stmt.setString(1, doctorName);
                stmt.setString(2, specialist);
                stmt.setInt(3, maxPatientsPerDay);
                stmt.setString(4, availability);
                stmt.setString(5, room);
                stmt.setInt(6, patientConsult);
                stmt.setInt(7, getDoctorIdFromRow(row, model)); // Assume a helper function retrieves the doctor ID
                
                stmt.executeUpdate();
    
                // Update the table model
                model.setValueAt(doctorName, row, 0);
                model.setValueAt(specialist, row, 1);
                model.setValueAt(room, row, 2);
                model.setValueAt(patientConsult, row, 3);
                model.setValueAt(availability, row, 4);
    
                // Close the dialog
                dialog.dispose();
                return true;
    
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error updating doctor in the database", e);
                JOptionPane.showMessageDialog(dialog, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error occurred", e);
            JOptionPane.showMessageDialog(dialog, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Helper method to find a component by name
private Component findComponentByName(JPanel panel, String name) {
    for (Component component : panel.getComponents()) {
        if (name.equals(component.getName())) {
            return component;
        }
    }
    return null;
}

// Helper method to retrieve doctor ID from the table model
private int getDoctorIdFromRow(int row, DefaultTableModel model) {
    // Assuming the ID is stored in a hidden column at index 5 (adjust as needed)
    return Integer.parseInt(model.getValueAt(row, 5).toString());
}
    

    public void scheduleAppointment(String doctorName, String patientName, String hospitalId, int age, String sex, String specialty, String selectedDate, String selectedTime) throws SQLException {
        String query = "INSERT INTO appointments (doctor_name, patient_name, hospital_id, age, sex, specialty, selected_date, selected_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, doctorName);
            stmt.setString(2, patientName);
            stmt.setString(3, hospitalId);
            stmt.setInt(4, age);
            stmt.setString(5, sex);
            stmt.setString(6, specialty);
            stmt.setString(7, selectedDate);
            stmt.setString(8, selectedTime);
            
            stmt.executeUpdate();
        }
    }



    private String getTimeFromComponents(Component[] components, int startIndex) {
        JComboBox<?> hourCombo = (JComboBox<?>) components[startIndex];
        JComboBox<?> minuteCombo = (JComboBox<?>) components[startIndex + 2];
        JComboBox<?> amPmCombo = (JComboBox<?>) components[startIndex + 3];
        
        String hour = (String) hourCombo.getSelectedItem();
        String minute = (String) minuteCombo.getSelectedItem();
        String amPm = (String) amPmCombo.getSelectedItem();
        
        // Convert to 24-hour format
        int hourInt = Integer.parseInt(hour);
        if ("PM".equals(amPm) && hourInt != 12) {
            hourInt += 12;
        } else if ("AM".equals(amPm) && hourInt == 12) {
            hourInt = 0;
        }
        
        return String.format("%02d:%s:00", hourInt, minute);
    }

// Save doctor to database (abstracted)
private void saveDoctorToDatabase(String doctorName, String specialist, String room, String patientConsult,
                                   String availability, int maxPatientsPerDay, int maxPatientsPerSlot,
                                   int slotDuration, String schedule, String lunchBreak, 
                                   boolean telemedicine, boolean emergency) throws SQLException {
    String insertQuery = """
        INSERT INTO doctors 
        (name, specialty, room, patient_consult, availability, max_patients_per_day, 
        max_patients_per_slot, slot_duration, schedule, lunch_break, telemedicine, emergency)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;
    try (Connection conn = dbConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
        stmt.setString(1, doctorName);
        stmt.setString(2, specialist);
        stmt.setString(3, room);
        stmt.setString(4, patientConsult);
        stmt.setString(5, availability);
        stmt.setInt(6, maxPatientsPerDay);
        stmt.setInt(7, maxPatientsPerSlot);
        stmt.setInt(8, slotDuration);
        stmt.setString(9, schedule);
        stmt.setString(10, lunchBreak);
        stmt.setBoolean(11, telemedicine);
        stmt.setBoolean(12, emergency);
        stmt.executeUpdate();
    }
}

    public void schedulePeriodicUpdates() {
        Timer hourlyTimer = new Timer();
        hourlyTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (model != null) {
                    updateDatabase();
                }
            }
        }, 0, 60 * 60 * 1000); // Run every hour

        Timer dailyTimer = new Timer();
        dailyTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (model != null) {
                    updateDatabase();
                }
            }
        }, 0, 24 * 60 * 60 * 1000); // Run every day
    }

    private void updateDatabase() {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE doctors SET patient_consult = ?, availability = ? WHERE name = ?")) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String doctorName = (String) model.getValueAt(i, 0);
                String patientConsult = (String) model.getValueAt(i, 3);
                String availability = (String) model.getValueAt(i, 4);

                pstmt.setString(1, patientConsult);
                pstmt.setString(2, availability);
                pstmt.setString(3, doctorName);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating database", e);
        }
    }

    private String formatTime(Component[] components) {
        JComboBox<?> fromHour = (JComboBox<?>) components[0];
        JComboBox<?> fromMinute = (JComboBox<?>) components[2];
        JComboBox<?> fromAmPm = (JComboBox<?>) components[3];
        JComboBox<?> toHour = (JComboBox<?>) components[5];
        JComboBox<?> toMinute = (JComboBox<?>) components[7];
        JComboBox<?> toAmPm = (JComboBox<?>) components[8];

        return String.format("%s:%s %s - %s:%s %s",
            fromHour.getSelectedItem(), fromMinute.getSelectedItem(), fromAmPm.getSelectedItem(),
            toHour.getSelectedItem(), toMinute.getSelectedItem(), toAmPm.getSelectedItem());
    }

    private Component findComponentByName(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName())) {
                return component;
            }
            if (component instanceof Container) {
                Component nested = findComponentByName((Container) component, name);
                if (nested != null) return nested;
            }
        }
        return null;
    }

    // Helper method for common validation logic
private boolean validateField(JComponent field, String message) {
    if (field instanceof JTextField && ((JTextField) field).getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, message, ERR_VALIDATION, JOptionPane.WARNING_MESSAGE);
        return false;
    }
    return true;
}


// Check if the doctor already exists (abstracted)
private boolean isDoctorExists(String doctorName) throws SQLException {
    String query = "SELECT COUNT(*) FROM doctors WHERE name = ?";
    try (Connection conn = dbConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, doctorName);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    return false;
}

    private String generateRoom(String specialist) {
        Random random = new Random();
        return specialist.substring(0, 2).toUpperCase() + (random.nextInt(9) + 1) + (random.nextInt(9) + 1);
    }

    public void setModel(DefaultTableModel model) {
        this.model = model;
    }
}