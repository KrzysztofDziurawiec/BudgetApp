package com.example.kdziurawiec.budgetapp.interfaces;

import com.example.kdziurawiec.budgetapp.model.Transaction;

import java.util.ArrayList;

/**
 * Created by Owner on 03/04/2017.
 */

public interface FragmentDataReceiver {
    public void receiveTransList(ArrayList<Transaction> transactionList);
}
