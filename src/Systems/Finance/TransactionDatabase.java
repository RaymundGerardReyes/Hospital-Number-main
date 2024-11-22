package Systems.Finance;

import Systems.Database.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDatabase {
    
    public TransactionDatabase() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patient_id TEXT NOT NULL,
                amount REAL NOT NULL,
                transaction_type TEXT NOT NULL,
                description TEXT,
                status TEXT NOT NULL,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            logError(e, "Error initializing database.");
        }
    }

    public void saveTransaction(String patientId, double amount, String transactionType, String description, String status) {
        String sql = "INSERT INTO transactions (patient_id, amount, transaction_type, description, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, transactionType);
            pstmt.setString(4, description);
            pstmt.setString(5, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logError(e, "Error saving transaction.");
        }
    }

    public boolean isTransactionProcessed(String patientId, double amount, String transactionType) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE patient_id = ? AND amount = ? AND transaction_type = ? AND status = 'PROCESSED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, transactionType);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            logError(e, "Error checking transaction status.");
        }
        return false;
    }

    public void updateTransactionStatus(int transactionId, String newStatus) {
        String sql = "UPDATE transactions SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, transactionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logError(e, "Error updating transaction status.");
        }
    }

    public List<Transaction> getTransactionsForPatient(String patientId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE patient_id = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getInt("id"),
                    rs.getString("patient_id"),
                    rs.getDouble("amount"),
                    rs.getString("transaction_type"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getTimestamp("timestamp").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            logError(e, "Error retrieving transactions.");
        }
        return transactions;
    }

    private void logError(SQLException e, String message) {
        System.err.println(message);
        e.printStackTrace();
    }

    public static class Transaction {
        private int id;
        private String patientId;
        private double amount;
        private String transactionType;
        private String description;
        private String status;
        private LocalDateTime timestamp;

        public Transaction(int id, String patientId, double amount, String transactionType, String description, String status, LocalDateTime timestamp) {
            this.id = id;
            this.patientId = patientId;
            this.amount = amount;
            this.transactionType = transactionType;
            this.description = description;
            this.status = status;
            this.timestamp = timestamp;
        }

        // Getters
        public int getId() { return id; }
        public String getPatientId() { return patientId; }
        public double getAmount() { return amount; }
        public String getTransactionType() { return transactionType; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
