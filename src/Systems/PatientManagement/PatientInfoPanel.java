package Systems.PatientManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class PatientInfoPanel extends JPanel {
    private PatientManagement patientManagement;
    private JTextField searchField;
    private JTable patientInfoTable;
    private JButton searchButton;
    private JLabel statusLabel;
    private JButton editButton;
    private JButton deleteButton;

    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);

    public PatientInfoPanel(PatientManagement patientManagement) {
        this.patientManagement = patientManagement;
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initializeComponents();
    }

    private void initializeComponents() {
        JLabel headerLabel = new JLabel("Patient Information");
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        add(headerLabel, BorderLayout.NORTH);

        JPanel searchPanel = createSearchPanel();

        statusLabel = new JLabel(" ");
        statusLabel.setFont(MAIN_FONT);
        statusLabel.setForeground(ERROR_COLOR);

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(statusLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.CENTER);

        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchField = new JTextField(20);
        searchField.setFont(MAIN_FONT);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchPatient();
                }
            }
        });

        searchButton = new JButton("Search");
        searchButton.setFont(MAIN_FONT);
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> searchPatient());

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        return searchPanel;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout(0, 10));
        infoPanel.setOpaque(false);

        String[] columnNames = {"Field", "Information"};
        Object[][] data = {
            {"Name", ""}, {"Age", ""}, {"Gender", ""},
            {"Contact", ""}, {"Address", ""}, {"Medical History", ""}
        };
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        patientInfoTable = new JTable(model);
        patientInfoTable.setFont(MAIN_FONT);
        patientInfoTable.setRowHeight(25);
        patientInfoTable.getTableHeader().setFont(MAIN_FONT.deriveFont(Font.BOLD));
        patientInfoTable.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(patientInfoTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        infoPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        editButton = new JButton("Edit");
        editButton.setFont(MAIN_FONT);
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editPatient());

        deleteButton = new JButton("Delete");
        deleteButton.setFont(MAIN_FONT);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deletePatient());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        infoPanel.add(buttonPanel, BorderLayout.SOUTH);

        return infoPanel;
    }

    private void searchPatient() {
        String patientName = searchField.getText().trim();
        if (!patientName.isEmpty()) {
            PatientManagement.Patient patient = patientManagement.getPatientInfo(patientName);
            if (patient != null) {
                updatePatientInfoTable(patient);
                statusLabel.setText("Patient found");
                statusLabel.setForeground(SUCCESS_COLOR);
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } else {
                clearPatientInfoTable();
                statusLabel.setText("No patient found with the name \"" + patientName + "\"");
                statusLabel.setForeground(ERROR_COLOR);
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        } else {
            statusLabel.setText("Please enter a patient name");
            statusLabel.setForeground(ERROR_COLOR);
        }
    }

    private void updatePatientInfoTable(PatientManagement.Patient patient) {
        DefaultTableModel model = (DefaultTableModel) patientInfoTable.getModel();
        model.setValueAt(patient.getName(), 0, 1);
        model.setValueAt(String.valueOf(patient.getAge()), 1, 1);
        model.setValueAt(patient.getGender(), 2, 1);
        model.setValueAt(patient.getContact(), 3, 1);
        model.setValueAt(patient.getAddress(), 4, 1);
        model.setValueAt(patient.getMedicalHistory(), 5, 1);
    }

    private void clearPatientInfoTable() {
        DefaultTableModel model = (DefaultTableModel) patientInfoTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt("", i, 1);
        }
    }

    private void editPatient() {
        String patientName = (String) patientInfoTable.getValueAt(0, 1);
        PatientManagement.Patient patient = patientManagement.getPatientInfo(patientName);
        if (patient != null) {
            PatientEditDialog dialog = new PatientEditDialog(SwingUtilities.getWindowAncestor(this), patient);
            dialog.setVisible(true);
            if (dialog.isPatientUpdated()) {
                updatePatientInfoTable(patient);
                statusLabel.setText("Patient information updated successfully");
                statusLabel.setForeground(SUCCESS_COLOR);
            }
        }
    }

    private void deletePatient() {
        String patientName = (String) patientInfoTable.getValueAt(0, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the patient: " + patientName + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = patientManagement.deletePatient(patientName);
            if (deleted) {
                clearPatientInfoTable();
                statusLabel.setText("Patient deleted successfully");
                statusLabel.setForeground(SUCCESS_COLOR);
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
            } else {
                statusLabel.setText("Failed to delete patient");
                statusLabel.setForeground(ERROR_COLOR);
            }
        }
    }

    private class PatientEditDialog extends JDialog {
        private PatientManagement.Patient patient;
        private JTextField nameField, ageField, genderField, contactField;
        private JTextArea addressArea, medicalHistoryArea;
        private boolean patientUpdated = false;

        public PatientEditDialog(Window owner, PatientManagement.Patient patient) {
            super(owner, "Edit Patient", ModalityType.APPLICATION_MODAL);
            this.patient = patient;
            initializeComponents();
            pack();
            setLocationRelativeTo(owner);
        }

        private void initializeComponents() {
            setLayout(new BorderLayout(10, 10));

            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 5);

            addFormField(formPanel, gbc, "Name:", nameField = new JTextField(patient.getName(), 20));
            addFormField(formPanel, gbc, "Age:", ageField = new JTextField(String.valueOf(patient.getAge()), 20));
            addFormField(formPanel, gbc, "Gender:", genderField = new JTextField(patient.getGender(), 20));
            addFormField(formPanel, gbc, "Contact:", contactField = new JTextField(patient.getContact(), 20));
            addFormField(formPanel, gbc, "Address:", addressArea = new JTextArea(patient.getAddress(), 3, 20));
            addFormField(formPanel, gbc, "Medical History:", medicalHistoryArea = new JTextArea(patient.getMedicalHistory(), 5, 20));

            add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> savePatient());
            buttonPanel.add(saveButton);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dispose());
            buttonPanel.add(cancelButton);

            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field) {
            gbc.gridx = 0;
            panel.add(new JLabel(label), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;
            if (field instanceof JTextArea) {
                panel.add(new JScrollPane(field), gbc);
            } else {
                panel.add(field, gbc);
            }
            gbc.gridy++;
        }

        private void savePatient() {
            try {
                patient.setName(nameField.getText());
                patient.setAge(Integer.parseInt(ageField.getText()));
                patient.setGender(genderField.getText());
                patient.setContact(contactField.getText());
                patient.setAddress(addressArea.getText());
                patient.setMedicalHistory(medicalHistoryArea.getText());
                patientUpdated = true;
                dispose();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isPatientUpdated() {
            return patientUpdated;
        }
    }
}