package com.leofanti.gat.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leofanti.gat.R;
import com.leofanti.gat.model.ExpReport;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.leofanti.gat.R.layout.expaudit_item_row;

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
        String col4 = NumberFormat.getInstance().format(itemRow.getCol4());
        //Float col4 = itemRow.getCol4();
        //DecimalFormat form = new DecimalFormat("#,00");
        col4Tv.setText(col4 );
        if( columna3.equalsIgnoreCase("__")) {
            col3Tv.setText("");
            if (columna2.equalsIgnoreCase("__")) {
                col2Tv.setText("");
                col1Tv.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
                col4Tv.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
            } else {
                col1Tv.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
                col2Tv.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
                col4Tv.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
            }

        } else {
            col1Tv.setTextAppearance(android.R.style.TextAppearance_Material_Inverse);
            col2Tv.setTextAppearance(android.R.style.TextAppearance_Material_Inverse);
            col3Tv.setTextAppearance(android.R.style.TextAppearance_Material_Inverse);
            col4Tv.setTextAppearance(android.R.style.TextAppearance_Material_Inverse);

        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mpInList.size();
    }

}
