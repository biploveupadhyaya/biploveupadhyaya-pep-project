package Service;

import DAO.MessageDAO;
import Model.Message;

import java.util.List;

public class MessageService {
    private final MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public Message createMessage(Message message) {
        if (message.getMessage_text() != null && !message.getMessage_text().isEmpty() && message.getMessage_text().length() <= 255 && message.getPosted_by() > 0) {
            System.out.println("Message validation passed in service layer");
            return messageDAO.createMessage(message);
        }
        System.out.println("Message validation failed in service layer");
        return null;
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int messageId) {
        return messageDAO.getMessageById(messageId);
    }

    public Message deleteMessage(int messageId) {
        return messageDAO.deleteMessage(messageId);
    }

    public Message updateMessage(int messageId, String newMessageText) {
        if (newMessageText != null && !newMessageText.isEmpty() && newMessageText.length() <= 255) {
            return messageDAO.updateMessage(messageId, newMessageText);
        }
        return null;
    }

    public List<Message> getMessagesByUserId(int userId) {
        return messageDAO.getMessagesByUserId(userId);
    }
}