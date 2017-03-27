package com.example.kdziurawiec.budgetapp.model;

/**
 * Created by Owner on 12/03/2017.
 */

public class Transaction {
    String createdBy;
    String transDate;
    String category;
    Double amount;

    public Transaction(){}

    public Transaction(String createdByIn, String transDateIn, String categoryIn, Double amountIn){
        this.createdBy = createdByIn;
        this.transDate = transDateIn;
        this.category = categoryIn;
        this.amount = amountIn;
    }


    public String getTransactionDate() {return transDate;}

    public String getCategory() {return category;}

    public String getCreatedBy() {return createdBy;}

    public Double getAmount() {return amount;}

    @Override
    public String toString() {
        return "By "+this.getCreatedBy()+" date: "+this.getTransactionDate();
    }
}
