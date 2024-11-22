/*package Systems.Pharmacy;

import Systems.Dashboard.DarkMode;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import Systems.Pharmacy.Sales.Transaction;

public class SalesHistoryPanel extends JPanel {
    private final Pharmacy pharmacy;
    private final DarkMode darkMode;
    private JTable salesTable;
    private DefaultTableModel tableModel;

    public SalesHistoryPanel(Pharmacy pharmacy, DarkMode darkMode) {
        this.pharmacy = pharmacy;
        this.darkMode = darkMode;
        setLayout(new BorderLayout());

        initComponents();
        layoutComponents();
        updateColors();
    }

    private void initComponents() {
        String[] columnNames = {"Receipt #", "Date", "Total Price", "Payment Method", "Hospital ID", "View Details"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Make table cells non-editable
            }
        };
        
        salesTable = new JTable(tableModel);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void layoutComponents() {
        JScrollPane scrollPane = new JScrollPane(salesTable);
        add(scrollPane, BorderLayout.CENTER);
    }

   public void updateColors() {
        setBackground(darkMode.getBackgroundColor());
        salesTable.setBackground(darkMode.getCardBackgroundColor());
        salesTable.setForeground(darkMode.getTextColor());
        salesTable.setGridColor(darkMode.getBorderColor());
        salesTable.getTableHeader().setBackground(darkMode.getButtonBackgroundColor());
        salesTable.getTableHeader().setForeground(darkMode.getButtonTextColor());

        // Set selected row colors
        salesTable.setSelectionBackground(darkMode.getPrimaryColor().brighter());
        salesTable.setSelectionForeground(darkMode.getTextColor());

        // Update row renderer for consistent colors
        salesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(isSelected ? darkMode.getPrimaryColor().brighter() : darkMode.getCardBackgroundColor());
                c.setForeground(darkMode.getTextColor());
                return c;
            }
        });
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Transaction> salesHistory = pharmacy.getSalesHistory(); // getSalesHistory() returns List<Transaction>
    
        if (salesHistory != null) {
            for (Transaction transaction : salesHistory) {
                tableModel.addRow(new Object[]{
                    transaction.getId(),
                    transaction.getDate(),
                    String.format("$%.2f", transaction.getTotalPrice()),
                    transaction.getPaymentMethod(),
                    transaction.getHospitalId(),
                    "View"
                });
            }
        }
    }
}    

*/