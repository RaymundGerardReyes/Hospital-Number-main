package Systems.HospitalID;

import Systems.Dashboard.DarkMode;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HospitalIDPanel extends JPanel {
    private DarkMode darkMode;
    private JTextField nameField, addressField, ageField, birthdayField, phoneField, emailField, medicalHistoryField, insuranceField, allergiesField, healthInsuranceIdField;
    private JComboBox<String> sexComboBox;
    private JTextField hospitalIdField;
    private JButton generateButton, clearButton;
    private JTextArea outputArea;
    private Map<String, String> patientDatabase;

    public HospitalIDPanel(DarkMode darkMode) {
        this.darkMode = darkMode;
        this.patientDatabase = new HashMap<>();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        layoutComponents();
        addListeners();
        updateColors(darkMode);
        System.out.println("HospitalIDPanel initialized"); // Debug log
    }

    private void initComponents() {
        nameField = new JTextField(20);
        addressField = new JTextField(20);
        ageField = new JTextField(5);
        birthdayField = new JTextField(10);
        sexComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        phoneField = new JTextField(15);
        emailField = new JTextField(20);
        medicalHistoryField = new JTextField(30);
        insuranceField = new JTextField(20);
        allergiesField = new JTextField(30);
        healthInsuranceIdField = new JTextField(15);
        hospitalIdField = new JTextField(10);
        hospitalIdField.setEditable(false);

        generateButton = new JButton("Generate Hospital ID");
        clearButton = new JButton("Clear Fields");

        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
    }

    private void layoutComponents() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        addLabelAndField(inputPanel, gbc, "Patient Name:", nameField);
        addLabelAndField(inputPanel, gbc, "Address:", addressField);
        addLabelAndField(inputPanel, gbc, "Age:", ageField);
        addLabelAndField(inputPanel, gbc, "Birthday:", birthdayField);
        addLabelAndField(inputPanel, gbc, "Sex:", sexComboBox);
        addLabelAndField(inputPanel, gbc, "Phone Number:", phoneField);
        addLabelAndField(inputPanel, gbc, "Email Address:", emailField);
        addLabelAndField(inputPanel, gbc, "Medical History:", medicalHistoryField);
        addLabelAndField(inputPanel, gbc, "Insurance:", insuranceField);
        addLabelAndField(inputPanel, gbc, "Allergies:", allergiesField);
        addLabelAndField(inputPanel, gbc, "Health Insurance ID:", healthInsuranceIdField);
        addLabelAndField(inputPanel, gbc, "Hospital ID:", hospitalIdField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(generateButton);
        buttonPanel.add(clearButton);

        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void addListeners() {
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Generate button clicked"); // Debug log
                generateHospitalId();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clear button clicked"); // Debug log
                clearFields();
            }
        });
    }

    private void generateHospitalId() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String sex = (String) sexComboBox.getSelectedItem();

        System.out.println("Generating Hospital ID for: " + name); // Debug log

        if (name.isEmpty() || address.isEmpty() || sex == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String key = name + "|" + address + "|" + sex;
        if (patientDatabase.containsKey(key)) {
            String existingId = patientDatabase.get(key);
            hospitalIdField.setText(existingId);
            outputArea.setText("Existing patient found. Hospital ID: " + existingId);
            System.out.println("Existing patient found: " + existingId); // Debug log
        } else {
            String newId = generateUniqueId();
            patientDatabase.put(key, newId);
            hospitalIdField.setText(newId);
            outputArea.setText("New patient registered. Hospital ID: " + newId);
            System.out.println("New patient registered: " + newId); // Debug log
        }
    }

    private String generateUniqueId() {
        Random random = new Random();
        String id;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                sb.append((char) (random.nextInt(26) + 'A'));
            }
            for (int i = 0; i < 3; i++) {
                sb.append(random.nextInt(10));
            }
            id = sb.toString();
        } while (patientDatabase.containsValue(id));
        return id;
    }

    private void clearFields() {
        nameField.setText("");
        addressField.setText("");
        ageField.setText("");
        birthdayField.setText("");
        sexComboBox.setSelectedIndex(0);
        phoneField.setText("");
        emailField.setText("");
        medicalHistoryField.setText("");
        insuranceField.setText("");
        allergiesField.setText("");
        healthInsuranceIdField.setText("");
        hospitalIdField.setText("");
        outputArea.setText("");
        System.out.println("Fields cleared"); // Debug log
    }

    
    public void updateColors(DarkMode darkMode) {
        setBackground(darkMode.getBackgroundColor());
        outputArea.setBackground(darkMode.getCardBackgroundColor());
        outputArea.setForeground(darkMode.getTextColor());

        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                comp.setBackground(darkMode.getBackgroundColor());
                for (Component innerComp : ((JPanel) comp).getComponents()) {
                    updateComponentColors(innerComp);
                }
            } else {
                updateComponentColors(comp);
            }
        }
    }

    private void updateComponentColors(Component comp) {
        if (comp instanceof JTextField || comp instanceof JComboBox) {
            comp.setBackground(darkMode.getCardBackgroundColor());
            comp.setForeground(darkMode.getTextColor());
        } else if (comp instanceof JButton) {
            comp.setBackground(darkMode.getPrimaryColor());
            comp.setForeground(darkMode.getTextColor());
        } else if (comp instanceof JLabel) {
            comp.setForeground(darkMode.getTextColor());
        }
    }

    private void updateComponentColorsRecursively(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JTextField || comp instanceof JComboBox) {
                comp.setBackground(darkMode.getCardBackgroundColor());
                comp.setForeground(darkMode.getTextColor());
            } else if (comp instanceof JButton) {
                comp.setBackground(darkMode.getPrimaryColor());
                comp.setForeground(darkMode.getTextColor());
            } else if (comp instanceof JLabel) {
                comp.setForeground(darkMode.getTextColor());
            } else if (comp instanceof JPanel) {
                comp.setBackground(darkMode.getBackgroundColor());
                updateComponentColorsRecursively((Container) comp);
            }
        }
    }
    
    

    public void refreshData() {
        System.out.println("Refreshing Hospital ID data"); // Debug log
        // Implement any data refresh logic here if needed
        outputArea.setText("Hospital ID data refreshed.");
    }
}