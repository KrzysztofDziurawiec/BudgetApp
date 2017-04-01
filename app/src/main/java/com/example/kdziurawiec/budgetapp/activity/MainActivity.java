package com.example.kdziurawiec.budgetapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kdziurawiec.budgetapp.R;
import com.example.kdziurawiec.budgetapp.adapter.ViewPagerAdapter;
import com.example.kdziurawiec.budgetapp.fragment.AccountFragment;
import com.example.kdziurawiec.budgetapp.fragment.ChartFragment;
import com.example.kdziurawiec.budgetapp.fragment.MembersFragment;
import com.example.kdziurawiec.budgetapp.interfaces.AccountRetriever;
import com.example.kdziurawiec.budgetapp.interfaces.UserRetriever;
import com.example.kdziurawiec.budgetapp.model.Account;
import com.example.kdziurawiec.budgetapp.model.DatabaseHelper;
import com.example.kdziurawiec.budgetapp.model.MyApplication;
import com.example.kdziurawiec.budgetapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    //creating firebaseAuth instance
    private FirebaseAuth mAuth;
    //creating firebaseAuth instance listener
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseHelper myFirebaseHelper;
    //MyApp class which stores values across activities
    MyApplication myApp;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_card,
            R.drawable.ic_chart,
            R.drawable.ic_add_group
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        myFirebaseHelper = new DatabaseHelper();
        //Checking if user is signed in. If not redirect to LoginActivity
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    myFirebaseHelper.getUserFromDb(user.getUid().toString(), new UserRetriever() {
                        @Override
                        public void get(User user) {
                            //setting user obj in the MyApp class
                            myApp.setUser(user);
                            //calling shared preferences to set username
                            SharedPreferences sharedPref = getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
                            SharedPreferences.Editor prefEditor = sharedPref.edit();
                            //setting set pref of username
                            prefEditor.putString(getString(R.string.pref_username), user.getUsername());
                            prefEditor.commit();
                        }

                        @Override
                        public void errorMessage(String message) {
                            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                        }
                    });
                    // User is signed in
                    System.out.println("onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getBaseContext(), "onAuthStateChanged:signed_in:" + user.getUid(), Toast.LENGTH_LONG).show();
                } else {
                    // User is signed out
                    //intent to LoginActivity
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                    System.out.println("onAuthStateChanged:signed_out");
                    Toast.makeText(getBaseContext(), "onAuthStateChanged:signed_out", Toast.LENGTH_LONG).show();
                }
            }
        };
        myApp = ((MyApplication) getApplicationContext());
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        //setting viePager and tabLayout for tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons(); //setting tab icons
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
    //setting tab icons
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }
    //setting viePager and tabLayout for tabs
    private void setupViewPager(ViewPager viewPager) {
        //creating adapter for tabs
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AccountFragment(), getString(R.string.tab_item_account));
        adapter.addFragment(new ChartFragment(), getString(R.string.tab_item_chart));
        adapter.addFragment(new MembersFragment(), getString(R.string.tab_item_members));
        Fragment acc = new AccountFragment();

        viewPager.setAdapter(adapter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //event listener for menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;
        switch (item.getItemId()) {

            case R.id.action_accounts:
                Toast.makeText(getApplicationContext(),"You cliked: "+ getString(R.string.action_accounts),Toast.LENGTH_SHORT).show();
                ShowAlertDialogWithListview();
                return true;
            case R.id.action_add_account:
                //intent to CreateAccountActivity
                intent = new Intent(this, CreateAccountActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_log_in:
                Toast.makeText(getApplicationContext(),"You clicked: "+ getString(R.string.action_log_in),Toast.LENGTH_SHORT).show();
                //intent to loginActivity
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_log_out:
                mAuth.signOut();
                Toast.makeText(getApplicationContext(),"Logged out",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_app_info:
                Toast.makeText(getApplicationContext(),"You clicked: "+ getString(R.string.action_app_info),Toast.LENGTH_SHORT).show();
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void ShowAlertDialogWithListview()
    {
        List<String> mAccounts = new ArrayList<String>();
        //looping through hashMap of accounts of current user and adding account Name to List
        for(String accName : myApp.getUser().getAccounts().values()){
            mAccounts.add(accName);
        }

        //Create sequence of items
        final CharSequence[] Accounts = mAccounts.toArray(new String[mAccounts.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.dialog_accounts_title));
        dialogBuilder.setItems(Accounts, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = Accounts[item].toString();  //Selected item in listview
                String selectedAccountId = "";
                //looping through hashMap of accounts of current user and checking for account id when account name selected
                for(String key : myApp.getUser().getAccounts().keySet()){
                    if(myApp.getUser().getAccounts().get(key).equals(selectedText)){
                        selectedAccountId = key;
                    }
                }

                //getting account from firebase
                myFirebaseHelper.getAccountFromDb(selectedAccountId, new AccountRetriever() {
                    @Override
                    public void get(Account account, String accountId, String message) {
                        myApp.setCurrentAccount(account);
                        myApp.setCurrentAccountId(accountId);
                        //calling shared preferences to set account uid
                        SharedPreferences sharedPref = getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor prefEditor = sharedPref.edit();
                        //setting set pref of account id and accountName
                        prefEditor.putString(getString(R.string.pref_account_id),accountId);
                        prefEditor.putString(getString(R.string.pref_accountName), account.getAccName());
                        prefEditor.commit();
                        //refresh fragments for current account
/*                        Fragment currentFragment = getFragmentManager().findFragmentById(0);
                        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                        fragTransaction.detach(currentFragment);
                        fragTransaction.attach(currentFragment);
                        fragTransaction.commit();*/

                        setupViewPager(viewPager);
                        setupTabIcons(); //setting tab icon
                    }

                    @Override
                    public void errorMessage(String message) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

}
