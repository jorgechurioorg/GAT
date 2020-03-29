package com.leofanti.gat.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.leofanti.gat.R;
import com.leofanti.gat.model.MpLotSheet;
import com.leofanti.gat.model.PtProducido;

import java.util.ArrayList;
import java.util.HashMap;

public class PtProdRecyclerView extends RecyclerView.Adapter<PtProdRecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<PtProducido> mpInList;

    public PtProdRecyclerView(Context context, ArrayList<PtProducido> mpInListIn ){
        this.mpInList = mpInListIn;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombrePt;
        public TextView qty;

        public ViewHolder(View itemView){
            super(itemView);

            nombrePt = (TextView)itemView.findViewById(R.id.ptprod_mpnombre);
            qty = (TextView) itemView.findViewById(R.id.ptprod_col4);
        }
    }

    @Override
    public PtProdRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.ptprod_item_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PtProdRecyclerView.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        PtProducido itemRow = mpInList.get(position);
        // Set item views based on your views and data model
        TextView textView = viewHolder.nombrePt;
        textView.setText(itemRow.getPtName());
        TextView qty = viewHolder.qty;
        if( itemRow.getQty() == 0L ) {
            qty.setText("-");
        } else {
            qty.setText(itemRow.getQtyAsString());
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        if( mpInList == null)
            return 0;
        else
            return mpInList.size();
    }

}
