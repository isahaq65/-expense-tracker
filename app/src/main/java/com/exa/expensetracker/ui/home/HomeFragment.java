package com.exa.expensetracker.ui.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private TextView cashinAmount;
    private TextView cashoutAmount;
    private TextView note;
    private RecyclerView recyclerViewHome;


    private Button btnCashin;
    private Button btnCashout;

    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;

    HomeAdapter homeAdapter;

    private String post_key = "";
    private String item="";
    private int amount=0;
    private String status="";
    private String noteHolder="";





    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth= FirebaseAuth.getInstance();
        budgetRef= FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(getActivity());

        btnCashin = root.findViewById(R.id.btn_cashin);
        btnCashout = root.findViewById(R.id.btn_cashout);
        cashinAmount =root.findViewById(R.id.cashin_amount);
        cashoutAmount= root.findViewById(R.id.cashout_amount);
        recyclerViewHome=root.findViewById(R.id.recyclerView_home);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewHome.setLayoutManager(linearLayoutManager);
       // recyclerViewHome.setHasFixedSize(true);



//        FirebaseRecyclerOptions<HomeDataModel>options= new FirebaseRecyclerOptions.Builder<HomeDataModel>()
//                .setQuery(budgetRef, HomeDataModel.class)
//                .build();
//
//
//        homeAdapter= new HomeAdapter(options);
//        recyclerViewHome.setAdapter(homeAdapter);
//        homeAdapter.notifyDataSetChanged();
//


        budgetRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalCashinAmount=0;
                int totalCashoutAmount=0;




                for(DataSnapshot snap:snapshot.getChildren()){

                    HomeDataModel homeDataModel=snap.getValue(HomeDataModel.class);
                   // Toast.makeText(getActivity()," home cash" + homeDataModel.getStatus(), Toast.LENGTH_SHORT).show();
                    if(homeDataModel.getStatus().endsWith("n")) {

                        totalCashinAmount += homeDataModel.getAmount();
                        //Toast.makeText(getActivity(), homeDataModel.getStatus() + " home cash in", Toast.LENGTH_SHORT).show();

                    }
                    else {

                        totalCashoutAmount +=homeDataModel.getAmount();
                        //Toast.makeText(getActivity(), homeDataModel.getStatus() + " home cash out", Toast.LENGTH_SHORT).show();
                    }

                    String cashinTotal=String.valueOf(totalCashinAmount);
                    String cashoutTotal=String.valueOf(totalCashoutAmount);
                    cashoutAmount.setText(cashoutTotal.toString());
                    cashinAmount.setText(cashinTotal.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });


        btnCashin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        btnCashout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCashoutItem();
            }
        });





        return root;
    }

    void addItem() {
        AlertDialog.Builder myDialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View dialogView= inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(dialogView);


        final AlertDialog dialog= myDialog.create();
        dialog.setCancelable(false);

        final Spinner  itemSpinner=dialogView.findViewById(R.id.itemsSpinner);
        final EditText amount=dialogView.findViewById(R.id.amount);
        final EditText note=dialogView.findViewById(R.id.note);
        final Button cancel= dialogView.findViewById(R.id.cancel);
        final Button save= dialogView.findViewById(R.id.save);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetAmount= amount.getText().toString();
                String budgetItem= itemSpinner.getSelectedItem().toString();
                String notesWritten= note.getText().toString().trim();

                if(TextUtils.isEmpty(budgetAmount)){
                    amount.setError("Amount is required");
                }
                if(budgetItem.equals("Select item")){
                    Toast.makeText(getActivity(), "Select a valid item", Toast.LENGTH_SHORT).show();


                }
                if(TextUtils.isEmpty(notesWritten)){
                    note.setError("write note");

                }
                else{
                    loader.setMessage("Adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id=budgetRef.push().getKey();
                    DateFormat dateFormat= new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal= Calendar.getInstance();
                    String date=dateFormat.format(cal.getTime());

                    DateFormat dt= new SimpleDateFormat("h:mm a");
                    String time=dt.format(cal.getTime());

                    MutableDateTime epoach=new MutableDateTime();
                    epoach.setTime(0);
                    DateTime now= new DateTime();
                    Weeks weeks=Weeks.weeksBetween(epoach,now);
                    Months months= Months.monthsBetween(epoach, now);

                    HomeDataModel homeDataModel= new HomeDataModel(budgetItem, date, id, notesWritten, Integer.parseInt(budgetAmount),months.getMonths(), "Cash In",weeks.getWeeks(), time);

                    budgetRef.child(id).setValue(homeDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(),"budget item is added successfully",Toast.LENGTH_SHORT ).show();
                            }else{
                                Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                            loader.dismiss();

                        }
                    });
                }
               dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();


    }

    void addCashoutItem(){
        AlertDialog.Builder myDialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View dialogView= inflater.inflate(R.layout.cashout_input_layout, null);
        myDialog.setView(dialogView);


        final AlertDialog dialog= myDialog.create();
        dialog.setCancelable(false);

        final Spinner  itemSpinner=dialogView.findViewById(R.id.itemsSpinner_Cashout);
        final EditText amount=dialogView.findViewById(R.id.amount_cashout);
        final EditText note=dialogView.findViewById(R.id.note_cashout);
        final Button cancel= dialogView.findViewById(R.id.cancel_cashout);
        final Button save= dialogView.findViewById(R.id.save_cashout);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetAmount= amount.getText().toString();
                String budgetItem= itemSpinner.getSelectedItem().toString();
                String notesWritten= note.getText().toString();

                if(TextUtils.isEmpty(budgetAmount)){
                    amount.setError("Amount is required");
                }
                if(budgetItem.equals("Select item")){
                    Toast.makeText(getActivity(), "Select a valid item", Toast.LENGTH_SHORT).show();

                }
                if(TextUtils.isEmpty(notesWritten)){
                    note.setError("write note");

                }
                else{
                    loader.setMessage("Adding a Cashout Item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id=budgetRef.push().getKey();
                    DateFormat dateFormat= new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal= Calendar.getInstance();
                    String date=dateFormat.format(cal.getTime());

                    DateFormat dt= new SimpleDateFormat("h:mm a");
                    String time=dt.format(cal.getTime());


                    MutableDateTime epoach=new MutableDateTime();
                    epoach.setTime(0);
                    DateTime now= new DateTime();
                    Weeks weeks=Weeks.weeksBetween(epoach,now);
                    Months months= Months.monthsBetween(epoach, now);

                    HomeDataModel homeDataModel=new HomeDataModel(budgetItem, date, id, notesWritten, Integer.parseInt(budgetAmount),months.getMonths(), "Cash Out",weeks.getWeeks(), time);

                    budgetRef.child(id).setValue(homeDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(),"Cashout item is added successfully",Toast.LENGTH_SHORT ).show();
                            }else{
                                Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                            loader.dismiss();

                        }
                    });
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();



    }

    @Override
    public void onStart() {
        super.onStart();
//        homeAdapter.startListening();
//        homeAdapter.notifyDataSetChanged();


        FirebaseRecyclerOptions<HomeDataModel>options= new FirebaseRecyclerOptions.Builder<HomeDataModel>()
                .setQuery(budgetRef, HomeDataModel.class)
                .build();

        FirebaseRecyclerAdapter<HomeDataModel, MyViewHolder> adapter= new FirebaseRecyclerAdapter<HomeDataModel, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull HomeDataModel model) {

                holder.amount.setText("Amount :" + String.valueOf(model.getAmount()));
                holder.date.setText("On : " + model.getDate());
                holder.itemName.setText("Item : " +model.getItem());
                holder.status.setText(model.getStatus());



                holder.cardRecycler.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key=getRef(position).getKey();
                        item= model.getItem();
                        amount=model.getAmount();
                        status=model.getStatus();
                        noteHolder=model.getNotes();
                        updateData();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent,false);
                return new MyViewHolder(view);
            }
        };
        recyclerViewHome.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onStop() {
        super.onStop();



    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView itemName;
        TextView amount;
        TextView date;
        LinearLayout cardRecycler;
        TextView status;



        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            itemName= itemView.findViewById(R.id.itemName_recycler);
            amount= itemView.findViewById(R.id.amount_recycler);
            date= itemView.findViewById(R.id.date_recycler);
            cardRecycler=itemView.findViewById(R.id.card_recycler);
            status=itemView.findViewById(R.id.status);

        }


    }

    private void updateData(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= LayoutInflater.from(getActivity());
        View mView= inflater.inflate(R.layout.update_delete_layout,null);

        myDialog.setView(mView);

        final AlertDialog dialog= myDialog.create();

        final TextView mItem= mView.findViewById(R.id.itemName_update);
        final EditText mAmount=mView.findViewById(R.id.amount_update);
        final EditText mNote=mView.findViewById(R.id.note_update);
        //notes

        mItem.setText(item);
        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());
        mNote.setText(noteHolder);

        Button delBtn= mView.findViewById(R.id.btnDelete);
        Button updateBtn= mView.findViewById(R.id.btnUpdate);


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Integer.parseInt(mAmount.getText().toString());
                String noteWrittenUpdate=mNote.getText().toString();

                DateFormat dateFormat= new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal= Calendar.getInstance();
                String date=dateFormat.format(cal.getTime());
                DateFormat dt= new SimpleDateFormat("h:mm a");
                String time=dt.format(cal.getTime());

                MutableDateTime epoach=new MutableDateTime();
                epoach.setTime(0);
                DateTime now= new DateTime();
                Weeks weeks=Weeks.weeksBetween(epoach,now);
                Months months= Months.monthsBetween(epoach, now);


                HomeDataModel homeDataModel=new HomeDataModel(item, date,post_key,noteWrittenUpdate, amount,months.getMonths(),status, weeks.getWeeks(), time);

                budgetRef.child(post_key).setValue(homeDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),"Updated Successfully",Toast.LENGTH_SHORT ).show();
                        }else{
                            Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }



                    }
                });
                dialog.dismiss();

            }
        });

         delBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 budgetRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             Toast.makeText(getActivity(),"deleted Successfully",Toast.LENGTH_SHORT ).show();
                         }else{
                             Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                         }



                     }
                 });
                 dialog.dismiss();

             }
         });

        dialog.show();


    }



}