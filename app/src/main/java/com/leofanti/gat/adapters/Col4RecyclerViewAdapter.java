package com.leofanti.gat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.leofanti.gat.R;
import com.leofanti.gat.model.StrFourColReport;

import java.text.NumberFormat;
import java.util.ArrayList;

import static com.leofanti.gat.R.layout.mpinaudit_item_row;

public class Col4RecyclerViewAdapter extends RecyclerView.Adapter<Col4RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<StrFourColReport> mpInList;

    public Col4RecyclerViewAdapter(Context context, ArrayList<StrFourColReport> mpInListIn ){
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
            expaudit_col1 = (TextView) itemView.findViewById(R.id.mpinaudit_col1) ;
            expaudit_col2 = (TextView)  itemView.findViewById(R.id.mpinaudit_col2);
            expaudit_col3 = (TextView)itemView.findViewById(R.id.mpinaudit_col3);
            expaudit_col4 = (TextView) itemView.findViewById(R.id.mpinaudit_col4);
        }
    }

    @Override
    public Col4RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate( mpinaudit_item_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(Col4RecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        StrFourColReport itemRow = mpInList.get(position);
        //Set item views based on your views and data model
        TextView col1Tv = viewHolder.expaudit_col1;
        String columna1 = itemRow.getCol1();
        col1Tv.setText(columna1);
        TextView col2Tv= viewHolder.expaudit_col2;
        String columna2 = itemRow.getCol2();
        col2Tv.setText(columna2);
        TextView col3Tv = viewHolder.expaudit_col3;
        String columna3 = itemRow.getCol3();
        col3Tv.setText(columna3);
        TextView col4Tv = viewHolder.expaudit_col4;
        String col4 = itemRow.getCol4();
        col4Tv.setText(col4 );

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mpInList.size();
    }

}
