package Systems.Login;

import Systems.Dashboard.Dashboard;
import Systems.Signup.SignUp;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginPanel extends JPanel {
    private JTextField userField;
    private JPasswordField passField;
    private JProgressBar progressBar;
    private JButton loginButton;
    private JButton signupButton;
    private static List<Runnable> loginSuccessListeners = new ArrayList<>();

    private Image logoImage;

    private static final Color PRIMARY_COLOR = new Color(0, 50, 100);
    private static final Color SECONDARY_COLOR = new Color(240, 248, 255);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color PLACEHOLDER_COLOR = new Color(158, 158, 158);

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public LoginPanel() {
        setPreferredSize(new Dimension(1300, 700));
        setBackground(SECONDARY_COLOR);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel logoPanel = createLogoPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(logoPanel, gbc);

        JPanel formPanel = createFormPanel();
        gbc.gridy = 1;
        add(formPanel, gbc);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(300, 25));
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setBackground(SECONDARY_COLOR);
        gbc.gridy = 2;
        add(progressBar, gbc);

        addActionListeners();

        try {
            logoImage = ImageIO.read(new File("C:\\Users\\User\\Downloads\\Mycarehealthcaresolution.png"));
            revalidate();
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Could not load logo image.");
        }
    }


    public static void addLoginSuccessListener(Runnable listener) {
        loginSuccessListeners.add(listener);
    }

    public void handleLoginSuccess() {
        for (Runnable listener : loginSuccessListeners) {
            listener.run();
        }
    }

    private JPanel createLogoPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                if (logoImage != null) {
                    int targetWidth = Math.min(getWidth() - 20, 400);
                    int targetHeight = (int) ((double) targetWidth / logoImage.getWidth(null) * logoImage.getHeight(null));

                    int x = (getWidth() - targetWidth) / 2;
                    int y = (getHeight() - targetHeight) / 2;

                    g2d.drawImage(logoImage, x, y, targetWidth, targetHeight, null);
                } else {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(TITLE_FONT);
                    String text = "MyCare HealthCare Solutions";
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(text);
                    g2d.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
                }

                g2d.dispose();
            }
        };

        panel.setPreferredSize(new Dimension(400, 200));
        panel.setBackground(PRIMARY_COLOR);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                new EmptyBorder(20, 40, 20, 40)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Username field
        userField = createStyledTextField("Username");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userField, gbc);

        // Password field
        passField = createStyledPasswordField("Password");
        gbc.gridy = 1;
        panel.add(passField, gbc);

        // Login button
        loginButton = createStyledButton("Login", PRIMARY_COLOR, Color.WHITE);
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        panel.add(loginButton, gbc);

        // Sign up button
        signupButton = createStyledButton("Sign Up", Color.WHITE, PRIMARY_COLOR);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(signupButton, gbc);

        return panel;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(20);
        textField.setFont(MAIN_FONT);
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        textField.setText(placeholder);
        textField.setForeground(PLACEHOLDER_COLOR);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(TEXT_COLOR);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
        return textField;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(MAIN_FONT);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        passwordField.setEchoChar((char) 0);
        passwordField.setText(placeholder);
        passwordField.setForeground(PLACEHOLDER_COLOR);
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordField.setForeground(TEXT_COLOR);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setText(placeholder);
                    passwordField.setEchoChar((char) 0);
                    passwordField.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
        return passwordField;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }


    private void addActionListeners() {
        loginButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (validateLogin(username, password)) {
                showLoginSuccessDialog();
                startProgressBar();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password. Please try again.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        signupButton.addActionListener(e -> {
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            currentFrame.dispose();
            SwingUtilities.invokeLater(() -> new SignUp());
        });
    }

    private boolean validateLogin(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return storedPassword.equals(password); // For actual security, compare hash values here
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    private void showLoginSuccessDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Success", true);
        dialog.setLayout(new BorderLayout());
        JLabel messageLabel = new JLabel("Login successful!", SwingConstants.CENTER);
        messageLabel.setFont(MAIN_FONT);
        dialog.add(messageLabel, BorderLayout.CENTER);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        Timer timer = new Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    private void startProgressBar() {
        progressBar.setVisible(true);
        Timer timer = new Timer(20, null);
        timer.addActionListener(event -> {
            int value = progressBar.getValue();
            if (value < 100) {
                progressBar.setValue(value + 1);
            } else {
                timer.stop();
                JFrame dashboardFrame = new Dashboard();
                dashboardFrame.setVisible(true);
                JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                loginFrame.dispose();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hospital Management System - Login");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new LoginPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}