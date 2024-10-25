package Systems.Finance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Systems.Dashboard.DarkMode;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

public class FinancePanel extends JPanel {
    private Map<String, Patient> patients;
    private Map<String, Service> services;
    private JTextArea outputArea;
    private JPanel controlPanel;
    private boolean isDarkMode = false;
    private DarkMode darkMode;

    // Color schemes
    private final Color LIGHT_BG = new Color(240, 240, 240);
    private final Color LIGHT_TEXT = new Color(33, 33, 33);
    private final Color LIGHT_BUTTON = new Color(0, 120, 212);
    private final Color LIGHT_BUTTON_TEXT = Color.WHITE;
    private final Color DARK_BG = new Color(33, 33, 33);
    private final Color DARK_TEXT = new Color(240, 240, 240);
    private final Color DARK_BUTTON = new Color(0, 102, 204);
    private final Color DARK_BUTTON_TEXT = Color.WHITE;

    public FinancePanel(DarkMode darkMode) {
        this.darkMode = darkMode;
        this.patients = new HashMap<>();
        this.services = new HashMap<>();
        initializeServices();
        initializeUI();
    }

    public void updateColors(DarkMode darkMode) {
        setBackground(darkMode.getBackgroundColor());
    }

    public void refreshData() {
        // Logic to refresh the data displayed in the panel
    }

    private void initializeServices() {
        services.put("ROOM", new Service("Daily Room Charge", 200.0));
        services.put("DOCTOR", new Service("Doctor's Fee", 150.0));
        services.put("MEDICATION", new Service("Medication", 50.0));
        services.put("LAB_TEST", new Service("Laboratory Test", 75.0));
        services.put("IMAGING", new Service("Imaging", 300.0));
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        controlPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        addButton("Admit Patient", this::admitPatient);
        addButton("Discharge Patient", this::dischargePatient);
        addButton("Add Service", this::addService);
        addButton("Generate Bill", this::generateBill);
        addButton("Process Payment", this::processPayment);
        addButton("View Patient List", this::viewPatientList);
        addButton("Toggle Dark Mode", this::toggleDarkMode);

        mainPanel.add(controlPanel, BorderLayout.NORTH);

        outputArea = new JTextArea(20, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        updateColors();
    }

    private void addButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        controlPanel.add(button);
    }

    private void admitPatient() {
        // Admit patient logic
    }

    private void dischargePatient() {
        // Discharge patient logic
    }

    private void addService() {
        // Add service logic
    }

    private void generateBill() {
        // Generate bill logic
    }

    private void processPayment() {
        // Process payment logic
    }

    private void viewPatientList() {
        // View patient list logic
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        updateColors();
        outputArea.append("Dark mode " + (isDarkMode ? "enabled" : "disabled") + ".\n");
    }

    private void updateColors() {
        Color bgColor = isDarkMode ? DARK_BG : LIGHT_BG;
        Color textColor = isDarkMode ? DARK_TEXT : LIGHT_TEXT;
        Color buttonColor = isDarkMode ? DARK_BUTTON : LIGHT_BUTTON;
        Color buttonTextColor = isDarkMode ? DARK_BUTTON_TEXT : LIGHT_BUTTON_TEXT;

        setBackground(bgColor);
        controlPanel.setBackground(bgColor);
        outputArea.setBackground(bgColor);
        outputArea.setForeground(textColor);
        outputArea.setCaretColor(textColor);

        for (Component comp : controlPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(buttonColor);
                button.setForeground(buttonTextColor);
            }
        }

        SwingUtilities.updateComponentTreeUI(this);
    }

    private BillingSummary generateBillingSummary(Patient patient) {
        List<BillingItem> items = new ArrayList<>();
        double totalAmount = 0.0;

        LocalDate endDate = patient.getDischargeDate() != null ? patient.getDischargeDate() : LocalDate.now();
        long days = Math.max(ChronoUnit.DAYS.between(patient.getAdmissionDate(), endDate), 1);
        Service roomService = services.get("ROOM");
        double roomCharge = roomService.getPrice() * days;
        items.add(new BillingItem("Room Charges", days, roomService.getPrice(), roomCharge));
        totalAmount += roomCharge;

        for (Map.Entry<Service, Integer> entry : patient.getServices().entrySet()) {
            Service service = entry.getKey();
            int quantity = entry.getValue();
            double amount = service.getPrice() * quantity;
            items.add(new BillingItem(service.getName(), quantity, service.getPrice(), amount));
            totalAmount += amount;
        }

        return new BillingSummary(patient, items, totalAmount);
    }

    private static class Patient {
        private String id;
        private String name;
        private LocalDate admissionDate;
        private LocalDate dischargeDate;
        private Map<Service, Integer> services;
        private double totalCharges;
        private double totalPayments;

        public Patient(String id, String name, LocalDate admissionDate) {
            this.id = id;
            this.name = name;
            this.admissionDate = admissionDate;
            this.services = new HashMap<>();
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

    private static class Service {
        private String name;
        private double price;

        public Service(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() { return name; }
        public double getPrice() { return price; }
    }

    private static class BillingItem {
        private String description;
        private long quantity;
        private double unitPrice;
        private double totalPrice;

        public BillingItem(String description, long quantity, double unitPrice, double totalPrice) {
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
        }

        public String getDescription() { return description; }
        public long getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getTotalPrice() { return totalPrice; }
    }

    private static class BillingSummary {
        private Patient patient;
        private List<BillingItem> items;
        private double totalAmount;

        public BillingSummary(Patient patient, List<BillingItem> items, double totalAmount) {
            this.patient = patient;
            this.items = items;
            this.totalAmount = totalAmount;
        }

        public Patient getPatient() { return patient; }
        public List<BillingItem> getItems() { return items; }
        public double getTotalAmount() { return totalAmount; }
    }

}