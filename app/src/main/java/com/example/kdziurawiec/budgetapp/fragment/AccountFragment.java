package com.example.kdziurawiec.budgetapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kdziurawiec.budgetapp.R;
import com.example.kdziurawiec.budgetapp.activity.CreateAccountActivity;
import com.example.kdziurawiec.budgetapp.activity.TransactionActivity;
import com.example.kdziurawiec.budgetapp.interfaces.AccountRetriever;
import com.example.kdziurawiec.budgetapp.interfaces.TransactionRetriever;
import com.example.kdziurawiec.budgetapp.model.Account;
import com.example.kdziurawiec.budgetapp.model.Category;
import com.example.kdziurawiec.budgetapp.model.CategoryManager;
import com.example.kdziurawiec.budgetapp.model.DatabaseHelper;
import com.example.kdziurawiec.budgetapp.model.MyApplication;
import com.example.kdziurawiec.budgetapp.model.Transaction;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Owner on 07/03/2017.
 */

public class AccountFragment extends Fragment{

    MyApplication myApp;
    DatabaseHelper myFirebaseHelper;

    ListView mListView;
    MyAdapter adapter;
    ArrayList<Transaction> transactionList;
    TextView titleTV, balanceTV, startBalanceTV, totIncomeTV, totExpenseTV;


    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        myApp = ((MyApplication) getActivity().getApplicationContext());

        titleTV= (TextView) rootView.findViewById(R.id.accTitleTextView);
        balanceTV= (TextView) rootView.findViewById(R.id.balanceTextView);
        startBalanceTV= (TextView) rootView.findViewById(R.id.startBalanceTextView);
        totIncomeTV= (TextView) rootView.findViewById(R.id.totIncomeTextView);
        totExpenseTV= (TextView) rootView.findViewById(R.id.totExpenseTextView);



        transactionList = new ArrayList<Transaction>();


        //getting shared pref for acount id
        SharedPreferences sharedPref = getActivity().getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
        String accountID = sharedPref.getString(getString(R.string.pref_account_id),null);


       // MyApplication appl = (MyApplication)(getActivity().getApplication());
//       if(myApp.getCurrentAccountId()!=null) {
//           String accountID2 = myApp.getCurrentAccountId().toString();
//       }

        myFirebaseHelper = new DatabaseHelper();
        myFirebaseHelper.getAccountFromDb(accountID, new AccountRetriever() {
            @Override
            public void get(Account account, String accountId, String message) {
                myApp.setCurrentAccount(account);
                myApp.setCurrentAccountId(accountId);
                //nested getTransactions inside getAccount becouse need to have account callback first
                myFirebaseHelper.getTransactionsForAccountFromDb(myApp.getCurrentAccountId(), new TransactionRetriever() {
                    @Override
                    public void get(Transaction transaction, String accountId, String message) {

                    }

                    @Override
                    public void getTransactions(ArrayList<Transaction> transactionListResults, String accountId, String message) {
                        transactionList = transactionListResults;
                        ListView mListView = (ListView) getView().findViewById(R.id.transactionList);
                        adapter = new MyAdapter();
                        mListView.setAdapter(adapter);

                        //calculating current balance
                        calculateBalance(transactionList, myApp.getCurrentAccount());
                    }

                    @Override
                    public void errorMessage(String message) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void errorMessage(String message) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });





        FloatingActionButton addTransBtn = (FloatingActionButton) rootView.findViewById(R.id.fab_trans_btn);
        addTransBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //intent to TransactionActivity
                Intent intent = new Intent(rootView.getContext(), TransactionActivity.class);
                startActivity(intent);
                Snackbar.make(view, "Adding new Transaction", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        // Inflate the layout for this fragment
        return rootView;
    }

    public void calculateBalance(ArrayList<Transaction> transList, Account account){

        Double balance = account.getStaringBalance();
        Double totIncome = balance;
        Double totExpense = 0.0;
        for(Transaction trans : transList){
            //checking if transaction is income or expense TO BE IMPLEMENTED
            if(0>1){
                balance = balance+ trans.getAmount();
                totIncome = totIncome + trans.getAmount();
            }else{
                balance = balance - trans.getAmount();
                totExpense = totExpense + trans.getAmount();
            }

        }
        titleTV.setText(account.getAccName());
        startBalanceTV.setText("Starting balance: £" + account.getStaringBalance());
        totIncomeTV.setText("Total income: £" + totIncome);
        totExpenseTV.setText("Total expense: £" + totExpense);
        balanceTV.setText("Current balance: £" + balance);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    //custom adapter list
    public class MyAdapter extends ArrayAdapter<Transaction> {
        public MyAdapter() {
            // We have to use ExampleListActivity.this to refer to the outer class (the activity)
            super(getActivity(), android.R.layout.simple_list_item_1, transactionList);
        }

        public View getView(int index, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_adapter_transaction, parent, false);
            }
            TextView title = (TextView) view.findViewById(R.id.trans_title), detail =
                    (TextView) view.findViewById(R.id.trans_desc), amount =
                    (TextView) view.findViewById(R.id.trans_amount);


            title.setText(transactionList.get(index).getCategory());
            detail.setText(transactionList.get(index).toString());
            amount.setText("£" + transactionList.get(index).getAmount().toString());

            //setting different icon for category

            ImageView icon = (ImageView) view.findViewById(R.id.listIcon);
            String category = transactionList.get(index).getCategory();

            CategoryManager categoryManager = new CategoryManager();
            //searching for matching category
            for(Category cat : categoryManager.getExpenseCategoryList()){
                String image = cat.getImageResource();
                String backgroundColor = cat.getBackgroundColor();
                //if category name match assign image and color
                if(category.toUpperCase().equals(cat.getName().toUpperCase())){
                    icon.setImageResource(getResources().getIdentifier(image, "drawable", getContext().getPackageName()));
                    int backgroundColor2 = ContextCompat.getColor(getContext(), getResources().getIdentifier(backgroundColor, "color", getContext().getPackageName())); ;
                    icon.getBackground().setColorFilter(backgroundColor2, PorterDuff.Mode.MULTIPLY);
                }
            }

/*            icon.setImageResource(R.drawable.ic_category_4);
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
            }*/

            return view;
        }
    }
}
