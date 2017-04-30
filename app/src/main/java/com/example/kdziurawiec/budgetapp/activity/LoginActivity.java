package com.example.kdziurawiec.budgetapp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kdziurawiec.budgetapp.R;
import com.example.kdziurawiec.budgetapp.interfaces.AccountRetriever;
import com.example.kdziurawiec.budgetapp.interfaces.UserRetriever;
import com.example.kdziurawiec.budgetapp.model.Account;
import com.example.kdziurawiec.budgetapp.model.DatabaseHelper;
import com.example.kdziurawiec.budgetapp.model.MyApplication;
import com.example.kdziurawiec.budgetapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //creating firebaseAuth instance
    private FirebaseAuth mAuth;
    //creating firebaseAuth instance listener
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SharedPreferences sharedPref;
    private MyApplication myApp;
    private DatabaseHelper myFirebaseHelper;

    private Button signUpBtn;
    private Button logInBtn;
    private View userFormView;
    private View mProgressView;
    private boolean isUserSignedIn = false;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
        myApp = new MyApplication();
        myFirebaseHelper = new DatabaseHelper();

        Button newUserBtn = (Button)findViewById(R.id.newUserBtn);
        Button existingUserBtn = (Button)findViewById(R.id.existingUserBtn);
        signUpBtn =(Button) findViewById(R.id.signUpBtn);
        logInBtn = (Button)findViewById(R.id.logInBtn);


        newUserBtn.setOnClickListener(this);
        existingUserBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        logInBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user =  firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    System.out.println("onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getBaseContext(), "onAuthStateChanged:signed_in:" + user.getUid(), Toast.LENGTH_LONG).show();

                    //calling shared preferences to set user uid
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    //setting set pref of user uid
                    prefEditor.putString(getString(R.string.pref_user_uid), user.getUid());
                    prefEditor.commit();
                    isUserSignedIn = true;
                } else {
                    // User is signed out
                    System.out.println("onAuthStateChanged:signed_out");
                    Toast.makeText(getBaseContext(), "onAuthStateChanged:signed_out", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void logIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getBaseContext(), "logInUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_LONG).show();
                        //sets up user default account from list of accounts
                        setUserAccount();
                        //intent to MainActivity
/*                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                        showProgress(true);*/

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "login_failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createAccount(final String email, final String password, final String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getBaseContext(), "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_LONG).show();

                        //getting shared pref for user uid
                        String userID = sharedPref.getString(getString(R.string.pref_user_uid),null);

                        //calling shared preferences to set username
                        SharedPreferences.Editor prefEditor = sharedPref.edit();
                        //setting set pref of username
                        prefEditor.putString(getString(R.string.pref_username), username);
                        prefEditor.commit();

                        //creating user in firebase
                        DatabaseHelper firebaseHalper = new DatabaseHelper();
                        firebaseHalper.createUserInDb(userID, email, username);

                        if(isUserSignedIn){
                            //intent to CreateAccountActivity
                            Intent intent = new Intent(getBaseContext(), CreateAccountActivity.class);
                            startActivity(intent);
                            finish();
                            showProgress(true);
                        }else{
                            Toast.makeText(getBaseContext(), "This email is already in use", Toast.LENGTH_SHORT).show();
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "auth_failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {

        View userOptionsButtonsView = (View)findViewById(R.id.userOptionsView);
        userFormView = (View)findViewById(R.id.userFormView);

        EditText email_et = (EditText)findViewById(R.id.email);
        EditText password_et = (EditText)findViewById(R.id.password);
        EditText username_et = (EditText)findViewById(R.id.username);
        String email = email_et.getText().toString();
        String password = password_et.getText().toString();
        String username = username_et.getText().toString();

        boolean cancel = false;
        View focusView = null;

        switch (v.getId()) {

            case R.id.newUserBtn:
                userOptionsButtonsView.setVisibility(View.GONE);
                userFormView.setVisibility(View.VISIBLE);
                logInBtn.setVisibility(View.GONE);
                break;

            case R.id.existingUserBtn:
                userOptionsButtonsView.setVisibility(View.GONE);
                userFormView.setVisibility(View.VISIBLE);
                username_et.setVisibility(View.GONE);
                signUpBtn.setVisibility(View.GONE);
                break;

            case R.id.logInBtn:

                // Check for a valid password.
                if (TextUtils.isEmpty(password)) {
                    password_et.setError(getString(R.string.error_field_required));
                    focusView = password_et;
                    cancel = true;
                } else if (!isPasswordValid(password)) {
                    password_et.setError(getString(R.string.error_invalid_password));
                    focusView = password_et;
                    cancel = true;
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(email)) {
                    email_et.setError(getString(R.string.error_field_required));
                    focusView = email_et;
                    cancel = true;
                } else if (!isEmailValid(email)) {
                    email_et.setError(getString(R.string.error_invalid_email));
                    focusView = email_et;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    logIn(email,password);
                }
                break;

            case R.id.signUpBtn:

                // Check for a valid password.
                if (TextUtils.isEmpty(password)) {
                    password_et.setError(getString(R.string.error_field_required));
                    focusView = password_et;
                    cancel = true;
                } else if (!containPasswordNumber(password)) {
                    password_et.setError(getString(R.string.error_invalid_password_number));
                    focusView = password_et;
                    cancel = true;
                } else if (!isPasswordValid(password)) {
                    password_et.setError(getString(R.string.error_invalid_password));
                    focusView = password_et;
                    cancel = true;
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(email)) {
                    email_et.setError(getString(R.string.error_field_required));
                    focusView = email_et;
                    cancel = true;
                } else if (!isEmailValid(email)) {
                    email_et.setError(getString(R.string.error_invalid_email));
                    focusView = email_et;
                    cancel = true;
                }

                // Check for a valid username.
                if (TextUtils.isEmpty(username)) {
                    username_et.setError(getString(R.string.error_field_required));
                    focusView = username_et;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    //showProgress(true);
                    createAccount(email, password, username);

                }

                break;

            default:
                break;
        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    private boolean containPasswordNumber(String password) {
        //TODO: Replace this with your own logic
        if(password.matches(".*\\d+.*"))
            return true;
        else
            return false;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        userFormView = (View)findViewById(R.id.userFormView);
        mProgressView = findViewById(R.id.login_progress);
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            userFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            userFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    userFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            userFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void setUserAccount(){
        //getting shared pref for user id
        String userID = sharedPref.getString(getString(R.string.pref_user_uid),null);

        myFirebaseHelper.getUserFromDb(userID, new UserRetriever() {
            @Override
            public void get(User user) {
                //setting user obj in the MyApp class
                myApp.setUser(user);
                //calling shared preferences to set username
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                //setting set pref of username
                prefEditor.putString(getString(R.string.pref_username), user.getUsername());
                prefEditor.commit();

                //setting first account as default if user sign out
                    String accountId = user.getAccounts().keySet().toArray()[0].toString();
                    selectAccountAsDefault(accountId);
            }

            @Override
            public void errorMessage(String message) {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void getUsers(ArrayList<User> usersList, String message) {

            }
        });
    }

    public void selectAccountAsDefault(String accountId){
        String selectedAccountId = accountId;

        //getting account from firebase
        myFirebaseHelper.getAccountFromDb(selectedAccountId, new AccountRetriever() {
            @Override
            public void get(Account account, String accountId, String message) {
                myApp.setCurrentAccount(account);
                myApp.setCurrentAccountId(accountId);
                //calling shared preferences to set account uid
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                //setting set pref of account id and accountName
                prefEditor.putString(getString(R.string.pref_account_id),accountId);
                prefEditor.putString(getString(R.string.pref_accountName), account.getAccName());
                prefEditor.commit();

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
                showProgress(true);
            }

            @Override
            public void errorMessage(String message) {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
