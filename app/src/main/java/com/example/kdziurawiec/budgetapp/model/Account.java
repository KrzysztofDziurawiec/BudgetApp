package com.example.kdziurawiec.budgetapp.model;

import java.util.List;
import java.util.Map;

/**
 * Created by Owner on 25/03/2017.
 */

public class Account {

    String accName;
    Double staringBalance;
    String startDate;
    String cycle;
    Map<String, String> users;

    public Account(){}

    public Account( String accNameIn, Double staringBalanceIn, String starDateIn, String cycleIn, Map<String, String> usersIn){

        this.accName = accNameIn;
        this.staringBalance = staringBalanceIn;
        this.startDate = starDateIn;
        this.cycle = cycleIn;
        this.users = usersIn;
    }



    public String getAccName() {return accName;}

    public Double getStaringBalance() {return staringBalance;}

    public String getStartDate() {return startDate;}

    public String getCycle() {return cycle;}

    public Map<String, String> getUsers() {return users;}

    @Override
    public String toString() {
        return "Account{" +

                "accName='" + accName + '\'' +
                ", staringBalance=" + staringBalance +
                ", startDate='" + startDate + '\'' +
                ", cycle='" + cycle + '\'' +
                ", users=" + users +
                '}';
    }
}
