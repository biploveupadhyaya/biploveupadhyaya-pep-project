package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.*;

public class AccountDAO {
    public Account createAccount(Account account) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "INSERT INTO Account (username, password) VALUES (?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, account.getUsername());
            pstmt.setString(2, account.getPassword());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                account.setAccount_id(rs.getInt(1));
            }
            return account;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Account getAccountById(int accountId) {
        String sql = "SELECT * FROM account WHERE account_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
    
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            } else {
                System.out.println("Account not found for ID: " + accountId);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in getAccountById: " + e.getMessage());
        }
        return null;
    }

    public Account getAccountByUsernameAndPassword(String username, String password) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM Account WHERE username = ? AND password = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Account account = new Account();
                account.setAccount_id(rs.getInt("account_id"));
                account.setUsername(rs.getString("username"));
                account.setPassword(rs.getString("password"));
                return account;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean accountExists(String username) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM Account WHERE username = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}