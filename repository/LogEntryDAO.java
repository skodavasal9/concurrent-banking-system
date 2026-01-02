package BankingSystem.repository;

import BankingSystem.model.LogEntry;
import BankingSystem.db.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogEntryDAO {

    public void save(LogEntry entry) throws SQLException {
        String sql = "INSERT INTO log_entries (timestamp, message, accountId) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Using the fields directly as they appear in your LogEntry.java
            pstmt.setLong(1, entry.getTimestamp());
            pstmt.setString(2, entry.getMessage());
            pstmt.setString(3, entry.getAccountId());

            pstmt.executeUpdate();
        }
    }

    public List<LogEntry> findByAccountId(String accountId) throws SQLException {
        List<LogEntry> logs = new ArrayList<>();
        String sql = "SELECT * FROM log_entries WHERE accountId = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new LogEntry(
                            rs.getLong("timestamp"),
                            rs.getString("message"),
                            rs.getString("accountId")
                    ));
                }
            }
        }
        return logs;
    }
}
