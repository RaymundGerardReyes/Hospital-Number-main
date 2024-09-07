package Systems.Consultation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class ConsultationPanel extends JPanel {
    private JLabel nameLabel;
    private JLabel ageLabel;
    private JLabel sexLabel;
    private JLabel addressLabel;
    private JLabel phoneLabel;
    private JLabel hospitalIdLabel;
    private JLabel healthConcernLabel;
    private JTextField nameField;
    private JTextField sexField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField hospitalIdField;
    private JTextField healthConcernField;
    private JComboBox<Integer> ageComboBox;
    private JButton verifyButton;
    private PatientDataManager patientDataManager;

    public ConsultationPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);
        initializeComponents();
        String filePath = "C:/Users/User/Desktop/Code/Code Practice Summer/Hospital Management System/Hospital Number/src/Systems/HospitalID/hospital_ids.txt";
        patientDataManager = new PatientDataManager(filePath);
        verifyButton.addActionListener(new VerifyButtonListener());
        patientDataManager.getExecutorService().submit(this::updateUI);
    }

    private void initializeComponents() {
        nameLabel = new JLabel("Name of Patient:");
        ageLabel = new JLabel("Age:");
        sexLabel = new JLabel("Sex:");
        addressLabel = new JLabel("Address:");
        phoneLabel = new JLabel("Phone Number:");
        hospitalIdLabel = new JLabel("Hospital ID Number:");
        healthConcernLabel = new JLabel("Health Concern:");
        nameField = new JTextField(20);
        sexField = new JTextField(20);
        addressField = new JTextField(20);
        phoneField = new JTextField(20);
        hospitalIdField = new JTextField(20);
        healthConcernField = new JTextField(20);
        Integer[] ageRange = new Integer[100];
        for (int i = 0; i < 100; i++) {
            ageRange[i] = i + 1;
        }
        ageComboBox = new JComboBox<>(ageRange);
        ageComboBox.setPreferredSize(new Dimension(200, 30));
        verifyButton = new JButton("Verify");
        addComponentToPanel(this, nameLabel, nameField);
        addComponentToPanel(this, ageLabel, ageComboBox);
        addComponentToPanel(this, sexLabel, sexField);
        addComponentToPanel(this, addressLabel, addressField);
        addComponentToPanel(this, phoneLabel, phoneField);
        addComponentToPanel(this, hospitalIdLabel, hospitalIdField);
        addComponentToPanel(this, healthConcernLabel, healthConcernField);
        addComponentToPanel(this, null, verifyButton);
    }

    private void addComponentToPanel(JPanel panel, JComponent label, JComponent component) {
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        subPanel.setBackground(Color.WHITE);
        if (label != null) {
            subPanel.add(label);
        }
        subPanel.add(component);
        panel.add(subPanel);
    }

    private class VerifyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            verifyAndDisplayPatientData();
        }
    }

    private void verifyAndDisplayPatientData() {
        String hospitalID = hospitalIdField.getText().trim();
        PatientDataManager.PatientData patientData = patientDataManager.getPatientDataByHospitalId(hospitalID);
        if (patientData != null) {
            SwingUtilities.invokeLater(() -> {
                nameField.setText(patientData.name);
                ageComboBox.setSelectedItem(patientData.age);
                sexField.setText(patientData.sex);
                addressField.setText(patientData.address);
                phoneField.setText(patientData.phone);
                healthConcernField.setText(patientData.healthConcern);
            });
            HealthConcernHandler handler = new HealthConcernHandler();
            String specialist = handler.getSpecialist(patientData.healthConcern);
            JOptionPane.showMessageDialog(this, "Patient data loaded successfully.\nRefer to: " + specialist, "Verification", JOptionPane.INFORMATION_MESSAGE);
        } else {
            SwingUtilities.invokeLater(this::clearFields);
            JOptionPane.showMessageDialog(this, "Invalid Hospital ID", "Verification", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateUI() {
        // Use a Timer instead of a while loop
        Timer timer = new Timer(1000, e -> {
            SwingUtilities.invokeLater(() -> {
                // Code to update the UI if needed
                // For example, you can refresh a list of patient data, etc.
            });
        });
        timer.start();
    }

    public void clearFields() {
        nameField.setText("");
        ageComboBox.setSelectedItem(null);
        sexField.setText("");
        addressField.setText("");
        phoneField.setText("");
        healthConcernField.setText("");
    }

    public String getPatientName() {
        return nameField.getText();
    }

    public String getPatientAge() {
        return ageComboBox.getSelectedItem().toString();
    }

    public String getPatientSex() {
        return sexField.getText();
    }

    public String getPatientAddress() {
        return addressField.getText();
    }

    public String getPatientPhone() {
        return phoneField.getText();
    }

    public String getPatientHospitalID() {
        return hospitalIdField.getText();
    }

    public String getPatientHealthConcern() {
        return healthConcernField.getText();
    }
}