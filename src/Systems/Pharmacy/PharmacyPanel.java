package Systems.Pharmacy;

import Systems.Dashboard.DarkMode;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PharmacyPanel extends JPanel {
    private final Pharmacy pharmacy;
    private final DarkMode darkMode;
    private final JTabbedPane tabbedPane;
    private final JPanel posPanel, prescriptionPanel, inventoryPanel;
    private final JTextField productIdField, amountField;
    private final JComboBox<String> paymentMethodCombo;
    private final JTextArea outputArea;
    private final JButton processSaleButton, fillPrescriptionButton, checkInventoryButton, generateReportButton;

    public PharmacyPanel(DarkMode darkMode) {
        this.darkMode = darkMode;
        this.pharmacy = new Pharmacy();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        tabbedPane = new JTabbedPane();
        posPanel = createPOSPanel();
        prescriptionPanel = createPrescriptionPanel();
        inventoryPanel = createInventoryPanel();

        productIdField = new JTextField(15);
        amountField = new JTextField(10);
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Insurance"});
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);

        processSaleButton = createButton("Process Sale", this::processSale);
        fillPrescriptionButton = createButton("Fill Prescription", this::fillPrescription);
        checkInventoryButton = createButton("Check Inventory", this::checkInventory);
        generateReportButton = createButton("Generate Report", this::generateReport);

        layoutComponents();
        updateColors();
    }

    private JPanel createPOSPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        // Add components to panel
        return panel;
    }

    private JPanel createPrescriptionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        // Add components to panel
        return panel;
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        // Add components to panel
        return panel;
    }

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        return button;
    }

    private void layoutComponents() {
        layoutPOSPanel();
        layoutPrescriptionPanel();
        layoutInventoryPanel();

        tabbedPane.addTab("Point of Sale", posPanel);
        tabbedPane.addTab("Prescriptions", prescriptionPanel);
        tabbedPane.addTab("Inventory", inventoryPanel);

        add(tabbedPane, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    private void layoutPOSPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        posPanel.add(new JLabel("Product ID:"), gbc);
        gbc.gridx++;
        posPanel.add(productIdField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        posPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx++;
        posPanel.add(amountField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        posPanel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx++;
        posPanel.add(paymentMethodCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        posPanel.add(processSaleButton, gbc);
    }

    private void layoutPrescriptionPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        prescriptionPanel.add(fillPrescriptionButton, gbc);
    }

    private void layoutInventoryPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        inventoryPanel.add(checkInventoryButton, gbc);
        gbc.gridy++;
        inventoryPanel.add(generateReportButton, gbc);
    }

    private void processSale() {
        try {
            String productId = productIdField.getText();
            String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());

            validateInput(productId, amount);

            pharmacy.processSale(productId, paymentMethod, amount);
            outputArea.setText("Sale processed successfully.");
            clearInputFields();
        } catch (Exception e) {
            showErrorMessage("Process Sale Error", e.getMessage());
        }
    }

    private void fillPrescription() {
        String prescriptionId = JOptionPane.showInputDialog("Enter Prescription ID:");
        try {
            validateInput(prescriptionId);
            pharmacy.managePrescription(prescriptionId, "fill");
            outputArea.setText("Prescription " + prescriptionId + " filled successfully.");
        } catch (Exception e) {
            showErrorMessage("Fill Prescription Error", e.getMessage());
        }
    }

    private void checkInventory() {
        String productId = JOptionPane.showInputDialog("Enter Product ID:");
        try {
            validateInput(productId);
            int stockLevel = pharmacy.manageInventory(productId, "check", 0);
            outputArea.setText("Stock level for product " + productId + ": " + stockLevel);
        } catch (Exception e) {
            showErrorMessage("Check Inventory Error", e.getMessage());
        }
    }

    private void generateReport() {
        try {
            String[] options = {"Sales", "Inventory", "Prescription Trends"};
            int choice = JOptionPane.showOptionDialog(this, "Select report type:", "Generate Report",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (choice != JOptionPane.CLOSED_OPTION) {
                String reportType = options[choice].toLowerCase().replace(" ", "");
                String report = pharmacy.generateReport(reportType, LocalDate.now().minusMonths(1), LocalDate.now());
                outputArea.setText(report);
            }
        } catch (Exception e) {
            showErrorMessage("Report Error", "Error generating report: " + e.getMessage());
        }
    }

    private void validateInput(String... inputs) {
        for (String input : inputs) {
            if (input == null || input.isEmpty()) {
                throw new IllegalArgumentException("Input cannot be empty.");
            }
        }
    }

    private void validateInput(String input, double amount) {
        validateInput(input);
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    public void updateColors() {
        setBackground(darkMode.getBackgroundColor());
        tabbedPane.setBackground(darkMode.getBackgroundColor());
        tabbedPane.setForeground(darkMode.getTextColor());

        updatePanelColors(posPanel);
        updatePanelColors(prescriptionPanel);
        updatePanelColors(inventoryPanel);

        outputArea.setBackground(darkMode.getCardBackgroundColor());
        outputArea.setForeground(darkMode.getTextColor());

        updateButtonColors(processSaleButton);
        updateButtonColors(fillPrescriptionButton);
        updateButtonColors(checkInventoryButton);
        updateButtonColors(generateReportButton);
    }

    private void updatePanelColors(JPanel panel) {
        panel.setBackground(darkMode.getBackgroundColor());
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(darkMode.getTextColor());
            } else if (comp instanceof JTextField || comp instanceof JComboBox) {
                comp.setBackground(darkMode.getCardBackgroundColor());
                comp.setForeground(darkMode.getTextColor());
            }
        }
    }

    private void updateButtonColors(JButton button) {
        button.setBackground(darkMode.getPrimaryColor());
        button.setForeground(darkMode.getTextColor());
    }

    public void refreshData() {
        clearInputFields();
        outputArea.setText("Pharmacy data refreshed.");
        // TODO: Implement actual data refresh logic
    }

    private void clearInputFields() {
        productIdField.setText("");
        amountField.setText("");
        paymentMethodCombo.setSelectedIndex(0);
    }

    private void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, "Error: " + message, title, JOptionPane.ERROR_MESSAGE);
    }
}