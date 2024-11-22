package Systems.Laboratory;

import Systems.Database.DatabaseConnection;
import Systems.Laboratory.LaboratoryManager.LaboratoryTest;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LaboratoryTestInserter {

    private final Window owner;
    private final LaboratoryManager labManager;

    public LaboratoryTestInserter(Window owner, LaboratoryManager labManager) {
        this.owner = owner;
        this.labManager = labManager;
    }

    /**
     * Please do provide a button for inserting a new Test on it. 
     */
     private void insertTest(LaboratoryTest test) {
        String insertSQL = "INSERT INTO tests (test_name, category_id, price) " +
                           "SELECT ?, category_id, 0 FROM categories WHERE category_name = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {

            statement.setString(1, test.getTestName());
            statement.setString(2, test.getCategory());

            int rowsInserted = statement.executeUpdate();
            System.out.println("Inserted " + rowsInserted + " test into the database.");

        } catch (SQLException e) {
            System.err.println("Error inserting test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertNewTest() {
        TestSelectionPanel dialog = new TestSelectionPanel(owner, labManager);
        dialog.setVisible(true);
        LaboratoryTest selectedTest = dialog.getSelectedTest();

        if (selectedTest != null) {
            insertTest(selectedTest);
        } else {
            System.out.println("No test selected. Aborting insertion.");
        }
    }


    /**
     * Updates the price for a specific test in the database.
     *
     * @param testName The name of the test.
     * @param price    The new price of the test.
     */
    public void updateTestPrice(String testName, BigDecimal price) {
        String updateSQL = "UPDATE tests SET price = ? WHERE test_name = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateSQL)) {

            statement.setBigDecimal(1, price);
            statement.setString(2, testName);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Updated price for test: " + testName + " to " + price);
            } else {
                System.out.println("No test found with name: " + testName);
            }

        } catch (SQLException e) {
            System.err.println("Error updating test price: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Derives the category name for a test. Replace with actual mapping logic.
     *
     * @param testName The name of the test.
     * @return The derived category name, or null if no mapping is found.
     */
    private String deriveCategoryFromTest(String testName) {
        // Replace with actual mapping logic or user input.
        return "Default Category";
    }
}
