package com.exa.expensetracker.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.exa.expensetracker.R;

import com.exa.expensetracker.ui.home.HomeDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SlideshowFragment extends Fragment {

    private TextView cashinAmountToday;
    private TextView cashoutAmountToday;
    private ProgressBar progressBarToday;
    private RecyclerView recyclerViewToday;

    private TodaySpentAdapter todaySpentAdapter;
    private List<HomeDataModel> myHomeDataModelList;

    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;
    private String onlineUser="";



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        cashinAmountToday=root.findViewById(R.id.cashin_amount_today);
        cashoutAmountToday=root.findViewById(R.id.cashout_amount_today);
        progressBarToday=root.findViewById(R.id.progessBar_today);
        recyclerViewToday=root.findViewById(R.id.recyclerView_today);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewToday.setLayoutManager(linearLayoutManager);


        mAuth= FirebaseAuth.getInstance();
        onlineUser=mAuth.getCurrentUser().getUid();
        budgetRef= FirebaseDatabase.getInstance().getReference().child("budget").child(onlineUser);

        myHomeDataModelList= new ArrayList<>();
        todaySpentAdapter = new TodaySpentAdapter(getActivity(), myHomeDataModelList);
        recyclerViewToday.setAdapter(todaySpentAdapter);
        todaySpentAdapter.notifyDataSetChanged();


        readTodayItems();


        return root;
    }

    private void readTodayItems(){
        DateFormat dateFormat= new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal= Calendar.getInstance();
        String date=dateFormat.format(cal.getTime());
//
//        MutableDateTime epoach=new MutableDateTime();
//        epoach.setTime(0);
//        DateTime now= new DateTime();
//        Weeks weeks=Weeks.weeksBetween(epoach,now);

        Query query= budgetRef.orderByChild("date").equalTo(date);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 myHomeDataModelList.clear();


                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    HomeDataModel homeDataModel=dataSnapshot.getValue(HomeDataModel.class);
                    myHomeDataModelList.add(homeDataModel);

                }

                todaySpentAdapter.notifyDataSetChanged();
                progressBarToday.setVisibility(View.GONE);

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
                    cashoutAmountToday.setText(cashoutTotal.toString());
                    cashinAmountToday.setText(cashinTotal.toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}