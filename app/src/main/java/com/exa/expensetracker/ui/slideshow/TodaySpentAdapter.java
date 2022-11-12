package com.exa.expensetracker.ui.slideshow;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exa.expensetracker.R;
import com.exa.expensetracker.ui.gallery.WeekSpentAdapter;
import com.exa.expensetracker.ui.home.HomeDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodaySpentAdapter extends RecyclerView.Adapter<TodaySpentAdapter.MyViewHolder>{


    private Context mContext;
    private List<HomeDataModel> myHomeDataModelList;
    private String post_key = "";
    private String item="";
    private int amount=0;
    private String status="";
    private  String notesWritten;


    public TodaySpentAdapter(Context mContext, List<HomeDataModel> myHomeDataModelList) {
        this.mContext = mContext;
        this.myHomeDataModelList = myHomeDataModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout, parent,false);
        return new TodaySpentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final HomeDataModel homeDataModel=myHomeDataModelList.get(position);

        holder.amount.setText("Amount :" + String.valueOf(homeDataModel.getAmount()));
        holder.date.setText("On : " + homeDataModel.getDate());
        holder.itemName.setText("Item : " + homeDataModel.getItem());
        holder.status.setText(homeDataModel.getStatus());

        holder.cardRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key=homeDataModel.getId();
                item= homeDataModel.getItem();
                amount=homeDataModel.getAmount();
                status=homeDataModel.getStatus();
                notesWritten=homeDataModel.getNotes();

                updateData();
            }
        });


    }

    private void updateData(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater= LayoutInflater.from(mContext);
        View mView= inflater.inflate(R.layout.update_delete_layout,null);

        myDialog.setView(mView);

        final AlertDialog dialog= myDialog.create();

        final TextView mItem= mView.findViewById(R.id.itemName_update);
        final EditText mAmount=mView.findViewById(R.id.amount_update);
        final EditText mNotes=mView.findViewById(R.id.note_update);

        mItem.setText(item);
        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());
        mNotes.setText(notesWritten);

        Button delBtn= mView.findViewById(R.id.btnDelete);
        Button updateBtn= mView.findViewById(R.id.btnUpdate);


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Integer.parseInt(mAmount.getText().toString());
                notesWritten=mNotes.getText().toString();

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


                HomeDataModel homeDataModel=new HomeDataModel(item, date,post_key, notesWritten, amount,months.getMonths(),status, weeks.getWeeks(), time);
                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                DatabaseReference budgetRef= FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
                budgetRef.child(post_key).setValue(homeDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext,"Updated Successfully",Toast.LENGTH_SHORT ).show();
                        }else{
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }



                    }
                });
                dialog.dismiss();

            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseAuth mAuth= FirebaseAuth.getInstance();
                DatabaseReference budgetRef= FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());

                budgetRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext,"deleted Successfully",Toast.LENGTH_SHORT ).show();
                        }else{
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }



                    }
                });
                dialog.dismiss();

            }
        });

        dialog.show();


    }


    @Override
    public int getItemCount() {
        return myHomeDataModelList.size();
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


}
