package com.example.kdziurawiec.budgetapp.activity;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kdziurawiec.budgetapp.R;
import com.example.kdziurawiec.budgetapp.fragment.DateDialog;
import com.example.kdziurawiec.budgetapp.model.Account;
import com.example.kdziurawiec.budgetapp.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        String accName = accName_et.getText().toString();
        Double accStartingBalance = Double.parseDouble(accStartingBalance_et.getText().toString());
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
        //getting shared pref for user uid
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
        String userID = sharedPref.getString(getString(R.string.pref_user_uid),null);
        users.put(userID,"true");
        //User user = getUser();
        //users.add(user.getUserID());

        Account newAccount = new Account(accName, accStartingBalance, accStartDate, cycle, users); //reading in and creating account object

        //connecting to firebase
        dbRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference accountsRef = dbRef.child("accounts").push(); //drilling down to account and setting it to accountsRef
        accountsRef.setValue(newAccount); //adding account object to accounts in firebase
        //getting key of new account
        String accountKey = accountsRef.getKey();
        //setting account key for current user
        DatabaseReference usersRef = dbRef.child("users").child(userID).child("accounts").child(accountKey); //drilling down to user and setting it to current userRef
        usersRef.setValue("true"); //adding account key to current user for double entry in firebase

        dbRef.child("accounts").addValueEventListener(new ValueEventListener() {
            //firebase event listener
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("eror in add account:" + dataSnapshot.toString());
                Toast.makeText(getBaseContext(), "Your transaction was  added successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), "eror in add account:" +databaseError.toString(), Toast.LENGTH_LONG).show();
                System.out.println("eror in add account:" + databaseError.toString());
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

    public User getUser(){

        //getting shared pref for user uid
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
        String userID = sharedPref.getString(getString(R.string.pref_user_uid),null);

        //connecting to firebase
        dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = dbRef.child("users").push(); //drilling down to transaction and setting it to tranRef
        Query query =  usersRef.equalTo(userID);
        System.out.println(query.toString());
        usersRef.addValueEventListener(new ValueEventListener() {
            //firebase event listener
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(getBaseContext(), "Your transaction was  added successfully", Toast.LENGTH_LONG).show();
                String transResults = "";
                final ArrayList<User> transactionList = new ArrayList<User>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User transFromFirabase = ds.getValue(User.class);
                    transResults = transResults + transFromFirabase.toString() +"\n";
                    transactionList.add(transFromFirabase);
                }
                String[] listItems = new String[transactionList.size()];

                for(int i=0; i<transactionList.size();i++){
                    User trans = transactionList.get(i);
                    listItems[i] = trans.toString();
                }
        }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getBaseContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println(dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println(dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                System.out.println(dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.toString());
            }
        });



        User user = new User();

        return user;
    }
}
