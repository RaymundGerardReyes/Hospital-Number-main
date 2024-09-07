package Systems.Dashboard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Systems.Consultation.ConsultationParentPanel;
import Systems.Employees.Employees;
import Systems.HospitalID.HospitalIDPanel;
import Systems.Login.LoginPanel;
import Systems.PatientInfo.PatientInfoButton;
import Systems.PatientInfo.PatientInformationViewPanel;

public class Dashboard extends JFrame {

    private final JPanel leftPanel;
    private final JPanel rightPanel;
    private ConsultationParentPanel consultationParentPanel;
    private final Employees employeesPanel;
    private final PatientInfoButton patientInfoButtonPanel;
    private final PatientInformationViewPanel patientInfoViewPanel;
    private JButton consultationButton;
    private JButton employeesButton;
    private JButton patientInfoButton;
    private JButton hospitalIdButton;
    private JButton patientInfoViewButton;
    private JButton signOutButton;
    private JButton currentButton;
    private HospitalIDPanel hospitalIDPanel;

    public Dashboard() {
        setTitle("Hospital Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize panels
        patientInfoViewPanel = new PatientInformationViewPanel();
        patientInfoButtonPanel = new PatientInfoButton(patientInfoViewPanel);
        consultationParentPanel = new ConsultationParentPanel();
        employeesPanel = new Employees();
        hospitalIDPanel = new HospitalIDPanel();

        leftPanel = createLeftPanel();
        rightPanel = new JPanel(new CardLayout());
        rightPanel.setBackground(Color.WHITE);

        // Add panels to right panel with CardLayout
        rightPanel.add(consultationParentPanel, "consultation");
        rightPanel.add(employeesPanel, "employees");
        rightPanel.add(patientInfoButtonPanel, "patientInfoButton");
        rightPanel.add(patientInfoViewPanel, "patientInfoView");
        rightPanel.add(hospitalIDPanel, "hospitalID");

        // Add panels to content pane
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(leftPanel, BorderLayout.WEST);
        contentPane.add(rightPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(230, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Create clock and date panel
        JPanel clockPanel = createClockPanel();

        // Create buttons for left panel
        consultationButton = createButton("Consultation");
        employeesButton = createButton("Employees");
        patientInfoButton = createButton("Patient Info");
        hospitalIdButton = createButton("Hospital ID");
        patientInfoViewButton = createButton("Patient Info View");
        signOutButton = createButton("Sign Out");

        // Add action listeners to buttons
        consultationButton.addActionListener(e -> {
            showPanel("consultation");
            highlightButton(consultationButton);
        });
        employeesButton.addActionListener(e -> {
            showPanel("employees");
            highlightButton(employeesButton);
        });
        patientInfoButton.addActionListener(e -> {
            showPanel("patientInfoButton");
            highlightButton(patientInfoButton);
        });
        hospitalIdButton.addActionListener(e -> {
            showPanel("hospitalID");
            highlightButton(hospitalIdButton);
        });
        patientInfoViewButton.addActionListener(e -> {
            showPanel("patientInfoView");
            highlightButton(patientInfoViewButton);
        });
        signOutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to sign out?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                JFrame loginFrame = new JFrame("Login");
                loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                loginFrame.setContentPane(new LoginPanel());
                loginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                loginFrame.setVisible(true);
                dispose();
            }
        });

        // Add clock panel and buttons to panel with equal spacing
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(clockPanel, gbc);
        panel.add(consultationButton, gbc);
        panel.add(employeesButton, gbc);
        panel.add(patientInfoButton, gbc);
        panel.add(hospitalIdButton, gbc);
        panel.add(patientInfoViewButton, gbc);
        panel.add(signOutButton, gbc);

        return panel;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) rightPanel.getLayout();
        cardLayout.show(rightPanel, panelName);
    }

    private void highlightButton(JButton button) {
        if (currentButton != null) {
            currentButton.setBackground(new Color(50, 50, 50));
            currentButton.setForeground(Color.WHITE);
        }
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        currentButton = button;
    }

    private JPanel createClockPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel clockLabel = new JLabel();
        clockLabel.setForeground(Color.WHITE);
        clockLabel.setFont(new Font("Arial", Font.BOLD, 18));
        clockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel dateLabel = new JLabel();
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(clockLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(dateLabel);

        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
            Date now = new Date();
            clockLabel.setText(timeFormat.format(now));
            dateLabel.setText(dateFormat.format(now));
        });
        timer.start();

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Dashboard();
        });
    }
}
