package com.exa.expensetracker.ui.history;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exa.expensetracker.R;
import com.exa.expensetracker.ui.home.HomeDataModel;
import com.exa.expensetracker.ui.slideshow.TodaySpentAdapter;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {


    private Context mContext;
    private List<HomeDataModel> myHomeDataModelList;
    private String post_key = "";
    private String item="";
    private int amount=0;
    private String status="";


    public HistoryAdapter(Context mContext, List<HomeDataModel> myHomeDataModelList) {

        this.mContext = mContext;
        this.myHomeDataModelList = myHomeDataModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout, parent,false);
        return new HistoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final HomeDataModel homeDataModel=myHomeDataModelList.get(position);

        holder.amount.setText("Amount :" + String.valueOf(homeDataModel.getAmount()));
        holder.date.setText("On : " + homeDataModel.getDate());
        holder.itemName.setText("Item : " + homeDataModel.getItem());
        holder.status.setText(homeDataModel.getStatus());

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
        TextView editCard;



        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            itemName= itemView.findViewById(R.id.itemName_recycler);
            amount= itemView.findViewById(R.id.amount_recycler);
            date= itemView.findViewById(R.id.date_recycler);
            cardRecycler=itemView.findViewById(R.id.card_recycler);
            status=itemView.findViewById(R.id.status);
            editCard=itemView.findViewById(R.id.edit_card);
            editCard.setVisibility(View.GONE);

        }


    }

}
