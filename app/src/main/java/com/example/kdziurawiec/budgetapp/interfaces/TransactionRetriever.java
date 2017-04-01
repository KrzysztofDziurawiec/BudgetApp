package com.example.kdziurawiec.budgetapp.interfaces;

import com.example.kdziurawiec.budgetapp.model.Transaction;

import java.util.ArrayList;

/**
 * Created by Owner on 01/04/2017.
 */

public interface TransactionRetriever {
    void get(Transaction transaction, String accountId, String message);
    void getTransactions(ArrayList<Transaction> transactionList, String accountId, String message);
    void errorMessage(String message);
}
