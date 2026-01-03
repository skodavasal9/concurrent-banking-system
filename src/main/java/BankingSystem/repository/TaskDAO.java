package BankingSystem.repository;

import BankingSystem.model.Task;
import BankingSystem.db.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public void save(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (fromId, toId, amount, scheduledTime, status, taskType, retryCount) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getFromId());
            pstmt.setString(2, task.getToId());
            pstmt.setInt(3, task.getAmount());
            pstmt.setLong(4, task.getScheduledTime());
            pstmt.setString(5, task.getStatus());
            pstmt.setString(6, task.getTaskType());
            pstmt.setInt(7, task.getRetryCount());

            pstmt.executeUpdate();
        }
    }

    public List<Task> findAll() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Task task = new Task.Builder()
                        .fromTaskId(rs.getInt("taskId"))
                        .fromId(rs.getString("fromId"))
                        .toId(rs.getString("toId"))
                        .amount(rs.getInt("amount"))
                        .scheduledTime(rs.getLong("scheduledTime"))
                        .status(rs.getString("status"))
                        .taskType(rs.getString("taskType"))
                        .retryCount(rs.getInt("retryCount"))
                        .build();
                tasks.add(task);
            }
        }
        return tasks;
    }

    public void update(Task task) throws SQLException {
        String sql = "UPDATE tasks SET status = ? WHERE taskId = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getStatus());
            pstmt.setInt(2, task.getTaskId());
            pstmt.executeUpdate();
        }
    }
}