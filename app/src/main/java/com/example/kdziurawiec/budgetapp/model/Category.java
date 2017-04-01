package com.example.kdziurawiec.budgetapp.model;

/**
 * Created by Owner on 01/04/2017.
 */

public class Category {

    String name;
    String imageResource;
    String backgroundColor;

    public Category(String nameIn, String imageResourceIn, String backgroundColorIn){
        this.name = nameIn;
        this.imageResource = imageResourceIn;
        this.backgroundColor = backgroundColorIn;
    }

    public String getName() {return name;}

    public String getImageResource() {return imageResource;}

    public String getBackgroundColor() {return backgroundColor;}
}
