package Systems.Consultation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class ConsultationParentPanel extends JPanel {
    private JTextPane transactionSlipPane;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private ConsultationPanel consultationPanel;
    private DoctorPanel doctorPanel;
    private Map<String, JButton> tabButtons;

    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 18);

    public ConsultationParentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initializeComponents();
    }

    private void initializeComponents() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createTransactionSlipPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Medical Consultation System");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        headerPanel.add(createTabPanel(), BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTabPanel() {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tabPanel.setOpaque(false);

        tabButtons = new HashMap<>();
        String[] tabs = {"Consultation", "Doctors"};

        for (String tab : tabs) {
            JButton tabButton = createTabButton(tab);
            tabButtons.put(tab, tabButton);
            tabPanel.add(tabButton);
        }

        return tabPanel;
    }

    private JButton createTabButton(String text) {
        JButton button = new JButton(text);
        button.setFont(SUBTITLE_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            cardLayout.show(contentPanel, text);
            updateTabButtonStyles();
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.getBackground().equals(SECONDARY_COLOR)) {
                    button.setBackground(PRIMARY_COLOR.brighter());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.getBackground().equals(SECONDARY_COLOR)) {
                    button.setBackground(PRIMARY_COLOR);
                }
            }
        });

        return button;
    }

    private JPanel createContentPanel() {
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(BACKGROUND_COLOR);

        consultationPanel = new ConsultationPanel(this);
        doctorPanel = new DoctorPanel(consultationPanel, this);

        contentPanel.add(consultationPanel, "Consultation");
        contentPanel.add(doctorPanel, "Doctors");

        // Set initial active tab
        updateTabButtonStyles();

        return contentPanel;
    }

    private JPanel createTransactionSlipPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel slipLabel = new JLabel("Transaction Slip");
        slipLabel.setFont(SUBTITLE_FONT);
        slipLabel.setForeground(TEXT_COLOR);
        panel.add(slipLabel, BorderLayout.NORTH);

        transactionSlipPane = new JTextPane();
        transactionSlipPane.setEditable(false);
        transactionSlipPane.setFont(MAIN_FONT);
        transactionSlipPane.setBackground(Color.WHITE);
        transactionSlipPane.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(SECONDARY_COLOR, 1),
                new EmptyBorder(10, 10, 10, 10)));

        JScrollPane scrollPane = new JScrollPane(transactionSlipPane);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void updateTabButtonStyles() {
        for (Map.Entry<String, JButton> entry : tabButtons.entrySet()) {
            JButton button = entry.getValue();
            boolean isActive = contentPanel.getComponent(0).isVisible() && entry.getKey().equals("Consultation")
                    || contentPanel.getComponent(1).isVisible() && entry.getKey().equals("Doctors");
            button.setBackground(isActive ? SECONDARY_COLOR : PRIMARY_COLOR);
        }
    }

    public void updateTransactionSlip(String text) {
        SwingUtilities.invokeLater(() -> {
            String currentText = transactionSlipPane.getText();
            String newText = currentText.isEmpty() ? text : currentText + "\n\n" + text;
            transactionSlipPane.setText(newText);
            transactionSlipPane.setCaretPosition(transactionSlipPane.getDocument().getLength());

            // Provide visual feedback
            transactionSlipPane.setBackground(new Color(240, 255, 240)); // Light green background
            Timer timer = new Timer(1500, e -> transactionSlipPane.setBackground(Color.WHITE));
            timer.setRepeats(false);
            timer.start();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Medical Consultation System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new ConsultationParentPanel());
            frame.pack();
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}