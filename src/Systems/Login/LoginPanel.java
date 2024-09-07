package Systems.Login;

import Systems.Dashboard.Dashboard;
import Systems.Signup.SignUp;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;

public class LoginPanel extends JPanel {
    private JTextField userField;
    private JPasswordField passField;
    private JProgressBar progressBar;

    public LoginPanel() {
        setPreferredSize(new Dimension(1300, 700));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Centering username label and text field
        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(userLabel, gbc);

        userField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(userField, gbc);

        // Centering password label and password field
        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(passLabel, gbc);

        passField = new JPasswordField(25);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(passField, gbc);

        // Centering login button
        JButton loginButton = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // Aligning signup button
        JButton signupButton = new JButton("Sign Up");
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(signupButton, gbc);

        // Adding ActionListener to signupButton to open SignUp window
        signupButton.addActionListener(e -> {
            new SignUp(); // Open the SignUp window
            JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            loginFrame.dispose(); // Close the login window
        });

        // Adding progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        add(progressBar, gbc);

        // Adding ActionListener to loginButton for login functionality
        loginButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            // Validate login credentials against saved sign-up data
            boolean loggedIn = validateLogin(username, password);

            if (loggedIn) {
                // Show custom "Login successful!" dialog
                showLoginSuccessDialog();

                // Display and start the loading bar
                progressBar.setVisible(true);
                Timer timer = new Timer(20, null);
                timer.addActionListener(event -> {
                    int value = progressBar.getValue();
                    if (value < 100) {
                        progressBar.setValue(value + 1);
                    } else {
                        timer.stop();
                        // Proceed with your application logic after loading is complete
                        JFrame dashboardFrame = new Dashboard();
                        dashboardFrame.setVisible(true);
                        JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                        loginFrame.dispose(); // Close the login window
                    }
                });
                timer.start();
            } else {
                // If login failed, show error message
                JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.");
            }
        });
    }

    // Method to validate login credentials
    private boolean validateLogin(String username, String password) {
        final String filePath = "src/Systems/UserInfo/signup_info.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String savedUsername = null;
            String savedPassword = null;
            boolean userFound = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username: ")) {
                    savedUsername = line.substring(10).trim();
                    userFound = savedUsername.equals(username);
                } else if (userFound && line.startsWith("Password: ")) {
                    savedPassword = line.substring(10).trim();
                    if (savedPassword.equals(password)) {
                        return true;
                    } else {
                        userFound = false;
                    }
                } else if (line.equals("---------------------------")) {
                    userFound = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to show the custom "Login successful!" dialog
    private void showLoginSuccessDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Success", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JLabel("Login successful!", SwingConstants.CENTER), BorderLayout.CENTER);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        Timer timer = new Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }
}
