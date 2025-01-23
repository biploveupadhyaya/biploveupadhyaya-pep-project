package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class SocialMediaController {
    private AccountService accountService = new AccountService();
    private MessageService messageService = new MessageService();

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        // User registration
        app.post("/register", this::registerUser);
        // User login
        app.post("/login", this::loginUser);
        // Create message
        app.post("/messages", this::createMessage);
        // Retrieve all messages
        app.get("/messages", this::retrieveAllMessages);
        // Retrieve messages by user ID
        app.get("/accounts/{accountId}/messages", this::retrieveAllMessagesByUserId);
        // Retrieve message by ID
        app.get("/messages/{id}", this::retrieveMessageById);
        // Update message
        app.patch("/messages/{id}", this::updateMessage);
        // Delete message
        app.delete("/messages/{id}", this::deleteMessage);

        return app;
    }

    // Method to handle user login
    private void loginUser(Context context) {
        Account loginAccount = context.bodyAsClass(Account.class);
        String username = loginAccount.getUsername();
        String password = loginAccount.getPassword();

        Account account = accountService.login(username, password);
        if (account != null) {
            context.json(account); // Successful login, return user object
        } else {
            context.status(401).result(""); // Unauthorized
        }
    }

    private void registerUser(Context context) {
        // Parse the request body
        Account account = context.bodyAsClass(Account.class);
        
        // Validate username
        if (account.getUsername() == null || account.getUsername().isEmpty()) {
            context.status(400).result(""); // Username is blank
            return;
        }

        // Validate password
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            context.status(400).result(""); // Password is too short
            return;
        }

        // Attempt to register the user
        Account registeredAccount = accountService.register(account);

        if (registeredAccount != null) {
            context.json(registeredAccount); // Return the registered account
        } else {
            context.status(400).result(""); // Username already exists
        }
    }

    private void createMessage(Context context) {
        try {
            Message message = context.bodyAsClass(Message.class);
            System.out.println("Received Message: " + message);
    
            if (message.getMessage_text() == null || message.getMessage_text().isEmpty() || message.getMessage_text().length() > 255) {
                System.out.println("Invalid message_text");
                context.status(400).result(""); 
                return;
            }
    
            Account account = accountService.getAccountById(message.getPosted_by());
            if (account == null) {
                System.out.println("Invalid user ID: " + message.getPosted_by());
                context.status(400).result("");
                return;
            }
    
            Message createdMessage = messageService.createMessage(message);
            if (createdMessage != null) {
                System.out.println("Message successfully created: " + createdMessage);
                context.status(200).json(createdMessage);
            } else {
                System.out.println("Failed to create message in Service/DAO");
                context.status(400).result("");
            }
        } catch (Exception e) {
            System.out.println("Exception in createMessage: " + e.getMessage());
            context.status(500).result("Internal Server Error");
        }
    }

    private void retrieveAllMessages(Context context) {
        List<Message> messages = messageService.getAllMessages(); // Get all messages from the service
        context.json(messages); // Return the messages in JSON format
    }

    private void retrieveAllMessagesByUserId(Context context) {
        int userId = Integer.parseInt(context.pathParam("accountId")); // Get user ID from path param
        List<Message> messages = messageService.getMessagesByUserId(userId); // Get messages by user ID
        context.json(messages); // Return the messages in JSON format
    }

    private void retrieveMessageById(Context context) {
        int id = Integer.parseInt(context.pathParam("id"));
        Message message = messageService.getMessageById(id); // Fetch the message from the service
        if (message != null) {
            context.json(message); // Message found, return it
        } else {
            context.status(200).result(""); // No message found, return 200 with empty body
        }
    }

    private void updateMessage(Context context) {
        int id = Integer.parseInt(context.pathParam("id")); // Get message ID from path param
        Message updatedMessage = context.bodyAsClass(Message.class); // Get updated message from request body

        // Validate the new message text
        if (updatedMessage.getMessage_text() == null || updatedMessage.getMessage_text().isEmpty()) {
            context.status(400).result(""); // Bad request for empty message text
            return;
        }
        
        // Check if the message text is too long
        if (updatedMessage.getMessage_text().length() > 255) {
            context.status(400).result(""); // Bad request for message text too long
            return;
        }

        // Attempt to update the message
        Message message = messageService.updateMessage(id, updatedMessage.getMessage_text());
        if (message != null) {
            context.json(message); // Return the updated message
        } else {
            context.status(400).result(""); // Message not found or update failed
        }
    }

    private void deleteMessage(Context context) {
        int id = Integer.parseInt(context.pathParam("id")); // Get message ID from path param
        
        // Attempt to delete the message
        Message deletedMessage = messageService.deleteMessage(id);
        
        if (deletedMessage != null) {
            context.json(deletedMessage); // Return the deleted message
        } else {
            context.status(200).result(""); // Message not found, return 200 with empty body
        }
    }
}