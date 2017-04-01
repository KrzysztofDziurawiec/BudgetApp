package com.example.kdziurawiec.budgetapp.firebaseHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.example.kdziurawiec.budgetapp.R;
import com.example.kdziurawiec.budgetapp.activity.MainActivity;
import com.example.kdziurawiec.budgetapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Owner on 25/03/2017.
 */

public class userAuthFirebaseHelper {
    //creating firebaseAuth instance
    private FirebaseAuth mAuth;
    //creating firebaseAuth instance listener
    private FirebaseAuth.AuthStateListener mAuthListener;

    private void createUserInDb(String userID, String email, String username){
        Date date = new Date();
        CharSequence stringDate = DateFormat.format("dd-MM-yy hh:mm", date.getTime());
        Map<String, String> accounts =  new HashMap<>();

        User newUser = new User(userID, email, username, stringDate.toString(),accounts); //reading in and creating transaction object

        //connecting to firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference usersRef = dbRef.child("users").push(); //drilling down to transaction and setting it to tranRef
        usersRef.setValue(newUser); //adding transaction object to transaction in firebase
    }


}