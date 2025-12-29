package BankingSystem;

public class ScheduledTask {
    String fromId, toId;
    int amount;
    long scheduledTime;
    public ScheduledTask(String fromId, String toId, int amount, long scheduledTime) {
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
        this.scheduledTime = scheduledTime;
    }
}