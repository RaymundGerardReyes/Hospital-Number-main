package Systems.HealthCareFacilities;

import javax.swing.*;
import java.awt.*;

public class EmergencyRoom extends Facility {
    private int triageCapacity;
    private int ambulanceBayCount;
    private JTextField hospitalIdField, patientNameField, temperatureField, heartRateField, bloodPressureField, respiratoryRateField, oxygenSaturationField, roomField;
    private JComboBox<String> doctorComboBox;

    public EmergencyRoom(String name, String address, String contact, int triageCapacity, int ambulanceBayCount) {
        super(name, address, contact, "Emergency Room");
        this.triageCapacity = triageCapacity;
        this.ambulanceBayCount = ambulanceBayCount;
        initializeFields();
    }

    private void initializeFields() {
        hospitalIdField = new JTextField(15);
        patientNameField = new JTextField(15);
        temperatureField = new JTextField(15);
        heartRateField = new JTextField(15);
        bloodPressureField = new JTextField(15);
        respiratoryRateField = new JTextField(15);
        oxygenSaturationField = new JTextField(15);
        roomField = new JTextField(15);
        String[] doctors = {"Select Doctor", "Dr. Smith", "Dr. Johnson", "Dr. Williams"};
        doctorComboBox = new JComboBox<>(doctors);
    }

    public int getTriageCapacity() {
        return triageCapacity;
    }

    public void setTriageCapacity(int triageCapacity) {
        this.triageCapacity = triageCapacity;
    }

    public int getAmbulanceBayCount() {
        return ambulanceBayCount;
    }

    public void setAmbulanceBayCount(int ambulanceBayCount) {
        this.ambulanceBayCount = ambulanceBayCount;
    }

    public JTextField getHospitalIdField() {
        return hospitalIdField;
    }

    public JTextField getPatientNameField() {
        return patientNameField;
    }

    public JTextField getTemperatureField() {
        return temperatureField;
    }

    public JTextField getHeartRateField() {
        return heartRateField;
    }

    public JTextField getBloodPressureField() {
        return bloodPressureField;
    }

    public JTextField getRespiratoryRateField() {
        return respiratoryRateField;
    }

    public JTextField getOxygenSaturationField() {
        return oxygenSaturationField;
    }

    public JTextField getRoomField() {
        return roomField;
    }

    public JComboBox<String> getDoctorComboBox() {
        return doctorComboBox;
    }

    @Override
    public String getDetails() {
        return String.format("%s\nTriage Capacity: %d\nAmbulance Bays: %d\n24/7 Emergency Services: Yes",
                super.getDetails(), triageCapacity, ambulanceBayCount);
    }

    public JPanel createPatientAdmissionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addLabelAndField(panel, gbc, "Hospital ID:", hospitalIdField);
        addLabelAndField(panel, gbc, "Patient Name:", patientNameField);
        addLabelAndField(panel, gbc, "Temperature:", temperatureField);
        addLabelAndField(panel, gbc, "Heart Rate:", heartRateField);
        addLabelAndField(panel, gbc, "Blood Pressure:", bloodPressureField);
        addLabelAndField(panel, gbc, "Respiratory Rate:", respiratoryRateField);
        addLabelAndField(panel, gbc, "Oxygen Saturation:", oxygenSaturationField);
        addLabelAndField(panel, gbc, "Assigned Doctor:", doctorComboBox);
        addLabelAndField(panel, gbc, "Room:", roomField);

        JButton admitPatientButton = new JButton("Admit Patient");
        admitPatientButton.addActionListener(e -> admitPatient());

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(admitPatientButton, gbc);

        return panel;
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void admitPatient() {
        // Implement patient admission logic here
        String hospitalId = hospitalIdField.getText();
        String patientName = patientNameField.getText();
        String temperature = temperatureField.getText();
        String heartRate = heartRateField.getText();
        String bloodPressure = bloodPressureField.getText();
        String respiratoryRate = respiratoryRateField.getText();
        String oxygenSaturation = oxygenSaturationField.getText();
        String doctor = (String) doctorComboBox.getSelectedItem();
        String room = roomField.getText();

        // Perform admission logic (e.g., save to database, update UI, etc.)
        // For now, we'll just print the information
        System.out.println("Patient admitted to Emergency Room:");
        System.out.println("Hospital ID: " + hospitalId);
        System.out.println("Patient Name: " + patientName);
        System.out.println("Temperature: " + temperature);
        System.out.println("Heart Rate: " + heartRate);
        System.out.println("Blood Pressure: " + bloodPressure);
        System.out.println("Respiratory Rate: " + respiratoryRate);
        System.out.println("Oxygen Saturation: " + oxygenSaturation);
        System.out.println("Assigned Doctor: " + doctor);
        System.out.println("Room: " + room);

        // Clear fields after admission
        clearFields();
    }

    private void clearFields() {
        hospitalIdField.setText("");
        patientNameField.setText("");
        temperatureField.setText("");
        heartRateField.setText("");
        bloodPressureField.setText("");
        respiratoryRateField.setText("");
        oxygenSaturationField.setText("");
        doctorComboBox.setSelectedIndex(0);
        roomField.setText("");
    }
}