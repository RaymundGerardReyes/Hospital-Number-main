package Systems.Laboratory;

import Systems.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Utility class for retrieving test categories, tests, and their prices from the database.
 */
public class TestCategoryUtils {

    /**
     * Fetches categories, their associated tests, and test prices from the database.
     *
     * @return A map of test categories, their associated tests, and prices.
     */
    public static Map<String, Map<String, Integer>> initializeTestCategoriesWithPrices() {
        Map<String, Map<String, Integer>> categories = new LinkedHashMap<>();
        String categoryQuery = "SELECT c.category_name, t.test_name, t.price " +
                               "FROM categories c " +
                               "LEFT JOIN tests t ON c.category_id = t.category_id " +
                               "ORDER BY c.category_name, t.test_name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(categoryQuery);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String categoryName = resultSet.getString("category_name");
                String testName = resultSet.getString("test_name");
                int testPrice = resultSet.getInt("price");

                // Initialize category map if not present
                categories.computeIfAbsent(categoryName, k -> new LinkedHashMap<>())
                          .put(testName, testPrice);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching test categories and prices: " + e.getMessage());
            e.printStackTrace();
        }

        return categories;
    }
}
