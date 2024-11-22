package Systems.Signup;

import Systems.Login.DatabaseConnection;
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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.stream.IntStream;



public class SignUp extends JFrame {
    private JTextField nameField, usernameField, addressField, emailField;
    private JFormattedTextField birthdayField, phoneField;
    private JSpinner ageSpinner;
    private JPasswordField passField, confirmPassField;
    private JComboBox<String> sexComboBox;
    private JButton signupButton, backToLoginButton;
    private LogoPanel logoPanel;

    private JTextField ageField;

    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> dayComboBox;
    private JComboBox<Integer> yearComboBox;

    private static final Color PRIMARY_COLOR = new Color(0, 50, 100);
    private static final Color SECONDARY_COLOR = new Color(240, 248, 255);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");


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

        addListeners();

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
        addFormField(panel, gbc, "Birthday", createBirthdayScrollPane());
        gbc.gridy++;
        addFormField(panel, gbc, "Age", ageField = createStyledTextField(""));
        ageField.setEditable(false);
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

        // Button panel
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
    

    private void addListeners() {
        ActionListener dateChangeListener = e -> updateAge();
        monthComboBox.addActionListener(dateChangeListener);
        dayComboBox.addActionListener(dateChangeListener);
        yearComboBox.addActionListener(dateChangeListener);
    }

    private void updateAgeFromBirthday() {
        String birthdayText = birthdayField.getText();
        if (!birthdayText.equals("__-__-____")) {
            try {
                LocalDate birthDate = LocalDate.parse(birthdayText, DATE_FORMATTER);
                int calculatedAge = Period.between(birthDate, LocalDate.now()).getYears();
                ageSpinner.setValue(calculatedAge);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use DD-MM-YYYY.", "Error", JOptionPane.ERROR_MESSAGE);
                birthdayField.setText("__-__-____");
            }
        }
    }

    private void updateBirthdayFromAge() {
        int age = (int) ageSpinner.getValue();
        if (age > 0) {
            LocalDate currentDate = LocalDate.now();
            LocalDate birthDate = currentDate.minusYears(age);
            birthdayField.setText(birthDate.format(DATE_FORMATTER));
        } else {
            birthdayField.setText("__-__-____");
        }
    }



    private JFormattedTextField createStyledBirthdayField() {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter("##-##-####");
            formatter.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JFormattedTextField field = new JFormattedTextField(formatter) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().equals("__-__-____")) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(Color.GRAY);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString("Day - Month - Year", getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
                    g2.dispose();
                }
            }
        };

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
                if (!field.getText().equals("__-__-____")) {
                    updateAgeFromBirthday();
                }
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

        Component editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
            defaultEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
        }

        return spinner;
    }


    private void updateBirthday() {
        int age = (int) ageSpinner.getValue();
        if (age > 0) {
            LocalDate currentDate = LocalDate.now();
            LocalDate birthDate = currentDate.minusYears(age);
            String formattedBirthday = birthDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            birthdayField.setText(formattedBirthday);
        }
    }

    
    private JFormattedTextField createStyledPhoneField() {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter("(09##) ###-####");
            formatter.setPlaceholderCharacter('X');
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
    
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.add(field, BorderLayout.CENTER);
    
        JLabel toggleLabel = new JLabel("👁");
        toggleLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        toggleLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        toggleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
        toggleLabel.addMouseListener(new MouseAdapter() {
            boolean passwordVisible = false;
    
            @Override
            public void mouseClicked(MouseEvent e) {
                passwordVisible = !passwordVisible;
                if (passwordVisible) {
                    field.setEchoChar((char) 0);
                    toggleLabel.setText("👁‍🗨");
                } else {
                    field.setEchoChar('•');
                    toggleLabel.setText("👁");
                }
            }
    
            @Override
            public void mouseEntered(MouseEvent e) {
                toggleLabel.setText(passwordVisible ? "👁‍🗨" : "👀");
            }
    
            @Override
            public void mouseExited(MouseEvent e) {
                toggleLabel.setText(passwordVisible ? "👁‍🗨" : "👁");
            }
        });
    
        passwordPanel.add(toggleLabel, BorderLayout.EAST);
    
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


    private JScrollPane createBirthdayScrollPane() {
        JPanel birthdayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        birthdayPanel.setOpaque(false);

        String[] months = {"January", "February", "March", "April", "May", "June", 
                           "July", "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setFont(MAIN_FONT);

        dayComboBox = new JComboBox<>(IntStream.rangeClosed(1, 31).boxed().toArray(Integer[]::new));
        dayComboBox.setFont(MAIN_FONT);

        yearComboBox = new JComboBox<>(IntStream.rangeClosed(1900, LocalDate.now().getYear()).boxed().toArray(Integer[]::new));
        yearComboBox.setFont(MAIN_FONT);

        birthdayPanel.add(monthComboBox);
        birthdayPanel.add(new JLabel(" - "));
        birthdayPanel.add(dayComboBox);
        birthdayPanel.add(new JLabel(" - "));
        birthdayPanel.add(yearComboBox);

        JScrollPane scrollPane = new JScrollPane(birthdayPanel);
        scrollPane.setPreferredSize(new Dimension(300, 40));
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        return scrollPane;
    }


    private void updateAge() {
        try {
            int month = monthComboBox.getSelectedIndex() + 1;
            int day = (Integer) dayComboBox.getSelectedItem();
            int year = (Integer) yearComboBox.getSelectedItem();

            LocalDate birthDate = LocalDate.of(year, month, day);
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(birthDate, currentDate).getYears();

            ageField.setText(String.valueOf(age));
        } catch (Exception e) {
            ageField.setText("");
        }
    }


    private void handleSignUp() {
    // Assume fields are set up in your GUI with variables like usernameField, nameField, etc.

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(
             "INSERT INTO users (username, full_name, age, sex, address, phone, email, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
        
        pstmt.setString(1, usernameField.getText().trim());
        pstmt.setString(2, nameField.getText().trim());
        pstmt.setInt(3, (Integer) ageSpinner.getValue());
        pstmt.setString(4, (String) sexComboBox.getSelectedItem());
        pstmt.setString(5, addressField.getText().trim());
        pstmt.setString(6, phoneField.getText().trim());
        pstmt.setString(7, emailField.getText().trim());
        pstmt.setString(8, new String(passField.getPassword()));

        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            JOptionPane.showMessageDialog(this, "Sign Up Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            openLoginPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save sign-up information. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        private Image logoImage;
        
        public LogoPanel() {
            setPreferredSize(new Dimension(getWidth(), 200));
            setBackground(PRIMARY_COLOR);
            
            // Load the logo image
            try {
                // Load image using ImageIO
                BufferedImage img = ImageIO.read(new File("C:\\Users\\User\\Downloads\\Mycarehealthcaresolution.png"));
                // Create a scaled version for better quality
                logoImage = img.getScaledInstance(-1, 150, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                e.printStackTrace();
                // Fallback text will be drawn if image loading fails
            }
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Enable antialiasing for better rendering
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            if (logoImage != null) {
                // Calculate position to center the image
                int imageWidth = logoImage.getWidth(this);
                int imageHeight = logoImage.getHeight(this);
                int x = (getWidth() - imageWidth) / 2;
                int y = (getHeight() - imageHeight) / 2;
                
                // Draw the image
                g2d.drawImage(logoImage, x, y, this);
            } else {
                // Fallback if image loading fails
                g2d.setFont(TITLE_FONT);
                g2d.setColor(Color.WHITE);
                String text = "MyCare HealthCare Solutions";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                g2d.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
            }
            
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