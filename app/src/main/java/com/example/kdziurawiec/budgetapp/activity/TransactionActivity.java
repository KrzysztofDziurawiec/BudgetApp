package com.example.kdziurawiec.budgetapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kdziurawiec.budgetapp.R;
import com.example.kdziurawiec.budgetapp.interfaces.TransactionRetriever;
import com.example.kdziurawiec.budgetapp.model.DatabaseHelper;
import com.example.kdziurawiec.budgetapp.model.MyApplication;
import com.example.kdziurawiec.budgetapp.model.Transaction;

import java.util.ArrayList;
import java.util.Date;


public class TransactionActivity extends AppCompatActivity implements  View.OnClickListener {

    private Toolbar toolbar;
    private ListView mListView;
    private MyAdapter adapter;
    private ArrayList<String> categoryList;
    private String selectedCategory;
    private Double amount;
    private DatabaseHelper myFirebaseHelper;
    //MyApp class which stores values across activities
    MyApplication myApp;
    View updateview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //sets the toolbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myApp = ((MyApplication) getApplicationContext());

        Button addTransBtn = (Button)findViewById(R.id.addTransactionBtn);
        addTransBtn.setOnClickListener(this);


        categoryList = new ArrayList<>();
        categoryList.add(getResources().getString(R.string.category_1));
        categoryList.add(getResources().getString(R.string.category_2));
        categoryList.add(getResources().getString(R.string.category_3));
        categoryList.add(getResources().getString(R.string.category_4));
        categoryList.add(getResources().getString(R.string.category_5));

        mListView = (ListView) findViewById(R.id.categoryList);
        //ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, memberList);
        adapter = new MyAdapter();
        mListView.setAdapter(adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (updateview != null) updateview.setBackgroundColor(Color.TRANSPARENT);
                updateview = view;

                view.setBackgroundColor( getResources().getColor(R.color.colorPrimary));;
                selectedCategory= categoryList.get(position);
            }
        });
    }

    //return to MainActivity when toolbar back btn pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onClick(View v) {
        createTransaction();
    }

    public void createTransaction(){
        View focusView = null;

        EditText amount_et = (EditText)findViewById(R.id.amountEditText);
        amount = Double.parseDouble(amount_et.getText().toString());

        //getting shared pref for account id
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
        String accountID = sharedPref.getString(getString(R.string.pref_account_id),null);

        //getting username from myApp
        String username = myApp.getUser().getUsername();

        Date date = new Date();
        CharSequence stringDate = DateFormat.format("dd-MM-yy hh:mm", date.getTime());

        // Check for a valid amount.
        if ((amount == null)||(amount==0)) {
            amount_et.setError(getString(R.string.error_field_required_transaction));
            focusView = amount_et;
            focusView.requestFocus();
        }else{

            Transaction newTransaction = new Transaction(username, stringDate.toString(),selectedCategory,amount);

            // creating new transaction in firebase
            myFirebaseHelper = new DatabaseHelper();
            myFirebaseHelper.createTransactionInDb(newTransaction, accountID, new TransactionRetriever() {
                @Override
                public void get(Transaction transaction, String accountId, String message) {
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                    //intent to CreateAccountActivity
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void getTransactions(ArrayList<Transaction> transactionList, String accountId, String message) {

                }

                @Override
                public void errorMessage(String message) {
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        }

    }




    //custom adapter list
    public class MyAdapter extends ArrayAdapter<String> {
        public MyAdapter() {
            // We have to use ExampleListActivity.this to refer to the outer class (the activity)
            super(getBaseContext() , android.R.layout.simple_list_item_1, categoryList);
        }

        public View getView(int index, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_adapter_category, parent, false);
            }
            TextView title = (TextView) view.findViewById(R.id.categoryTextView);

            String category = categoryList.get(index);
            title.setText(category);

            //setting different icon for category

            ImageView icon = (ImageView) view.findViewById(R.id.listIcon);

            icon.setImageResource(R.drawable.ic_category_4);
            icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconRed), PorterDuff.Mode.MULTIPLY);

            if(category.toUpperCase().equals((getResources().getString(R.string.category_1)).toUpperCase())){
                icon.setImageResource(R.drawable.ic_category_1);
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconIndigo), PorterDuff.Mode.MULTIPLY);
            }
            else if(category.toUpperCase().equals((getResources().getString(R.string.category_2)).toUpperCase())){
                icon.setImageResource(R.drawable.ic_category_2);
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconGreen), PorterDuff.Mode.MULTIPLY);
            }
            else if(category.toUpperCase().equals((getResources().getString(R.string.category_3)).toUpperCase())){
                icon.setImageResource(R.drawable.ic_category_3);
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconTeal), PorterDuff.Mode.MULTIPLY);
            }
            else if(category.toUpperCase().equals((getResources().getString(R.string.category_4)).toUpperCase())){
                icon.setImageResource(R.drawable.ic_category_4);
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorDeepOrange), PorterDuff.Mode.MULTIPLY);
            }
            else if(category.toUpperCase().equals((getResources().getString(R.string.category_5)).toUpperCase())){
                icon.setImageResource(R.drawable.ic_category_5);
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconBlueGrey), PorterDuff.Mode.MULTIPLY);
            }

            return view;
        }

    }
}