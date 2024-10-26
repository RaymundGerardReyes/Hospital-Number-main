package Systems.HospitalID;

import Systems.Dashboard.DarkMode;
import Systems.Dashboard.Dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HospitalIDPanel extends JPanel {
    private DarkMode darkMode;
    private JTextField nameField, streetAddressField, cityField, zipCodeField;
    private JFormattedTextField birthdayField, phoneField;
    private JTextArea notesArea;
    private JComboBox<String> sexComboBox, stateComboBox;
    private JTextField hospitalIdField, ageField, emailField;
    private JTextField emergencyContactNameField, emergencyContactRelationshipField, emergencyContactPhoneField;
    private JTextField insuranceProviderField, policyNumberField;
    private JButton generateButton, clearButton, saveButton;
    private JTextArea outputArea;
    private Map<String, String> patientDatabase;

    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public HospitalIDPanel(DarkMode darkMode) {
        this.darkMode = darkMode;
        this.patientDatabase = new HashMap<>();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        layoutComponents();
        addListeners();
    }

    private void initComponents() {
        nameField = createStyledTextField();
        streetAddressField = createStyledTextField();
        cityField = createStyledTextField();
        zipCodeField = createStyledTextField();
        
        try {
            MaskFormatter birthdayFormatter = new MaskFormatter("##/##/####");
            birthdayField = new JFormattedTextField(birthdayFormatter);
            birthdayField.setFont(MAIN_FONT);
            
            MaskFormatter phoneFormatter = new MaskFormatter("(###) ###-####");
            phoneField = new JFormattedTextField(phoneFormatter);
            phoneField.setFont(MAIN_FONT);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sexComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other", "Prefer not to say"});
        sexComboBox.setFont(MAIN_FONT);
        
        String[] states = {"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"};
        stateComboBox = new JComboBox<>(states);
        stateComboBox.setFont(MAIN_FONT);

        notesArea = new JTextArea(4, 20);
        notesArea.setFont(MAIN_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        hospitalIdField = createStyledTextField();
        hospitalIdField.setEditable(false);
        ageField = createStyledTextField();
        ageField.setEditable(false);
        emailField = createStyledTextField();

        emergencyContactNameField = createStyledTextField();
        emergencyContactRelationshipField = createStyledTextField();
        emergencyContactPhoneField = createStyledTextField();

        insuranceProviderField = createStyledTextField();
        policyNumberField = createStyledTextField();

        generateButton = createStyledButton("Generate Hospital ID");
        clearButton = createStyledButton("Clear Fields");
        saveButton = createStyledButton("Save Patient Info");

        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        outputArea.setFont(MAIN_FONT);
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(createTitledBorder("Hospital ID Registration"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        addLabelAndField(inputPanel, gbc, "Name:", nameField);
        addLabelAndField(inputPanel, gbc, "Birthday:", birthdayField);
        addLabelAndField(inputPanel, gbc, "Age:", ageField);
        addLabelAndField(inputPanel, gbc, "Sex:", sexComboBox);
        addLabelAndField(inputPanel, gbc, "Phone:", phoneField);
        addLabelAndField(inputPanel, gbc, "Email:", emailField);
        addLabelAndField(inputPanel, gbc, "Street Address:", streetAddressField);
        addLabelAndField(inputPanel, gbc, "City:", cityField);
        addLabelAndField(inputPanel, gbc, "State:", stateComboBox);
        addLabelAndField(inputPanel, gbc, "ZIP Code:", zipCodeField);
        addLabelAndField(inputPanel, gbc, "Emergency Contact Name:", emergencyContactNameField);
        addLabelAndField(inputPanel, gbc, "Emergency Contact Relationship:", emergencyContactRelationshipField);
        addLabelAndField(inputPanel, gbc, "Emergency Contact Phone:", emergencyContactPhoneField);
        addLabelAndField(inputPanel, gbc, "Insurance Provider:", insuranceProviderField);
        addLabelAndField(inputPanel, gbc, "Policy Number:", policyNumberField);
        addLabelAndField(inputPanel, gbc, "Additional Notes:", new JScrollPane(notesArea));
        addLabelAndField(inputPanel, gbc, "Hospital ID:", hospitalIdField);

        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(generateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(createTitledBorder("System Output"));
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        add(outputPanel, BorderLayout.SOUTH);
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
    }

    private void addListeners() {
        generateButton.addActionListener(e -> generateHospitalId());
        clearButton.addActionListener(e -> clearFields());
        saveButton.addActionListener(e -> savePatientInfo());
        birthdayField.addPropertyChangeListener("value", evt -> updateAge());
    }

    private void updateAge() {
        String birthdayText = birthdayField.getText();
        if (isValidDate(birthdayText)) {
            LocalDate birthDate = LocalDate.parse(birthdayText, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            ageField.setText(String.valueOf(age));
        } else {
            ageField.setText("");
        }
    }

    private void generateHospitalId() {
        if (!validateFields()) {
            return;
        }

        String name = nameField.getText().trim();
        String birthday = birthdayField.getText().trim();
        String address = streetAddressField.getText().trim();
        String sex = (String) sexComboBox.getSelectedItem();

        String key = name + "|" + birthday + "|" + address + "|" + sex;
        if (patientDatabase.containsKey(key)) {
            String existingId = patientDatabase.get(key);
            hospitalIdField.setText(existingId);
            showMessage("Existing patient found. Hospital ID: " + existingId, false);
        } else {
            String newId = generateUniqueId();
            patientDatabase.put(key, newId);
            hospitalIdField.setText(newId);
            showMessage("New patient registered. Hospital ID: " + newId, false);
        }
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty() || !nameField.getText().matches("^[a-zA-Z\\s-']+$")) {
            showMessage("Please enter a valid name (letters, spaces, hyphens, and apostrophes only).", true);
            return false;
        }
        if (!isValidDate(birthdayField.getText())) {
            showMessage("Please enter a valid birthday (MM/DD/YYYY).", true);
            return false;
        }
        if (ageField.getText().isEmpty() || !ageField.getText().matches("^\\d+$")) {
            showMessage("Please enter a valid age (positive integer).", true);
            return false;
        }
        if (!isValidEmail(emailField.getText())) {
            showMessage("Please enter a valid email address.", true);
            return false;
        }
        if (streetAddressField.getText().trim().isEmpty()) {
            showMessage("Please enter a street address.", true);
            return false;
        }
        if (cityField.getText().trim().isEmpty()) {
            showMessage("Please enter a city.", true);
            return false;
        }
        if (!zipCodeField.getText().matches("^\\d{5}(-\\d{4})?$")) {
            showMessage("Please enter a valid ZIP code (5 digits or 5+4 format).", true);
            return false;
        }
        if (emergencyContactNameField.getText().trim().isEmpty()) {
            showMessage("Please enter an emergency contact name.", true);
            return false;
        }
        if (emergencyContactRelationshipField.getText().trim().isEmpty()) {
            showMessage("Please enter the emergency contact's relationship.", true);
            return false;
        }
        if (!isValidPhoneNumber(emergencyContactPhoneField.getText())) {
            showMessage("Please enter a valid emergency contact phone number ((XXX) XXX-XXXX).", true);
            return false;
        }
        if (!isValidPhoneNumber(phoneField.getText())) {
            showMessage("Please enter a valid phone number ((XXX) XXX-XXXX).", true);
            return false;
        }

        return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneNumberRegex = "^\\(\\d{3}\\) \\d{3}-\\d{4}$";
        Pattern pat = Pattern.compile(phoneNumberRegex);
        if (phoneNumber == null) {
            return false;
        }
        return pat.matcher(phoneNumber).matches();
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }


    private String generateUniqueId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new Date());
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        return "H" + datePart + randomPart;
    }

    private void savePatientInfo() {
        String hospitalId = hospitalIdField.getText().trim();
        if (hospitalId.isEmpty()) {
            showMessage("Please generate a Hospital ID first.", true);
            return;
        }

        StringBuilder info = new StringBuilder();
        info.append("Patient Information Saved:\n");
        info.append("Hospital ID: ").append(hospitalId).append("\n");
        info.append("Name: ").append(nameField.getText()).append("\n");
        info.append("Birthday: ").append(birthdayField.getText()).append("\n");
        info.append("Age: ").append(ageField.getText()).append("\n");
        info.append("Sex: ").append(sexComboBox.getSelectedItem()).append("\n");
        info.append("Phone: ").append(phoneField.getText()).append("\n");
        info.append("Email: ").append(emailField.getText()).append("\n");
        info.append("Address: ").append(streetAddressField.getText()).append(", ")
            .append(cityField.getText()).append(", ")
            
            .append(stateComboBox.getSelectedItem()).append(" ")
            .append(zipCodeField.getText()).append("\n");
        info.append("Emergency Contact: ").append(emergencyContactNameField.getText())
            .append(" (").append(emergencyContactRelationshipField.getText()).append(") ")
            .append(emergencyContactPhoneField.getText()).append("\n");
        info.append("Insurance Provider: ").append(insuranceProviderField.getText()).append("\n");
        info.append("Policy Number: ").append(policyNumberField.getText()).append("\n");
        info.append("Additional Notes: ").append(notesArea.getText()).append("\n");

        showMessage(info.toString(), false);
    }

    private void clearFields() {
        nameField.setText("");
        birthdayField.setText("");
        ageField.setText("");
        sexComboBox.setSelectedIndex(0);
        phoneField.setText("");
        emailField.setText("");
        streetAddressField.setText("");
        cityField.setText("");
        stateComboBox.setSelectedIndex(0);
        zipCodeField.setText("");
        emergencyContactNameField.setText("");
        emergencyContactRelationshipField.setText("");
        emergencyContactPhoneField.setText("");
        insuranceProviderField.setText("");
        policyNumberField.setText("");
        notesArea.setText("");
        hospitalIdField.setText("");
        showMessage("All fields cleared.", false);
    }

    private void showMessage(String message, boolean isError) {
        outputArea.setForeground(isError ? Color.RED : darkMode.getTextColor());
        outputArea.setText(message);
    }

    public void updateColors(DarkMode darkMode) {
        setBackground(darkMode.getBackgroundColor());
        updateComponentColors(this);
    }

    private void updateComponentColors(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(darkMode.getBackgroundColor());
                updateComponentColors((Container) comp);
            } else if (comp instanceof JLabel) {
                comp.setForeground(darkMode.getTextColor());
            } else if (comp instanceof JTextField || comp instanceof JTextArea || comp instanceof JFormattedTextField) {
                comp.setBackground(darkMode.getCardBackgroundColor());
                comp.setForeground(darkMode.getTextColor());
                if (comp instanceof JTextComponent) {
                    ((JTextComponent) comp).setCaretColor(darkMode.getTextColor());
                }
            } else if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(darkMode.getPrimaryColor());
                button.setForeground(Color.WHITE);
            } else if (comp instanceof JComboBox) {
                comp.setBackground(darkMode.getCardBackgroundColor());
                comp.setForeground(darkMode.getTextColor());
            } else if (comp instanceof JScrollPane) {
                comp.setBackground(darkMode.getBackgroundColor());
                JViewport viewport = ((JScrollPane) comp).getViewport();
                viewport.setBackground(darkMode.getBackgroundColor());
                updateComponentColors(viewport);
            }
        }
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(MAIN_FONT);
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setFocusPainted(true);
        return button;
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(darkMode.getBorderColor()),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            TITLE_FONT,
            darkMode.getTextColor()
        );
        border.setTitleColor(darkMode.getTextColor());
        return border;
    }

    public void refreshData() {
        clearFields();
        outputArea.setText("Hospital ID panel refreshed.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dashboard::new);
    }
}