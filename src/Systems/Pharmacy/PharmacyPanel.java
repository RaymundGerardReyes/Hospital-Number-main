package Systems.Pharmacy;

import Systems.Dashboard.DarkMode;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PharmacyPanel extends JPanel {
    private Pharmacy pharmacy;
    private DarkMode darkMode;
    private JTabbedPane tabbedPane;
    private JPanel posPanel, prescriptionPanel, inventoryPanel;
    private JTextField productIdField, amountField;
    private JComboBox<String> paymentMethodCombo;
    private JTextArea outputArea;
    private JButton processSaleButton, fillPrescriptionButton, checkInventoryButton, generateReportButton;

    public PharmacyPanel(DarkMode darkMode) {
        this.darkMode = darkMode;
        this.pharmacy = new Pharmacy();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        layoutComponents();
        updateColors();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        posPanel = new JPanel(new GridBagLayout());
        prescriptionPanel = new JPanel(new GridBagLayout());
        inventoryPanel = new JPanel(new GridBagLayout());

        productIdField = new JTextField(15);
        amountField = new JTextField(10);
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Insurance"});
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);

        processSaleButton = new JButton("Process Sale");
        fillPrescriptionButton = new JButton("Fill Prescription");
        checkInventoryButton = new JButton("Check Inventory");
        generateReportButton = new JButton("Generate Report");

        processSaleButton.addActionListener(e -> processSale());
        fillPrescriptionButton.addActionListener(e -> fillPrescription());
        checkInventoryButton.addActionListener(e -> checkInventory());
        generateReportButton.addActionListener(e -> generateReport());
    }

    private void layoutComponents() {
        // POS Panel
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

        // Prescription Panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        prescriptionPanel.add(fillPrescriptionButton, gbc);

        // Inventory Panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        inventoryPanel.add(checkInventoryButton, gbc);
        gbc.gridy++;
        inventoryPanel.add(generateReportButton, gbc);

        tabbedPane.addTab("Point of Sale", posPanel);
        tabbedPane.addTab("Prescriptions", prescriptionPanel);
        tabbedPane.addTab("Inventory", inventoryPanel);

        add(tabbedPane, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    private void processSale() {
        try {
            String productId = productIdField.getText();
            String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
            double amount;

            if (productId.isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be empty.");
            }

            try {
                amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    throw new IllegalArgumentException("Amount must be greater than zero.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid amount entered.");
            }

            pharmacy.processSale(productId, paymentMethod, amount);
            outputArea.setText("Sale processed successfully.");
            clearInputFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Process Sale Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillPrescription() {
        try {
            String prescriptionId = JOptionPane.showInputDialog("Enter Prescription ID:");
            if (prescriptionId != null && !prescriptionId.isEmpty()) {
                pharmacy.managePrescription(prescriptionId, "fill");
                outputArea.setText("Prescription " + prescriptionId + " filled successfully.");
            } else if (prescriptionId != null) {
                throw new IllegalArgumentException("Prescription ID cannot be empty.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Fill Prescription Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkInventory() {
        try {
            String productId = JOptionPane.showInputDialog("Enter Product ID:");
            
            if (productId != null && !productId.isEmpty()) {
                if (pharmacy == null) {
                    throw new IllegalStateException("Pharmacy object is not initialized.");
                }

                int stockLevel = pharmacy.manageInventory(productId, "check");
                if (stockLevel < 0) {
                    throw new IllegalArgumentException("Invalid product ID entered.");
                } else {
                    outputArea.setText("Stock level for product " + productId + ": " + stockLevel);
                }
            } else if (productId != null) {
                throw new IllegalArgumentException("Product ID cannot be empty.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Check Inventory Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Report Error", JOptionPane.ERROR_MESSAGE);
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
            } else if (comp instanceof JTextField) {
                comp.setBackground(darkMode.getCardBackgroundColor());
                comp.setForeground(darkMode.getTextColor());
            } else if (comp instanceof JComboBox) {
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
        // Refresh data from the Pharmacy system
        clearInputFields();
        outputArea.setText("Pharmacy data refreshed.");
        // TODO: Implement actual data refresh logic
    }

    private void clearInputFields() {
        productIdField.setText("");
        amountField.setText("");
        paymentMethodCombo.setSelectedIndex(0);
    }
}