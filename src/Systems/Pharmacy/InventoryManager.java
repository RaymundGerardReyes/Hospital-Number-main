package Systems.Pharmacy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Systems.Database.DatabaseConnection;

public class InventoryManager {
    public int checkStockLevel(String productId) {
        String query = "SELECT stock_level FROM products WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock_level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}