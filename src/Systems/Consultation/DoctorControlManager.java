package Systems.Consultation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Systems.Database.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.*;
import java.sql.Connection;

import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.Stack;



public class DoctorControlManager {
    private static final Logger LOGGER = Logger.getLogger(DoctorControlManager.class.getName());
    private final DatabaseConnection dbaseConnection;
    private final DefaultTableModel model;
    private Stack<Object[]> undoStack;
    private Connection connection;

    public DoctorControlManager(DatabaseConnection dbConnection, DefaultTableModel model) {
        this.dbaseConnection = dbConnection;
        this.model = model;
        this.undoStack = new Stack<>(); // Initialize the undoStack
    }

    public void addDoctor(JPanel parentPanel, JTable doctorTable) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parentPanel), "Add New Doctor", true);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);

        JPanel basicInfoPanel = createBasicInfoPanel();
        basicInfoPanel.setBorder(BorderFactory.createTitledBorder("Basic Information"));
        mainPanel.add(basicInfoPanel, gbc);

        JPanel availabilityPanel = createAvailabilityPanel();
        availabilityPanel.setBorder(BorderFactory.createTitledBorder("Availability"));
        mainPanel.add(availabilityPanel, gbc);

        JPanel additionalOptionsPanel = createAdditionalOptionsPanel();
        additionalOptionsPanel.setBorder(BorderFactory.createTitledBorder("Additional Options"));
        mainPanel.add(additionalOptionsPanel, gbc);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save", () -> {
            if (validateAndSaveDoctor(dialog, basicInfoPanel, availabilityPanel, additionalOptionsPanel)) {
                dialog.dispose();
                loadDoctorData(doctorTable); // Now doctorTable is available
            }
        });
        JButton cancelButton = createStyledButton("Cancel", dialog::dispose);

        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.weighty = 1;
        mainPanel.add(buttonsPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        dialog.add(scrollPane);
        dialog.pack();
        dialog.setSize(new Dimension(Math.max(dialog.getWidth(), 600), Math.min(dialog.getHeight(), 700)));
        dialog.setLocationRelativeTo(parentPanel);
        dialog.setVisible(true);
    }


    public void editAction(JTable doctorTable) {
        int viewRow = doctorTable.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = doctorTable.convertRowIndexToModel(viewRow);
            DefaultTableModel tableModel = (DefaultTableModel) doctorTable.getModel();
    
            // Ensure we're using the correct column indices
            String doctorName = (String) tableModel.getValueAt(modelRow, 1); // Assuming name is in column 1
            String specialist = (String) tableModel.getValueAt(modelRow, 2);
            String room = (String) tableModel.getValueAt(modelRow, 3);
            String patientConsult = (String) tableModel.getValueAt(modelRow, 4);
            String availability = (String) tableModel.getValueAt(modelRow, 5);
    
            JTextField doctorNameField = new JTextField(doctorName);
            JComboBox<String> specialistField = new JComboBox<>(getSpecialistList());
            specialistField.setSelectedItem(specialist);
            JTextField roomField = new JTextField(room);
            JTextField patientConsultField = new JTextField(patientConsult);
            JTextField availabilityField = new JTextField(availability);
    
            Object[] message = {
                "Doctor Name:", doctorNameField,
                "Specialist:", specialistField,
                "Room:", roomField,
                "List of Patient Consult:", patientConsultField,
                "Availability:", availabilityField
            };
    
            int option = JOptionPane.showConfirmDialog(null, message, "Edit Doctor", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                tableModel.setValueAt(doctorNameField.getText(), modelRow, 1);
                tableModel.setValueAt(specialistField.getSelectedItem(), modelRow, 2);
                tableModel.setValueAt(roomField.getText(), modelRow, 3);
                tableModel.setValueAt(patientConsultField.getText(), modelRow, 4);
                tableModel.setValueAt(availabilityField.getText(), modelRow, 5);
    
                updateDoctorInDatabase(doctorNameField.getText(), (String) specialistField.getSelectedItem(), 
                                       roomField.getText(), patientConsultField.getText(), availabilityField.getText());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a doctor to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateDoctorInDatabase(String name, String specialist, String room, String patientConsult, String availability) {
        try (Connection conn = dbaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE doctors SET specialist = ?, room = ?, patient_consult = ?, availability = ? WHERE name = ?")) {
            
            pstmt.setString(1, specialist);
            pstmt.setString(2, room);
            pstmt.setString(3, patientConsult);
            pstmt.setString(4, availability);
            pstmt.setString(5, name);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Doctor information updated successfully.", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No doctor found with the given name.", "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error updating doctor information: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error updating doctor information. Please check the logs.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void deleteAction(JTable doctorTable) {
        int viewRow = doctorTable.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = doctorTable.convertRowIndexToModel(viewRow);
            DefaultTableModel tableModel = (DefaultTableModel) doctorTable.getModel();
    
            // Save row data for undo
            Object[] deletedRowData = new Object[tableModel.getColumnCount()];
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                deletedRowData[i] = tableModel.getValueAt(modelRow, i);
            }
    
            String doctorName = (String) deletedRowData[1]; // Assuming 'name' is in column 1
    
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete the doctor: " + doctorName + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
    
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = dbaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(
                             "DELETE FROM doctors WHERE name = ?")) {
    
                    pstmt.setString(1, doctorName);
                    int affectedRows = pstmt.executeUpdate();
    
                    if (affectedRows > 0) {
                        // Add to undo stack
                        undoStack.push(deletedRowData);
    
                        // Remove from JTable
                        tableModel.removeRow(modelRow);
    
                        JOptionPane.showMessageDialog(null, "Doctor deleted successfully.", "Delete Successful", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "No doctor found with the given name.", "Delete Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    LOGGER.severe("Error deleting doctor: " + e.getMessage());
                    JOptionPane.showMessageDialog(null, "Error deleting doctor. Please check the logs.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a doctor to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public void saveAction() {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO doctors (name, specialist, room, patient_consult, availability) " +
                "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                "specialist=?, room=?, patient_consult=?, availability=?")) {
            
            for (int i = 0; i < model.getRowCount(); i++) {
                pstmt.setString(1, (String) model.getValueAt(i, 0));
                pstmt.setString(2, (String) model.getValueAt(i, 1));
                pstmt.setString(3, (String) model.getValueAt(i, 2));
                pstmt.setString(4, (String) model.getValueAt(i, 3));
                pstmt.setString(5, (String) model.getValueAt(i, 4));
                pstmt.setString(6, (String) model.getValueAt(i, 1));
                pstmt.setString(7, (String) model.getValueAt(i, 2));
                pstmt.setString(8, (String) model.getValueAt(i, 3));
                pstmt.setString(9, (String) model.getValueAt(i, 4));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            JOptionPane.showMessageDialog(null, "Data saved successfully.", "Save", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            LOGGER.severe("Error saving doctor data: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error saving doctor data. Please check the logs.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void undoAction(JTable doctorTable) {
        if (!undoStack.isEmpty()) {
            Object[] previousState = undoStack.pop();
    
            try (Connection conn = dbaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO doctors (id, name, specialist, room, patient_consult, availability, " +
                         "created_at, updated_at, max_patients_per_day, max_patients_per_slot, slot_duration, " +
                         "availability_details, lunch_break, telemedicine, emergency_appointments, day_of_week, " +
                         "from_time, to_time, lunch_start, lunch_end, available) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
    
                for (int i = 0; i < previousState.length; i++) {
                    pstmt.setObject(i + 1, previousState[i]);
                }
    
                pstmt.executeUpdate();
    
                DefaultTableModel tableModel = (DefaultTableModel) doctorTable.getModel();
                tableModel.addRow(previousState);
    
                JOptionPane.showMessageDialog(null, "Undo successful. Doctor record restored.", "Undo", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                LOGGER.severe("Error during undo: " + e.getMessage());
                JOptionPane.showMessageDialog(null, "Error restoring doctor data. Check the logs.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No actions to undo.", "Undo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    
    private JPanel createBasicInfoPanel() {
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
    
        addLabelAndComponent(panel, gbc, "Doctor Name:", nameField, 0, 0);
        addLabelAndComponent(panel, gbc, "Specialty:", specialistCombo, 0, 1);
        addLabelAndComponent(panel, gbc, "Max Patients Per Day:", maxPatientsPerDaySpinner, 0, 2);
        addLabelAndComponent(panel, gbc, "Max Patients Per Slot:", maxPatientsPerSlotSpinner, 0, 3);
        addLabelAndComponent(panel, gbc, "Slot Duration (minutes):", slotDurationSpinner, 0, 4);
    
        return panel;
    }
    
    private JPanel createAvailabilityPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
    
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] hours = generateHours();
        String[] minutes = {"00", "15", "30", "45"};
        String[] ampm = {"AM", "PM"};
    
        for (int i = 0; i < days.length; i++) {
            JComboBox<String> fromHourCombo = new JComboBox<>(hours);
            JComboBox<String> fromMinuteCombo = new JComboBox<>(minutes);
            JComboBox<String> fromAmPmCombo = new JComboBox<>(ampm);
            JComboBox<String> toHourCombo = new JComboBox<>(hours);
            JComboBox<String> toMinuteCombo = new JComboBox<>(minutes);
            JComboBox<String> toAmPmCombo = new JComboBox<>(ampm);
    
            JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
            timePanel.add(fromHourCombo);
            timePanel.add(new JLabel(":"));
            timePanel.add(fromMinuteCombo);
            timePanel.add(fromAmPmCombo);
            timePanel.add(new JLabel(" to "));
            timePanel.add(toHourCombo);
            timePanel.add(new JLabel(":"));
            timePanel.add(toMinuteCombo);
            timePanel.add(toAmPmCombo);
    
            JCheckBox dayAvailable = new JCheckBox("Available");
            dayAvailable.setSelected(true);
            dayAvailable.addActionListener(e -> {
                boolean isAvailable = dayAvailable.isSelected();
                for (Component comp : timePanel.getComponents()) {
                    comp.setEnabled(isAvailable);
                }
            });
    
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(new JLabel(days[i] + ":"), gbc);
    
            gbc.gridx = 1;
            panel.add(timePanel, gbc);
    
            gbc.gridx = 2;
            panel.add(dayAvailable, gbc);
        }
    
        // Lunch Break
        JPanel lunchBreakPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        JComboBox<String> lunchBreakFromHourCombo = new JComboBox<>(hours);
        JComboBox<String> lunchBreakFromMinuteCombo = new JComboBox<>(minutes);
        JComboBox<String> lunchBreakFromAmPmCombo = new JComboBox<>(ampm);
        JComboBox<String> lunchBreakToHourCombo = new JComboBox<>(hours);
        JComboBox<String> lunchBreakToMinuteCombo = new JComboBox<>(minutes);
        JComboBox<String> lunchBreakToAmPmCombo = new JComboBox<>(ampm);
    
        lunchBreakPanel.add(lunchBreakFromHourCombo);
        lunchBreakPanel.add(new JLabel(":"));
        lunchBreakPanel.add(lunchBreakFromMinuteCombo);
        lunchBreakPanel.add(lunchBreakFromAmPmCombo);
        lunchBreakPanel.add(new JLabel(" to "));
        lunchBreakPanel.add(lunchBreakToHourCombo);
        lunchBreakPanel.add(new JLabel(":"));
        lunchBreakPanel.add(lunchBreakToMinuteCombo);
        lunchBreakPanel.add(lunchBreakToAmPmCombo);
    
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Lunch Break:"), gbc);
    
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(lunchBreakPanel, gbc);
    
        return panel;
    }
    
    private JPanel createAdditionalOptionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
    
        JTextField roomField = new JTextField(10);
        roomField.setName("Room Number");
        JCheckBox telemedicineAvailable = new JCheckBox("Telemedicine Available");
        telemedicineAvailable.setName("Telemedicine");
        JCheckBox emergencyAppointments = new JCheckBox("Emergency Appointments");
        emergencyAppointments.setName("Emergency");
    
        addLabelAndComponent(panel, gbc, "Room Number:", roomField, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(telemedicineAvailable, gbc);
        gbc.gridy = 2;
        panel.add(emergencyAppointments, gbc);
    
        return panel;
    }
    
    private String[] generateHours() {
        String[] hours = new String[12];
        for (int i = 0; i < 12; i++) {
            hours[i] = String.format("%02d", i == 0 ? 12 : i);
        }
        return hours;
    }
    
    private String[] getSpecialistList() {
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
    private JButton createStyledButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 50, 100));
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        return button;
    }

    private boolean validateAndSaveDoctor(JDialog dialog, JPanel basicInfoPanel, JPanel availabilityPanel, JPanel additionalOptionsPanel) {
        try (Connection conn = dbaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO hospital_management.doctors \r\n" + //
                                          "(name, specialist, room, patient_consult, availability, max_patients_per_day, max_patients_per_slot, slot_duration, \r\n" + //
                                          " telemedicine, emergency_appointments, availability_details, lunch_break) \r\n" + //
                                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\r\n" + //
                                          "")) {
    
            // Extract basic information
            JTextField nameField = (JTextField) getComponentByName(basicInfoPanel, "Doctor Name");
            JComboBox<?> specialistCombo = (JComboBox<?>) getComponentByName(basicInfoPanel, "Specialty");
            JSpinner maxPatientsPerDaySpinner = (JSpinner) getComponentByName(basicInfoPanel, "Max Patients Per Day");
            JSpinner maxPatientsPerSlotSpinner = (JSpinner) getComponentByName(basicInfoPanel, "Max Patients Per Slot");
            JSpinner slotDurationSpinner = (JSpinner) getComponentByName(basicInfoPanel, "Slot Duration (minutes)");
    
            JTextField roomField = (JTextField) getComponentByName(additionalOptionsPanel, "Room Number");
            JCheckBox telemedicineCheckbox = (JCheckBox) getComponentByName(additionalOptionsPanel, "Telemedicine");
            JCheckBox emergencyCheckbox = (JCheckBox) getComponentByName(additionalOptionsPanel, "Emergency");
    
            // Validate fields
            if (nameField.getText().isEmpty() || roomField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and Room Number are required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
    
            // Extract data
            pstmt.setString(1, nameField.getText());
            pstmt.setString(2, (String) specialistCombo.getSelectedItem());
            pstmt.setString(3, roomField.getText());
            pstmt.setString(4, ""); // patient_consult - modify as needed
            pstmt.setString(5, ""); // availability - modify as needed
            pstmt.setInt(6, (Integer) maxPatientsPerDaySpinner.getValue());
            pstmt.setInt(7, (Integer) maxPatientsPerSlotSpinner.getValue());
            pstmt.setInt(8, (Integer) slotDurationSpinner.getValue());
            pstmt.setBoolean(9, telemedicineCheckbox.isSelected());
            pstmt.setBoolean(10, emergencyCheckbox.isSelected());
            pstmt.setString(11, ""); // availability_details
            pstmt.setString(12, ""); // lunch_break
    
            // Execute and return
            pstmt.executeUpdate();
            return true;
    
        } catch (SQLException e) {
            LOGGER.severe("Error adding doctor: " + e.getMessage());
            JOptionPane.showMessageDialog(dialog, "Database error while adding doctor. Check logs.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private Component getComponentByName(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName())) {
                return component;
            } else if (component instanceof Container) {
                Component nested = getComponentByName((Container) component, name);
                if (nested != null) return nested;
            }
        }
        return null;
    }
    private void loadDoctorData(JTable doctorTable) {
        try (Connection conn = dbaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM hospital_management.doctors");
             ResultSet rs = pstmt.executeQuery()) {
            
            DefaultTableModel tableModel = (DefaultTableModel) doctorTable.getModel();
            tableModel.setRowCount(0); // Clear existing data
    
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("specialist"),
                    rs.getString("room"),
                    rs.getString("patient_consult"),
                    rs.getString("availability"),
                    rs.getInt("max_patients_per_day"),
                    rs.getInt("max_patients_per_slot"),
                    rs.getInt("slot_duration"),
                    rs.getBoolean("telemedicine"),
                    rs.getBoolean("emergency_appointments"),
                    rs.getString("availability_details"),
                    rs.getString("lunch_break")
                });
            }
        } catch (SQLException e) {
            LOGGER.severe("Error loading doctor data: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error loading doctor data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addLabelAndComponent(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = x + 1;
        panel.add(component, gbc);
    }

    // Add other necessary methods here
}