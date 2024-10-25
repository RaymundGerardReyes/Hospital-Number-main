package Systems.PatientManagement;

import Systems.Dashboard.DarkMode;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class PatientInfoPanel extends JPanel {
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton editButton, deleteButton, saveButton, cancelButton;
    private JComboBox<String> sortComboBox;
    private List<Patient> patients;
    private Patient editingPatient;
    private int editingRow = -1;
    private DarkMode darkMode;

    private JScrollPane tableScrollPane;
    private JPanel buttonPanel;

    private static final String[] COLUMN_NAMES = {
        "Name", "Age", "Birthday", "Sex", "Contact", "Email", "Street Address", "City", "State", "ZIP Code",
        "Emergency Contact Name", "Emergency Contact Relationship", "Emergency Contact Phone",
        "Insurance Provider", "Policy Number", "Additional Notes", "Hospital ID"
    };

    // PatientInfoPanel.java - Constructor Initialization
public PatientInfoPanel(DarkMode darkMode) {
    this.darkMode = darkMode;
    this.patients = new ArrayList<>(); // Initialize patient list
    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(20, 20, 20, 20));

    initComponents();     // Step 1: Initialize components
    layoutComponents();   // Step 2: Set up layout
    addListeners();       // Step 3: Add event listeners
    loadDummyData();      // Optional: Load dummy data for testing
    updateColors();       // Apply dark mode styling
}


    private void initComponents() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        searchField = new JTextField(20);
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        sortComboBox = new JComboBox<>(COLUMN_NAMES);

        saveButton.setVisible(false);
        cancelButton.setVisible(false);
    }

   // PatientInfoPanel.java - Example layoutComponents Method
private void layoutComponents() {
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topPanel.add(new JLabel("Search: "));
    topPanel.add(searchField);
    topPanel.add(new JLabel("Sort by: "));
    topPanel.add(sortComboBox);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    JScrollPane tableScrollPane = new JScrollPane(patientTable);

    add(topPanel, BorderLayout.NORTH);     // Top panel with search and sort
    add(tableScrollPane, BorderLayout.CENTER); // Center with patient table
    add(buttonPanel, BorderLayout.SOUTH);  // Bottom panel with CRUD buttons
}

    // Dashboard.java - Corrected Action Listener for Switching Panels
    // Dashboard.java - Corrected Action Listener for Switching Panels
    private ActionListener createActionListener(String panelName) {
    return e -> {
        System.out.println("Button clicked for panel: " + panelName); // Debug info
        showPanel(panelName.toLowerCase().replace(" ", ""));
    };
    }


    private void showPanel(String panelName) {
        // Implement the logic to switch between different panels
        // You can use a switch-case statement or any other method to handle the panel switching
        // For example, you can have a HashMap or a list of Panel objects and use the panelName as the key
        // Then, you can retrieve the corresponding Panel object and set it as the visible component of the container
        // Or, you can use a CardLayout and add the Panel objects to the CardLayout and then show the desired Panel
    
        // Example implementation using a HashMap
        Map<String, JPanel> panelMap = new HashMap<>();
        // Add Panel objects to the panelMap with their corresponding panel names
        // For example:
        // panelMap.put("dashboard", dashboardPanel);
        // panelMap.put("patientinfo", patientInfoPanel);
        // panelMap.put("settings", settingsPanel);
    
        // Retrieve the corresponding Panel object from the panelMap using the panelName
        JPanel panel = panelMap.get(panelName);
    
        // Set the panel as the visible component of the container
        // For example, if you are using a CardLayout:
        // cardLayout.showCard(container, panelName);
        // Or, if you are using a JFrame:
        // container.setContentPane(panel);
        // container.revalidate();
    }

    // PatientInfoPanel.java - Corrected addListeners Method
private void addListeners() {
    // Search field listener
    searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
        public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
    });

    // Sort combo box listener
    sortComboBox.addActionListener(e -> sortTable());

    // CRUD buttons listeners
    editButton.addActionListener(e -> editSelectedPatient());
    deleteButton.addActionListener(e -> deleteSelectedPatient());
    saveButton.addActionListener(e -> saveEditedPatient());
    cancelButton.addActionListener(e -> cancelEdit());

    // Test button to confirm PatientInfoPanel displays
    JButton testButton = new JButton("Test Output");
    testButton.addActionListener(e -> {
        System.out.println("Test button clicked - output should display."); // Debug
        JOptionPane.showMessageDialog(this, "PatientInfoPanel is displayed correctly.");
    });
    add(testButton, BorderLayout.SOUTH); // Temporarily add for debugging
}


    private void search() {
        String searchTerm = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        for (Patient patient : patients) {
            if (patientMatchesSearch(patient, searchTerm)) {
                addPatientToTable(patient);
            }
        }
    }

    private boolean patientMatchesSearch(Patient patient, String searchTerm) {
        return patient.getName().toLowerCase().contains(searchTerm) ||
               patient.getContact().toLowerCase().contains(searchTerm) ||
               patient.getEmail().toLowerCase().contains(searchTerm) ||
               patient.getHospitalId().toLowerCase().contains(searchTerm);
    }

    private void sortTable() {
        int columnIndex = sortComboBox.getSelectedIndex();
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        patientTable.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(columnIndex, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    private void editSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow != -1) {
            editingRow = selectedRow;
            editingPatient = patients.get(selectedRow);
            enableEditMode(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a patient to edit.");
        }
    }

    private void deleteSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this patient?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                patients.remove(selectedRow);
                tableModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.");
        }
    }

    private void saveEditedPatient() {
        if (editingPatient != null && editingRow != -1) {
            updatePatientFromTable(editingPatient, editingRow);
            patients.set(editingRow, editingPatient);
            enableEditMode(false);
            editingPatient = null;
            editingRow = -1;
            search(); // Refresh the table
        }
    }

    private void cancelEdit() {
        enableEditMode(false);
        editingPatient = null;
        editingRow = -1;
        search(); // Refresh the table
    }

    private void enableEditMode(boolean enable) {
        patientTable.setEnabled(!enable);
        editButton.setVisible(!enable);
        deleteButton.setVisible(!enable);
        saveButton.setVisible(enable);
        cancelButton.setVisible(enable);
        if (enable) {
            for (int i = 0; i < COLUMN_NAMES.length; i++) {
                patientTable.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(new JTextField()));
            }
        } else {
            for (int i = 0; i < COLUMN_NAMES.length; i++) {
                patientTable.getColumnModel().getColumn(i).setCellEditor(null);
            }
        }
    }

    private void updatePatientFromTable(Patient patient, int row) {
        patient.setName((String) patientTable.getValueAt(row, 0));
        patient.setAge(Integer.parseInt((String) patientTable.getValueAt(row, 1)));
        patient.setBirthday((String) patientTable.getValueAt(row, 2));
        patient.setSex((String) patientTable.getValueAt(row, 3));
        patient.setContact((String) patientTable.getValueAt(row, 4));
        patient.setEmail((String) patientTable.getValueAt(row, 5));
        patient.setStreetAddress((String) patientTable.getValueAt(row, 6));
        patient.setCity((String) patientTable.getValueAt(row, 7));
        patient.setState((String) patientTable.getValueAt(row, 8));
        patient.setZipCode((String) patientTable.getValueAt(row, 9));
        patient.setEmergencyContactName((String) patientTable.getValueAt(row, 10));
        patient.setEmergencyContactRelationship((String) patientTable.getValueAt(row, 11));
        patient.setEmergencyContactPhone((String) patientTable.getValueAt(row, 12));
        patient.setInsuranceProvider((String) patientTable.getValueAt(row, 13));
        patient.setPolicyNumber((String) patientTable.getValueAt(row, 14));
        patient.setAdditionalNotes((String) patientTable.getValueAt(row, 15));
        patient.setHospitalId((String) patientTable.getValueAt(row, 16));
    }

    private void addPatientToTable(Patient patient) {
        tableModel.addRow(new Object[]{
            patient.getName(), patient.getAge(), patient.getBirthday(), patient.getSex(),
            patient.getContact(), patient.getEmail(), patient.getStreetAddress(), patient.getCity(),
            patient.getState(), patient.getZipCode(), patient.getEmergencyContactName(),
            patient.getEmergencyContactRelationship(), patient.getEmergencyContactPhone(),
            patient.getInsuranceProvider(), patient.getPolicyNumber(), patient.getAdditionalNotes(),
            patient.getHospitalId()
        });
    }

    private void loadDummyData() {
        patients.add(new Patient("John Doe", 30, "1990-05-15", "Male", "123-456-7890", "john@example.com",
                "123 Main St", "Anytown", "CA", "12345", "Jane Doe", "Spouse", "987-654-3210",
                "Health Inc", "H12345", "No allergies", "PAT001"));
        patients.add(new Patient("Jane Smith", 25, "1995-08-20", "Female", "098-765-4321", "jane@example.com",
                "456 Elm St", "Somewhere", "NY", "67890", "John Smith", "Brother", "123-789-4560",
                "Care Co", "C67890", "Allergic to penicillin", "PAT002"));
        search(); // Populate the table
    }

    public void updateColors() {
        setBackground(darkMode.getBackgroundColor());
        patientTable.setBackground(darkMode.getCardBackgroundColor());
        patientTable.setForeground(darkMode.getTextColor());
        patientTable.getTableHeader().setBackground(darkMode.getCardBackgroundColor());
        patientTable.getTableHeader().setForeground(darkMode.getTextColor());

        searchField.setBackground(darkMode.getCardBackgroundColor());
        searchField.setForeground(darkMode.getTextColor());
        searchField.setCaretColor(darkMode.getTextColor());

        sortComboBox.setBackground(darkMode.getCardBackgroundColor());
        sortComboBox.setForeground(darkMode.getTextColor());

        updateButtonColors(editButton);
        updateButtonColors(deleteButton);
        updateButtonColors(saveButton);
        updateButtonColors(cancelButton);

        repaint();
    }

    private void updateButtonColors(JButton button) {
        button.setBackground(darkMode.getPrimaryColor());
        button.setForeground(darkMode.getTextColor());
        button.setFocusPainted(false);
    }

    // Inner class to represent a Patient
    private static class Patient {
        private String name, birthday, sex, contact, email, streetAddress, city, state, zipCode;
        private String emergencyContactName, emergencyContactRelationship, emergencyContactPhone;
        private String insuranceProvider, policyNumber, additionalNotes, hospitalId;
        private int age;

        public Patient(String name, int age, String birthday, String sex, String contact, String email,
                       String streetAddress, String city, String state, String zipCode,
                       String emergencyContactName, String emergencyContactRelationship, String emergencyContactPhone,
                       String insuranceProvider, String policyNumber, String additionalNotes, String hospitalId) {
            this.name = name;
            this.age = age;
            this.birthday = birthday;
            this.sex = sex;
            this.contact = contact;
            this.email = email;
            this.streetAddress = streetAddress;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
            this.emergencyContactName = emergencyContactName;
            this.emergencyContactRelationship = emergencyContactRelationship;
            this.emergencyContactPhone = emergencyContactPhone;
            this.insuranceProvider = insuranceProvider;
            this.policyNumber = policyNumber;
            this.additionalNotes = additionalNotes;
            this.hospitalId = hospitalId;
        }

        // Getters and setters for all fields
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getBirthday() { return birthday; }
        public void setBirthday(String birthday) { this.birthday = birthday; }
        public String getSex() { return sex; }
        public void setSex(String sex) { this.sex = sex; }
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getStreetAddress() { return streetAddress; }
        public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        public String getEmergencyContactName() { return emergencyContactName; }
        public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
        public String getEmergencyContactRelationship() { return emergencyContactRelationship; }
        public void setEmergencyContactRelationship(String emergencyContactRelationship) { this.emergencyContactRelationship = emergencyContactRelationship; }
        public String getEmergencyContactPhone() { return emergencyContactPhone; }
        public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
        public String getInsuranceProvider() { return insuranceProvider; }
        public void setInsuranceProvider(String insuranceProvider) { this.insuranceProvider = insuranceProvider; }
        public String getPolicyNumber() { return policyNumber; }
        public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
        public String getAdditionalNotes() { return additionalNotes; }
        public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = 

 additionalNotes; }
        public String getHospitalId() { return hospitalId; }
        public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }
    }
}