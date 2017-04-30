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
        expenseCatList.add(new Category("Shopping","ic_category_1","colorIconRed",true));
        expenseCatList.add(new Category("Fuel","ic_category_2","colorDeepGrey",true));
        expenseCatList.add(new Category("Takeaway","ic_category_3","colorIconBlue",true));
        expenseCatList.add(new Category("Grocery","ic_category_4","colorIconPurple",true));
        expenseCatList.add(new Category("Cinema","ic_category_5","colorIconDeepPurple",true));
        expenseCatList.add(new Category("Restaurant","ic_restaurant_white","colorIconIndigo",true));
        expenseCatList.add(new Category("Car","ic_car_white","colorIconYellow",true));
        expenseCatList.add(new Category("Children","ic_child_white","colorIconBlueGrey",true));
        expenseCatList.add(new Category("Entertainment","ic_cake_white","colorIconTeal",true));
        expenseCatList.add(new Category("Gifts","ic_gift_white","colorIconPink",true));
        expenseCatList.add(new Category("Health","ic_health_white","colorIconGreen",true));
        expenseCatList.add(new Category("Pets","ic_pets_white","colorIconLime",true));
        expenseCatList.add(new Category("Rent","ic_rent_white","colorIconCyan",true));
        expenseCatList.add(new Category("Bills","ic_acccount_balance_white","colorIconOrange",true));
        expenseCatList.add(new Category("Insurance","ic_insurance_white","colorIconLightGreen",true));
        expenseCatList.add(new Category("Commuting","ic_commuting_white","colorIconDeepOrange",true));
        expenseCatList.add(new Category("Gambling","ic_gambling_white","colorIconBrown",true));
        expenseCatList.add(new Category("Other","ic_other_white","colorDeepOrange",true));

        return expenseCatList;
    }

    public ArrayList<Category> getIncomeCategoryList(){
        incomeCatList = new ArrayList<Category>();
        incomeCatList.add(new Category("Cash","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Transfer","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Check","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Salary","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Pension","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Refund","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Gift","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Grant","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Sale","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Dividends","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Lending","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Odd job","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Gambling winnings","ic_half_star_light","colorIncome",false));
        incomeCatList.add(new Category("Other","ic_half_star_light","colorIncome",false));

        return incomeCatList;
    }


}
