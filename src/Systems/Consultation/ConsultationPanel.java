package Systems.Consultation;

import Systems.Database.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import Systems.Consultation.DoctorPanel;
import Systems.Consultation.ConsultationParentPanel;

public class ConsultationPanel extends JPanel {
    private ConsultationParentPanel parentPanel;
    private DoctorPanel doctorPanel;
    private JTextField hospitalIdField, lastNameField, firstNameField, middleNameField, sexField, streetAddressField, cityField, phoneField, healthConcernField;
    private JComboBox<Integer> ageComboBox;
    private JButton verifyButton, scheduleButton;
    private JComboBox<String> specialtyComboBox;
    private JComboBox<String> doctorComboBox; // Store doctor names with corresponding IDs
    private Map<String, Integer> doctorIdMap = new HashMap<>();

    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private static final Map<String, Double> CONSULTATION_FEES = new LinkedHashMap<>();
    static {
        CONSULTATION_FEES.put("Select Specialty", 0.0);
        TreeMap<String, Double> sortedFees = new TreeMap<>();
        sortedFees.put("Allergist/Immunologist", 700.0);
        sortedFees.put("Anesthesiologist", 550.0);
        sortedFees.put("Cardiologist", 800.0);
        sortedFees.put("Dermatologist", 500.0);
        sortedFees.put("Endocrinologist", 600.0);
        sortedFees.put("Gastroenterologist", 650.0);
        sortedFees.put("Geriatrician", 450.0);
        sortedFees.put("Hematologist", 700.0);
        sortedFees.put("Infectious Disease Specialist", 750.0);
        sortedFees.put("Internist", 400.0);
        sortedFees.put("Nephrologist", 600.0);
        sortedFees.put("Neurologist", 750.0);
        sortedFees.put("Obstetrician/Gynecologist", 550.0);
        sortedFees.put("Oncologist", 800.0);
        sortedFees.put("Orthopedic Surgeon", 700.0);
        sortedFees.put("Otolaryngologist (ENT)", 550.0);
        sortedFees.put("Pediatrician", 400.0);
        sortedFees.put("Physiatrist", 500.0);
        sortedFees.put("Plastic Surgeon", 900.0);
        sortedFees.put("Podiatrist", 450.0);
        sortedFees.put("Pulmonologist", 600.0);
        sortedFees.put("Rheumatologist", 650.0);
        sortedFees.put("Surgeon", 800.0);
        sortedFees.put("Urologist", 550.0);
        sortedFees.put("Vascular Surgeon", 750.0);
        CONSULTATION_FEES.putAll(sortedFees);

       

    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
    
        String[] specialties = CONSULTATION_FEES.keySet().toArray(new String[0]);
        specialtyComboBox = new JComboBox<>(specialties);
        doctorComboBox = new JComboBox<>();

        // Create form panel with GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
    
        // Initialize fields
        hospitalIdField = createStyledTextField();
        lastNameField = createStyledTextField();
        firstNameField = createStyledTextField();
        middleNameField = createStyledTextField();
        ageComboBox = createStyledComboBox(generateAgeRange());
        sexField = createStyledTextField();
        streetAddressField = createStyledTextField();
        cityField = createStyledTextField();
        phoneField = createStyledTextField();
        healthConcernField = createStyledTextField();
    
        // Add fields to form panel
        addFormField(formPanel, gbc, "Hospital ID:", hospitalIdField);
        addFormField(formPanel, gbc, "Last Name:", lastNameField);
        addFormField(formPanel, gbc, "First Name:", firstNameField);
        addFormField(formPanel, gbc, "Middle Name:", middleNameField);
        addFormField(formPanel, gbc, "Age:", ageComboBox);
        addFormField(formPanel, gbc, "Sex:", sexField);
        addFormField(formPanel, gbc, "Street Address:", streetAddressField);
        addFormField(formPanel, gbc, "City:", cityField);
        addFormField(formPanel, gbc, "Phone:", phoneField);
        addFormField(formPanel, gbc, "Health Concern:", healthConcernField);
        addFormField(formPanel, gbc, "Select Specialty:", specialtyComboBox);
    
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
    
        verifyButton = createStyledButton("Verify");
        scheduleButton = createStyledButton("Schedule Appointment");
    
        verifyButton.addActionListener(e -> verifyPatient());
        scheduleButton.addActionListener(e -> scheduleAppointment());
    
        buttonPanel.add(verifyButton);
        buttonPanel.add(scheduleButton);
    
        // Add form panel and button panel to the main panel
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void scheduleAppointmentWithPaymentCheck() {
        String hospitalId = hospitalIdField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String specialty = (String) specialtyComboBox.getSelectedItem();
    
        if (hospitalId.isEmpty() || lastName.isEmpty() || firstName.isEmpty() || specialty.equals("Select Specialty")) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        String existingReceiptId = checkExistingReceiptBySpecialty(hospitalId, lastName, firstName, specialty);
        if (existingReceiptId != null) {
            String status = getReceiptStatus(existingReceiptId);
            showMessage(lastName, firstName, status, specialty);
            if ("Paid".equalsIgnoreCase(status)) {
                if (specialtyComboBox.getSelectedItem() != null) {
                    proceedWithAppointment(hospitalId, lastName, firstName, specialty);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a doctor before proceeding.", "Doctor Not Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        } else {
            // No existing receipt for this specialty, create a new pending receipt
            double consultationFee = CONSULTATION_FEES.get(specialty);
            String newReceiptId = createPendingReceipt(hospitalId, lastName, firstName, specialty, consultationFee);
            if (newReceiptId != null) {
                showMessage(lastName, firstName, "Pending", specialty);
                return;
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create a new receipt. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    
        // Proceed with scheduling
        int doctorId = (int) specialtyComboBox.getSelectedItem();
        String patientName = lastName + " " + firstName;
        int age = (int) ageComboBox.getSelectedItem();
        String sex = sexField.getText();
        String healthConcern = healthConcernField.getText();
    
        saveAppointment(doctorId, patientName, hospitalId, age, sex, specialty, healthConcern);
    }

    private String checkExistingReceiptBySpecialty(String hospitalId, String lastName, String firstName, String specialty) {
        String query = "SELECT receipt_id FROM receipts WHERE hospital_id = ? AND last_name = ? AND first_name = ? " +
                       "AND specialtyComboBox = ? AND (status = 'Pending' OR status = 'Paid') ORDER BY date DESC, time DESC LIMIT 1";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setString(1, hospitalId);
            stmt.setString(2, lastName);
            stmt.setString(3, firstName);
            stmt.setString(4, specialty);
    
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("receipt_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }


    private boolean isPaymentCleared(String hospitalId, String lastName, String firstName) {
        String url = "jdbc:mysql://localhost:3306/hospital_management.receipts";
        String username = "root";
        String password = "";

        String query = "SELECT status FROM receipts WHERE hospital_id = ? AND last_name = ? AND first_name = ? ORDER BY date DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, hospitalId);
            stmt.setString(2, lastName);
            stmt.setString(3, firstName);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String paymentStatus = rs.getString("status");
                return "Paid".equalsIgnoreCase(paymentStatus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }
    
    private void scheduleAppointment() {
        String hospitalId = hospitalIdField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String specialty = (String) specialtyComboBox.getSelectedItem();
    
        if (hospitalId.isEmpty() || lastName.isEmpty() || firstName.isEmpty() || specialty.equals("Select Specialty")) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        if (isAppointmentLimitReached(hospitalId, specialty)) {
            JOptionPane.showMessageDialog(this, "Maximum appointments reached for this specialty today.", "Limit Reached", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Check for an existing receipt for this specialty
        String existingReceiptId = checkExistingReceiptBySpecialty(hospitalId, lastName, firstName, specialty);
        if (existingReceiptId != null) {
            String status = getReceiptStatus(existingReceiptId);
            showMessage(lastName, firstName, status, specialty);
            if ("Paid".equalsIgnoreCase(status)) {
                proceedWithAppointment(hospitalId, lastName, firstName, specialty);
            }
            return;
        }
    
        // Create a new receipt if no receipt exists for this specialty
        double consultationFee = CONSULTATION_FEES.get(specialty);
        String receiptId = createPendingReceipt(hospitalId, lastName, firstName, specialty, consultationFee);
        if (receiptId != null) {
            showMessage(lastName, firstName, "Pending", specialty);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create receipt. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void proceedWithAppointment(String hospitalId, String lastName, String firstName, String specialty) {
        if (specialtyComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a doctor before proceeding.", "Doctor Not Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedDoctor = (String) specialtyComboBox.getSelectedItem();
        int doctorId = doctorIdMap.get(selectedDoctor); // Assuming doctorIdMap is populated elsewhere
        
        String patientName = lastName + " " + firstName;
        int age = (int) ageComboBox.getSelectedItem();
        String sex = sexField.getText();
        String healthConcern = healthConcernField.getText();
    
        saveAppointment(doctorId, patientName, hospitalId, age, sex, specialty, healthConcern);
    }
    
    private void showMessage(String lastName, String firstName, String status, String specialty) {
        String message;
        switch (status) {
            case "Pending" -> message = String.format("%s, %s has a pending appointment for %s. Please complete the payment first.", lastName, firstName, specialty);
            case "Paid" -> message = String.format("%s, %s has paid for %s. Patient may proceed to schedule.", lastName, firstName, specialty);
            default -> message = String.format("Appointment for %s scheduled successfully for %s, %s. Please proceed to payment.", specialty, lastName, firstName);
        }
        JOptionPane.showMessageDialog(this, message, "Appointment Status", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String checkExistingReceiptToday(String hospitalId, String lastName, String firstName) {
        LocalDate today = LocalDate.now();
        String query = "SELECT receipt_id FROM receipts WHERE hospital_id = ? AND last_name = ? AND first_name = ? " +
                       "AND (status = 'Pending' OR status = 'Paid') AND DATE(date) = ? ORDER BY time DESC LIMIT 1";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setString(1, hospitalId);
            stmt.setString(2, lastName);
            stmt.setString(3, firstName);
            stmt.setDate(4, java.sql.Date.valueOf(today));
    
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("receipt_id"); // Return receipt ID if one exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null; // No receipt found for today
    }

    private String checkExistingReceipt(String hospitalId, String lastName, String firstName) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT receipt_id, status FROM receipts WHERE hospital_id = ? AND last_name = ? AND first_name = ? AND (status = 'Pending' OR status = 'Paid') ORDER BY date DESC, time DESC LIMIT 1")) {
            stmt.setString(1, hospitalId);
            stmt.setString(2, lastName);
            stmt.setString(3, firstName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("receipt_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private String formatAppointmentMessage(String lastName, String firstName, String status) {
        if ("Pending".equals(status)) {
            return lastName + ", " + firstName + " was " + status;
        } else if ("Paid".equals(status)) {
            return lastName + ", " + firstName + " was " + status + ". Patient is now proceeding for the schedule.";
        } else {
            return "Appointment scheduled successfully! Please proceed to payment.";
        }
    }

     private boolean isAppointmentLimitReached(String hospitalId, String specialty) {
        LocalDate today = LocalDate.now();
        String query = "SELECT COUNT(*) FROM appointments WHERE hospital_id = ? AND specialty = ? AND DATE(appointment_date) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, hospitalId);
            stmt.setString(2, specialty);
            stmt.setDate(3, java.sql.Date.valueOf(today));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) >= 2; // Limit for daily appointments per specialty
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    private String getReceiptStatus(String receiptId) {
        String query = "SELECT status FROM receipts WHERE receipt_id = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setString(1, receiptId);
    
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return "Unknown";
    }

    public ConsultationPanel(ConsultationParentPanel parentPanel) {
        if (parentPanel == null) {
            throw new IllegalArgumentException("parentPanel cannot be null");
        }
        this.parentPanel = parentPanel;
        initializeComponents();
    }


    // Define the method to link doctorPanel back to consultationPanel
    public void setConsultationPanel(ConsultationPanel consultationPanel) {
        // Implement the linking logic here
    }

    

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);
    
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        panel.add(component, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(MAIN_FONT);
        return field;
    }

    private JComboBox<Integer> createStyledComboBox(Integer[] items) {
        JComboBox<Integer> comboBox = new JComboBox<>(items);
        comboBox.setFont(MAIN_FONT);
        return comboBox;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private Integer[] generateAgeRange() {
        Integer[] ageRange = new Integer[100];
        for (int i = 0; i < 100; i++) {
            ageRange[i] = i + 1;
        }
        return ageRange;
    }

    private void verifyPatient() {
        String hospitalId = hospitalIdField.getText().trim();
        if (hospitalId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Hospital ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT last_name, first_name, middle_name, age, sex, street_address, city, phone " +
                     "FROM patients WHERE hospital_id = ?")) {
            
            stmt.setString(1, hospitalId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                lastNameField.setText(rs.getString("last_name"));
                firstNameField.setText(rs.getString("first_name"));
                middleNameField.setText(rs.getString("middle_name"));
                ageComboBox.setSelectedItem(rs.getInt("age"));
                sexField.setText(rs.getString("sex"));
                streetAddressField.setText(rs.getString("street_address"));
                cityField.setText(rs.getString("city"));
                phoneField.setText(rs.getString("phone"));

                parentPanel.updateTransactionSlip("Patient verified: " + rs.getString("first_name") + " " + rs.getString("last_name"));
            } else {
                JOptionPane.showMessageDialog(this, "Patient not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setDoctorPanel(DoctorPanel doctorPanel) {
        this.doctorPanel = doctorPanel;
    }


    private void clearFields() {
        hospitalIdField.setText("");
        lastNameField.setText("");
        firstNameField.setText("");
        middleNameField.setText("");
        ageComboBox.setSelectedIndex(0);
        sexField.setText("");
        streetAddressField.setText("");
        cityField.setText("");
        phoneField.setText("");
        healthConcernField.setText("");
        specialtyComboBox.setSelectedIndex(0);
    }



    private boolean validateFields() {
        // Implement field validation logic
        // Return true if all required fields are filled, false otherwise
        // Show appropriate error messages for missing fields
        return true; // Placeholder
    }
    private boolean isPaymentProcessed() {
        String hospitalId = hospitalIdField.getText().trim();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM Receipts WHERE hospital_id = ? AND consultation_fee IS NOT NULL")) {
            stmt.setString(1, hospitalId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if payment is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void saveAppointment(int doctorId, String patientName, String hospitalId, int age, String sex, String specialty, String healthConcern) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO appointments (doctor_id, patient_name, hospital_id, age, sex, specialty, health_concern, appointment_date, appointment_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
    
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, patientName);
            pstmt.setString(3, hospitalId);
            pstmt.setInt(4, age);
            pstmt.setString(5, sex);
            pstmt.setString(6, specialty);
            pstmt.setString(7, healthConcern);
            pstmt.setDate(8, Date.valueOf(LocalDate.now())); // Current date for appointment
            pstmt.setTime(9, Time.valueOf(LocalTime.now())); // Current time for appointment
            pstmt.executeUpdate();
    
            JOptionPane.showMessageDialog(this, "Appointment scheduled successfully for " + patientName + " with Dr. " + doctorId, "Appointment Scheduled", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving appointment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private String createPendingReceipt(String hospitalId, String lastName, String firstName, String specialty, double consultationFee) {
        String query = "INSERT INTO receipts (hospital_id, consultation_fee, date, time, status, last_name, first_name, specialtyComboBox) " +
                       "VALUES (?, ?, ?, ?, 'Pending', ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
    
            stmt.setString(1, hospitalId);
            stmt.setDouble(2, consultationFee);
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setTime(4, java.sql.Time.valueOf(LocalTime.now()));
            stmt.setString(5, lastName);
            stmt.setString(6, firstName);
            stmt.setString(7, specialty);
    
            int affectedRows = stmt.executeUpdate();
    
            if (affectedRows == 0) {
                throw new SQLException("Creating receipt failed, no rows affected.");
            }
    
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getString(1);
                } else {
                    throw new SQLException("Creating receipt failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}