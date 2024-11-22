/* package Systems.Pharmacy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Systems.Pharmacy.Pharmacy.Sale;
import Systems.Pharmacy.Pharmacy.Transaction;

public class TransactionService {

    // Method to retrieve sales history
    public List<Transaction> getSalesHistory() {
        List<Transaction> transactions = new ArrayList<>();

        try {
            // Connect to MySQL database
            Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/your_database_name", "root", "Raymund@Estaca01");

            // Retrieve Sale records from the database
            String saleQuery = "SELECT * FROM Sale"; // Adjust this query as per your table structure
            PreparedStatement saleStmt = connection.prepareStatement(saleQuery);
            ResultSet saleResult = saleStmt.executeQuery();

            // Process Sale records and add them to transactions list
            while (saleResult.next()) {
                Sale sale = new Sale();
                sale.setId(saleResult.getInt("id")); // Replace with actual column names
                sale.setAmount(saleResult.getDouble("amount"));
                sale.setDate(saleResult.getDate("date"));
                // Set other Sale properties as needed
                transactions.add(sale);
            }
            saleStmt.close();

            // Retrieve Receipt records from the database
            String receiptQuery = "SELECT * FROM Receipt"; // Adjust this query as per your table structure
            PreparedStatement receiptStmt = connection.prepareStatement(receiptQuery);
            ResultSet receiptResult = receiptStmt.executeQuery();

            // Process Receipt records and add them to transactions list
            while (receiptResult.next()) {
                Receipt receipt = new Receipt();
                receipt.setId(receiptResult.getInt("id")); // Replace with actual column names
                receipt.setAmount(receiptResult.getDouble("amount"));
                receipt.setDate(receiptResult.getDate("date"));
                // Set other Receipt properties as needed
                transactions.add(receipt);
            }
            receiptStmt.close();

            // Close the database connection
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions such as SQL or database connection errors
        }

        return transactions;
    }
}
*/