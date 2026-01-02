package BankingSystem.model;

public class Account {
    private User user;
    private long balance;
    private String accountType;
    private String accountId;

    public enum AccountType { SAVINGS, CHECKING }

    public Account(String id, int balance, User user, String accountType) {
        this.user = user;
        this.balance = balance;
        this.accountType = accountType;
        this.accountId = id;
    }
    public void add(long amount) { balance += amount; }
    public void subtract(long amount) { balance -= amount; }

    public long getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    public String getAccountId() { return accountId; }
    public User getUser() { return user; }
}
