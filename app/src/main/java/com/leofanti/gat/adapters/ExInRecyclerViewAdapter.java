package com.leofanti.gat.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leofanti.gat.R;
import com.leofanti.gat.model.ExpRegistro;

import java.util.ArrayList;

import static com.leofanti.gat.R.layout.exin_item_row;

public class ExInRecyclerViewAdapter extends RecyclerView.Adapter<ExInRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ExpRegistro> mpInList;

    public ExInRecyclerViewAdapter(Context context, ArrayList <ExpRegistro> mpInListIn ){
        this.mpInList = mpInListIn;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cCosto;
        public TextView timestamp;
        public TextView nombreItem;
        public TextView nombreProv;
        public TextView monto;

        public ViewHolder(View itemView){
            super(itemView);
            cCosto = (TextView) itemView.findViewById(R.id.exinrow_ccosto) ;
            timestamp = (TextView)  itemView.findViewById(R.id.exinrow_timestamp);
            nombreItem = (TextView)itemView.findViewById(R.id.exinrow_item);
            nombreProv = (TextView) itemView.findViewById(R.id.exinrow_proveedor);
            monto = (TextView) itemView.findViewById(R.id.exinrow_monto);

        }
    }

    @Override
    public ExInRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(exin_item_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ExInRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ExpRegistro itemRow = mpInList.get(position);
        //Set item views based on your views and data model
        TextView textView = viewHolder.nombreItem;
        textView.setText(itemRow.getItemName());
        TextView provView = viewHolder.nombreProv;
        provView.setText(itemRow.getProveedor());
        TextView lotint = viewHolder.monto;
        lotint.setText(itemRow.getMontoAsString());
        TextView tsView = viewHolder.timestamp;
        tsView.setText(itemRow.getTimestampSlashed());
        TextView ccost = viewHolder.cCosto;
        ccost.setText(itemRow.getcCosto());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        Log.d("JCHEXP","expRv RV itemcount " + mpInList.size());
        return mpInList.size();
    }

}
