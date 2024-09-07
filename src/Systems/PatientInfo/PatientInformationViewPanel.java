package Systems.PatientInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class PatientInformationViewPanel extends JPanel {

    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton editButton;
    private boolean isEditing = false;
    private int editingRow = -1;

    public PatientInformationViewPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Initialize search field and add listener
        searchField = new JTextField(20);
        searchField.setMaximumSize(new Dimension(200, 30));
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchField.addActionListener(e -> filterTable());

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalGlue());
        searchPanel.setBorder(new EmptyBorder(5, 0, 10, 0));
        add(searchPanel, BorderLayout.NORTH);

        // Initialize table
        tableModel = new DefaultTableModel(new String[]{"Name", "Age", "Birthday", "Sex Identity", "Address", "Phone Number", "Hospital ID", "Health Concern"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow editing if isEditing is true and the row is the editingRow
                return isEditing && row == editingRow;
            }
        };
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Set up table sorting and filtering
        sorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(sorter);

        // Load data from file
        PatientInformationHandler.readDataFromFile(tableModel);

        // Add button panel for editing features
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // Add edit button
        editButton = new JButton("Edit");
        editButton.addActionListener(e -> enableEditing());
        buttonPanel.add(editButton);

        // Add delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedRows());
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void filterTable() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            } catch (PatternSyntaxException ex) {
                System.err.println("Error in search syntax: " + ex.getMessage());
            }
        }
    }

    private void enableEditing() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            if (isEditing && selectedRow == editingRow) {
                isEditing = false;
                editingRow = -1;
                editButton.setText("Edit");
            } else {
                isEditing = true;
                editingRow = selectedRow;
                editButton.setText("Save");
            }
            tableModel.fireTableDataChanged();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedRows() {
        int[] selectedRows = employeeTable.getSelectedRows();
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            tableModel.removeRow(employeeTable.convertRowIndexToModel(selectedRows[i]));
        }
    }

    public void updateTable(String[] data) {
        tableModel.addRow(data);
    }

    private String[] getEmployeeData() {
        // Return dummy data for example purposes
        return new String[]{"", "", "", "", "", "", "", "", "", "", ""};
    }

    private void clearFields() {
        // Implement if necessary
    }

   
}
