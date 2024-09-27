package Systems.Dashboard;

import Systems.Consultation.ConsultationParentPanel;
import Systems.Employees.Employees;
import Systems.HospitalID.HospitalIDPanel;
import Systems.PatientInfo.PatientInfoButton;
import Systems.PatientInfo.PatientInformationViewPanel;
import Systems.Reports.ReportsPanel;
import Systems.Laboratory.Laboratory;
import Systems.HealthCareFacilities.Healthcare;
import Systems.Pharmacy.PharmacyPanel;
import Systems.Finance.Finance;
import Systems.Finance.FinancePanel;
import Systems.Home.Home;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DashboardDesign extends JFrame {

    private final JScrollPane leftScrollPane;
    private final JPanel rightPanel;
    private JPanel contentPanel; 
    private final Home homePanel;
    private final ConsultationParentPanel consultationParentPanel;
    private final Employees employeesPanel;
    private final PatientInfoButton patientInfoButtonPanel;
    private final PatientInformationViewPanel patientInfoViewPanel;
    private final HospitalIDPanel hospitalIDPanel;
    private final ReportsPanel reportsPanel;
    private final Laboratory laboratoryPanel;
    private final Healthcare healthcareFacilitiesPanel;
    private final PharmacyPanel pharmacyPanel;
    private final FinancePanel financePanel;
    private JButton currentButton;
    private JLabel titleLabel;
    private JToggleButton darkModeToggle;
    private DarkMode darkMode;
    private Finance financeSystem;

    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI Light", Font.PLAIN, 24);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font CLOCK_FONT = new Font("Segoe UI Light", Font.PLAIN, 18);

    public DashboardDesign() {
        this.darkMode = darkMode;

        setTitle("MyCare HealthCare Solutions");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize panels
        homePanel = new Home(darkMode);
        patientInfoViewPanel = new PatientInformationViewPanel();
        patientInfoButtonPanel = new PatientInfoButton(patientInfoViewPanel);
        consultationParentPanel = new ConsultationParentPanel();
        employeesPanel = new Employees();
        hospitalIDPanel = new HospitalIDPanel();
        reportsPanel = new ReportsPanel();
        laboratoryPanel = new Laboratory(darkMode);
        healthcareFacilitiesPanel = new Healthcare(darkMode);
        pharmacyPanel = new PharmacyPanel(darkMode);
        financeSystem = new Finance();
        financePanel = new FinancePanel(financeSystem, darkMode);

        leftScrollPane = createLeftScrollPane();
        rightPanel = createRightPanel();

        // Add both left and right panels to the main content pane
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(leftScrollPane, BorderLayout.WEST);
        contentPane.add(rightPanel, BorderLayout.CENTER);

        // Set initial colors and make the frame visible
        updateColors();
        setVisible(true);
    }

    private JScrollPane createLeftScrollPane() {
        JPanel leftPanel = createLeftPanel();
        JScrollPane scrollPane = new JScrollPane(leftPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(280, 0));
        return scrollPane;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkMode.getBackgroundColor());
    
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        panel.add(headerPanel, BorderLayout.NORTH);
    
        // Create content panel with CardLayout for switching panels
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(darkMode.getBackgroundColor());
    
        // Add all panels to contentPanel
        contentPanel.add(homePanel, "home");
        contentPanel.add(consultationParentPanel, "consultation");
        contentPanel.add(employeesPanel, "employees");
        contentPanel.add(patientInfoButtonPanel, "patientInfo");
        contentPanel.add(patientInfoViewPanel, "patientInfoView");
        contentPanel.add(hospitalIDPanel, "hospitalID");
        contentPanel.add(reportsPanel, "reports");
        contentPanel.add(laboratoryPanel, "laboratory");
        contentPanel.add(healthcareFacilitiesPanel, "healthcareFacilities");
        contentPanel.add(pharmacyPanel, "pharmacy");
        contentPanel.add(financePanel, "finance");
    
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(darkMode.getBackgroundColor());
        panel.setBorder(new EmptyBorder(30, 20, 20, 20));

        // Add logo and company name
        panel.add(createLogoPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createClockPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createButtonPanel());
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        ImageIcon logoIcon = new ImageIcon("path/to/your/logo.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoPanel.add(logoLabel);

        JLabel companyName = new JLabel("MyCare HealthCare");
        companyName.setForeground(darkMode.getTextColor());
        companyName.setFont(TITLE_FONT);
        logoPanel.add(Box.createHorizontalStrut(10));
        logoPanel.add(companyName);

        return logoPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        String[][] buttonData = {
                {"Home", "\uD83C\uDFE0"},
                {"Consultation", "\uD83D\uDC68\u200D⚕\uFE0F"},
                {"Patient Info", "\uD83C\uDD94"},
                {"Hospital ID", "\uD83D\uDCCB"},
                {"Reports", "\uD83D\uDCCA"},
                {"Laboratory", "\uD83E\uDDEA"},
                {"Pharmacy", "\uD83D\uDC8A"},
                {"Healthcare Facility", "\uD83C\uDFE5"},
                {"Finance", "\uD83D\uDCB0"},
                {"Sign Out", "\uD83D\uDEAA"}
        };

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        for (String[] data : buttonData) {
            JButton button = createButton(data[0], data[1]);
            button.addActionListener(createActionListener(data[0].toLowerCase()));
            buttonPanel.add(button);
            buttonPanel.add(Box.createVerticalStrut(10));
        }
        return buttonPanel;
    }

    private JButton createButton(String text, String icon) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 0));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(darkMode.getTextColor());
        button.add(iconLabel, BorderLayout.WEST);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(BUTTON_FONT);
        textLabel.setForeground(darkMode.getTextColor());
        button.add(textLabel, BorderLayout.CENTER);

        button.setBackground(darkMode.getBackgroundColor());
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(240, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darkMode.getPrimaryColor());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != currentButton) {
                    button.setBackground(darkMode.getBackgroundColor());
                }
            }
        });

        return button;
    }

    private ActionListener createActionListener(String panelName) {
        return e -> {
            if (panelName.equals("sign out")) {
                signOut();
            } else if (panelName.equals("healthcare facility")) {
                showPanel("healthcareFacilities");
            } else {
                showPanel(panelName.replace(" ", ""));
            }
        };
    }

    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);
        highlightButton(panelName);
        updateTitle(panelName);
        updateColors();

        // Refresh specific panels when shown
        switch (panelName) {
            case "pharmacy":
                pharmacyPanel.refreshData();
                break;
            case "finance":
                financePanel.refreshData();
                break;
            case "healthcareFacilities":
                healthcareFacilitiesPanel.refreshData();
                break;
        }
    }

    private void highlightButton(String panelName) {
        if (currentButton != null) {
            currentButton.setBackground(darkMode.getBackgroundColor());
        }
    
        for (Component component : ((JPanel) ((JScrollPane) leftScrollPane).getViewport().getView()).getComponents()) {
            if (component instanceof JButton && component.getName() != null && component.getName().equals(panelName)) {
                currentButton = (JButton) component;
                currentButton.setBackground(darkMode.getPrimaryColor());
                break;
            }
        }
    }

    private void updateTitle(String panelName) {
        String title = switch (panelName) {
            case "home" -> "Home";
            case "consultation" -> "Consultation";
            case "employees" -> "Employees";
            case "patientInfo" -> "Patient Info";
            case "hospitalID" -> "Hospital ID";
            case "reports" -> "Reports";
            case "laboratory" -> "Laboratory";
            case "pharmacy" -> "Pharmacy";
            case "healthcareFacilities" -> "Healthcare Facilities";
            case "finance" -> "Finance";
            default -> "";
        };

        titleLabel.setText(title);
    }

    private JPanel createClockPanel() {
        JPanel clockPanel = new JPanel();
        clockPanel.setLayout(new BoxLayout(clockPanel, BoxLayout.Y_AXIS));
        clockPanel.setOpaque(false);

        JLabel clockLabel = new JLabel();
        clockLabel.setFont(CLOCK_FONT);
        clockLabel.setForeground(darkMode.getTextColor());
        clockPanel.add(clockLabel);

        // Update the clock every second
        Timer timer = new Timer(1000, e -> {
            String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
            clockLabel.setText(currentTime);
        });
        timer.start();

        return clockPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(darkMode.getBackgroundColor());
        
        titleLabel = new JLabel("Home");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(darkMode.getTextColor());
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        darkModeToggle = new JToggleButton("Dark Mode");
        darkModeToggle.setSelected(darkMode.isDarkMode());
        darkModeToggle.addActionListener(e -> toggleDarkMode());
        headerPanel.add(darkModeToggle, BorderLayout.EAST);

        return headerPanel;
    }

    private void toggleDarkMode() {
        darkMode.setDarkMode(!darkMode.isDarkMode());
        updateColors();
    }

    private void updateColors() {
        getContentPane().setBackground(darkMode.getBackgroundColor());
        rightPanel.setBackground(darkMode.getBackgroundColor());
        contentPanel.setBackground(darkMode.getBackgroundColor());
        titleLabel.setForeground(darkMode.getTextColor());
        darkModeToggle.setForeground(darkMode.getTextColor());
        leftScrollPane.getViewport().setBackground(darkMode.getBackgroundColor());
    }

    private void signOut() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to sign out?", "Sign Out", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            // Handle sign out logic here
            System.exit(0);
        }
    }

}
