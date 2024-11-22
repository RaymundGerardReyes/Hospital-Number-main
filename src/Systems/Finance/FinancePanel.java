package Systems.Finance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import Systems.Dashboard.DarkMode;
import Systems.Database.DatabaseConnection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FinancePanel extends JPanel {
    protected Map<String, Patient> patients;
    protected Map<String, Service> services;
    protected JTextArea outputArea;
    protected JPanel controlPanel;
    protected boolean isDarkMode = false;
    protected DarkMode darkMode;
    private JTextArea receiptArea;
    private JPanel inputPanel;
    private JTextField hospitalIdField;
    private JTextField lastNameField;
    private JTextField firstNameField;
    private JTextField middleNameField;
    private JTable transactionsTable;
    private JButton verifyButton;
    private JButton processPaymentButton;
    private JButton verifybutton;

    private final Color LIGHT_BG = new Color(240, 240, 240);
    private final Color LIGHT_TEXT = new Color(33, 33, 33);
    private final Color LIGHT_BUTTON = new Color(0, 120, 212);
    private final Color LIGHT_BUTTON_TEXT = Color.WHITE;
    private final Color DARK_BG = new Color(33, 33, 33);
    private final Color DARK_TEXT = new Color(240, 240, 240);
    private final Color DARK_BUTTON = new Color(0, 102, 204);
    private final Color DARK_BUTTON_TEXT = Color.WHITE;
    private static final double CONSULTATION_FEE = 100.0;

    private static final Logger LOGGER = Logger.getLogger(FinancePanel.class.getName());
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_management";
    private static final String DB_TABLE = "hospital_management.receipts";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Raymund@Estaca01";

    public FinancePanel(DarkMode darkMode) {
        this.darkMode = darkMode;
        this.patients = new HashMap<>();
        this.services = new HashMap<>();
        initializeUI();
        updateColors(darkMode);
        initializeServices();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel patientInfoPanel = new JPanel(new GridLayout(0, 2));
        patientInfoPanel.setBorder(BorderFactory.createTitledBorder("Patient Information"));

        patientInfoPanel.add(new JLabel("Hospital ID:"));
        hospitalIdField = new JTextField(20);
        patientInfoPanel.add(hospitalIdField);

        patientInfoPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField(20);
        patientInfoPanel.add(lastNameField);

        patientInfoPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField(20);
        patientInfoPanel.add(firstNameField);

        patientInfoPanel.add(new JLabel("Middle Name:"));
        middleNameField = new JTextField(20);
        patientInfoPanel.add(middleNameField);

        verifyButton = new JButton("Verify");
        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifyAndFetchTransactions();
            }
        });
        patientInfoPanel.add(verifyButton);

        add(patientInfoPanel, BorderLayout.NORTH);

        // Transactions Panel
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBorder(BorderFactory.createTitledBorder("Transactions"));

        transactionsTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(transactionsTable);
        transactionsPanel.add(tableScrollPane, BorderLayout.CENTER);

        processPaymentButton = new JButton("Process Payment");
        processPaymentButton.addActionListener(e -> processPayment());
        transactionsPanel.add(processPaymentButton, BorderLayout.SOUTH);

        add(transactionsPanel, BorderLayout.CENTER);

        // Receipt Panel
        JPanel receiptPanel = new JPanel(new BorderLayout());
        receiptPanel.setBorder(BorderFactory.createTitledBorder("Receipt"));

        receiptArea = new JTextArea(10, 30);
        receiptArea.setEditable(false);
        JScrollPane receiptScrollPane = new JScrollPane(receiptArea);
        receiptPanel.add(receiptScrollPane, BorderLayout.CENTER);

        add(receiptPanel, BorderLayout.SOUTH);

        // Output area for logs
        outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.EAST);
    }

    
    private boolean verifyPatient(String hospitalId, String lastName, String firstName, String middleName) {
        String query = "SELECT 1 FROM " + DB_TABLE + " WHERE hospital_id = ? AND last_name = ? AND first_name = ? AND middle_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hospitalId);
            stmt.setString(2, lastName);
            stmt.setString(3, firstName);
            stmt.setString(4, middleName != null ? middleName : "");
            try (ResultSet rs = stmt.executeQuery()) {
                boolean verified = rs.next();
                LOGGER.info(verified ? "Patient verified successfully: " + hospitalId 
                                     : "Patient verification failed for Hospital ID: " + hospitalId);
                return verified;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during patient verification", e);
            return false;
        }
    }
    
    private void verifyAndFetchTransactions() {
        String hospitalId = hospitalIdField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();

        outputArea.setText(""); // Clear previous output

        if (!hospitalId.isEmpty()) {
            processHospitalIdInput(hospitalId);
        } else if (!lastName.isEmpty() && !firstName.isEmpty()) {
            processNameInput(lastName, firstName, middleName);
        } else {
            JOptionPane.showMessageDialog(this, "Please enter either Hospital ID or Patient Name.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void processNameInput(String lastName, String firstName, String middleName) {
        String retrievedHospitalId = retrieveHospitalId(lastName, firstName, middleName);
        if (retrievedHospitalId != null) {
            outputArea.append("Name: " + lastName + ", " + firstName + " " + middleName + "\n");
            outputArea.append("Hospital ID: " + retrievedHospitalId + "\n");
            hospitalIdField.setText(retrievedHospitalId);
            displayTransactionsForPatient(retrievedHospitalId);
        } else {
            JOptionPane.showMessageDialog(this, "Patient verification failed for Name: " + lastName + ", " + firstName + " " + middleName, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayTransactionsForPatient(String hospitalId) {
        List<Transaction> transactions = fetchTransactions(hospitalId);
        if (transactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No transactions found for this patient.", "Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            displayTransactions(transactions);
        }
    }


    private void processHospitalIdInput(String hospitalId) {
        String[] patientInfo = retrievePatientInfo(hospitalId);
        if (patientInfo != null) {
            outputArea.append("Hospital ID: " + hospitalId + "\n");
            outputArea.append("Name: " + patientInfo[0] + ", " + patientInfo[1] + " " + patientInfo[2] + "\n");
            lastNameField.setText(patientInfo[0]);
            firstNameField.setText(patientInfo[1]);
            middleNameField.setText(patientInfo[2]);
            displayTransactionsForPatient(hospitalId);
        } else {
            JOptionPane.showMessageDialog(this, "Patient verification failed for Hospital ID: " + hospitalId, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] retrievePatientInfo(String hospitalId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT last_name, first_name, middle_name FROM patients WHERE hospital_id = ?")) {
            stmt.setString(1, hospitalId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new String[]{
                    rs.getString("last_name"),
                    rs.getString("first_name"),
                    rs.getString("middle_name")
                };
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving patient info", e);
        }
        return null;
    }


    private String retrieveHospitalId(String lastName, String firstName, String middleName) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT hospital_id FROM patients WHERE last_name = ? AND first_name = ? AND middle_name = ?")) {
            stmt.setString(1, lastName);
            stmt.setString(2, firstName);
            stmt.setString(3, middleName != null ? middleName : "");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("hospital_id");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving Hospital ID", e);
        }
        return null;
    }
    
    
    private List<Transaction> fetchTransactions(String hospitalId) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT receipt_id, date, consultation_fee, status FROM " + DB_TABLE + " WHERE hospital_id = ? ORDER BY date DESC, time DESC";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setString(1, hospitalId);
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getString("receipt_id"),
                        rs.getString("date"),
                        rs.getDouble("consultation_fee"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while fetching transactions", e);
            outputArea.append("\nDatabase error: " + e.getMessage());
        }
    
        return transactions;
    }
    
    
    private void displayTransactions(List<Transaction> transactions) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Date", "Amount", "Status", "Action", "receipt_id"}, 0);
        for (Transaction transaction : transactions) {
            model.addRow(new Object[]{
                transaction.date,
                transaction.amount,
                transaction.status,
                "View Receipt",
                transaction.receiptId  // Add receipt_id as a hidden field
            });
        }
        transactionsTable.setModel(model);
    
        // Hide the receipt_id column
        transactionsTable.getColumnModel().getColumn(4).setMinWidth(0);
        transactionsTable.getColumnModel().getColumn(4).setMaxWidth(0);
        transactionsTable.getColumnModel().getColumn(4).setWidth(0);
    
        // Add a button column for "View Receipt"
        TableColumn actionColumn = transactionsTable.getColumnModel().getColumn(3);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
    }
    
    private void displayReceipt(int selectedRow) {
        String date = (String) transactionsTable.getValueAt(selectedRow, 0);
        Double amount = (Double) transactionsTable.getValueAt(selectedRow, 1);
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT r.*, p.last_name, p.first_name, p.middle_name FROM " + DB_TABLE + " r " +
                     "JOIN patients p ON r.hospital_id = p.hospital_id " +
                     "WHERE r.hospital_id = ? AND r.date = ? AND r.consultation_fee = ?")) {
            stmt.setString(1, hospitalIdField.getText());
            stmt.setString(2, date);
            stmt.setDouble(3, amount);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                String patientName = rs.getString("last_name") + ", " + rs.getString("first_name") + " " + 
                                     (rs.getString("middle_name") != null ? rs.getString("middle_name") : "");
                String receiptText = formatReceiptText(
                        rs.getString("hospital_id"),
                        patientName,
                        rs.getString("admin_name"),
                        rs.getString("payment_method"),
                        rs.getDouble("consultation_fee"),
                        rs.getString("date"),
                        rs.getString("time")
                );
                receiptArea.setText(receiptText);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error displaying receipt", e);
            outputArea.append("Database error: " + e.getMessage() + "\n");
        }
    }

    private void updateComponentColors(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JTextField) {
                c.setBackground(darkMode.getCardBackgroundColor());
                c.setForeground(darkMode.getTextColor());
            } else if (c instanceof JTextArea) {
                c.setBackground(darkMode.getCardBackgroundColor());
                c.setForeground(darkMode.getTextColor());
            }
            
            if (c instanceof Container) {
                updateComponentColors((Container) c);
            }
        }
    }

    private void processPayment() {
        int selectedRow = transactionsTable.getSelectedRow();
        if (selectedRow != -1) {
            String receiptId = (String) transactionsTable.getValueAt(selectedRow, 4);  // Get receipt_id
            String date = (String) transactionsTable.getValueAt(selectedRow, 0);
            Double amount = (Double) transactionsTable.getValueAt(selectedRow, 1);
            String status = (String) transactionsTable.getValueAt(selectedRow, 2);
    
            if ("Pending".equals(status)) {
                String paymentMethod = JOptionPane.showInputDialog(this, "Enter Payment Method (Cash/Credit Card/Insurance):");
                if (paymentMethod != null && !paymentMethod.isEmpty()) {
                    if ("Cash".equalsIgnoreCase(paymentMethod)) {
                        processCashPayment(receiptId, date, amount);
                    } else {
                        // For non-cash methods, directly update payment status as "Paid"
                        updatePaymentStatus(receiptId, amount, paymentMethod, amount);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "This transaction is already paid.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a transaction to process payment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void processCashPayment(String receiptId, String date, double amountDue) {
        String amountPaidStr = JOptionPane.showInputDialog(this, "Enter Amount Paid:");
        if (amountPaidStr == null || amountPaidStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No amount entered. Payment cancelled.", "Cancelled", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double amountPaid = Double.parseDouble(amountPaidStr.trim());
            if (amountPaid < 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amountPaid < amountDue) {
                handlePartialPayment(receiptId, amountDue, amountPaid);
            } else {
                handleFullPayment(receiptId, amountDue, amountPaid);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount entered. Please enter a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handlePartialPayment(String receiptId, double amountDue, double amountPaid) {
        double balance = amountDue - amountPaid;
        JOptionPane.showMessageDialog(this, String.format("Partial payment received.\nAmount due: %.2f\nAmount paid: %.2f\nRemaining balance: %.2f", 
                                                          amountDue, amountPaid, balance), 
                                      "Payment Status", JOptionPane.INFORMATION_MESSAGE);
        updatePaymentStatus(receiptId, amountPaid, "Cash", balance);
    }

    private void handleFullPayment(String receiptId, double amountDue, double amountPaid) {
        double change = amountPaid - amountDue;
        JOptionPane.showMessageDialog(this, String.format("Payment completed.\nChange: %.2f", change), "Payment Success", JOptionPane.INFORMATION_MESSAGE);
        updatePaymentStatus(receiptId, amountDue, "Cash", 0.0);
    }

    
    private void updatePaymentStatus(String receiptId, double amountPaid, String paymentMethod, double balance) {
        String status = balance > 0 ? "Pending" : "Paid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE " + DB_TABLE + " SET consultation_fee = ?, payment_method = ?, balance = ?, status = ? WHERE receipt_id = ?")) {
    
            stmt.setDouble(1, amountPaid);
            stmt.setString(2, paymentMethod);
            stmt.setDouble(3, balance);
            stmt.setString(4, status);
            stmt.setString(5, receiptId);  // Use receipt_id to update the specific row
    
            int affectedRows = stmt.executeUpdate();
    
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Payment processed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                verifyAndFetchTransactions();  // Refresh the transactions table to reflect the payment update
            } else {
                JOptionPane.showMessageDialog(this, "Failed to process payment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during payment update", e);
            outputArea.append("Database error: " + e.getMessage() + "\n");
        }
    }
    

    private String formatReceiptText(String hospitalId, String patientName, String adminFinanceName, String paymentMethod, double consultationFee, String date, String time) {
        return String.format(
                "MyCare Healthcare Solutions\n" +
                "123 Main St\n" +
                "City, State, ZIP Code\n" +
                "Phone Number: (123) 456-7890\n" +
                "Email Address: info@clinic.com\n\n" +
                "Receipt for Consultation Fee\n" +
                "Receipt No: %s\n" +
                "Hospital ID: %s\n" +
                "Date: %s\n" +
                "Time: %s\n\n" +
                "Patient Name: %s\n" +
                "Consultation Fee: ₱ %.2f\n\n" +
                "Payment Method: %s\n" +
                "Admin Finance Name: %s\n\n" +
                "Thank you for your visit!\n" +
                "For any inquiries, please contact us at (123) 456-7890.",
                UUID.randomUUID().toString(),
                hospitalId,
                date,
                time,
                patientName,
                consultationFee,
                paymentMethod,
                adminFinanceName
        );
    }

   // Helper method to clear patient fields if no data is found
        private void clearPatientFields() {
            lastNameField.setText("");
            firstNameField.setText("");
            middleNameField.setText("");
            ((DefaultTableModel) transactionsTable.getModel()).setRowCount(0);
            outputArea.setText("");
        }

        public void updateColors(DarkMode darkMode) {
            setBackground(darkMode.getBackgroundColor());
            // Update colors for all components
            updateComponentColors(this);
        }

        public boolean processTestPayment(String patientId, String testCode) {
            // Implement the logic to process the payment for a test
            // This is a placeholder implementation
            try {
                // Check if the patient exists
                if (!verifyPatient(patientId, "", "", "")) {
                    LOGGER.warning("Patient not found: " + patientId);
                    return false;
                }
        
                // Fetch the test price (you may need to implement this method)
                double testPrice = getTestPrice(testCode);
        
                // Process the payment
                String paymentMethod = JOptionPane.showInputDialog(this, "Enter Payment Method (Cash/Credit Card/Insurance):");
                if (paymentMethod != null && !paymentMethod.isEmpty()) {
                    // For simplicity, we're assuming the full amount is paid
                    updatePaymentStatus(generateReceiptId(), testPrice, paymentMethod, 0.0);
                    LOGGER.info("Test payment processed for patient: " + patientId + ", test: " + testCode);
                    return true;
                } else {
                    LOGGER.warning("Payment cancelled for patient: " + patientId + ", test: " + testCode);
                    return false;
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error processing test payment", e);
                return false;
            }
        }
        
        public boolean isTestPaid(String patientId, String testCode) {
            // Implement the logic to check if a test has been paid
            // This is a placeholder implementation
            try {
                String query = "SELECT status FROM " + DB_TABLE + " WHERE hospital_id = ? AND test_code = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, patientId);
                    stmt.setString(2, testCode);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String status = rs.getString("status");
                            return "Paid".equalsIgnoreCase(status);
                        }
                    }
                }
                LOGGER.warning("No payment record found for patient: " + patientId + ", test: " + testCode);
                return false;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error checking test payment status", e);
                return false;
            }
        }
        
        private String generateReceiptId() {
            // Generate a unique receipt ID
            return "REC-" + UUID.randomUUID().toString();
        }
        
        private double getTestPrice(String testCode) {
            // Implement the logic to fetch the test price from the database
            // This is a placeholder implementation
            // You should replace this with actual database query
            return 100.0; // Default price
        }

    private void initializeServices() {
        services.put("ROOM", new Service("Daily Room Charge", 200.0));
        services.put("DOCTOR", new Service("Doctor's Fee", 150.0));
        services.put("MEDICATION", new Service("Medication", 50.0));
        services.put("LAB_TEST", new Service("Laboratory Test", 75.0));
        services.put("IMAGING", new Service("Imaging", 300.0));
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = transactionsTable.getSelectedRow();
                if (selectedRow != -1) {
                    displayReceipt(selectedRow);
                }
            }
            isPushed = false;
            return label;
        }
    }

    private static class Transaction {
        String receiptId;
        String date;
        double amount;
        String status;
    
        Transaction(String receiptId, String date, double amount, String status) {
            this.receiptId = receiptId;
            this.date = date;
            this.amount = amount;
            this.status = status;
        }
    }
    
    public void refreshData() {
        // Logic to refresh the data displayed in the FinancePanel
        // For example, you can fetch the latest data from the database and update the UI components
        // ...
    }

    protected static class Service {
        private String name;
        private double price;

        public Service(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() { return name; }
        public double getPrice() { return price; }
    }

    protected  static class Patient {
        private String id;
        private String name;
        private LocalDate admissionDate;
        private LocalDate dischargeDate;
        private Map<Service, Integer> services = new HashMap<>();
        private double totalCharges;
        private double totalPayments;

        public Patient(String id, String name, LocalDate admissionDate) {
            this.id = id;
            this.name = name;
            this.admissionDate = admissionDate;
        }

        public void addService(Service service, int quantity) {
            services.merge(service, quantity, Integer::sum);
            totalCharges += service.getPrice() * quantity;
        }

        public void addPayment(double amount) {
            totalPayments += amount;
        }


        public String getId() { return id; }
        public String getName() { return name; }
        public LocalDate getAdmissionDate() { return admissionDate; }
        public LocalDate getDischargeDate() { return dischargeDate; }
        public void setDischargeDate(LocalDate dischargeDate) { this.dischargeDate = dischargeDate; }
        public Map<Service, Integer> getServices() { return services; }
        public double getTotalCharges() { return totalCharges; }
        public double getTotalPayments() { return totalPayments; }
        public String getPaymentStatus() { return totalPayments >= totalCharges ? "Paid" : "Pending"; }
    }
}