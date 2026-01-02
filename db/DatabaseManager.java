package BankingSystem.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // REPLACE THIS with the actual path from your Mac (pwd in terminal)
    private static final String ACTUAL_PATH = "/Users/snigdhakodavasal/CodingFun/APITesting/banking.db";
    private static final String URL = "jdbc:sqlite:" + ACTUAL_PATH;

    public static Connection getConnection() throws SQLException {

        try {
            // This forces the SQLite driver to register itself
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite Driver not found on classpath!");
            e.printStackTrace();
        }

        return DriverManager.getConnection(URL);
    }
}