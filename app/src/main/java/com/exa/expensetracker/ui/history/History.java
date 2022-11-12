package com.exa.expensetracker.ui.history;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.exa.expensetracker.R;
import com.exa.expensetracker.ui.home.HomeDataModel;
import com.exa.expensetracker.ui.slideshow.TodaySpentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class History extends Fragment implements DatePickerDialog.OnDateSetListener  {

    private RecyclerView recyclerViewHistory;


    private HistoryAdapter historyAdapter;
    private List<HomeDataModel>myHomeDataModelList;

    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;
    private String onlineUser="";

    private EditText search;
    private TextView cashinAmountHistory;
    private TextView cashoutAmountHistory;




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        search= root.findViewById(R.id.search);
        cashinAmountHistory=root.findViewById(R.id.cashin_amount_history);
        cashoutAmountHistory=root.findViewById(R.id.cashout_amount_history);

        mAuth= FirebaseAuth.getInstance();
        onlineUser=mAuth.getCurrentUser().getUid();
        budgetRef= FirebaseDatabase.getInstance().getReference().child("budget").child(onlineUser);

        recyclerViewHistory= root.findViewById(R.id.recyclerView_history);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewHistory.setLayoutManager(linearLayoutManager);

        myHomeDataModelList= new ArrayList<>();
        historyAdapter = new HistoryAdapter(getActivity(), myHomeDataModelList);
        recyclerViewHistory.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();


         search.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 showDatePickerDialog();

             }
         });






        return root;
    }

    private void showDatePickerDialog(){

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

        int months= month+1;
        String date;
        if(months<10)   date=dayOfMonth+"-"+"0"+months+"-"+year;
        else date = dayOfMonth+"-"+months+"-"+year;

        search.setText(date.toString());

        Toast.makeText(getActivity(), date, Toast.LENGTH_SHORT).show();

        Query query=budgetRef.orderByChild("date").equalTo(date);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myHomeDataModelList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    HomeDataModel homeDataModel=dataSnapshot.getValue(HomeDataModel.class);
                    myHomeDataModelList.add(homeDataModel);

                }

                historyAdapter.notifyDataSetChanged();
                recyclerViewHistory.setVisibility(View.VISIBLE);

                int totalCashinAmount=0;
                int totalCashoutAmount=0;

                for(DataSnapshot snap:snapshot.getChildren()){
                    HomeDataModel homeDataModel=snap.getValue(HomeDataModel.class);
                    if(homeDataModel.getStatus().endsWith("n"))
                        totalCashinAmount += homeDataModel.getAmount();
                    else{
                        totalCashoutAmount +=homeDataModel.getAmount();
                    }
                    String cashinTotal=String.valueOf(totalCashinAmount);
                    String cashoutTotal=String.valueOf(totalCashoutAmount);
                    cashoutAmountHistory.setText(cashoutTotal.toString());
                    cashinAmountHistory.setText(cashinTotal.toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}