package Systems.Dashboard;

import Systems.Consultation.ConsultationParentPanel;
import Systems.Finance.FinancePanel;
import Systems.HealthCareFacilities.Healthcare;
import Systems.HospitalID.HospitalIDPanel;
import Systems.Laboratory.Laboratory;
import Systems.Login.LoginPanel;
import Systems.PatientManagement.PatientInfoPanel;
import Systems.PatientManagement.PatientManagement;
import Systems.Pharmacy.PharmacyPanel;
import Systems.Reports.ReportsPanel;
import Systems.Home.Home;
import Systems.PatientManagement.PatientInfoTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dashboard extends JFrame {

    private final JScrollPane leftScrollPane;
    private final JPanel rightPanel;
    private final Home homePanel;
    private final ConsultationParentPanel consultationParentPanel;
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
    private final PatientInfoTable patientInfoPanel;

    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI Light", Font.PLAIN, 24);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font CLOCK_FONT = new Font("Segoe UI Light", Font.PLAIN, 18);

    public Dashboard() {
        setTitle("MyCare HealthCare Solutions");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        darkMode = new DarkMode();
        createDarkModeToggle();

        // Initialize panels
        homePanel = new Home(darkMode);
        consultationParentPanel = new ConsultationParentPanel();
        hospitalIDPanel = new HospitalIDPanel(darkMode);
        reportsPanel = new ReportsPanel(darkMode);
        laboratoryPanel = new Laboratory(darkMode);
        healthcareFacilitiesPanel = new Healthcare(darkMode);
        pharmacyPanel = new PharmacyPanel(darkMode);
        financePanel = new FinancePanel(darkMode);
        patientInfoPanel = new PatientInfoTable(darkMode);

        JPanel leftPanel = createLeftPanel();
        leftScrollPane = new JScrollPane(leftPanel);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        leftScrollPane.setBorder(null);
        leftScrollPane.setPreferredSize(new Dimension(280, 0));

        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(darkMode.getBackgroundColor());

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        rightPanel.add(headerPanel, BorderLayout.NORTH);

        // Create content panel with CardLayout
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(darkMode.getBackgroundColor());

        // Add panels to content panel with CardLayout
        contentPanel.add(homePanel, "home");
        contentPanel.add(consultationParentPanel, "consultation");
        contentPanel.add(hospitalIDPanel, "hospitalid");
        // Ensure PatientInfoPanel is added correctly
        contentPanel.add(patientInfoPanel, "patientinfo");
        contentPanel.add(reportsPanel, "reports");
        contentPanel.add(laboratoryPanel, "laboratory");
        contentPanel.add(healthcareFacilitiesPanel, "healthcarefacilities");
        contentPanel.add(pharmacyPanel, "pharmacy");
        contentPanel.add(financePanel, "finance");

        rightPanel.add(contentPanel, BorderLayout.CENTER);

        // Add panels to content pane
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(leftScrollPane, BorderLayout.WEST);
        contentPane.add(rightPanel, BorderLayout.CENTER);

        updateColors();
        setVisible(true);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(darkMode.getBackgroundColor());
        panel.setBorder(new EmptyBorder(30, -50, 20, 20));

        // Add logo and company name
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        ImageIcon logoIcon = new ImageIcon("path/to/your/logo.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoPanel.add(logoLabel);

        JLabel companyName = new JLabel("Logo name");
        companyName.setForeground(darkMode.getTextColor());
        companyName.setFont(TITLE_FONT);
        logoPanel.add(Box.createHorizontalStrut(4));
        logoPanel.add(companyName);

        panel.add(logoPanel);
        panel.add(Box.createVerticalStrut(20));

        // Create clock and date panel
        JPanel clockPanel = createClockPanel();
        panel.add(clockPanel);
        panel.add(Box.createVerticalStrut(20));

        // Create buttons for left panel with icons and text
        String[][] buttonData = {
            {"Home", "\uD83C\uDFE0"},
            {"Consultation", "\uD83D\uDC68\u200D⚕\uFE0F"},
            {"Patient Information", "\uD83C\uDD94"},
            {"Hospital ID", "\uD83D\uDCCB"},
            {"Reports", "\uD83D\uDCCA"},
            {"Laboratory", "\uD83E\uDDEA"},
            {"Pharmacy", "\uD83D\uDC8A"},
            {"Healthcare Facility", "\uD83C\uDFE5"},
            {"Finance", "\uD83D\uDCB0"},
            {"Sign Out", "\uD83D\uDEAA"}
        };
        
        for (String[] data : buttonData) {
            JButton button = createButton(data[0], data[1]);
            panel.add(button);
            panel.add(Box.createVerticalStrut(10));
        
            // Add action listeners to buttons
            button.addActionListener(createActionListener(data[0].toLowerCase()));
        }
        panel.add(Box.createVerticalGlue());

        return panel;
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
            System.out.println("Button clicked for panel: " + panelName);
            showPanel(panelName.toLowerCase().replace(" ", ""));
        };
    }
    

    private void showPanel(String panelName) {
        System.out.println("Attempting to display panel: " + panelName); // Debug print
        CardLayout cardLayout = (CardLayout) ((JPanel) rightPanel.getComponent(1)).getLayout();
        cardLayout.show((JPanel) rightPanel.getComponent(1), panelName);
    
        System.out.println("Panel displayed: " + panelName); // Debug confirmation
    
        rightPanel.revalidate(); // Ensure the layout is refreshed
        rightPanel.repaint();
    
        // Refresh specific panels when shown
        switch (panelName) {
            case "pharmacy":
                pharmacyPanel.refreshData();
                break;
            case "finance":
                financePanel.refreshData();
                break;
             case "patientinfo":
                ((PatientInfoTable) patientInfoPanel).updateColors();
                ((PatientInfoTable) patientInfoPanel).refreshData(); // Add this line
                break;
            case "healthcarefacilities":
                healthcareFacilitiesPanel.refreshData();
                break;
            case "hospitalid":
                hospitalIDPanel.refreshData();
                break;
            case "reports":
                reportsPanel.refreshData();
                break;
        }
    }
    
    private void highlightButton(String panelName) {
        if (currentButton != null) {
            currentButton.setBackground(darkMode.getBackgroundColor());
        }
        for (Component comp : ((Container) leftScrollPane.getViewport().getView()).getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().toLowerCase().contains(panelName)) {
                currentButton = (JButton) comp;
                break;
            }
        }
        if (currentButton != null) {
            currentButton.setBackground(darkMode.getPrimaryColor());
        }
    }

    private void updateTitle(String panelName) {
        String title = switch (panelName) {
            case "home" -> "Dashboard";
            case "consultation" -> "Consultation";
            case "patientinfo" -> "Patient Information";
            case "hospitalid" -> "Hospital ID";
            case "reports" -> "Reports";
            case "laboratory" -> "Laboratory";
            case "pharmacy" -> "Pharmacy Management";
            case "healthcarefacilities" -> "Healthcare Facilities";
            case "finance" -> "Finance";
            default -> "MyCare HealthCare Solutions";
        };
        titleLabel.setText(title);
    }

    private JPanel createClockPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(darkMode.getBackgroundColor());
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(240, 60));

        JLabel clockLabel = new JLabel();
        clockLabel.setForeground(darkMode.getTextColor());
        clockLabel.setFont(CLOCK_FONT);
        clockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel();
        dateLabel.setForeground(darkMode.getTextColor());
        dateLabel.setFont(CLOCK_FONT);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(clockLabel);
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

    private void createDarkModeToggle() {
        darkModeToggle = new JToggleButton();
        darkModeToggle.setText(darkMode.isDarkMode() ? "Light Mode" : "Dark Mode");
        darkModeToggle.setToolTipText("Toggle Dark Mode");
        darkModeToggle.setBorderPainted(false);
        darkModeToggle.setFocusPainted(false);
        darkModeToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        darkModeToggle.addActionListener(e -> toggleDarkMode());
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkMode.getCardBackgroundColor());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(darkMode.getTextColor());
        panel.add(titleLabel, BorderLayout.WEST);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeaderPanel.setOpaque(false);

        JTextField searchBar = new JTextField(20);
        searchBar.setFont(MAIN_FONT);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(darkMode.getPrimaryColor()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchBar.setBackground(darkMode.getCardBackgroundColor());
        searchBar.setForeground(darkMode.getTextColor());
        rightHeaderPanel.add(searchBar);

        JButton notificationButton = new JButton("\uD83D\uDD14");
        notificationButton.setFont(MAIN_FONT);
        notificationButton.setFocusPainted(false);
        notificationButton.setBorderPainted(false);
        notificationButton.setBackground(darkMode.getCardBackgroundColor());
        notificationButton.setForeground(darkMode.getTextColor());
        notificationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightHeaderPanel.add(notificationButton);

        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        profilePanel.setOpaque(false);

        JLabel profileNameLabel = new JLabel("Dr. John Doe");
        profileNameLabel.setFont(MAIN_FONT);
        profileNameLabel.setForeground(darkMode.getTextColor());
        profilePanel.add(profileNameLabel);

        darkModeToggle = new JToggleButton(darkMode.isDarkMode() ? "Light Mode" : "Dark Mode");
        darkModeToggle.setFont(MAIN_FONT);
        darkModeToggle.addActionListener(e -> {
            darkMode.toggleDarkMode();
            updateColors();
            darkModeToggle.setText(darkMode.isDarkMode() ? "Light Mode" : "Dark Mode");
        });
        darkModeToggle.setBackground(darkMode.getCardBackgroundColor());
        darkModeToggle.setForeground(darkMode.getTextColor());
        profilePanel.add(darkModeToggle);

        JButton profileButton = new JButton("Profile \uD83D\uDC64");
        profileButton.setFont(MAIN_FONT);
        profileButton.setFocusPainted(false);
        profileButton.setBorderPainted(false);
        profileButton.setBackground(darkMode.getCardBackgroundColor());
        profileButton.setForeground(darkMode.getTextColor());
        profileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profilePanel.add(profileButton);

        rightHeaderPanel.add(profilePanel);
        panel.add(rightHeaderPanel, BorderLayout.EAST);

        return panel;
    }

    private void toggleDarkMode() {
        darkMode.toggleDarkMode();
        updateColors();
        JOptionPane.showMessageDialog(this, "Dark mode " + (darkMode.isDarkMode() ? "enabled" : "disabled"), "Dark Mode", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateColors() {
        getContentPane().setBackground(darkMode.getBackgroundColor());
        updateLeftPanelColors();
        updateRightPanelColors();
        updateHeaderColors();
    
        hospitalIDPanel.updateColors(darkMode);
        homePanel.updateColors(darkMode);
        laboratoryPanel.updateColors();
        healthcareFacilitiesPanel.updateColors();
        patientInfoPanel.updateColors();
        pharmacyPanel.updateColors();
        financePanel.updateColors(darkMode);
        repaint();
    }

    private void updateLeftPanelColors() {
        JPanel leftPanel = (JPanel) leftScrollPane.getViewport().getView();
        leftPanel.setBackground(darkMode.getBackgroundColor());
        for (Component comp : leftPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(darkMode.getBackgroundColor());
                button.setForeground(darkMode.getTextColor());
                for (Component innerComp : button.getComponents()) {
                    if (innerComp instanceof JLabel) {
                        innerComp.setForeground(darkMode.getTextColor());
                    }
                }
            } else if (comp instanceof JLabel) {
                comp.setForeground(darkMode.getTextColor());
            } else if (comp instanceof JPanel) {
                comp.setBackground(darkMode.getBackgroundColor());
                for (Component innerComp : ((JPanel) comp).getComponents()) {
                    if (innerComp instanceof JLabel) {
                        innerComp.setForeground(darkMode.getTextColor());
                    }
                }
            }
        }
    }

    private void updateRightPanelColors() {
        rightPanel.setBackground(darkMode.getBackgroundColor());
        for (Component comp : rightPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(darkMode.getBackgroundColor());
                for (Component innerComp : panel.getComponents()) {
                    if (innerComp instanceof JLabel) {
                        innerComp.setForeground(darkMode.getTextColor());
                    } else if (innerComp instanceof JTextField) {
                        JTextField textField = (JTextField) innerComp;
                        textField.setBackground(darkMode.getCardBackgroundColor());
                        textField.setForeground(darkMode.getTextColor());
                        textField.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(darkMode.getPrimaryColor()),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                    } else if (innerComp instanceof JButton) {
                        JButton button = (JButton) innerComp;
                        button.setBackground(darkMode.getPrimaryColor());
                        button.setForeground(darkMode.getTextColor());
                    }
                }
            }
        }
    }

    private void updateHeaderColors() {
        JPanel headerPanel = (JPanel) rightPanel.getComponent(0);
        darkMode.updateComponentColors(headerPanel);
        for (Component comp : headerPanel.getComponents()) {
            if (comp instanceof JPanel) {
                for (Component innerComp : ((JPanel) comp).getComponents()) {
                    if (innerComp instanceof JLabel || innerComp instanceof JButton || innerComp instanceof JToggleButton) {
                        innerComp.setBackground(darkMode.getCardBackgroundColor());
                        innerComp.setForeground(darkMode.getTextColor());
                    }
                }
            }
        }
    }

    private void signOut() {
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to sign out?", "Confirm Sign Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            JFrame loginFrame = new JFrame("Login");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setContentPane(new LoginPanel());
            loginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            loginFrame.setVisible(true);
            dispose();
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dashboard::new);
    }
}
