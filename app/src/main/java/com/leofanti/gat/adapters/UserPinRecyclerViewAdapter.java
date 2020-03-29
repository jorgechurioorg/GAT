package com.leofanti.gat.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.leofanti.gat.R;
import com.leofanti.gat.model.UserPin;
import static com.leofanti.gat.R.layout.card_userpin_card;

import java.util.ArrayList;
import java.util.HashMap;

/*
public class ExpAuditRecyclerViewAdapter extends RecyclerView.Adapter<ExpAuditRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ExpReport> mpInList;

    public ExpAuditRecyclerViewAdapter(Context context, ArrayList<ExpReport> mpInListIn ){
        this.mpInList = mpInListIn;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView expaudit_col1;
        public TextView expaudit_col2;
        public TextView expaudit_col3;
        public TextView expaudit_col4;

        public ViewHolder(View itemView){
            super(itemView);
            expaudit_col1 = (TextView) itemView.findViewById(R.id.expaudit_col1) ;
            expaudit_col2 = (TextView)  itemView.findViewById(R.id.expaudit_col2);
            expaudit_col3 = (TextView)itemView.findViewById(R.id.expaudit_col3);
            expaudit_col4 = (TextView) itemView.findViewById(R.id.expaudit_col4);
        }
    }

    @Override
    public ExpAuditRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(expaudit_item_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ExpAuditRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ExpReport itemRow = mpInList.get(position);
        //Set item views based on your views and data model
        TextView textView = viewHolder.expaudit_col1;
        textView.setText(itemRow.getCol1());
        TextView provView = viewHolder.expaudit_col2;
        provView.setText(itemRow.getCol2());
        TextView lotint = viewHolder.expaudit_col3;
        lotint.setText(itemRow.getCol3());
        TextView ccost = viewHolder.expaudit_col4;
        ccost.setText(Float.toString(itemRow.getCol4()));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mpInList.size();
    }

}

 */

//ExpAuditRecyclerViewAdapter.ViewHolder

public class UserPinRecyclerViewAdapter extends RecyclerView.Adapter<UserPinRecyclerViewAdapter.ViewHolder> {

    private ArrayList< UserPin> usersList;
    private Context context;

    public UserPinRecyclerViewAdapter(Context context, ArrayList< UserPin> usersList) {
        this.usersList = usersList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userAvatar;
        public TextView userNickName;

        public ViewHolder(View itemView){
            super(itemView);
            userAvatar = (TextView)itemView.findViewById(R.id.userpincard_initial);
            userNickName = (TextView) itemView.findViewById(R.id.userpincard_nick);
        }
    }

    @Override
    public UserPinRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(card_userpin_card, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(UserPinRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        UserPin itemCard = usersList.get(position);
        //Set item views based on your views and data model
        String nick = itemCard.getNick();
        TextView capitalLetter = viewHolder.userAvatar;
        capitalLetter.setText(nick.substring(0,1).toUpperCase());
        TextView nickName = viewHolder.userNickName;
        nickName.setText(nick);
    }



    @Override
    public int getItemCount() {
        return this.usersList.size();
    }
}