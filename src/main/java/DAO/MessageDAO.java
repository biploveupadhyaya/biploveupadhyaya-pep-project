package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
   
    public Message createMessage(Message message) {
        String sql = "INSERT INTO message (message_text, posted_by) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    
            ps.setString(1, message.getMessage_text());
            ps.setInt(2, message.getPosted_by());
    
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int messageId = rs.getInt(1);
                    message.setMessage_id(messageId);
                    return message; // Return the updated message with ID
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in createMessage: " + e.getMessage());
        }
        System.out.println("Failed to create message in Service/DAO");
        return null; // Return null if insertion fails
    }
    


    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM Message";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Message message = new Message();
                message.setMessage_id(rs.getInt("message_id"));
                message.setPosted_by(rs.getInt("posted_by"));
                message.setMessage_text(rs.getString("message_text"));
                message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public Message getMessageById(int messageId) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM Message WHERE message_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, messageId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Message message = new Message();
                message.setMessage_id(rs.getInt("message_id"));
                message.setPosted_by(rs.getInt("posted_by"));
                message.setMessage_text(rs.getString("message_text"));
                message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
                return message;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message deleteMessage(int messageId) {
        Message message = getMessageById(messageId);
        if (message != null) {
            try (Connection connection = ConnectionUtil.getConnection()) {
                String sql = "DELETE FROM Message WHERE message_id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, messageId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    public Message updateMessage(int messageId, String newMessageText) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "UPDATE Message SET message_text = ? WHERE message_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, newMessageText);
            pstmt.setInt(2, messageId);
            pstmt.executeUpdate();
            return getMessageById(messageId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> getMessagesByUserId(int userId) {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM Message WHERE posted_by = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Message message = new Message();
                message.setMessage_id(rs.getInt("message_id"));
                message.setPosted_by(rs.getInt("posted_by"));
                message.setMessage_text(rs.getString("message_text"));
                message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}