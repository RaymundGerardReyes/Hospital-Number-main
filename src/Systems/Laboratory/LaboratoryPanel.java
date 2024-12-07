package Systems.Laboratory;

import Systems.Dashboard.DarkMode;
import Systems.Database.DatabaseConnection;
import Systems.Finance.FinancePanel;
import Systems.Laboratory.LaboratoryManager.LaboratoryTest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.List;

public class LaboratoryPanel extends JPanel {
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);

    private JTextField hospitalIDField, patientNameField, ageField, doctorNameField;
    private JComboBox<String> sexField;
    private JButton verifyButton, addTestButton, updateResultButton;
    private JTable testsTable;
    private DefaultTableModel tableModel;
    private JTextArea resultDetailsArea;
    private JTextField resultField;
    private JLabel statusLabel;
    private DarkMode darkMode;
    private LaboratoryManager labManager;
    private FinancePanel financePanel;
    private JTextField testNameField, categoryField, testCodeField, testName;

    public LaboratoryPanel(DarkMode darkMode) {
        this.darkMode = darkMode;
        this.financePanel = financePanel;
        this.labManager = LaboratoryManager.getInstance();
        labManager.setFinancePanel(financePanel);

        darkMode = new DarkMode(); 

        initComponents();
        layoutComponents();
        setupListeners();
        updateColors(darkMode);  // Change this line
    }

    private void initComponents() {
        hospitalIDField = createStyledTextField("Hospital ID");
        patientNameField = createStyledTextField("Patient Name");
        ageField = createStyledTextField("Age");
        doctorNameField = createStyledTextField("Doctor Name");
        sexField = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        verifyButton = createStyledButton("Verify Patient");
        addTestButton = createStyledButton("Add Test");
        updateResultButton = createStyledButton("Update Result");

        testNameField = createStyledTextField("Test Name");
        categoryField = createStyledTextField("Category");
        testCodeField = createStyledTextField("Test Code");

        String[] columnNames = {"Patient ID", "Test Name", "Category", "Test Code", "Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        testsTable = new JTable(tableModel);
        testsTable.setFont(MAIN_FONT);
        testsTable.getTableHeader().setFont(MAIN_FONT);

        resultDetailsArea = new JTextArea(5, 20);
        resultDetailsArea.setEditable(false);
        resultDetailsArea.setFont(MAIN_FONT);

        resultField = createStyledTextField("Enter test result");
        statusLabel = new JLabel("Ready");
    }
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel inputPanel = createInputPanel();
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(testsTable), BorderLayout.CENTER);

        JPanel resultPanel = createResultPanel();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(inputPanel, BorderLayout.WEST);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(resultPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addLabelAndComponent(panel, "Hospital ID:", hospitalIDField, gbc, 0);
        addLabelAndComponent(panel, "Patient Name:", patientNameField, gbc, 1);
        addLabelAndComponent(panel, "Age:", ageField, gbc, 2);
        addLabelAndComponent(panel, "Sex:", sexField, gbc, 3);
        addLabelAndComponent(panel, "Doctor Name:", doctorNameField, gbc, 4);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(verifyButton, gbc);

        gbc.gridy = 6;
        panel.add(addTestButton, gbc);

        return panel;
    }

    private void addLabelAndComponent(JPanel panel, String labelText, JComponent component, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(component, gbc);
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Result:"), BorderLayout.WEST);
        panel.add(resultField, BorderLayout.CENTER);
        panel.add(updateResultButton, BorderLayout.EAST);
        panel.add(new JScrollPane(resultDetailsArea), BorderLayout.SOUTH);
        return panel;
    }

    private class VerifyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String hospitalID = hospitalIDField.getText().trim();
            String patientName = patientNameField.getText().trim();
            String ageText = ageField.getText().trim();
            String sex = (String) sexField.getSelectedItem();

            if (hospitalID.isEmpty() || patientName.isEmpty() || ageText.isEmpty() || sex == null) {
                showMessage("Please fill all fields before verifying.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int age = Integer.parseInt(ageText);
                boolean verified = verifyPatient(hospitalID, patientName, age, sex);
                if (verified) {
                    showMessage("Patient information verified successfully!", "Verification Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showMessage("Patient information could not be verified. Please check the details.", "Verification Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                showMessage("Invalid age format. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean verifyPatient(String hospitalID, String patientName, int age, String sex) {
        String query = "SELECT COUNT(*) AS count FROM patients WHERE hospital_id = ? AND TRIM(CONCAT_WS(' ', first_name, COALESCE(middle_name, ''), last_name)) = ? AND age = ? AND sex = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hospitalID);
            stmt.setString(2, patientName);
            stmt.setInt(3, age);
            stmt.setString(4, sex);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException ex) {
            handleSQLException(ex, "Error verifying patient information");
        }
        return false;
    }

    private void openTestSelectionWindow() {
        TestSelectionPanel dialog = new TestSelectionPanel(SwingUtilities.getWindowAncestor(this), labManager);
        dialog.setVisible(true);
        LaboratoryTest selectedTest = dialog.getSelectedTest();
        if (selectedTest != null) {
            String patientId = hospitalIDField.getText().trim();
            if (!patientId.isEmpty()) {
                try {
                    selectedTest.setPatientId(patientId);
                    addTest(selectedTest);
                } catch (IllegalArgumentException e) {
                    showMessage("Invalid patient ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                showMessage("Please enter a Hospital ID before adding a test.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void addTestToTable(LaboratoryTest test) {
        tableModel.addRow(new Object[]{
            test.getPatientId(),
            test.getTestName(),
            test.getCategory(),
            test.getTestCode(),
            new SimpleDateFormat("yyyy-MM-dd").format(test.getDate()),
            test.getStatus()
        });
    }
    private void updateSelectedTestDetails() {
        int selectedRow = testsTable.getSelectedRow();
        if (selectedRow != -1) {
            String patientId = (String) tableModel.getValueAt(selectedRow, 0);
            String testCode = (String) tableModel.getValueAt(selectedRow, 3);
            LaboratoryTest test = labManager.getTest(patientId, testCode);
            if (test != null) {
                resultDetailsArea.setText(test.toString());
                resultField.setText(test.getResult());
                resultField.setEditable("Pending".equals(test.getStatus()));
                updateResultButton.setEnabled("Pending".equals(test.getStatus()));
            }
        }
    }

    private void setupListeners() {
        verifyButton.addActionListener(new VerifyButtonListener());
        addTestButton.addActionListener(e -> openTestSelectionWindow());
        updateResultButton.addActionListener(e -> updateTestResult());

        hospitalIDField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                fetchPatientInfoByHospitalID(hospitalIDField.getText());
            }
        });

        patientNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                fetchPatientInfoByName(patientNameField.getText());
            }
        });

        testsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedTestDetails();
            }
        });

        FocusListener focusListener = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField textField = (JTextField) e.getSource();
                textField.setText(textField.getText().trim());
            }
        };
    
        hospitalIDField.addFocusListener(focusListener);
        testNameField.addFocusListener(focusListener);
        categoryField.addFocusListener(focusListener);
        testCodeField.addFocusListener(focusListener);
    }

    public void setFinancePanel(FinancePanel financePanel) {
        if (financePanel == null) {
            throw new IllegalArgumentException("FinancePanel cannot be null");
        }
        this.financePanel = financePanel;
    }

    private void addTest(LaboratoryTest test) {
        // Validate the input LaboratoryTest object
        if (test == null || isTestDataInvalid(test)) {
            showMessage("Please fill in all fields", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
    
    
        // Process payment and proceed if successful
        if (!financePanel.processTestPayment(test.getPatientId(), test.getTestCode())) {
            showMessage("Test cannot be added without payment", "Payment Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Add test to the system
        if (labManager.addTest(test.getPatientId(), test.getTestName(), test.getCategory(), test.getTestCode())) {
            showMessage("Test added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            addTestToTable(test);
            updateTestsTable();
            clearTestFields();
        } else {
            showMessage("Failed to add test", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private boolean isTestDataInvalid(LaboratoryTest test) {
        return test.getPatientId().isEmpty() || test.getTestName().isEmpty()
                || test.getCategory().isEmpty() || test.getTestCode().isEmpty();
    }
    
    private void updateTestResult() {
        int selectedRow = testsTable.getSelectedRow();
        if (selectedRow != -5) {
            String patientId = (String) tableModel.getValueAt(selectedRow, 0);
            String testCode = (String) tableModel.getValueAt(selectedRow, 3);
            String result = resultField.getText().trim();
            if (!result.isEmpty()) {
                boolean updated = labManager.updateTestResult(patientId, testCode, result);
                if (updated) {
                    tableModel.setValueAt("Completed", selectedRow, 5);
                    updateSelectedTestDetails();
                    showMessage("Result updated successfully", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showMessage("Failed to update result", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                showMessage("Please enter a result", "Missing Information", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            showMessage("Please select a test to update", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateTestsTable() {
        tableModel.setRowCount(0);
        String patientId = hospitalIDField.getText();
        List<LaboratoryTest> tests = labManager.getTestsByPatient(patientId);
        for (LaboratoryTest test : tests) {
            addTestToTable(test);
        }
    }

    private void clearTestFields() {
        testNameField.setText("");
        categoryField.setText("");
        testCodeField.setText("");
    }


    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(15);
        textField.setFont(MAIN_FONT);
        textField.setToolTipText(placeholder);
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setFocusPainted(false);
        return button;
    }

   private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void handleSQLException(SQLException e, String errorMessage) {
        e.printStackTrace();
        showMessage(errorMessage + ": " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateComponentColors(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(darkMode.getBackgroundColor());
                updateComponentColors((Container) comp);
            } else if (comp instanceof JTextField) {
                updateTextFieldColors((JTextField) comp);
            } else if (comp instanceof JButton) {
                updateButtonColors((JButton) comp);
            } else if (comp instanceof JComboBox) {
                updateComboBoxColors((JComboBox<?>) comp);
            } else if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(darkMode.getTextColor());
            } else if (comp instanceof JTextArea) {
                updateTextAreaColors((JTextArea) comp);
            } else if (comp instanceof JScrollPane) {
                updateScrollPaneColors((JScrollPane) comp);
            }
        }
        updateTableColors();
    }

    private void updateTextFieldColors(JTextField textField) {
        textField.setBackground(darkMode.getInputBackgroundColor());
        textField.setForeground(darkMode.getInputTextColor());
        textField.setCaretColor(darkMode.getInputTextColor());
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(darkMode.getBorderColor()),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
    }

    private void updateButtonColors(JButton button) {
        button.setBackground(darkMode.getButtonBackgroundColor());
        button.setForeground(darkMode.getButtonTextColor());
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(darkMode.getBorderColor()),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void updateComboBoxColors(JComboBox<?> comboBox) {
        comboBox.setBackground(darkMode.getCardBackgroundColor());
        comboBox.setForeground(darkMode.getTextColor());
        comboBox.setBorder(BorderFactory.createLineBorder(darkMode.getBorderColor()));
    }

    private void updateTextAreaColors(JTextArea textArea) {
        textArea.setBackground(darkMode.getCardBackgroundColor());
        textArea.setForeground(darkMode.getTextColor());
        textArea.setCaretColor(darkMode.getTextColor());
        textArea.setBorder(BorderFactory.createLineBorder(darkMode.getBorderColor()));
    }

    private void updateScrollPaneColors(JScrollPane scrollPane) {
        scrollPane.getViewport().setBackground(darkMode.getBackgroundColor());
        scrollPane.setBorder(BorderFactory.createLineBorder(darkMode.getBorderColor()));
    }

    private void updateTableColors() {
        testsTable.setBackground(darkMode.getCardBackgroundColor());
        testsTable.setForeground(darkMode.getTextColor());
        testsTable.setSelectionBackground(darkMode.getButtonBackgroundColor());
        testsTable.setSelectionForeground(darkMode.getButtonTextColor());
        testsTable.setGridColor(darkMode.getBorderColor());

        testsTable.getTableHeader().setBackground(darkMode.getButtonBackgroundColor());
        testsTable.getTableHeader().setForeground(darkMode.getButtonTextColor());
    }

   
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(statusLabel, BorderLayout.WEST);
        return panel;
    }

    
    private void setupTableRenderers() {
        testsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(MAIN_FONT);
                c.setForeground(darkMode != null ? darkMode.getButtonTextColor() : Color.BLACK);
                c.setBackground(isSelected ? darkMode.getButtonBackgroundColor() : darkMode.getCardBackgroundColor());
                return c;
            }
        });

        testsTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(MAIN_FONT);
                label.setForeground(darkMode != null ? darkMode.getButtonTextColor() : Color.BLACK);
                label.setBackground(darkMode != null ? darkMode.getButtonBackgroundColor() : Color.LIGHT_GRAY);
                return label;
            }
        });
    }

     private void fetchPatientInfoByHospitalID(String hospitalID) {
        if (hospitalID == null || hospitalID.trim().isEmpty()) {
            showMessage("Please enter a valid Hospital ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        hospitalID = hospitalID.trim();

        String query = "SELECT TRIM(CONCAT_WS(' ', first_name, COALESCE(middle_name, ''), last_name)) AS full_name, age, sex FROM patients WHERE hospital_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hospitalID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    patientNameField.setText(rs.getString("full_name"));
                    ageField.setText(String.valueOf(rs.getInt("age")));
                    sexField.setSelectedItem(rs.getString("sex"));
                } else {
                    showMessage("No patient found with the provided Hospital ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, "Error fetching patient info by Hospital ID");
        }
    }

    private void fetchPatientInfoByName(String fullName) {
        if (fullName.isEmpty()) return;

        String[] nameParts = fullName.split("\\s+");
        String query = nameParts.length > 2 ?
                "SELECT hospital_id, age, sex FROM patients WHERE first_name = ? AND middle_name = ? AND last_name = ?" :
                "SELECT hospital_id, age, sex FROM patients WHERE first_name = ? AND last_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (nameParts.length == 2) {
                stmt.setString(1, nameParts[0]);
                stmt.setString(2, nameParts[1]);
            } else if (nameParts.length > 2) {
                stmt.setString(1, nameParts[0]);
                stmt.setString(2, nameParts[1]);
                stmt.setString(3, nameParts[nameParts.length - 1]);
            } else {
                showMessage("Please enter a full name (First [Middle] Last)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    hospitalIDField.setText(rs.getString("hospital_id"));
                    ageField.setText(String.valueOf(rs.getInt("age")));
                    sexField.setSelectedItem(rs.getString("sex"));
                } else {
                    showMessage("No patient found with this Name", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, "Error fetching patient info by name");
        }
    }

    public void addTest(String testName, String category, String testCode) {
        String patientId = hospitalIDField.getText();
        if (!patientId.isEmpty() && !testName.isEmpty()) {
            labManager.addTest(patientId, testName, category, testCode);
            addTestToTable(patientId, testName, category, "Pending");
            updateStatus("Test added successfully");
        } else {
            showError("Please fill in all fields");
        }
    }    private void updateResult() {
        int selectedRow = testsTable.getSelectedRow();
        if (selectedRow != -1) {
            String patientId = (String) tableModel.getValueAt(selectedRow, 0);
            String testName = (String) tableModel.getValueAt(selectedRow, 1);
            String result = resultField.getText();
            if (!result.isEmpty()) {
                tableModel.setValueAt("Completed", selectedRow, 4);
                String details = String.format("Hospital ID: %s\nTest Name: %s\nTest Type: %s\nResult: %s\nDate: %s",
                        patientId, testName, tableModel.getValueAt(selectedRow, 2), result,
                        tableModel.getValueAt(selectedRow, 3));
                resultDetailsArea.setText(details);
                clearFields();
                updateStatus("Result updated successfully");
            }
        }
    }

    private void addTestToTable(String patientId, String testName, String testType, String status) {
        Vector<String> row = new Vector<>();
        row.add(patientId);
        row.add(testName);
        row.add(testType);
        row.add(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        row.add(status);
        tableModel.addRow(row);
    }

    private void clearFields() {
        hospitalIDField.setText("");
        patientNameField.setText("");
        ageField.setText("");
        sexField.setSelectedIndex(0);
        doctorNameField.setText("");
        testName.setText("Select Laboratory Test");
        resultField.setText("");
        resultDetailsArea.setText("");
        updateStatus("Fields cleared");
    }

    private void updateResultField() {
        int selectedRow = testsTable.getSelectedRow();
        if (selectedRow != -1) {
            String status = (String) tableModel.getValueAt(selectedRow, 4);
            if ("Completed".equals(status)) {
                resultField.setText("Result already entered");
                resultField.setEditable(false);
            } else {
                resultField.setText("");
                resultField.setEditable(true);
            }
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
        Timer timer = new Timer(3000, e -> statusLabel.setText("Ready"));
        timer.setRepeats(false);
        timer.start();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

  
    private Map<String, Integer> initializeTestPrices() {
        Map<String, Integer> prices = new HashMap<>();
        String query = "SELECT test_name, price FROM tests";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String testName = resultSet.getString("test_name");
                int priceInCents = resultSet.getBigDecimal("price").multiply(new BigDecimal(100)).intValue();
                prices.put(testName, priceInCents);
            }
        } catch (SQLException e) {
            handleSQLException(e, "Error fetching test prices");
        }

        return prices;
    }

    public void updateColors(DarkMode darkMode) {
        this.darkMode = darkMode;
        updateComponentColors(this);
        repaint();
    }
   
}