package BankingSystem.repository;

import BankingSystem.model.User;
import BankingSystem.db.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public void save(User user) throws SQLException {
        String sql = "INSERT OR REPLACE INTO users (userId, firstName, lastName, email, phoneNumber, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhoneNumber());
            pstmt.setString(6, user.getPassword());

            pstmt.executeUpdate();
        }
    }

    public User findById(String userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE userId = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User.Builder()
                            .userId(rs.getString("userId"))
                            .firstName(rs.getString("firstName"))
                            .lastName(rs.getString("lastName"))
                            .email(rs.getString("email"))
                            .phoneNumber(rs.getString("phoneNumber"))
                            .password(rs.getString("password"))
                            .build();
                }
            }
        }
        return null;
    }
}
