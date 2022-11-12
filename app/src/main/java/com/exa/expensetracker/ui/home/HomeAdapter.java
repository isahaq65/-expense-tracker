package com.exa.expensetracker.ui.home;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exa.expensetracker.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class HomeAdapter extends FirebaseRecyclerAdapter<HomeDataModel, HomeAdapter.MyViewHolder> {

    private String post_key = "";
    private String item="";
    private int amount=0;

    public HomeAdapter(@NonNull FirebaseRecyclerOptions<HomeDataModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull HomeDataModel model) {

        holder.itemName.setText(model.getItem());
        holder.amount.setText(String.valueOf(model.getAmount()));
        holder.date.setText(model.getDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key = getRef(position).getKey();
                item= model.getItem();
                amount=model.getAmount();
                //Toast.makeText(this,item,Toast.LENGTH_SHORT ).show();
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

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView itemName;
        TextView amount;
        TextView date;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            itemName= itemView.findViewById(R.id.itemName_recycler);
            amount= itemView.findViewById(R.id.amount_recycler);
            date= itemView.findViewById(R.id.date_recycler);
        }


    }

    private void updateData(){

    }

}
