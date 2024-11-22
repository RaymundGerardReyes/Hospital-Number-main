package Systems.Consultation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AppointmentScheduler {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Raymund@Estaca01";

    public void scheduleAppointmentTime(String doctorName, String patientName, String hospitalId, int age, String sex, String specialty, String healthConcern) {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField hospitalIdField = new JTextField(hospitalId);
        JTextField patientNameField = new JTextField(patientName);
        JTextField ageField = new JTextField(String.valueOf(age));
        JTextField doctorNameField = new JTextField(doctorName);
        JTextField specialtyField = new JTextField(specialty);
        JComboBox<String> dateCombo = new JComboBox<>(getAvailableDates());
        JComboBox<String> timeCombo = new JComboBox<>(getAvailableTimes());

        panel.add(new JLabel("Hospital ID:"));
        panel.add(hospitalIdField);
        panel.add(new JLabel("Patient Name:"));
        panel.add(patientNameField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Doctor Name:"));
        panel.add(doctorNameField);
        panel.add(new JLabel("Specialty:"));
        panel.add(specialtyField);
        panel.add(new JLabel("Select Date:"));
        panel.add(dateCombo);
        panel.add(new JLabel("Select Time:"));
        panel.add(timeCombo);

        JButton verifyButton = new JButton("Verify Payment");
        verifyButton.addActionListener(e -> verifyPayment(hospitalIdField.getText()));
        panel.add(verifyButton);

        int result = JOptionPane.showConfirmDialog(null, panel, "Schedule Appointment for " + doctorName,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String selectedDate = (String) dateCombo.getSelectedItem();
            String selectedTime = (String) timeCombo.getSelectedItem();
            saveAppointment(doctorNameField.getText(), patientNameField.getText(), hospitalIdField.getText(),
                    Integer.parseInt(ageField.getText()), sex, specialtyField.getText(), healthConcern, selectedDate, selectedTime);
        }
    }

    private void verifyPayment(String hospitalId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT status FROM receipts WHERE hospital_id = ? ORDER BY date DESC LIMIT 1")) {
            pstmt.setString(1, hospitalId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                if ("Paid".equalsIgnoreCase(status)) {
                    JOptionPane.showMessageDialog(null, "Payment verified. You can proceed with scheduling.", "Verification Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Payment not verified. Please complete the payment before scheduling.", "Verification Failed", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No receipt found for this Hospital ID.", "Verification Failed", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error verifying payment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAppointment(String doctorName, String patientName, String hospitalId, int age, String sex, String specialty, String healthConcern, String selectedDate, String selectedTime) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO appointments (doctor_name, patient_name, hospital_id, age, sex, specialty, health_concern, appointment_date, appointment_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            pstmt.setString(1, doctorName);
            pstmt.setString(2, patientName);
            pstmt.setString(3, hospitalId);
            pstmt.setInt(4, age);
            pstmt.setString(5, sex);
            pstmt.setString(6, specialty);
            pstmt.setString(7, healthConcern);
            pstmt.setString(8, selectedDate);
            pstmt.setString(9, selectedTime);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Appointment scheduled successfully for " + patientName + " with Dr. " + doctorName + " on " + selectedDate + " at " + selectedTime, "Appointment Scheduled", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to schedule appointment. Please try again.", "Scheduling Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving appointment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] getAvailableDates() {
        // Implementation for getting available dates
        return new String[]{"2023-05-01", "2023-05-02", "2023-05-03", "2023-05-04", "2023-05-05"};
    }

    private String[] getAvailableTimes() {
        // Implementation for getting available times
        return new String[]{"09:00", "10:00", "11:00", "14:00", "15:00", "16:00"};
    }

    public static void main(String[] args) {
        // Create the appointments table if it doesn't exist
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS appointments (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "doctor_name VARCHAR(100) NOT NULL," +
                    "patient_name VARCHAR(100) NOT NULL," +
                    "hospital_id VARCHAR(50) NOT NULL," +
                    "age INT NOT NULL," +
                    "sex VARCHAR(10) NOT NULL," +
                    "specialty VARCHAR(100) NOT NULL," +
                    "health_concern TEXT," +
                    "appointment_date DATE NOT NULL," +
                    "appointment_time TIME NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createTableSQL);
            System.out.println("Appointments table created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating appointments table: " + e.getMessage());
        }

        // Test the appointment scheduler
        AppointmentScheduler scheduler = new AppointmentScheduler();
        scheduler.scheduleAppointmentTime("Dr. Smith", "John Doe", "H12345", 35, "Male", "Cardiology", "Chest pain");
    }
}