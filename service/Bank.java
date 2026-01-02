package BankingSystem.service;

import BankingSystem.model.*;
import BankingSystem.repository.AccountDAO;
import BankingSystem.repository.LogEntryDAO;
import BankingSystem.repository.TaskDAO;
import BankingSystem.repository.UserDAO;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

class Bank {
    private Map<String, Account> accounts;
    private Map<String, List<String>> userToAccounts;
    TransactionLedger ledger;
    private Map<String, Long> totalOutflows;
    private PriorityQueue<Task> scheduledTasks;
    private PriorityQueue<Task> skippedTasks;

    private final AccountDAO accountDAO;
    private final TaskDAO taskDAO;
    private final LogEntryDAO logEntryDAO;
    private final UserDAO userDAO;


    public Bank() {
        accounts = new HashMap<>();
        userToAccounts = new HashMap<>();
        ledger = new TransactionLedger();
        accountDAO = new AccountDAO();
        taskDAO = new TaskDAO();
        logEntryDAO = new LogEntryDAO();
        userDAO = new UserDAO();
        totalOutflows = new HashMap<>();
        scheduledTasks = new PriorityQueue<>(Comparator.comparingLong(task -> task.getScheduledTime()));
        skippedTasks = new PriorityQueue<>(Comparator.comparingLong(task -> task.getScheduledTime()));

        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try {
            List<Account> dbAccounts = accountDAO.findAll();
            for (Account acc : dbAccounts) {
                accounts.put(acc.getAccountId(), acc);
                userToAccounts.computeIfAbsent(acc.getUser().getUserId(), k -> new ArrayList<>())
                        .add(acc.getAccountId());
            }

            System.out.println("MAP CONTENTS: " + accounts.keySet());

            List<Task> dbTasks = taskDAO.findAll();
            for (Task t : dbTasks) {
                String status = t.getStatus();

                if ("PENDING".equals(status)) {
                    scheduledTasks.offer(t);
                } else if ("SKIPPED".equals(status) || "FAILED".equals(status)) {
                    skippedTasks.offer(t);
                }
                // COMPLETED tasks are ignored (they stay in DB for history only)
            }

            System.out.println("Warm-up complete. Active tasks: " + scheduledTasks.size());
        } catch (Exception e) {
            System.err.println("Critical: Database load failed: " + e.getMessage());
        }
    }

    public String createAccount(String id, int balance, String userId, long timestamp) {
        if (accounts.containsKey(id)) {
            throw new AccountAlreadyExistsException(id);
        }

        try {
            User user = userDAO.findById(userId);

            if (user == null) {
                return "false: User " + userId + " not found";
            }

            Account account = new Account(id, balance, user, Account.AccountType.SAVINGS.name());

            accountDAO.save(account);

            accounts.put(id, account);
            userToAccounts.computeIfAbsent(userId, k -> new ArrayList<>()).add(id);
            return "true";

        } catch (Exception e) {
            return "false: " + e.getMessage();
        }
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

        long fromAccBalance = from.getBalance();
        if (fromAccBalance < amount) {
            throw new InsufficientFundsException(fromId, fromAccBalance, amount);
        }

        Account firstLock = from.hashCode() < to.hashCode() ? from : to;
        Account secondLock = from.hashCode() < to.hashCode() ? to : from;

        synchronized (firstLock) {
            synchronized (secondLock) {
                if (from.getBalance() < amount) { // using your balance check
                    throw new InsufficientFundsException(fromId, fromAccBalance, amount);
                }

                from.subtract(amount);
                to.add(amount);
                totalOutflows.put(fromId, totalOutflows.getOrDefault(fromId, 0L) + amount);

                try {
                    accountDAO.save(from);
                    accountDAO.save(to);
                } catch (java.sql.SQLException e) {
                    // If DB update fails, we must revert memory to keep them in sync
                    from.add(amount);
                    to.subtract(amount);
                    totalOutflows.put(fromId, totalOutflows.get(fromId) - amount);
                    return "Transfer failed: Database sync error";
                }

                String message = "TRANSFER OUT" + amount + "TO" + toId +  "(Balance:" + from.getBalance() + ")";
                LogEntry entry = new LogEntry(timestamp, message, fromId);
                ledger.recordTransaction(entry, fromId);

                String toMessage = "TRANSFER IN"  + amount + "FROM" +  fromId + "(Balance:" + to.getBalance() + ")";
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

        Task task = new Task.Builder()
                .fromId(fromId)
                .toId(toId)
                .amount(amount)
                .scheduledTime(timestamp + delay)
                .taskType("TRANSFER")
                .build();

        try {
            taskDAO.save(task);
            scheduledTasks.offer(task);
            return "true";
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Failed to persist scheduled task: " + e.getMessage(), e);
        }
    }

    private void processScheduledTasks(long currentTimestamp) {
        List<Task> retryList = new ArrayList<>();

        while (!scheduledTasks.isEmpty() && scheduledTasks.peek().getScheduledTime() <= currentTimestamp) {
            Task task = scheduledTasks.poll();
            if (task == null) continue;

            if (!"PENDING".equals(task.getStatus())) {
                continue;
            }

            try {
                if (task.getTaskType().equals("TRANSFER")) {
                    executeTransfer(task.getFromId(), task.getToId(), task.getAmount(), currentTimestamp);

                    task.setStatus("COMPLETED");
                    taskDAO.update(task);

                    ledger.recordTransaction(new LogEntry(currentTimestamp,
                            "Scheduled Transfer Successful", task.getFromId()), task.getFromId());
                }
            } catch (java.sql.SQLException e) {
                throw new RuntimeException("Failed to persist scheduled task state: " + e.getMessage(), e);
            } catch (InsufficientFundsException e) {
                // Check if we can retry
                if (task.getRetryCount() < 3) {
                    int delay = (int) Math.pow(2, task.getRetryCount() + 1) * 60;

                    Task nextAttempt = new Task.Builder()
                            .fromId(task.getFromId())
                            .toId(task.getToId())
                            .amount(task.getAmount())
                            .scheduledTime(currentTimestamp + delay) // Scheduled for the FUTURE
                            .retryCount(task.getRetryCount() + 1)
                            .taskType(task.getTaskType())
                            .status("PENDING")
                            .build();

                    try {
                        taskDAO.save(nextAttempt);
                    } catch (SQLException ex) {
                        System.err.println("Failed to persist retry task: " + ex.getMessage());
                    }

                    retryList.add(nextAttempt); // Will go back to scheduledTasks

                    ledger.recordTransaction(new LogEntry(currentTimestamp,
                            "Insufficient Funds. Retry #" + nextAttempt.getRetryCount() + " scheduled.",
                            task.getFromId()), task.getFromId());
                } else {
                    // retry attempts exhausted : Add to skippedTasks
                    task.setStatus("SKIPPED");
                    try {
                        taskDAO.save(task);
                    } catch (SQLException ex) {
                        System.err.println("Retry attempts exhausted: " + ex.getMessage());
                    }
                    skippedTasks.offer(task);
                    ledger.recordTransaction(new LogEntry(currentTimestamp,
                            "CRITICAL: Max retries reached. Task moved to Skipped Queue.", task.getFromId()), task.getFromId());
                }
            } catch (BankingException e) {
                // NON-RETRYABLE: Move to skippedTasks immediately
                task.setStatus("FAILED");
                try {
                    taskDAO.save(task);
                } catch (SQLException ex) {
                    System.err.println("Task Failed, non retryable: " + ex.getMessage());
                }
                skippedTasks.offer(task);
                ledger.recordTransaction(new LogEntry(currentTimestamp,
                        "Task Aborted (Permanent Error): " + e.getMessage(), task.getFromId()), task.getFromId());
            }
        }

        // Only add the retryable tasks back to the main schedule
        scheduledTasks.addAll(retryList);
    }

    public Account getAccount(String accountId) {
        return this.accounts.get(accountId);
    }



}
