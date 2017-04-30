package com.example.kdziurawiec.budgetapp.model;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.widget.ListView;

import com.example.kdziurawiec.budgetapp.R;
import com.example.kdziurawiec.budgetapp.fragment.AccountFragment;
import com.example.kdziurawiec.budgetapp.interfaces.AccountRetriever;
import com.example.kdziurawiec.budgetapp.interfaces.TransactionRetriever;
import com.example.kdziurawiec.budgetapp.interfaces.UserRetriever;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Owner on 01/04/2017.
 */


public class DatabaseHelper {

    //connecting to firebase
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public DatabaseHelper(){}


    public void getUserFromDb(String userID, @NonNull final UserRetriever userRetriever){

        dbRef.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //creating user from DataSnapshot obj
                User user = dataSnapshot.getValue(User.class);
                userRetriever.get(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = "error in get user:" + databaseError.toString();
                userRetriever.errorMessage(message);
            }
        });
    }

    public void getUserFromDbByEmail(final String email, @NonNull final UserRetriever userRetriever){

        dbRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message;
                Boolean foundUser = false;

                for (DataSnapshot childEventSnapshot : dataSnapshot.getChildren()) {

                    //creating user from DataSnapshot obj
                    User user = childEventSnapshot.getValue(User.class);
                    if(user.getEmail().equals(email)) {
                        userRetriever.get(user);
                        foundUser = true;
                    }
                }
                if(!foundUser){
                    message = "Can't find user with "+email;
                    userRetriever.errorMessage(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = "error in get user:" + databaseError.toString();
                userRetriever.errorMessage(message);
            }
        });
    }

    public void getUsersListFromDbByUsername(final String username, @NonNull final UserRetriever userRetriever){
        dbRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            //firebase event listener
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> usersList = new ArrayList<User>();
                String message;
                Boolean foundUser = false;
                //looping through results, creating transaction objects and adding them to the transactionList
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    String compare = user.getUsername().trim();
                    if(username.toUpperCase().trim().equals(compare.toUpperCase())){
                        usersList.add(user);
                        foundUser = true;
                    }
                }
                if(!foundUser){
                    message = "Can't find user: "+username;
                    userRetriever.errorMessage(message);
                }else{
                    message ="Users list retrieved";
                    userRetriever.getUsers(usersList, message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = "error in get user:" + databaseError.toString();
                userRetriever.errorMessage(message);
            }
        });
    }

    public void getAccountFromDb(String accountID, @NonNull final AccountRetriever accountRetriever){
        dbRef.child("accounts").child(accountID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message ="Your account was  added successfully";
                //creating account from DataSnapshot obj
                Account account = dataSnapshot.getValue(Account.class);
                String accountId = dataSnapshot.getKey().toString();
                //callback interface
                accountRetriever.get(account, accountId, message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = "error in get account:" + databaseError.toString();
                accountRetriever.errorMessage(message);
                System.out.println("error in get account:" + databaseError.toString());
            }
        });
    }


    public void getTransactionsForAccountFromDb(final String accountID, @NonNull final TransactionRetriever transactionRetriever){
        dbRef.child("transactions").child(accountID).addValueEventListener(new ValueEventListener() {
            //firebase event listener
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
                //looping through results, creating transaction objects and adding them to the transactionList
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Transaction transFromFirabase = ds.getValue(Transaction.class);
                    transactionList.add(transFromFirabase);
                }
                String message ="Transactions list retrieved";
                transactionRetriever.getTransactions(transactionList, accountID, message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = "error in get transactions:" + databaseError.toString();
                transactionRetriever.errorMessage(message);
                System.out.println("error in get transactions:" + databaseError.toString());
            }
        });
    }


    public void createUserInDb(String userID, String email, String username){
        Date date = new Date();
        CharSequence stringDate = DateFormat.format("dd-MM-yy hh:mm", date.getTime());
        Map<String, String> accounts = new HashMap<>();

        User newUser = new User(userID, username, email, stringDate.toString(),accounts); //reading in and creating user object

        DatabaseReference usersRef = dbRef.child("users").child(userID); //drilling down to user and setting it to userRef
        usersRef.setValue(newUser); //adding user object to users in firebase
    }

    public void createTransactionInDb(Transaction transaction, final String accountID, final TransactionRetriever transactionRetriever){
        DatabaseReference transRef = dbRef.child("transactions").child(accountID).push(); //drilling down to transactions and setting it to transRef
        transRef.setValue(transaction); //adding transaction object to account id in transactions in firebase

        dbRef.child("transactions").child(accountID).child(transRef.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            //firebase event listener
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //setting account key for current user
                Transaction transaction = dataSnapshot.getValue(Transaction.class);

                String message ="Transaction was  added successfully";
                //callback interface
                transactionRetriever.get(transaction, accountID, message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = "error in add transaction:" + databaseError.toString();
                transactionRetriever.errorMessage(message);
                System.out.println("error in add transaction:" + databaseError.toString());
            }
        });
    }


    public void createAccountInDb(final Account newAccount, final String userID, @NonNull final AccountRetriever accountRetriever){

        DatabaseReference accountsRef = dbRef.child("accounts").push(); //drilling down to account and setting it to accountsRef
        accountsRef.setValue(newAccount); //adding account object to accounts in firebase
        //getting key of new account
        final String accountKey = accountsRef.getKey();

        dbRef.child("accounts").addListenerForSingleValueEvent(new ValueEventListener() {
            //firebase event listener
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //setting account key for current user
                DatabaseReference usersRef = dbRef.child("users").child(userID).child("accounts").child(accountKey); //drilling down to user and setting it to current userRef
                usersRef.setValue(newAccount.getAccName()); //adding account key to current user for double entry in firebase

                String message ="Your account was  added successfully";
                //callback interface
                accountRetriever.get(newAccount, accountKey, message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = "error in add account:" + databaseError.toString();
                accountRetriever.errorMessage(message);
                System.out.println("error in add account:" + databaseError.toString());
            }
        });
    }

    public void addUserToAccountInDb(User user, String accountID, String accountName){
        //setting account key for current user
        DatabaseReference usersRef = dbRef.child("users").child(user.getUserID()).child("accounts").child(accountID); //drilling down to user and setting it to current userRef
        usersRef.setValue(accountName); //adding account key to current user for double entry in firebase

        //setting user key for current account
        DatabaseReference accountRef = dbRef.child("accounts").child(accountID).child("users").child(user.getUserID()); //drilling down to user and setting it to current userRef
        accountRef.setValue(user.getUsername()); //adding account key to current user for double entry in firebase
    }

}


