package Systems.Signup;

import Systems.Login.LoginPanel;
import Systems.UserInfo.SignUpInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SignUp extends JFrame {
    private JTextField nameField, usernameField, addressField, emailField;
    private JFormattedTextField birthdayField, phoneField;
    private JSpinner ageSpinner;
    private JPasswordField passField, confirmPassField;
    private JComboBox<String> sexComboBox;
    private JButton signupButton, backToLoginButton;
    private LogoPanel logoPanel;

    private static final Color PRIMARY_COLOR = new Color(0, 50, 100);
    private static final Color SECONDARY_COLOR = new Color(240, 248, 255);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);

    public SignUp() {
        setTitle("MyCare HealthCare Solutions - Sign Up");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        logoPanel = new LogoPanel();
        add(logoPanel, BorderLayout.NORTH);

        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(new EmptyBorder(10, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 20, 15, 20);

        // First column
        gbc.gridx = 0;
        gbc.gridy = 0;
        addFormField(panel, gbc, "Full Name", nameField = createStyledTextField("Enter your full name"));
        gbc.gridy++;
        addFormField(panel, gbc, "Username", usernameField = createStyledTextField("Enter your unique username"));
        gbc.gridy++;
        addFormField(panel, gbc, "Birthday", birthdayField = createStyledBirthdayField());
        gbc.gridy++;
        addFormField(panel, gbc, "Age", ageSpinner = createStyledAgeSpinner());
        gbc.gridy++;
        addFormField(panel, gbc, "Sex", sexComboBox = createStyledComboBox());

        // Second column
        gbc.gridx = 2;
        gbc.gridy = 0;
        addFormField(panel, gbc, "Address", addressField = createStyledTextField("Enter your full address"));
        gbc.gridy++;
        addFormField(panel, gbc, "Phone Number", phoneField = createStyledPhoneField());
        gbc.gridy++;
        addFormField(panel, gbc, "Email Address", emailField = createStyledTextField("Enter your email address"));
        gbc.gridy++;
        addFormField(panel, gbc, "Password", passField = createStyledPasswordField("Enter a strong password"));
        gbc.gridy++;
        addFormField(panel, gbc, "Confirm Password", confirmPassField = createStyledPasswordField("Confirm your password"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        backToLoginButton = createStyledButton("Back to Login");
        backToLoginButton.addActionListener(e -> openLoginPanel());
        buttonPanel.add(backToLoginButton);

        signupButton = createStyledButton("Sign Up");
        signupButton.addActionListener(e -> handleSignUp());
        buttonPanel.add(signupButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(30, 0, 10, 0);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(MAIN_FONT);
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);

        gbc.gridx++;
        panel.add(field, gbc);
        gbc.gridx--;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        field.setBackground(Color.WHITE);
        setPlaceholder(field, placeholder);
        return field;
    }

    private JFormattedTextField createStyledBirthdayField() {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter("##-##-####");
            formatter.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        field.setBackground(Color.WHITE);
        field.setColumns(10);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateAge();
            }
        });
        return field;
    }

    private JSpinner createStyledAgeSpinner() {
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 120, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(MAIN_FONT);
        spinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        spinner.setBackground(Color.WHITE);
        return spinner;
    }

    private JFormattedTextField createStyledPhoneField() {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter("(###) ###-####");
            formatter.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        field.setBackground(Color.WHITE);
        field.setColumns(14);
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20);
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        field.setBackground(Color.WHITE);
        setPlaceholder(field, placeholder);

        field.setLayout(new BorderLayout());
        JLabel iconLabel = new JLabel("👁");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        field.add(iconLabel, BorderLayout.EAST);

        return field;
    }

    private void setPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }

    private JComboBox<String> createStyledComboBox() {
        String[] options = {"Select", "Male", "Female", "Other", "Prefer not to say"};
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setFont(MAIN_FONT);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        return comboBox;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }

    private void updateAge() {
        String birthdayText = birthdayField.getText().replaceAll("[^0-9]", "");
        if (birthdayText.length() == 8) {
            try {
                LocalDate birthDate = LocalDate.parse(birthdayText, DateTimeFormatter.ofPattern("MMddyyyy"));
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                ageSpinner.setValue(age);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSignUp() {
        List<String> errors = validateFields();
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, String.join("\n", errors), "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SignUpInfo signUpInfo = new SignUpInfo(
                usernameField.getText().trim(),
                nameField.getText().trim(),
                ageSpinner.getValue().toString(),
                (String) sexComboBox.getSelectedItem(),
                addressField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                new String(passField.getPassword())
        );

        boolean saved = signUpInfo.saveToFile();
        if (saved) {
            JOptionPane.showMessageDialog(this, "Sign Up Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            openLoginPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save sign-up information. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> validateFields() {
        List<String> errors = new ArrayList<>();
        if (isPlaceholder(nameField)) errors.add("Name is required");
        if (isPlaceholder(usernameField)) errors.add("Username is required");
        if (birthdayField.getText().contains("_")) errors.add("Birthday is required");
        if ((int)ageSpinner.getValue() == 0) errors.add("Age must be greater than 0");
        if (sexComboBox.getSelectedIndex() == 0) errors.add("Sex is required");
        if (isPlaceholder(addressField)) errors.add("Address is required");
        if (phoneField.getText().contains("_")) errors.add("Phone number is required");
        if (isPlaceholder(emailField) || !emailField.getText().matches("\\S+@\\S+\\.\\S+")) errors.add("Valid email is required");
        String password = new String(passField.getPassword());
        if (password.isEmpty() || isPlaceholder(passField)) errors.add("Password is required");
        if (!password.equals(new String(confirmPassField.getPassword()))) errors.add("Passwords do not match");
        return errors;
    }

    private boolean isPlaceholder(JTextField field) {
        return field.getForeground() == Color.GRAY;
    }

    private void openLoginPanel() {
        SwingUtilities.invokeLater(() -> {
            JFrame loginFrame = new JFrame("MyCare HealthCare Solutions - Login");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setContentPane(new LoginPanel());
            loginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            loginFrame.setVisible(true);
            dispose();
        });
    }

    private class LogoPanel extends JPanel {
        public LogoPanel() {
            setPreferredSize(new Dimension(getWidth(), 200));
            setBackground(PRIMARY_COLOR);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            g2d.setColor(Color.WHITE);
            g2d.fillOval(centerX - 50, centerY - 50, 100, 100);

            g2d.setFont(TITLE_FONT);
            g2d.setColor(Color.WHITE);
            String text = "MyCare HealthCare Solutions";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            g2d.drawString(text, centerX - textWidth / 2, centerY + 80);

            g2d.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SignUp();
        });
    }
}