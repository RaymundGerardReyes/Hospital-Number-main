package Systems.PatientManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PatientManagement extends JPanel {
    private JTextField nameField, ageField, genderField, contactField;
    private JTextArea addressArea, medicalHistoryArea;
    private JButton registerButton, updateButton, viewButton;
    private JList<String> patientList;
    private DefaultListModel<String> listModel;
    private List<Patient> patients;

    public PatientManagement() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        patients = new ArrayList<>();

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.WEST);

        JPanel listPanel = createListPanel();
        add(listPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public Patient getPatientInfo(String patientName) {
        for (Patient patient : patients) {
            if (patient.getName().equalsIgnoreCase(patientName)) {
                return patient;
            }
        }
        return null;
    }

    public boolean deletePatient(String patientName) {
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getName().equalsIgnoreCase(patientName)) {
                patients.remove(i);
                listModel.remove(i);
                return true;
            }
        }
        return false;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Age:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Contact:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Medical History:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        nameField = new JTextField(20);
        panel.add(nameField, gbc);
        gbc.gridy++;

        ageField = new JTextField(20);
        panel.add(ageField, gbc);
        gbc.gridy++;

        genderField = new JTextField(20);
        panel.add(genderField, gbc);
        gbc.gridy++;

        contactField = new JTextField(20);
        panel.add(contactField, gbc);
        gbc.gridy++;

        addressArea = new JTextArea(3, 20);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        panel.add(addressScroll, gbc);
        gbc.gridy++;

        medicalHistoryArea = new JTextArea(5, 20);
        JScrollPane medicalHistoryScroll = new JScrollPane(medicalHistoryArea);
        panel.add(medicalHistoryScroll, gbc);

        return panel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Patient List"));
        listModel = new DefaultListModel<>();
        patientList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(patientList);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        registerButton = new JButton("Register Patient");
        updateButton = new JButton("Update Patient");
        viewButton = new JButton("View Patient");

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerPatient();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePatient();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPatient();
            }
        });

        panel.add(registerButton);
        panel.add(updateButton);
        panel.add(viewButton);

        return panel;
    }

    private void registerPatient() {
        try {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String gender = genderField.getText();
            String contact = contactField.getText();
            String address = addressArea.getText();
            String medicalHistory = medicalHistoryArea.getText();

            Patient patient = new Patient(name, age, gender, contact, address, medicalHistory);
            patients.add(patient);
            listModel.addElement(patient.getName());

            clearFields();
            JOptionPane.showMessageDialog(this, "Patient registered successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePatient() {
        int selectedIndex = patientList.getSelectedIndex();
        if (selectedIndex != -1) {
            try {
                Patient patient = patients.get(selectedIndex);
                patient.setName(nameField.getText());
                patient.setAge(Integer.parseInt(ageField.getText()));
                patient.setGender(genderField.getText());
                patient.setContact(contactField.getText());
                patient.setAddress(addressArea.getText());
                patient.setMedicalHistory(medicalHistoryArea.getText());

                listModel.setElementAt(patient.getName(), selectedIndex);
                clearFields();
                JOptionPane.showMessageDialog(this, "Patient information updated successfully!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a patient to update.");
        }
    }

    private void viewPatient() {
        int selectedIndex = patientList.getSelectedIndex();
        if (selectedIndex != -1) {
            Patient patient = patients.get(selectedIndex);
            nameField.setText(patient.getName());
            ageField.setText(String.valueOf(patient.getAge()));
            genderField.setText(patient.getGender());
            contactField.setText(patient.getContact());
            addressArea.setText(patient.getAddress());
            medicalHistoryArea.setText(patient.getMedicalHistory());
        } else {
            JOptionPane.showMessageDialog(this, "Please select a patient to view.");
        }
    }

    private void clearFields() {
        nameField.setText("");
        ageField.setText("");
        genderField.setText("");
        contactField.setText("");
        addressArea.setText("");
        medicalHistoryArea.setText("");
    }

    public class Patient {
        private String name;
        private int age;
        private String gender;
        private String contact;
        private String address;
        private String medicalHistory;

        public Patient(String name, int age, String gender, String contact, String address, String medicalHistory) {
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.contact = contact;
            this.address = address;
            this.medicalHistory = medicalHistory;
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getMedicalHistory() { return medicalHistory; }
        public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    }
}