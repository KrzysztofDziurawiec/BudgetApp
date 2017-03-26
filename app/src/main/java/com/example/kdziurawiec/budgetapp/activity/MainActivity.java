package com.example.kdziurawiec.budgetapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity  {

    //creating firebaseAuth instance
    private FirebaseAuth mAuth;
    //creating firebaseAuth instance listener
    private FirebaseAuth.AuthStateListener mAuthListener;

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

//Checking if user is signed in. If not redirect to LoginActivity
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    System.out.println("onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getBaseContext(), "onAuthStateChanged:signed_in:" + user.getUid(), Toast.LENGTH_LONG).show();
                } else {
                    // User is signed out
                    //intent to MainActivity
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
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

            case R.id.action_settings:
                Toast.makeText(getApplicationContext(),"You cliked: "+ getString(R.string.action_settings),Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_add_account:
                Toast.makeText(getApplicationContext(),"You clicked: "+ getString(R.string.action_add_account),Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"You clicked: "+ getString(R.string.action_log_out),Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_app_info:
                Toast.makeText(getApplicationContext(),"You clicked: "+ getString(R.string.action_app_info),Toast.LENGTH_SHORT).show();
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
