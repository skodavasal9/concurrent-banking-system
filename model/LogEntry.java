package BankingSystem.model;

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
    public String getAccountId() { return accountId; }
    public long getTimestamp() { return timestamp; }
}


