package Systems.Consultation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConsultationPanel extends JPanel {
    private ConsultationParentPanel parentPanel;
    private JTextField nameField, sexField, addressField, phoneField, hospitalIdField, healthConcernField;
    private JComboBox<Integer> ageComboBox;
    private JButton verifyButton, scheduleButton;

    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public ConsultationPanel(ConsultationParentPanel parentPanel) {
        this.parentPanel = parentPanel;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        initializeComponents();
    }

    private void initializeComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        addFormField(formPanel, gbc, "Name:", createStyledTextField());
        addFormField(formPanel, gbc, "Age:", createStyledComboBox(generateAgeRange()));
        addFormField(formPanel, gbc, "Sex:", createStyledTextField());
        addFormField(formPanel, gbc, "Address:", createStyledTextField());
        addFormField(formPanel, gbc, "Phone:", createStyledTextField());
        addFormField(formPanel, gbc, "Hospital ID:", createStyledTextField());
        addFormField(formPanel, gbc, "Health Concern:", createStyledTextField());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        verifyButton = createStyledButton("Verify");
        scheduleButton = createStyledButton("Schedule Appointment");

        verifyButton.addActionListener(e -> verifyPatient());
        scheduleButton.addActionListener(e -> scheduleAppointment());

        buttonPanel.add(verifyButton);
        buttonPanel.add(scheduleButton);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        panel.add(component, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(MAIN_FONT);
        return field;
    }

    private JComboBox<Integer> createStyledComboBox(Integer[] items) {
        JComboBox<Integer> comboBox = new JComboBox<>(items);
        comboBox.setFont(MAIN_FONT);
        return comboBox;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private Integer[] generateAgeRange() {
        Integer[] ageRange = new Integer[100];
        for (int i = 0; i < 100; i++) {
            ageRange[i] = i + 1;
        }
        return ageRange;
    }

    private void verifyPatient() {
        // Implement verification logic
        parentPanel.updateTransactionSlip("Patient verified: " + nameField.getText());
    }

    private void scheduleAppointment() {
        // Implement scheduling logic
        parentPanel.updateTransactionSlip("Appointment scheduled for: " + nameField.getText());
    }

    // Add getters and setters as needed
}