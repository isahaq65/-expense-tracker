package com.exa.expensetracker.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

     private TextView cashinAmountWeek;
     private TextView cashoutAmountWeek;
     private ProgressBar progressBarWeek;
     private RecyclerView recyclerViewWeek;

     private WeekSpentAdapter weekSpentAdapter;
     private List<HomeDataModel>myHomeDataModelList;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private String onlineUser="";





    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        cashinAmountWeek=root.findViewById(R.id.cashin_amount_week);
        cashoutAmountWeek=root.findViewById(R.id.cashout_amount_week);
        progressBarWeek=root.findViewById(R.id.progessBar_week);
        recyclerViewWeek=root.findViewById(R.id.recyclerView_week);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewWeek.setLayoutManager(linearLayoutManager);


        mAuth= FirebaseAuth.getInstance();
        onlineUser=mAuth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference().child("budget").child(onlineUser);

        myHomeDataModelList= new ArrayList<>();
        weekSpentAdapter=new WeekSpentAdapter(getActivity(), myHomeDataModelList);
        recyclerViewWeek.setAdapter(weekSpentAdapter);

        readWeekItems();


        return root;
    }

    private void readWeekItems(){

        MutableDateTime epoach=new MutableDateTime();
        epoach.setTime(0);
        DateTime now= new DateTime();
        Weeks weeks=Weeks.weeksBetween(epoach,now);

        Query query= reference.orderByChild("week").equalTo(weeks.getWeeks());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myHomeDataModelList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    HomeDataModel homeDataModel=dataSnapshot.getValue(HomeDataModel.class);
                    myHomeDataModelList.add(homeDataModel);

                }

                weekSpentAdapter.notifyDataSetChanged();
                progressBarWeek.setVisibility(View.GONE);

                int totalCashinAmount=0;
                int totalCashoutAmount=0;

                for(DataSnapshot snap:snapshot.getChildren()){
                    HomeDataModel homeDataModel=snap.getValue(HomeDataModel.class);
                    if(homeDataModel.getStatus().endsWith("n"))
                        totalCashinAmount += homeDataModel.getAmount();
                    else {
                        totalCashoutAmount +=homeDataModel.getAmount();
                    }
                    String cashinTotal=String.valueOf(totalCashinAmount);
                    String cashoutTotal=String.valueOf(totalCashoutAmount);
                    cashoutAmountWeek.setText(cashoutTotal);
                    cashinAmountWeek.setText(cashinTotal);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}