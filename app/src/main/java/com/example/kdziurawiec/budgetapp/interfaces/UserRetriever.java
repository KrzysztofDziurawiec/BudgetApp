package com.example.kdziurawiec.budgetapp.interfaces;

import com.example.kdziurawiec.budgetapp.model.User;

/**
 * Created by Owner on 01/04/2017.
 */

public interface UserRetriever {
    void get(User user);
    void errorMessage(String message);
}
