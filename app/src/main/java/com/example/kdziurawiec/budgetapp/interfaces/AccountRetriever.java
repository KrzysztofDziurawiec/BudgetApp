package com.example.kdziurawiec.budgetapp.interfaces;

import com.example.kdziurawiec.budgetapp.model.Account;

/**
 * Created by Owner on 01/04/2017.
 */

public interface AccountRetriever {
    void get(Account account, String accountId, String message);
    void errorMessage(String message);
}
