package Systems.PatientManagement;

import Systems.Dashboard.DarkMode;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTabbedPane;
import Systems.PatientManagement.MedicalRecordPanel;

public class PatientInformationPanel extends JPanel {
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, saveButton, cancelButton;
    private JComboBox<String> sortComboBox;
    private List<Patient> patients = new ArrayList<>();
    private DarkMode darkMode;
    private TableRowSorter<DefaultTableModel> sorter;

    private PatientManager patientManager;
    private JTabbedPane tabbedPane;

    private static final String[] COLUMN_NAMES = {
        "Hospital ID", "Last Name", "First Name", "Middle Name", "Birthday", "Age", "Sex",
    };

    public PatientInformationPanel(DarkMode darkMode) {
        this.darkMode = darkMode;
        setLayout(new BorderLayout());
        initComponents();
        layoutComponents();
        addListeners();
        patientManager = new PatientManager(tableModel);
        loadPatientData();
        updateColors();
        setVisible(true);
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
        sorter = new TableRowSorter<>(tableModel);
        patientTable.setRowSorter(sorter);
    
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Patient Information", this);
        MedicalRecordPanel medicalRecordPanel = new MedicalRecordPanel(darkMode);
        tabbedPane.addTab("Patient Medical Records", medicalRecordPanel);
    
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
    
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        tabbedPane.setComponentAt(0, mainPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void addListeners() {
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });

        sortComboBox.addActionListener(e -> sortTable());

        addButton.addActionListener(e -> addNewPatient());
        editButton.addActionListener(e -> patientManager.editSelectedPatient(patientTable.getSelectedRow()));
        deleteButton.addActionListener(e -> patientManager.deleteSelectedPatient(patientTable.getSelectedRow()));
        saveButton.addActionListener(e -> patientManager.saveEditedPatient());
        cancelButton.addActionListener(e -> patientManager.cancelEdit());
    }

    private void search() {
        String searchTerm = searchField.getText().toLowerCase();
        patientManager.search(searchTerm);
    }


    private void sortTable() {
        int columnIndex = sortComboBox.getSelectedIndex();
        sorter.setSortKeys(List.of(new RowSorter.SortKey(columnIndex, SortOrder.ASCENDING)));
    }

    private void addNewPatient() {
        // Implement this method to add a new patient
        // For now, we'll just show a message
        JOptionPane.showMessageDialog(this, "Add New Patient functionality not implemented yet.", "Add Patient", JOptionPane.INFORMATION_MESSAGE);
    }

    public void loadPatientData() {
        patientManager.loadPatientsFromDatabase();
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Patient patient : patientManager.getPatients()) {
            tableModel.addRow(new Object[]{
                patient.getHospitalId(),
                patient.getLastName(),
                patient.getFirstName(),
                patient.getMiddleName(),
                patient.getBirthday(),
                patient.getAge(),
                patient.getSex()
            });
        }
    }

    public void updateColors() {

        setBackground(darkMode.getBackgroundColor());
        tabbedPane.setBackground(darkMode.getBackgroundColor());
        tabbedPane.setForeground(darkMode.getTextColor());

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
}