package com.example.kdziurawiec.budgetapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
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
import com.example.kdziurawiec.budgetapp.activity.LoginActivity;

import java.util.ArrayList;

/**
 * Created by Owner on 07/03/2017.
 */

public class MembersFragment extends Fragment {


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

        memberList = new ArrayList<>();
        memberList.add("Bini");
        memberList.add("Chris");
        memberList.add("Keeya");
        memberList.add("Crystal");


        mListView = (ListView) rootView.findViewById(R.id.membersList);
        //ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, memberList);
        adapter = new MyAdapter();
        mListView.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Adding member to the group", Snackbar.LENGTH_LONG)
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