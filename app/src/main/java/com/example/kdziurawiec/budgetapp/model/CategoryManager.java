package com.example.kdziurawiec.budgetapp.model;

import com.example.kdziurawiec.budgetapp.R;

import java.util.ArrayList;

/**
 * Created by Owner on 01/04/2017.
 */

public class CategoryManager {
    ArrayList<Category> expenseCatList;
    ArrayList<Category> incomeCatList;

    public CategoryManager(){}

    public ArrayList<Category> getExpenseCategoryList(){
        expenseCatList = new ArrayList<Category>();
        expenseCatList.add(new Category("Shopping","ic_category_1","colorIconIndigo"));
        expenseCatList.add(new Category("Petrol","ic_category_2","colorIconGreen"));
        expenseCatList.add(new Category("Takeaway","ic_category_3","colorIconTeal"));
        expenseCatList.add(new Category("Grocery","ic_category_4","colorDeepOrange"));
        expenseCatList.add(new Category("Cinema","ic_category_5","colorIconBlueGrey"));

        return expenseCatList;
    }

    public ArrayList<Category> getIncomeCategoryList(){

        return incomeCatList;
    }


}
