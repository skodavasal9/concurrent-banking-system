package BankingSystem.repository;

import BankingSystem.model.Account;
import BankingSystem.model.User;
import BankingSystem.db.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public void save(Account account) throws SQLException {
        String userSql = "INSERT OR REPLACE INTO users (userId, firstName, lastName, email, phoneNumber, password) VALUES (?, ?, ?, ?, ?, ?)";
        String accountSql = "INSERT OR REPLACE INTO accounts (accountId, balance, accountType, userId) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Ensure both save or neither does
            try {
                // 1. Save User first (due to Foreign Key)
                try (PreparedStatement pstmt = conn.prepareStatement(userSql)) {
                    User u = account.getUser();
                    pstmt.setString(1, u.getUserId());
                    pstmt.setString(2, u.getFirstName());
                    pstmt.setString(3, u.getLastName());
                    pstmt.setString(4, u.getEmail());
                    pstmt.setString(5, u.getPhoneNumber());
                    pstmt.setString(6, u.getPassword());
                    pstmt.executeUpdate();
                }

                // 2. Save Account
                try (PreparedStatement pstmt = conn.prepareStatement(accountSql)) {
                    pstmt.setString(1, account.getAccountId());
                    pstmt.setLong(2, account.getBalance());
                    pstmt.setString(3, account.getAccountType());
                    pstmt.setString(4, account.getUser().getUserId());
                    pstmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Account> findAll() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        // Join tables to get User data and Account data at once
        String sql = "SELECT a.*, u.* FROM accounts a JOIN users u ON a.userId = u.userId";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User.Builder()
                        .userId(rs.getString("userId"))
                        .password(rs.getString("password"))
                        .firstName(rs.getString("firstName"))
                        .lastName(rs.getString("lastName"))
                        .email(rs.getString("email"))
                        .phoneNumber(rs.getString("phoneNumber"))
                        .build();

                accounts.add(new Account(
                        rs.getString("accountId"),
                        (int) rs.getLong("balance"),
                        user,
                        rs.getString("accountType")
                ));
            }
        }
        return accounts;
    }
}
