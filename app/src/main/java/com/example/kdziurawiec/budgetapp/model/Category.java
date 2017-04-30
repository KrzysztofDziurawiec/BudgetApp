package com.example.kdziurawiec.budgetapp.model;

/**
 * Created by Owner on 01/04/2017.
 */

public class Category {

    String name;
    String imageResource;
    String backgroundColor;
    Boolean isExpense;

    public Category(String nameIn, String imageResourceIn, String backgroundColorIn, Boolean isExpenseIn){
        this.name = nameIn;
        this.imageResource = imageResourceIn;
        this.backgroundColor = backgroundColorIn;
        this.isExpense = isExpenseIn;
    }

    public String getName() {return name;}

    public String getImageResource() {return imageResource;}

    public String getBackgroundColor() {return backgroundColor;}

    public Boolean getIsExpense() {return isExpense;}
}
