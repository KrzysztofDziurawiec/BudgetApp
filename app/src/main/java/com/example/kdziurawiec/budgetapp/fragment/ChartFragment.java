package com.example.kdziurawiec.budgetapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kdziurawiec.budgetapp.R;
import com.example.kdziurawiec.budgetapp.interfaces.TransactionRetriever;
import com.example.kdziurawiec.budgetapp.model.Category;
import com.example.kdziurawiec.budgetapp.model.CategoryManager;
import com.example.kdziurawiec.budgetapp.model.DatabaseHelper;
import com.example.kdziurawiec.budgetapp.model.Transaction;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Owner on 07/03/2017.
 */

public class ChartFragment extends Fragment {

    public ChartFragment() {
        // Required empty public constructor
    }
    DatabaseHelper myFirebaseHelper;
    private static String TAG = "ChartFragment";
    private ArrayList<Transaction> transactionList;
    private  Map<String, Double> chartEntries;
    PieChart pieChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chart, container, false);
        Log.d(TAG, "onCreateView: I am in the fragment");
        pieChart =(PieChart) rootView.findViewById(R.id.piechart);
        pieChart.setRotationEnabled(true);
        pieChart.setTransparentCircleAlpha(20);
        pieChart.setHoleRadius(25f);
        pieChart.setCenterText("Current spending");
        pieChart.setCenterTextSize(10);
        //pieChart.setDrawEntryLabels(true);

        //getting shared pref for acount id
        SharedPreferences sharedPref = getActivity().getSharedPreferences("BudgetAppSettings", Context.MODE_PRIVATE);
        String accountID = sharedPref.getString(getString(R.string.pref_account_id),null);
        myFirebaseHelper = new DatabaseHelper();
        //nested getTransactions inside getAccount becouse need to have account callback first
        myFirebaseHelper.getTransactionsForAccountFromDb(accountID, new TransactionRetriever() {
            @Override
            public void get(Transaction transaction, String accountId, String message) {

            }

            @Override
            public void getTransactions(ArrayList<Transaction> transactionListResults, String accountId, String message) {
                transactionList = transactionListResults;
                addDataSetToChart(transactionList);
            }

            @Override
            public void errorMessage(String message) {
                Log.d(TAG, "ERROR in chartFragment: "+message);
            }
        });


        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: Value from chart");
                Log.d(TAG, "onValueSelected: Value e" + e.toString());
                Log.d(TAG, "onValueSelected: Value h" + h.toString());

                int pos = e.toString().indexOf("Entry");
                String amount = e.toString().substring(pos + 17);
                String category = "";
                //getting category from Map by comparing amount
                for (Map.Entry<String,Double> cat : chartEntries.entrySet()) {
                    String key = cat.getKey();
                    Double value = cat.getValue();
                    if(Double.parseDouble(amount)==value){
                        category = key;
                    }
                }
                Log.d(TAG, "substring: " + amount);
                Toast.makeText(getActivity(), "Category: " + category + "\n" + "Amount: £" + String.format("%.2f",Double.parseDouble(amount)), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    //receiving transactionList object from account fragment by using interface implemented in the MainActivity
/*    public void setTransList(ArrayList<Transaction> transactionListReceived){
        transactionList = transactionListReceived;
        addDataSetToChart(transactionList);
    }*/

    private void addDataSetToChart(ArrayList<Transaction> transactionList) {
        Log.d(TAG, "addDataSetToChart: started");

        ArrayList<PieEntry> amountEntries = new ArrayList<>();
        ArrayList<String> categoryEntries = new ArrayList<>();

        //creating Map for category and amount
        chartEntries =new HashMap<>();;
        //looping through transactions to add category and amount to the Map
        for(Transaction t : transactionList){

                if(t.getIsExpense()){
                    Boolean exist = chartEntries.containsKey(t.getCategory());
                    if(exist){
                        Double value = chartEntries.get(t.getCategory());
                        chartEntries.put(t.getCategory(),(value + t.getAmount()));
                    }else{
                        chartEntries.put(t.getCategory(),t.getAmount());
                    }
                }
        }
        //using category manager to define color
        CategoryManager categoryManager = new CategoryManager();
        //add colors
        ArrayList<Integer> colors = new ArrayList<>();

        //looping through Map categories to add them to pie chart arrayLists
        for (Map.Entry<String,Double> cat : chartEntries.entrySet()) {
            String key = cat.getKey();
            Double value = cat.getValue();
            amountEntries.add(new PieEntry(Float.valueOf(value.toString())));
            categoryEntries.add(key);
            //looping through categories and assigning color to current category
            for(Category c : categoryManager.getExpenseCategoryList()){
                String image = c.getImageResource();
                String backgroundColor = c.getBackgroundColor();
                //if category name match assign image and color
                if(key.toUpperCase().equals(c.getName().toUpperCase())){
                    int categoryColor = ContextCompat.getColor(getContext(), getResources().getIdentifier(backgroundColor, "color", getContext().getPackageName())); ;
                    colors.add(categoryColor);
                }
            }
        }


        //create dat set
        PieDataSet pieDataSet = new PieDataSet(amountEntries,"Current spending");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        pieDataSet.setColors(colors);


        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}