package Systems.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hospital_management"; // Replace with actual DB URL
    private static final String USER = "root"; // Replace with actual DB username
    private static final String PASSWORD = "Raymund@Estaca01"; // Replace with actual DB password

    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
