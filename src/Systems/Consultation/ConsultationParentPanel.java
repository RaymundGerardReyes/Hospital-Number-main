package Systems.Consultation;

import java.awt.*;
import javax.swing.*;

public class ConsultationParentPanel extends JPanel {
    private JTextPane transactionSlipPane; // Use JTextPane for better styling

    public ConsultationParentPanel() {
        setLayout(new BorderLayout());

        // Initialize consultation panel
        ConsultationPanel consultationPanel = new ConsultationPanel();
        if (consultationPanel == null) {
            throw new IllegalStateException("ConsultationPanel cannot be null.");
        }

        // Initialize doctor panel and pass this panel to it
        DoctorPanel doctorPanel = new DoctorPanel(consultationPanel, this);
        if (doctorPanel == null) {
            throw new IllegalStateException("DoctorPanel cannot be null.");
        }

        // Create a split pane to hold the consultation and doctor panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, consultationPanel, doctorPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        // Initialize the text pane for transaction slips with border highlights
        transactionSlipPane = new JTextPane();
        if (transactionSlipPane == null) {
            throw new IllegalStateException("transactionSlipPane cannot be null.");
        }
        transactionSlipPane.setEditable(false);
        transactionSlipPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        JScrollPane transactionScrollPane = new JScrollPane(transactionSlipPane);
        transactionScrollPane.setPreferredSize(new Dimension(1200, 200));

        // Add components to the main panel
        add(splitPane, BorderLayout.CENTER);
        add(transactionScrollPane, BorderLayout.SOUTH); // Add the transaction slip area below the split pane
    }

    public JTextPane getTransactionSlipPane() {
        return transactionSlipPane;
    }
}
