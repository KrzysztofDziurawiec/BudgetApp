package com.example.kdziurawiec.budgetapp.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kdziurawiec.budgetapp.R;
import com.example.kdziurawiec.budgetapp.activity.MainActivity;
import com.example.kdziurawiec.budgetapp.interfaces.AccountRetriever;
import com.example.kdziurawiec.budgetapp.interfaces.UserRetriever;
import com.example.kdziurawiec.budgetapp.model.Account;
import com.example.kdziurawiec.budgetapp.model.DatabaseHelper;
import com.example.kdziurawiec.budgetapp.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owner on 07/03/2017.
 */

public class MembersFragment extends Fragment {

    DatabaseHelper myFirebaseHelper;
    ListView mListView;
    MyAdapter adapter;
    ArrayList<String> memberList;

    public MembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_members, container, false);

        myFirebaseHelper = new DatabaseHelper();

        memberList = new ArrayList<>();

        //getting shared pref for acount id
        SharedPreferences sharedPref = getActivity().getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
        String accountID = sharedPref.getString(getString(R.string.pref_account_id),null);

        getListOfUsersForAccount(accountID);




        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling dialog for adding member by email
                showAddMemberDialog();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void showAddMemberDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_add_member_title);
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_member_email_input, (ViewGroup) getView(), false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String  email = input.getText().toString();
                addUserToAccount(email);
                //dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void getListOfUsersForAccount(String accountID){
        myFirebaseHelper.getAccountFromDb(accountID, new AccountRetriever() {
            @Override
            public void get(Account account, String accountId, String message) {
                for(String username : account.getUsers().values()){
                     memberList.add(username);
                }
                mListView = (ListView) getView().findViewById(R.id.membersList);
                //ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, memberList);
                adapter = new MyAdapter();
                mListView.setAdapter(adapter);

            }

            @Override
            public void errorMessage(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addUserToAccount(String email){
        //searching for user
        myFirebaseHelper.getUsersListFromDbByUsername(email, new UserRetriever() {
            @Override
            public void get(User user) {

            }

            @Override
            public void errorMessage(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void getUsers(final ArrayList<User> usersList, String message) {
                ShowAlertDialogWithListview(usersList);
            }
        });
    }

    public void ShowAlertDialogWithListview(final ArrayList<User> usersList)
    {
        List<String> mUsers = new ArrayList<String>();
        //looping through hashMap of accounts of current user and adding account Name to List
        for(User u : usersList){
            mUsers.add(u.getUsername());
        }

        //Create sequence of items
        final CharSequence[] Users = mUsers.toArray(new String[mUsers.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(getString(R.string.dialog_add_member_list_title));
        dialogBuilder.setItems(Users, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = Users[item].toString();  //Selected item in listview
                User selectedUser = new User();
                //looping through hashMap of accounts of current user and checking for account id when account name selected
                for(User u : usersList){
                    if(u.getUsername().equals(selectedText)){
                        selectedUser = u;
                    }
                }

                //getting shared pref for acount id
                SharedPreferences sharedPref = getActivity().getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
                String accountID = sharedPref.getString(getString(R.string.pref_account_id),null);
                String accountName = sharedPref.getString(getString(R.string.pref_accountName),null);
                //adding user to account and account to user
                myFirebaseHelper.addUserToAccountInDb(selectedUser, accountID, accountName);

                //refresh fragments for members
                List<Fragment> fragmentsList = getFragmentManager().getFragments();
                Fragment currentFragment = getFragmentManager().findFragmentById(fragmentsList.get(2).getId());
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }


    //custom adapter list
    public class MyAdapter extends ArrayAdapter<String> implements View.OnClickListener {
        public MyAdapter() {
            // We have to use ExampleListActivity.this to refer to the outer class (the activity)
            super(getActivity(), android.R.layout.simple_list_item_1, memberList);
        }

        public View getView(int index, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_adapter_member, parent, false);
            }
            TextView title = (TextView) view.findViewById(R.id.member_name);
            ImageButton btn_delete = (ImageButton) view.findViewById(R.id.btn_delete);

            btn_delete.setOnClickListener(this);
            String memberName = memberList.get(index);
            title.setText(memberName);

            //setting different icon for members by adding initial  of the name and setting colour
            Button icon = (Button) view.findViewById(R.id.listIcon);
            String memberInitial = memberName.substring(0, 1).toUpperCase();

            icon.setText(memberInitial);


            if(memberInitial.equals("A") || memberInitial.equals("L")|| memberInitial.equals("Y"))
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconRed), PorterDuff.Mode.MULTIPLY);
            else if (memberInitial.equals("B")|| memberInitial.equals("M")|| memberInitial.equals("Z"))
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconPurple), PorterDuff.Mode.MULTIPLY);
            else if (memberInitial.equals("C")|| memberInitial.equals("N"))
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconIndigo), PorterDuff.Mode.MULTIPLY);
            else if (memberInitial.equals("D")|| memberInitial.equals("O"))
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconBlue), PorterDuff.Mode.MULTIPLY);
            else if (memberInitial.equals("E")|| memberInitial.equals("P"))
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconTeal), PorterDuff.Mode.MULTIPLY);
            else if (memberInitial.equals("F")|| memberInitial.equals("R"))
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconGreen), PorterDuff.Mode.MULTIPLY);
            else if (memberInitial.equals("G")|| memberInitial.equals("S"))
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconLime), PorterDuff.Mode.MULTIPLY);
            else if (memberInitial.equals("H")|| memberInitial.equals("T"))
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconYellow), PorterDuff.Mode.MULTIPLY);
            else if (memberInitial.equals("I")|| memberInitial.equals("V"))
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconOrange), PorterDuff.Mode.MULTIPLY);
            else if (memberInitial.equals("J")|| memberInitial.equals("W"))
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorDeepOrange), PorterDuff.Mode.MULTIPLY);
            else if (memberInitial.equals("K")|| memberInitial.equals("X"))
                icon.getBackground().setColorFilter(getResources().getColor(R.color.colorIconBlueGrey), PorterDuff.Mode.MULTIPLY);

            return view;
        }

        @Override
        public void onClick(View v) {
            int position = mListView.getPositionForView(v);
            //delete selected position
            memberList.remove(position);
            adapter.notifyDataSetChanged();
        }
    }
}