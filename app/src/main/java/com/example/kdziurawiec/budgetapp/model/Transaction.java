package com.example.kdziurawiec.budgetapp.model;

/**
 * Created by Owner on 12/03/2017.
 */

public class Transaction {
    String member;
    String transDate;
    String category;
    Double amount;

    public Transaction(){}

    public Transaction(String memberIn, String transDateIn, String categoryIn, Double amountIn){
        this.member = memberIn;
        this.transDate = transDateIn;
        this.category = categoryIn;
        this.amount = amountIn;
    }


    public String getTransactionDate() {return transDate;}

    public String getCategory() {return category;}

    public String getMember() {return member;}

    public Double getAmount() {return amount;}

    @Override
    public String toString() {
        return "By "+this.getMember()+" date: "+this.getTransactionDate();
    }
}
