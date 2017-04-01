package com.example.kdziurawiec.budgetapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

    ListView mListView;
    MyAdapter adapter;
    ArrayList<Transaction> transactionList;


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

        TextView balanceTextView = (TextView) rootView.findViewById(R.id.balanceTextView);
        balanceTextView.setText("Current balance is : £259.99");

        transactionList = new ArrayList<Transaction>();


        //getting shared pref for acount id
        SharedPreferences sharedPref = getActivity().getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
        String accountID = sharedPref.getString(getString(R.string.pref_account_id),null);

       // MyApplication appl = (MyApplication)(getActivity().getApplication());
//       if(myApp.getCurrentAccountId()!=null) {
//           String accountID2 = myApp.getCurrentAccountId().toString();
//       }



        //connecting to firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("transactions").child(accountID).addValueEventListener(new ValueEventListener() {
            //firebase event listener
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //looping through results, creating transaction objects and adding them to the transactionList
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Transaction transFromFirabase = ds.getValue(Transaction.class);
                    transactionList.add(transFromFirabase);
                }

                ListView mListView = (ListView) getView().findViewById(R.id.transactionList);
                adapter = new MyAdapter();
                mListView.setAdapter(adapter);
               // adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                //Toast.makeText(getActivity(), databaseError.toString(), Toast.LENGTH_LONG).show();
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
