package com.example.kdziurawiec.budgetapp.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Owner on 25/03/2017.
 */

public class User {
    String userID;
    String username;
    String email;
    String created_at;
    Map<String, String> accounts;

    public User(){}

    public User(String userIdIn, String usernameIn, String emailIn,String created_atIn, Map<String, String> accountsIn){
        this.userID = userIdIn;
        this.username = usernameIn;
        this.email = emailIn;
        this.created_at = created_atIn;
        this.accounts = accountsIn;
    }

    public String getUserID() {return userID;}

    public String getUsername() {return username;}

    public String getEmail() {return email;}

    public String getCreated_at() {return created_at;}

    public Map<String, String> getAccounts() {return accounts;}


    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", created_at='" + created_at + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}


