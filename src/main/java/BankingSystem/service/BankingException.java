package BankingSystem.service;

public class BankingException extends RuntimeException {
    public BankingException(String message) {
        super(message);
    }
}

    class AccountNotFoundException extends BankingException {
        public AccountNotFoundException(String id) {
            super("Account with ID " + id + " not found.");
        }
    }

    class AccountAlreadyExistsException extends BankingException {
        public AccountAlreadyExistsException(String id) {
            super("Account with ID " + id + " already exists.");
        }
    }

    class InsufficientFundsException extends BankingException {
        public InsufficientFundsException(String id, long balance, int attempted) {
            super("Account " + id + " has insufficient funds. Balance: " + balance + ", Attempted: " + attempted);
        }
    }

    class InvalidTransactionException extends BankingException {
        public InvalidTransactionException(String fromId, String toId) {
            super("Invalid transaction from " + fromId + " to " + toId);
        }
    }


