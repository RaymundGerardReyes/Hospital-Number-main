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
    private JButton addButton, editButton, deleteButton, saveButton, cancelButton;
    private JComboBox<String> sortComboBox;
    private List<Patient> patients;
    private Patient editingPatient;
    private int editingRow = -1;
    private DarkMode darkMode;

    private static final String[] COLUMN_NAMES = {
        "Name", "Age", "Birthday", "Sex", "Contact", "Email", "Street Address", "City", "State", "ZIP Code",
        "Emergency Contact Name", "Emergency Contact Relationship", "Emergency Contact Phone",
        "Insurance Provider", "Policy Number", "Additional Notes", "Hospital ID"
    };

    public PatientInfoPanel(DarkMode darkMode) {
        this.darkMode = darkMode;
        this.patients = new ArrayList<>();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setPreferredSize(new Dimension(800, 600));

        initComponents();
        layoutComponents();
        addListeners();
        loadDummyData();
        updateColors();

        // Add this line to ensure the panel is visible
        setVisible(true);
        System.out.println("PatientInfoPanel constructed");
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editingRow == row;
            }
        };
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        searchField = new JTextField(20);
        addButton = new JButton("Add Patient");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        sortComboBox = new JComboBox<>(COLUMN_NAMES);

        saveButton.setVisible(false);
        cancelButton.setVisible(false);
    }

    private void layoutComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search: "));
        topPanel.add(searchField);
        topPanel.add(new JLabel("Sort by: "));
        topPanel.add(sortComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        JScrollPane tableScrollPane = new JScrollPane(patientTable);

        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });

        sortComboBox.addActionListener(e -> sortTable());

        addButton.addActionListener(e -> addNewPatient());
        editButton.addActionListener(e -> editSelectedPatient());
        deleteButton.addActionListener(e -> deleteSelectedPatient());
        saveButton.addActionListener(e -> saveEditedPatient());
        cancelButton.addActionListener(e -> cancelEdit());

        // Add this listener to log when the panel becomes visible
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                System.out.println("PatientInfoPanel is now visible");
            }
        });
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

    private void addNewPatient() {
        Patient newPatient = new Patient("", 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
        patients.add(newPatient);
        addPatientToTable(newPatient);
        int newRow = patients.size() - 1;
        editingRow = newRow;
        editingPatient = newPatient;
        enableEditMode(true);
        patientTable.setRowSelectionInterval(newRow, newRow);
        patientTable.scrollRectToVisible(patientTable.getCellRect(newRow, 0, true));
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
        addButton.setVisible(!enable);
        editButton.setVisible(!enable);
        deleteButton.setVisible(!enable);
        saveButton.setVisible(enable);
        cancelButton.setVisible(enable);
        tableModel.fireTableDataChanged();
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

        updateButtonColors(addButton);
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

    public void refreshData() {
        System.out.println("Refreshing PatientInfoPanel data");
        search(); // This will reload the table with current data
        revalidate();
        repaint();
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
        public void setEmergencyContactName(String emergencyContactName) { 
            this.emergencyContactName = emergencyContactName;
        }
        public String getEmergencyContactRelationship() { return emergencyContactRelationship; }
        public void setEmergencyContactRelationship(String emergencyContactRelationship) {
            this.emergencyContactRelationship = emergencyContactRelationship;
        }
        public String getEmergencyContactPhone() { return emergencyContactPhone; }
        public void setEmergencyContactPhone(String emergencyContactPhone) {
            this.emergencyContactPhone = emergencyContactPhone;
        }
        public String getInsuranceProvider() { return insuranceProvider; }
        public void setInsuranceProvider(String insuranceProvider) { this.insuranceProvider = insuranceProvider; }
        public String getPolicyNumber() { return policyNumber; }
        public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
        public String getAdditionalNotes() { return additionalNotes; }
        public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
        public String getHospitalId() { return hospitalId; }
        public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }
    }
}