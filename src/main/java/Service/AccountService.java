package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private final AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }

    public Account register(Account account) {
        // Validate the account before registration
        if (isValidAccount(account)) {
            return accountDAO.createAccount(account); // Create the account if valid
        }
        return null; // Return null if the account is not valid
    }

    public Account login(String username, String password) {
        return accountDAO.getAccountByUsernameAndPassword(username, password);
    }
    
    public Account getAccountById(int id) {
        return accountDAO.getAccountById(id); // Assuming accountDAO retrieves the account by ID
    }

    public boolean isValidAccount(Account account) {
        return account.getUsername() != null && !account.getUsername().isEmpty() &&
               account.getPassword() != null && account.getPassword().length() >= 4 &&
               !accountDAO.accountExists(account.getUsername());
    }
}