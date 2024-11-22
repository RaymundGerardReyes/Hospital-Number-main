package Systems.Finance;

import Systems.Database.DatabaseConnection;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public class FinanceOperations {

    public List<Transaction> getPatientTransactions(String hospitalId, String lastName, String firstName, String middleName) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM transactions WHERE hospital_id = ? AND last_name = ? AND first_name = ? AND middle_name = ? ORDER BY date DESC")) {
            
            stmt.setString(1, hospitalId);
            stmt.setString(2, lastName);
            stmt.setString(3, firstName);
            stmt.setString(4, middleName != null ? middleName : ""); // Handle potential null

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getDate("date"),
                    rs.getDouble("amount"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException ex) {
            // Log and rethrow exception or handle accordingly
            System.err.println("Error while fetching transactions: " + ex.getMessage());
        }
        return transactions;
    }

    public String generateReceiptText(String hospitalId, String firstName, String middleName, String lastName, String date, String amount) {
        // Ensure java.util.Date is used here
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(new java.util.Date());

        return String.format(
            "MyCare Healthcare Solutions\n" +
            "123 Main St\n" +
            "City, State, ZIP Code\n" +
            "Phone Number: (123) 456-7890\n" +
            "Email Address: info@clinic.com\n\n" +
            "Receipt for Consultation Fee\n" +
            "Receipt No: %s\n" +
            "Hospital ID: %s\n" +
            "Date: %s\n" +
            "Time: %s\n\n" +
            "Patient Name: %s %s %s\n" +
            "Consultation Fee: %s\n\n" +
            "Payment Method: Credit Card\n" +
            "Admin Finance Name: John Doe\n\n" +
            "Thank you for your visit!\n" +
            "For any inquiries, please contact us at (123) 456-7890.",
            UUID.randomUUID().toString(),
            hospitalId,
            date,
            currentTime,
            firstName,
            middleName != null ? middleName : "", // Handle potential null middle name
            lastName,
            amount
        );
    }


    public static class Transaction {
        private Date date;
        private double amount;
        private String status;

        public Transaction(Date date, double amount, String status) {
            this.date = date;
            this.amount = amount;
            this.status = status;
        }

        public Date getDate() { return date; }
        public double getAmount() { return amount; }
        public String getStatus() { return status; }
    }
}
