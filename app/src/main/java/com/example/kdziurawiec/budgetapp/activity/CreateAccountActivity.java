package com.example.kdziurawiec.budgetapp.activity;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.kdziurawiec.budgetapp.R;
import com.example.kdziurawiec.budgetapp.fragment.DateDialog;
import com.example.kdziurawiec.budgetapp.interfaces.AccountRetriever;
import com.example.kdziurawiec.budgetapp.model.Account;
import com.example.kdziurawiec.budgetapp.model.DatabaseHelper;
import com.example.kdziurawiec.budgetapp.model.MyApplication;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private DatabaseHelper myFirebaseHelper;
    MyApplication myApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myApp =((MyApplication) getApplicationContext());

    //sets the toolbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button createAccountBtn = (Button)findViewById(R.id.createAccountBtn);

        createAccountBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        // UI references.
        EditText accName_et = (EditText)findViewById(R.id.accNameEditText);
        EditText accStartingBalance_et = (EditText)findViewById(R.id.accBalanceEditText);
        EditText accStartDate_et = (EditText)findViewById(R.id.accStartDateEditText);
        RadioButton cycleWeekly_rb = (RadioButton) findViewById(R.id.acc_radio_weekly);
        RadioButton cycleMonthly_rb = (RadioButton) findViewById(R.id.acc_radio_monthly);

        String accName = getString(R.string.tab_item_account);
        Double accStartingBalance = 0.0;
        try{
            accName = accName_et.getText().toString();
        }catch (NumberFormatException e){
            Toast.makeText(getBaseContext(), R.string.error_field_required_acc_name, Toast.LENGTH_LONG).show();
        }
        try{
            accStartingBalance = Double.parseDouble(accStartingBalance_et.getText().toString());
        }catch (NumberFormatException e){
            Toast.makeText(getBaseContext(), R.string.error_field_required_start_balance, Toast.LENGTH_LONG).show();
        }
        String accStartDate = accStartDate_et.getText().toString();
        String cycle;
        if (cycleWeekly_rb.isChecked()){
            cycle = "weekly";
        }else if(cycleMonthly_rb.isChecked()){
            cycle = "monthly";
        }else{
            cycle = "monthly";
        }

        Map<String, String> users = new HashMap<>();

        //getting user uid from myApp
        //String userID = myApp.getUser().getUserID();
       // users.put(userID,"true");
        //getting shared pref for user id
        SharedPreferences sharedPref = getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
        String userID = sharedPref.getString(getString(R.string.pref_user_uid),null);
        String username = sharedPref.getString(getString(R.string.pref_username),null);
        users.put(userID,username);

        Account newAccount = new Account(accName, accStartingBalance, accStartDate, cycle, users); //reading in and creating account object
        // creating new account in firebase
        myFirebaseHelper = new DatabaseHelper();
        myFirebaseHelper.createAccountInDb(newAccount, userID, new AccountRetriever() {
            @Override
            public void get(Account account, String accountId, String message) {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                //calling shared preferences to set account uid
                SharedPreferences sharedPref = getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                //setting set pref of account id
                prefEditor.putString(getString(R.string.pref_account_id), accountId);
                prefEditor.putString(getString(R.string.pref_accountName), account.getAccName());
                prefEditor.commit();
                //setting user obj in the MyApp class
                myApp.setCurrentAccount(account);
                myApp.setCurrentAccountId(accountId);
                //intent to CreateAccountActivity
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void errorMessage(String message) {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });

    }


    public void onStart(){
        super.onStart();
        //starting date picker fragment dialog when focus is on the select date edit text
        EditText txtDate = (EditText)findViewById(R.id.accStartDateEditText);
        txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    DateDialog dialog = new DateDialog(v);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft,"DatePicker");
                }
            }
        });
    }

    //return to MainActivity when toolbar back btn pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
