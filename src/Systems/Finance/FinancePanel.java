package Systems.Finance;

import Systems.Dashboard.DarkMode;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class FinancePanel extends JPanel {
    private Finance financeSystem;
    private DarkMode darkMode;
    private JTextArea outputArea;

    public FinancePanel(Finance financeSystem, DarkMode darkMode) {
        this.financeSystem = financeSystem;
        this.darkMode = darkMode;
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel controlPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        JButton admitPatientButton = new JButton("Admit Patient");
        JButton dischargePatientButton = new JButton("Discharge Patient");
        JButton addServiceButton = new JButton("Add Service");
        JButton generateBillButton = new JButton("Generate Bill");
        JButton processPaymentButton = new JButton("Process Payment");

        controlPanel.add(admitPatientButton);
        controlPanel.add(dischargePatientButton);
        controlPanel.add(addServiceButton);
        controlPanel.add(generateBillButton);
        controlPanel.add(processPaymentButton);

        add(controlPanel, BorderLayout.NORTH);

        outputArea = new JTextArea(20, 50);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        admitPatientButton.addActionListener(e -> admitPatient());
        dischargePatientButton.addActionListener(e -> dischargePatient());
        addServiceButton.addActionListener(e -> addService());
        generateBillButton.addActionListener(e -> generateBill());
        processPaymentButton.addActionListener(e -> processPayment());
    }

    private void admitPatient() {
        String patientId = JOptionPane.showInputDialog("Enter patient ID:");
        String name = JOptionPane.showInputDialog("Enter patient name:");
        financeSystem.admitPatient(patientId, name, LocalDate.now());
        outputArea.append("Patient " + patientId + " admitted.\n");
    }

    private void dischargePatient() {
        String patientId = JOptionPane.showInputDialog("Enter patient ID:");
        financeSystem.dischargePatient(patientId, LocalDate.now());
        outputArea.append("Patient " + patientId + " discharged.\n");
    }

    private void addService() {
        String patientId = JOptionPane.showInputDialog("Enter patient ID:");
        String serviceCode = JOptionPane.showInputDialog("Enter service code (ROOM, DOCTOR, MEDICATION, LAB_TEST, IMAGING):");
        int quantity = Integer.parseInt(JOptionPane.showInputDialog("Enter quantity:"));
        financeSystem.addService(patientId, serviceCode, quantity);
        outputArea.append("Service " + serviceCode + " added for patient " + patientId + ".\n");
    }

    private void generateBill() {
        String patientId = JOptionPane.showInputDialog("Enter patient ID:");
        Finance.BillingSummary summary = financeSystem.generateBillingSummary(patientId);
        if (summary != null) {
            outputArea.append("Billing Summary for patient " + patientId + ":\n");
            for (Finance.BillingItem item : summary.getItems()) {
                outputArea.append(item.getDescription() + ": $" + item.getTotalPrice() + "\n");
            }
            outputArea.append("Total Amount: $" + summary.getTotalAmount() + "\n");
        } else {
            outputArea.append("Patient not found.\n");
        }
    }

    private void processPayment() {
        String patientId = JOptionPane.showInputDialog("Enter patient ID:");
        double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter payment amount:"));
        String paymentMethod = JOptionPane.showInputDialog("Enter payment method:");
        financeSystem.processPayment(patientId, amount, paymentMethod);
        outputArea.append("Payment processed for patient " + patientId + ".\n");
    }

    public void updateColors() {
        setBackground(darkMode.getBackgroundColor());
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(darkMode.getBackgroundColor());
                for (Component innerComp : ((JPanel) comp).getComponents()) {
                    if (innerComp instanceof JButton) {
                        innerComp.setBackground(darkMode.getPrimaryColor());
                        innerComp.setForeground(darkMode.getTextColor());
                    }
                }
            } else if (comp instanceof JScrollPane) {
                comp.setBackground(darkMode.getBackgroundColor());
                JViewport viewport = ((JScrollPane) comp).getViewport();
                viewport.setBackground(darkMode.getBackgroundColor());
                Component[] components = viewport.getComponents();
                for (Component component : components) {
                    if (component instanceof JTextArea) {
                        component.setBackground(darkMode.getCardBackgroundColor());
                        component.setForeground(darkMode.getTextColor());
                    }
                }
            }
        }
    }

    public void refreshData() {
        outputArea.setText("Finance data refreshed.\n");
        // Add any additional refresh logic here
    }
}