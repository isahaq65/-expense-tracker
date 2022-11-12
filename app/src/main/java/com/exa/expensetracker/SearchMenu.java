package com.exa.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exa.expensetracker.ui.history.HistoryAdapter;
import com.exa.expensetracker.ui.home.HomeDataModel;
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

public class SearchMenu extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerViewSearchMenu;


    private HistoryAdapter historyAdapter;
    private List<HomeDataModel> myHomeDataModelList;

    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;
    private String onlineUser="";

    private EditText search;
    private TextView cashinAmountSearch;
    private TextView cashoutAmountSearch;
    private CardView cardSummurySearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search");

        search= findViewById(R.id.search_menu);
        cashinAmountSearch=findViewById(R.id.cashin_amount_search_menu);
        cashoutAmountSearch=findViewById(R.id.cashout_amount_search_menu);
        cardSummurySearch=findViewById(R.id.cardSummurySearch);
        cardSummurySearch.setVisibility(View.GONE);

        mAuth= FirebaseAuth.getInstance();
        onlineUser=mAuth.getCurrentUser().getUid();
        budgetRef= FirebaseDatabase.getInstance().getReference().child("budget").child(onlineUser);

        recyclerViewSearchMenu=findViewById(R.id.recyclerView_search_menu);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewSearchMenu.setLayoutManager(linearLayoutManager);

        myHomeDataModelList= new ArrayList<>();
        historyAdapter = new HistoryAdapter(this, myHomeDataModelList);
        recyclerViewSearchMenu.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();


       search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();

            }
        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 String mSearchText= search.getText().toString();
                 dataRetrive(mSearchText);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void dataRetrive(String searchString){

        Query query=budgetRef.orderByChild("item").startAt(searchString).endAt(searchString +"\uf8ff");

        cardSummurySearch.setVisibility(View.VISIBLE);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myHomeDataModelList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    HomeDataModel homeDataModel=dataSnapshot.getValue(HomeDataModel.class);
                    myHomeDataModelList.add(homeDataModel);

                }

                historyAdapter.notifyDataSetChanged();
                recyclerViewSearchMenu.setVisibility(View.VISIBLE);

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
                    cashoutAmountSearch.setText(cashoutTotal.toString());
                    cashinAmountSearch.setText(cashinTotal.toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showDatePickerDialog(){

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
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

        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();

        Query query=budgetRef.orderByChild("date").equalTo(date);
        cardSummurySearch.setVisibility(View.VISIBLE);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myHomeDataModelList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    HomeDataModel homeDataModel=dataSnapshot.getValue(HomeDataModel.class);
                    myHomeDataModelList.add(homeDataModel);

                }

                historyAdapter.notifyDataSetChanged();
                recyclerViewSearchMenu.setVisibility(View.VISIBLE);

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
                    cashoutAmountSearch.setText(cashoutTotal.toString());
                    cashinAmountSearch.setText(cashinTotal.toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            Intent intent= new Intent( SearchMenu.this,MainActivity.class);
            startActivity(intent);


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode== KeyEvent.KEYCODE_BACK)   {
            Intent intent= new Intent( SearchMenu.this,MainActivity.class);
            startActivity(intent);

            finish();
        }
        return true;
    }
}