package coursework_manager.repos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbManager {

    private static final String userName = "rat";
    private static final String password = "rat";

    private static final String url = "jdbc:postgresql://localhost:5434/vacancies";  // Replace with your database URL


    public static Connection getConnection() throws SQLException {
        try {
            // TODO
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, userName, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
