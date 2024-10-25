package Systems.Laboratory;

import Systems.Dashboard.DarkMode;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Laboratory extends JPanel {
    private JTable testsTable;
    private DefaultTableModel tableModel;
    private JTextField patientIdField, testNameField, resultField;
    private JComboBox<String> testTypeComboBox;
    private JButton addTestButton, updateResultButton, clearFieldsButton, generateReportButton;
    private JTextArea resultDetailsArea;
    private DarkMode darkMode;
    private JLabel statusLabel;

    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);

    public Laboratory(DarkMode darkMode) {
        this.darkMode = darkMode;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initComponents();
        layoutComponents();
        updateColors();
    }

    private void initComponents() {
       // Initialize table
    String[] columnNames = {"Patient ID", "Test Name", "Test Type", "Date", "Status"};
    tableModel = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Make the table non-editable
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return String.class; // Set the column class to String
        }
    };
        testsTable = new JTable(tableModel);
        testsTable.setFont(MAIN_FONT);
        testsTable.getTableHeader().setFont(MAIN_FONT);
        testsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        testsTable.getSelectionModel().addListSelectionListener(e -> updateResultField());

     // Set the table header foreground color to light white
     testsTable.getTableHeader().setForeground(Color.decode("#FFFFFF"));


        // Initialize input fields
        patientIdField = createStyledTextField();
        testNameField = createStyledTextField();
        resultField = createStyledTextField();

        // Initialize test type combo box
        String[] testTypes = {"Blood Test", "Urine Test", "X-Ray", "MRI", "CT Scan", "Other"};
        testTypeComboBox = new JComboBox<>(testTypes);
        testTypeComboBox.setFont(MAIN_FONT);

        // Initialize buttons
        addTestButton = createStyledButton("Add Test");
        updateResultButton = createStyledButton("Update Result");
        clearFieldsButton = createStyledButton("Clear Fields");
        generateReportButton = createStyledButton("Generate Report");

        // Initialize result details area
        resultDetailsArea = new JTextArea(5, 20);
        resultDetailsArea.setFont(MAIN_FONT);
        resultDetailsArea.setEditable(false);

        // Initialize status label
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(MAIN_FONT);

        // Add action listeners
        addTestButton.addActionListener(e -> addTest());
        updateResultButton.addActionListener(e -> updateResult());
        clearFieldsButton.addActionListener(e -> clearFields());
        generateReportButton.addActionListener(e -> generateReport());
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Laboratory Management", JLabel.CENTER);
        headerLabel.setFont(HEADER_FONT);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Left Panel (Add Test Form)
        JPanel leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Center Panel (Tests Table)
        JScrollPane tableScrollPane = new JScrollPane(testsTable);
        tableScrollPane.setBorder(createStyledTitledBorder("Laboratory Tests"));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Right Panel (Results)
        JPanel rightPanel = createRightPanel();
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        // Status Bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(createStyledTitledBorder("Add New Test"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        addLabelAndField(leftPanel, gbc, "Hospital ID:", patientIdField);
        addLabelAndField(leftPanel, gbc, "Test Name:", testNameField);
        addLabelAndField(leftPanel, gbc, "Test Type:", testTypeComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addTestButton);
        buttonPanel.add(clearFieldsButton);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        leftPanel.add(buttonPanel, gbc);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(createStyledTitledBorder("Test Results"));
        
        JPanel resultInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resultInputPanel.add(new JLabel("Result:"));
        resultInputPanel.add(resultField);
        resultInputPanel.add(updateResultButton);
        
        rightPanel.add(resultInputPanel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(resultDetailsArea), BorderLayout.CENTER);
        rightPanel.add(generateReportButton, BorderLayout.SOUTH);

        return rightPanel;
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(MAIN_FONT);
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setFocusPainted(false);
        return button;
    }

    private TitledBorder createStyledTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(darkMode.getBorderColor()),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            TITLE_FONT,
            darkMode.getTitleTextColor()
        );
    }

    private void addTest() {
        String patientId = patientIdField.getText();
        String testName = testNameField.getText();
        String testType = (String) testTypeComboBox.getSelectedItem();
        if (!patientId.isEmpty() && !testName.isEmpty()) {
            Vector<String> row = new Vector<>();
            row.add(patientId);
            row.add(testName);
            row.add(testType);
            row.add(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            row.add("Pending");
            tableModel.addRow(row);
            clearFields();
            updateStatus("Test added successfully");
        } else {
            showError("Please fill in all fields");
        }
    }

    private void updateResult() {
        int selectedRow = testsTable.getSelectedRow();
        if (selectedRow != -1) {
            String patientId = (String) tableModel.getValueAt(selectedRow, 0);
            String testName = (String) tableModel.getValueAt(selectedRow, 1);
            String testType = (String) tableModel.getValueAt(selectedRow, 2);
            String result = resultField.getText();
            if (!result.isEmpty()) {
                tableModel.setValueAt("Completed", selectedRow, 4);
                String details = String.format("Patient ID: %s\nTest Name: %s\nTest Type: %s\nResult: %s\nDate: %s",
                        patientId, testName, testType, result, tableModel.getValueAt(selectedRow, 3));
                resultDetailsArea.setText(details);
                resultField.setText("");
                updateStatus("Result updated successfully");
            } else {
                showError("Please enter a result");
            }
        } else {
            showError("Please select a test from the table");
        }
    }

    private void clearFields() {
        patientIdField.setText("");
        testNameField.setText("");
        testTypeComboBox.setSelectedIndex(0);
        resultField.setText("");
        resultDetailsArea.setText("");
        updateStatus("Fields cleared");
    }

    private void generateReport() {
        StringBuilder report = new StringBuilder("Laboratory Test Report\n\n");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            report.append(String.format("Patient ID: %s\nTest Name: %s\nTest Type: %s\nDate: %s\nStatus: %s\n\n",
                    tableModel.getValueAt(i, 0),
                    tableModel.getValueAt(i, 1),
                    tableModel.getValueAt(i, 2),
                    tableModel.getValueAt(i, 3),
                    tableModel.getValueAt(i, 4)));
        }
        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Laboratory Test Report", JOptionPane.INFORMATION_MESSAGE);
        updateStatus("Report generated");
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

    public void updateColors() {
        setBackground(darkMode.getBackgroundColor());
        
        // Update table colors
        testsTable.setBackground(darkMode.getCardBackgroundColor());
        testsTable.setForeground(darkMode.getTextColor());
        testsTable.getTableHeader().setBackground(darkMode.getPrimaryColor());
        testsTable.getTableHeader().setForeground(darkMode.getTextColor());
        testsTable.setGridColor(darkMode.getBorderColor());
        testsTable.setSelectionBackground(darkMode.getPrimaryColor().brighter());
        testsTable.setSelectionForeground(darkMode.getTextColor());

        // Update text field colors
        updateTextFieldColors(patientIdField);
        updateTextFieldColors(testNameField);
        updateTextFieldColors(resultField);

        // Update combo box colors
        testTypeComboBox.setBackground(darkMode.getCardBackgroundColor());
        testTypeComboBox.setForeground(darkMode.getTextColor());

        // Update button colors
        updateButtonColors(addTestButton);
        updateButtonColors(updateResultButton);
        updateButtonColors(clearFieldsButton);
        updateButtonColors(generateReportButton);

        // Update text area colors
        resultDetailsArea.setBackground(darkMode.getCardBackgroundColor());
        resultDetailsArea.setForeground(darkMode.getTextColor());
        resultDetailsArea.setCaretColor(darkMode.getTextColor());

        // Update status label colors
        statusLabel.setForeground(darkMode.getTextColor());

        // Update panel colors
        updatePanelColors(this);

        // Update titled borders
        updateTitledBorders();

        repaint();
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

    private void updatePanelColors(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(darkMode.getBackgroundColor());
                updatePanelColors((Container) comp);
            } else if (comp instanceof JLabel) {
                comp.setForeground(darkMode.getTextColor());
            } else if (comp instanceof JScrollPane) {
                comp.setBackground(darkMode.getBackgroundColor());
                JViewport viewport = ((JScrollPane) comp).getViewport();
                viewport.setBackground(darkMode.getBackgroundColor());
                Component[] components = viewport.getComponents();
                for (Component component : components) {
                    component.setBackground(darkMode.getBackgroundColor());
                    if (component instanceof JTextComponent) {
                        ((JTextComponent) component).setForeground(darkMode.getTextColor());
                        ((JTextComponent) component).setCaretColor(darkMode.getTextColor());
                    }
                }
            }
        }
    }

    private void  updateTitledBorders() {
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                Border border = ((JPanel) comp).getBorder();
                if (border instanceof TitledBorder) {
                    TitledBorder titledBorder = (TitledBorder) border;
                    titledBorder.setTitleColor(darkMode.getTitleTextColor());
                    titledBorder.setBorder(BorderFactory.createLineBorder(darkMode.getBorderColor()));
                }
            }
        }
    }
}