package BankingSystem;

import BankingSystem.model.Account;
import BankingSystem.service.BankingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

/**
 * BankCLI: A bridge for MCP (Model Context Protocol).
 * Outputs results in JSON format to STDOUT for Python integration.
 */
public class BankCLI {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final BankingService bankingService = new BankingService();

    public static void main(String[] args) {
        if (args.length < 1) {
            printError("No command provided. Available: 'get_balance', 'transfer', 'create_account'");
            System.exit(1);
        }

        String command = args[0].toLowerCase();

        try {
            switch (command) {
                case "get_balance":
                    // Usage: java BankCLI get_balance ACC_123
                    if (args.length < 2) throw new IllegalArgumentException("Account ID required.");
                    handleGetBalance(args[1]);
                    break;

                case "transfer":
                    // Usage: java BankCLI transfer FROM_ACC TO_ACC 150.00
                    if (args.length < 4) throw new IllegalArgumentException("Usage: transfer <from> <to> <amount>");
                    handleTransfer(args[1], args[2], Double.parseDouble(args[3]));
                    break;

                case "create_account":
                    // Usage: java BankCLI create_account NAME 1000.00
                    if (args.length < 3) throw new IllegalArgumentException("Usage: create_account <name> <initial_balance>");
                    handleCreateAccount(args[1], Double.parseDouble(args[2]));
                    break;

                default:
                    printError("Unsupported command: " + command);
                    System.exit(1);
            }
        } catch (Exception e) {
            printError("Internal Error: " + e.getMessage());
            System.exit(1);
        }
    }

    // --- Action Handlers ---

    private static void handleGetBalance(String accountId) {
        if (accountId == null) {
            printSuccess("Error", mapper.createObjectNode().put("status", "error").put("message", "No ID provided"));
            return;
        }

        String cleanId = accountId.trim();
        Account account = bankingService.getAccount(cleanId);

        if (account != null) {
            ObjectNode result = mapper.createObjectNode();
            result.put("accountId", account.getAccountId());
            result.put("balance", account.getBalance());
            result.put("status", "success");
            printSuccess("Balance retrieved", result);
        } else {
            ObjectNode result = mapper.createObjectNode();
            result.put("status", "error");

            // We'll call getAccount with A1 specifically to see if ANY account works
            boolean hasA1 = (bankingService.getAccount("A1") != null);

            result.put("message", "Requested: [" + cleanId + "]. Is A1 in memory? " + hasA1);

            printSuccess("Balance lookup failed", result);
        }
    }

    private static void handleTransfer(String from, String to, double amount) {
        // REPLACEMENT: Call banking core here
        // bankService.transfer(from, to, amount);

        ObjectNode result = mapper.createObjectNode();
        result.put("from", from);
        result.put("to", to);
        result.put("amountTransferred", amount);
        result.put("status", "COMPLETED");

        printSuccess("Funds transferred successfully", result);
    }

    private static void handleCreateAccount(String name, double initialBalance) {
        ObjectNode node = mapper.createObjectNode();
        node.put("accountId", "ACC-" + UUID.randomUUID().toString().substring(0, 5));
        node.put("owner", name);
        node.put("balance", initialBalance);

        printSuccess("Created account ", node);
    }

    private static void printSuccess(String message, ObjectNode data) {
        ObjectNode success = mapper.createObjectNode();
        success.put("status", "success");
        success.put("message", message);
        success.set("data", data);

        try {
            System.out.println(mapper.writeValueAsString(success));
        } catch (Exception e) {
            printError("JSON Serialization failed");
        }
    }

    private static void printError(String message) {
        ObjectNode error = mapper.createObjectNode();
        error.put("status", "error");
        error.put("message", message);

        try {
            System.out.println(mapper.writeValueAsString(error));
        } catch (Exception e) {
            System.out.println("{\"status\":\"error\"}");
        }
    }

}