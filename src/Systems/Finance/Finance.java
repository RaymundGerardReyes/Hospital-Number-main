package Systems.Finance;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Finance {
    private Map<String, Patient> patients;
    private Map<String, Service> services;

    public Finance() {
        this.patients = new HashMap<>();
        this.services = new HashMap<>();
        initializeServices();
    }

    private void initializeServices() {
        services.put("ROOM", new Service("Daily Room Charge", 200.0));
        services.put("DOCTOR", new Service("Doctor's Fee", 150.0));
        services.put("MEDICATION", new Service("Medication", 50.0));
        services.put("LAB_TEST", new Service("Laboratory Test", 75.0));
        services.put("IMAGING", new Service("Imaging", 300.0));
    }

    public void admitPatient(String patientId, String name, LocalDate admissionDate) {
        patients.put(patientId, new Patient(patientId, name, admissionDate));
    }

    public void dischargePatient(String patientId, LocalDate dischargeDate) {
        Patient patient = patients.get(patientId);
        if (patient != null) {
            patient.setDischargeDate(dischargeDate);
        }
    }

    public void addService(String patientId, String serviceCode, int quantity) {
        Patient patient = patients.get(patientId);
        Service service = services.get(serviceCode);
        if (patient != null && service != null) {
            patient.addService(service, quantity);
        }
    }

    public BillingSummary generateBillingSummary(String patientId) {
        Patient patient = patients.get(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient with ID " + patientId + " not found.");
        }
    
        List<BillingItem> items = new ArrayList<>();
        double totalAmount = 0.0;
    
        // Add room charges
        LocalDate endDate = patient.getDischargeDate() != null ? patient.getDischargeDate() : LocalDate.now();
        long days = Math.max(ChronoUnit.DAYS.between(patient.getAdmissionDate(), endDate), 1); // Ensure at least 1 day
        Service roomService = services.get("ROOM");
        if (roomService == null) {
            throw new IllegalStateException("Room service not found in the services list.");
        }
        double roomCharge = roomService.getPrice() * days;
        items.add(new BillingItem("Room Charges", (int) days, roomService.getPrice(), roomCharge));
        totalAmount += roomCharge;
    
        // Add other services
        for (Map.Entry<Service, Integer> entry : patient.getServices().entrySet()) {
            Service service = entry.getKey();
            int quantity = entry.getValue();
            double amount = service.getPrice() * quantity;
            items.add(new BillingItem(service.getName(), quantity, service.getPrice(), amount));
            totalAmount += amount;
        }
    
        return new BillingSummary(patient, items, totalAmount);
    }


    public void processPayment(String patientId, double amount, String paymentMethod) {
        Patient patient = patients.get(patientId);
        if (patient != null) {
            patient.addPayment(amount);
            patient.setPaymentStatus(patient.getTotalPayments() >= patient.getTotalCharges() ? "Paid" : "Pending");
            // In a real system, you would integrate with a payment gateway here
            System.out.println("Payment of $" + amount + " processed for patient " + patientId + " via " + paymentMethod);
        }
    }

    // Inner classes

    private static class Patient {
        private String id;
        private String name;
        private LocalDate admissionDate;
        private LocalDate dischargeDate;
        private Map<Service, Integer> services;
        private double totalCharges;
        private double totalPayments;
        private String paymentStatus;

        public Patient(String id, String name, LocalDate admissionDate) {
            this.id = id;
            this.name = name;
            this.admissionDate = admissionDate;
            this.services = new HashMap<>();
            this.paymentStatus = "Pending";
        }

        public void addService(Service service, int quantity) {
            services.merge(service, quantity, Integer::sum);
            totalCharges += service.getPrice() * quantity;
        }

        public void addPayment(double amount) {
            totalPayments += amount;
        }

        // Getters and setters
        public String getId() { return id; }
        public String getName() { return name; }
        public LocalDate getAdmissionDate() { return admissionDate; }
        public LocalDate getDischargeDate() { return dischargeDate; }
        public void setDischargeDate(LocalDate dischargeDate) { this.dischargeDate = dischargeDate; }
        public Map<Service, Integer> getServices() { return services; }
        public double getTotalCharges() { return totalCharges; }
        public double getTotalPayments() { return totalPayments; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
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

    public static class BillingItem {
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

    // Getters
    public String getDescription() { return description; }
    public long getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return totalPrice; }
}

    public static class BillingSummary {
        private Patient patient;
        private List<BillingItem> items;
        private double totalAmount;

        public BillingSummary(Patient patient, List<BillingItem> items, double totalAmount) {
            this.patient = patient;
            this.items = items;
            this.totalAmount = totalAmount;
        }

        // Getters
        public Patient getPatient() { return patient; }
        public List<BillingItem> getItems() { return items; }
        public double getTotalAmount() { return totalAmount; }
    }
}