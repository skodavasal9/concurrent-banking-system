package BankingSystem;

public class LogEntry {
    long timestamp;
    String message;
    String accountId;
    public LogEntry(long timestamp, String message, String accountId) {
        this.timestamp = timestamp;
        this.message = message;
        this.accountId = accountId;
    }
    public String getMessage() { return message; }
}


