package BankingSystem.service;

import BankingSystem.model.LogEntry;
import BankingSystem.repository.LogEntryDAO;

import java.sql.SQLException;
import java.util.*;

public class TransactionLedger {
    private final Map<String, List<LogEntry>> logsByAccount = new HashMap<>();
    private final LogEntryDAO logEntryDAO = new LogEntryDAO();


    public void recordTransaction(LogEntry logEntry, String accountId) {
        logsByAccount.computeIfAbsent(accountId, k -> new ArrayList<>()).add(logEntry);
        try {
            logEntryDAO.save(logEntry);
        } catch (Exception e) {
            // Log error but allow memory operation to continue
            System.err.println("DB Persistence failed for log: " + e.getMessage());
        }
    }
    public List<LogEntry> getHistory(String accountId) {
        if (!logsByAccount.containsKey(accountId)) {
            try {
                List<LogEntry> dbLogs = logEntryDAO.findByAccountId(accountId);
                logsByAccount.put(accountId, dbLogs);
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }

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
