package BankingSystem.service;

import BankingSystem.model.Account;

public class BankingService {

    public static final Bank bank = new Bank();


    public Account getAccount(String accountId) {
        return bank.getAccount(accountId);
    }

    public static void main(String[] args) {

    }
}
