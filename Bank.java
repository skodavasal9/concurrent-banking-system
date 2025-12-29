package BankingSystem;

import java.util.*;
import java.util.stream.Collectors;

class Bank {
    private Map<String, Account> accounts;
    private Map<String, List<String>> userToAccounts;
    TransactionLedger ledger;
    private Map<String, Long> totalOutflows;
    private PriorityQueue<ScheduledTask> scheduledTasks;

    public Bank() {
        accounts = new HashMap<>();
        userToAccounts = new HashMap<>();
        ledger = new TransactionLedger();
        totalOutflows = new HashMap<>();
        scheduledTasks = new PriorityQueue<>(Comparator.comparingLong(task -> task.scheduledTime));

    }

    public String createAccount(String id, int balance, long timestamp)  {
        if (accounts.containsKey(id)) {
            throw new AccountAlreadyExistsException(id);
        }

        User user = new User.Builder().userId("accUser1")
                .password("abcde123")
                .firstName("John")
                .lastName("Doe")
                .build();

        Account account = new Account(id, balance, user, Account.AccountType.SAVINGS.name());
        accounts.put(id, account);
        userToAccounts.computeIfAbsent("accUser1", k -> new ArrayList<>()).add(id);
        return "true";
    }

    public String transfer(String fromId, String toId, int amount, long timestamp) {
        try {
            processScheduledTasks(timestamp);
            return executeTransfer(fromId, toId, amount, timestamp);
        } catch (BankingException e) {
            return "Transfer failed" + e.getMessage();
        }
    }

    private String executeTransfer(String fromId, String toId, int amount, long timestamp) {
        if (!accounts.containsKey(fromId)) {
            throw new AccountNotFoundException(fromId);
        }

        if (!accounts.containsKey(toId)) {
            throw new AccountNotFoundException(toId);
        }

        if (fromId.equals(toId)) {
            throw new InvalidTransactionException(fromId, toId);
        }

        Account from = accounts.get(fromId);
        Account to = accounts.get(toId);

        long fromAccBalance = from.balance;
        if (fromAccBalance < amount) {
            throw new InsufficientFundsException(fromId, fromAccBalance, amount);
        }

        Account firstLock = from.hashCode() < to.hashCode() ? from : to;
        Account secondLock = from.hashCode() < to.hashCode() ? to : from;

        synchronized (firstLock) {
            synchronized (secondLock) {
                if (from.balance < amount) { // using your balance check
                    throw new InsufficientFundsException(fromId, fromAccBalance, amount);
                }

                from.subtract(amount);
                to.add(amount);
                totalOutflows.put(fromId, totalOutflows.getOrDefault(fromId, 0L) + amount);

                String message = "TRANSFER OUT" + amount + "TO" + toId +  "(Balance:" + from.balance + ")";
                LogEntry entry = new LogEntry(timestamp, message, fromId);
                ledger.recordTransaction(entry, fromId);

                String toMessage = "TRANSFER IN"  + amount + "FROM" +  fromId + "(Balance:" + to.balance + ")";
                LogEntry entry2 = new LogEntry(timestamp, toMessage, toId);
                ledger.recordTransaction(entry2, toId);

                return "true";
            }
        }
    }

    public String getHistory(String accountId, long timestamp) {
        processScheduledTasks(timestamp);

        if (!accounts.containsKey(accountId)) {
            return "";
        }

        List<LogEntry> transactions = ledger.getHistory(accountId);
        if (transactions == null || transactions.isEmpty()) {
            return "";
        }

        return transactions.stream()
                .map(LogEntry::getMessage)
                .collect(Collectors.joining(", "));
    }

    public String topSpenders(int n, String timestamp) {
        processScheduledTasks(Long.parseLong(timestamp));

        Comparator<Map.Entry<String, Long>> comparator =
                (e1, e2) -> {
                    int cmp = Long.compare(e1.getValue(), e2.getValue());
                    if (cmp != 0) return cmp;
                    return e2.getKey().compareTo(e1.getKey());
                };

        PriorityQueue<Map.Entry<String, Long>> heap =
                new PriorityQueue<>(comparator);

        for (Map.Entry<String, Long> entry : totalOutflows.entrySet()) {
            heap.offer(entry);

            if (heap.size() > n) {
                heap.poll();
            }
        }

        List<Map.Entry<String, Long>> top = new ArrayList<>(heap);

        top.sort((e1, e2) -> {
            int cmp = Long.compare(e2.getValue(), e1.getValue());
            if (cmp != 0) return cmp;
            return e1.getKey().compareTo(e2.getKey());
        });

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < top.size(); i++) {
            if (i > 0) result.append(", ");
            result.append(top.get(i).getKey())
                    .append("(")
                    .append(top.get(i).getValue())
                    .append(")");
        }

        return result.toString();
    }

    public String schedulePayment(String fromId, String toId, int amount, long timestamp, int delay) {
        if (!accounts.containsKey(fromId)) {
            throw new AccountNotFoundException(fromId);
        }

        if (!accounts.containsKey(toId)) {
            throw new AccountNotFoundException(toId);
        }

        scheduledTasks.offer(new ScheduledTask(fromId, toId, amount, timestamp + delay));
        return "true";
    }

    private void processScheduledTasks(long currentTimestamp) {
        while (!scheduledTasks.isEmpty() && scheduledTasks.peek().scheduledTime <= currentTimestamp) {
            ScheduledTask task = scheduledTasks.poll();
            executeTransfer(task.fromId, task.toId, task.amount, task.scheduledTime);
        }
    }
}
