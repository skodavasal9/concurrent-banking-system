package BankingSystem;

public class Account {
    User user;
    long balance;
    String accountType;
    String accountId;

    public enum AccountType { SAVINGS, CHECKING }

    Account(String id, int balance, User user, String accountType) {
        this.user = user;
        this.balance = balance;
        this.accountType = accountType;
        this.accountId = id;
    }
    public void add(long amount) { balance += amount; }
    public void subtract(long amount) { balance -= amount; }
}
