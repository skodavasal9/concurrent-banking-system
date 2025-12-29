package BankingSystem;

import java.util.*;

public class TransactionLedger {
    private final Map<String, List<LogEntry>> logsByAccount = new HashMap<>();
    public void recordTransaction(LogEntry logEntry, String accountId) {
        logsByAccount.computeIfAbsent(accountId, k -> new ArrayList<>()).add(logEntry);
    }
    public List<LogEntry> getHistory(String accountId) {
        List<LogEntry> logsForAcct = logsByAccount.get(accountId);
        if (logsForAcct == null) {
            return Collections.emptyList();
        }
        List<LogEntry> result = new ArrayList<>(logsForAcct.size());
        for (int i = logsForAcct.size() - 1; i >= 0; i--) {
            result.add(logsForAcct.get(i));
        }
        return result;
    }
}
