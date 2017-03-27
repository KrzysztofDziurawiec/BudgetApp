package com.example.kdziurawiec.budgetapp.model;

import android.app.Application;

/**
 * Created by Owner on 26/03/2017.
 * This class stores values which can be set and retrieved across all activities
 */

public class MyApplication extends Application
{

    private static MyApplication singleInstance = null;
    private String userUID = "fsdfdfdsfs";
    private User user = new User();
    private String currentAccountId = "";
    private Account currentAccount = new Account();

    public static MyApplication getInstance()
    {
        return singleInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleInstance = this;
    }

    public MyApplication() {}

    public String getUserUID() {return userUID;}

    public void setUserUID(String userUID) {this.userUID = userUID;}

    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}

    public String getCurrentAccountId() {return currentAccountId;}

    public void setCurrentAccountId(String currentAccountId) {this.currentAccountId = currentAccountId;}

    public Account getCurrentAccount() {return currentAccount;}

    public void setCurrentAccount(Account currentAccount) {this.currentAccount = currentAccount;}
}