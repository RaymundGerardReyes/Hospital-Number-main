package Systems.Laboratory;

import Systems.Database.DatabaseConnection;
import Systems.Finance.FinancePanel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LaboratoryManager {
    private static final Logger LOGGER = Logger.getLogger(LaboratoryManager.class.getName());
    private static LaboratoryManager instance;
    private FinancePanel financePanel;

    private LaboratoryManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized LaboratoryManager getInstance() {
        if (instance == null) {
            instance = new LaboratoryManager();
        }
        return instance;
    }

    public void setFinancePanel(FinancePanel financePanel) {
        this.financePanel = financePanel;
    }

    public boolean addTest(String patientId, String testName, String category, String testCode) {
        if (patientId == null || testName == null || category == null || testCode == null) {
            LOGGER.log(Level.WARNING, "Null values provided for test addition");
            return false;
        }

        // Check if payment has been made
        if (financePanel == null) {
            LOGGER.log(Level.SEVERE, "FinancePanel not set");
            return false;
        }

        if (!financePanel.isTestPaid(patientId, testCode)) {
            LOGGER.log(Level.WARNING, "Payment not made for test: {0} for patient: {1}", new Object[]{testCode, patientId});
            return false;
        }

        // Save test data
        if (!saveTestToDatabase(patientId, testName, category, testCode)) {
            LOGGER.log(Level.SEVERE, "Failed to save test data for patient: {0}, test: {1}", new Object[]{patientId, testCode});
            return false;
        }

        // Generate receipt after saving test data
        if (!generateReceipt(patientId, testCode)) {
            LOGGER.log(Level.SEVERE, "Failed to generate receipt for patient: {0}, test: {1}", new Object[]{patientId, testCode});
            return false;
        }

        LOGGER.log(Level.INFO, "Test added and receipt generated successfully for patient: {0}, test: {1}", new Object[]{patientId, testCode});
        return true;
    }

    private boolean saveTestToDatabase(String patientId, String testName, String category, String testCode) {
        String query = "INSERT INTO patient_tests (patient_id, test_code, test_name, category, test_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, patientId);
            pstmt.setString(2, testCode);
            pstmt.setString(3, testName);
            pstmt.setString(4, category);
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(6, "Pending");
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving test to database", e);
            return false;
        }
    }

    private boolean generateReceipt(String patientId, String testCode) {
        // Simulate receipt generation logic
        try {
            LOGGER.log(Level.INFO, "Generating receipt for patient: {0}, test: {1}", new Object[]{patientId, testCode});
            // Add your receipt generation logic here (e.g., PDF generation or database entry)
            System.out.println("Receipt generated for patient: " + patientId + ", test: " + testCode);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating receipt", e);
            return false;
        }
    }

    // Additional methods like getTest, updateTestResult, and others remain unchanged.

    public boolean updateTestResult(String patientId, String testCode, String result) {
        String query = "UPDATE patient_tests SET result = ?, status = ? WHERE patient_id = ? AND test_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, result);
            pstmt.setString(2, "Completed");
            pstmt.setString(3, patientId);
            pstmt.setString(4, testCode);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating test result in database", e);
            return false;
        }
    }

    public List<LaboratoryTest> getAllTests() {
        List<LaboratoryTest> tests = new ArrayList<>();
        String query = "SELECT * FROM patient_tests";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                tests.add(createTestFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching tests from database", e);
        }
        return tests;
    }

    public LaboratoryTest getTest(String patientId, String testCode) {
        String query = "SELECT * FROM patient_tests WHERE patient_id = ? AND test_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, patientId);
            pstmt.setString(2, testCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createTestFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching test from database", e);
        }
        return null;
    }

    private LaboratoryTest createTestFromResultSet(ResultSet rs) throws SQLException {
        return new LaboratoryTest(
                rs.getString("patient_id"),
                rs.getString("test_name"),
                rs.getString("category"),
                rs.getString("test_code"),
                rs.getTimestamp("test_date"),
                rs.getString("status"),
                rs.getString("result")
        );
    }

    public List<LaboratoryTest> getTestsByPatient(String patientId) {
        List<LaboratoryTest> tests = new ArrayList<>();
        String query = "SELECT * FROM patient_tests WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tests.add(createTestFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching tests for patient from database", e);
        }
        return tests;
    }

    public static class LaboratoryTest {
        private String patientId;
        private final String testName;
        private final String category;
        private final String testCode;
        private final Date date;
        private String status;
        private String result;

        public LaboratoryTest(String testName, String category, String testCode, Date date, String status) {
            this(null, testName, category, testCode, date, status, null);
        }

        public LaboratoryTest(String patientId, String testName, String category, String testCode, Date date, String status, String result) {
            if (testName == null || category == null || testCode == null || date == null || status == null) {
                throw new IllegalArgumentException("TestName, Category, TestCode, Date, and Status cannot be null");
            }
            this.patientId = patientId; // Allow patientId to be null initially
            this.testName = testName;
            this.category = category;
            this.testCode = testCode;
            this.date = new Date(date.getTime());
            this.status = status;
            this.result = result;
        }

        // Getters
        public String getPatientId() {
            return patientId;
        }
    
        public String getTestName() {
            return testName;
        }
    
        public String getCategory() {
            return category;
        }
    
        public String getTestCode() {
            return testCode;
        }
    
        public Date getDate() {
            return new Date(date.getTime()); // Defensive copy
        }
    
        public String getStatus() {
            return status;
        }
    
        public String getResult() {
            return result;
        }
    
        // Setters
        public void setPatientId(String patientId) {
            if (patientId == null || patientId.trim().isEmpty()) {
                throw new IllegalArgumentException("Patient ID cannot be null or empty");
            }
            this.patientId = patientId;
        }

    
        public void setStatus(String status) {
            if (status == null || status.trim().isEmpty()) {
                throw new IllegalArgumentException("Status cannot be null or empty");
            }
            this.status = status;
        }
    
        public void setResult(String result) {
            if (result == null || result.trim().isEmpty()) {
                throw new IllegalArgumentException("Result cannot be null or empty");
            }
            this.result = result;
            this.status = "Completed"; // Automatically update status to "Completed"
        }

        
    
        // Override toString for better debugging
        @Override
        public String toString() {
            return String.format(
                "Hospital ID: %s\nTest Name: %s\nCategory: %s\nTest Code: %s\nDate: %s\nStatus: %s\nResult: %s",
                patientId, testName, category, testCode, date, status, result
            );
        }
    }
}