package Systems.Signup;

import Systems.Login.LoginPanel;
import Systems.UserInfo.SignUpInfo;
import java.awt.*;
import javax.swing.*;

public class SignUp {
    private JFrame frame;

    public SignUp() {
        frame = new JFrame("Sign Up");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this frame
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.LINE_END;

        // Name
        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(nameField, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(usernameField, gbc);

        // Age
        JLabel ageLabel = new JLabel("Age:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(ageLabel, gbc);

        JTextField ageField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(ageField, gbc);

        // Sex Label
        JLabel sexLabel = new JLabel("Sex:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(sexLabel, gbc);

        // Sex Options (for JComboBox)
        String[] sexOptions = {"Male", "Female", "Other", "Prefer not to say", "Non-binary", "Genderqueer", "Transgender", "Intersex"};
        JComboBox<String> sexComboBox = new JComboBox<>(sexOptions);
        sexComboBox.setPreferredSize(new Dimension(200, 30)); // Set preferred size as needed

        // Wrap the ComboBox in a JScrollPane
        JScrollPane sexScrollPane = new JScrollPane(sexComboBox);
        sexScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scroll bar

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridheight = 1; // Reset grid height if needed
        panel.add(sexScrollPane, gbc);

        // Address
        JLabel addressLabel = new JLabel("Address:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(addressLabel, gbc);

        JTextField addressField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(addressField, gbc);

        // Phone
        JLabel phoneLabel = new JLabel("Phone:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(phoneLabel, gbc);

        JTextField phoneField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(phoneField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(emailField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(passField, gbc);

        // Confirm Password
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(confirmPassLabel, gbc);

        JPasswordField confirmPassField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(confirmPassField, gbc);

        // Sign Up Button
        JButton signupButton = new JButton("Sign Up");
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(signupButton, gbc);

        // Add panel to frame
        frame.add(panel);
        frame.setVisible(true);

        // Action listener for sign up button
        signupButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim(); // Retrieve username
            String age = ageField.getText().trim();
            String sex = (String) sexComboBox.getSelectedItem(); // Retrieve selected sex
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String confirmPassword = new String(confirmPassField.getPassword());

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match. Please try again.");
                return;
            }

            // Create SignUpInfo object
            SignUpInfo signUpInfo = new SignUpInfo(username, name, age, sex, address, phone, email, password);

            // Save to file
            boolean saved = signUpInfo.saveToFile();
            if (saved) {
                JOptionPane.showMessageDialog(frame, "Sign Up Successful!");

                // Close the sign up window
                frame.dispose();

                // Open the LoginPanel window
                JFrame loginFrame = new JFrame("Login");
                loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Adjust as per your application
                loginFrame.setSize(1300, 700); // Adjust size as per your LoginPanel preferences
                loginFrame.setLocationRelativeTo(null); // Center on screen
                loginFrame.setResizable(false);

                // Add LoginPanel to loginFrame
                LoginPanel loginPanel = new LoginPanel();
                loginFrame.add(loginPanel);

                // Display the loginFrame
                loginFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to save sign-up information. Please try again.");
            }
        });
    }
}
