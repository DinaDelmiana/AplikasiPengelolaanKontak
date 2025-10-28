package database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
        private static final String URL = "jdbc:sqlite:contacts.db"; 
    public static java.sql.Connection getConnection() throws SQLException { 
        return DriverManager.getConnection(URL);
    }
}
