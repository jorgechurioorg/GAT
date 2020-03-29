package com.leofanti.gat.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.leofanti.gat.R;
import com.leofanti.gat.model.Rece;
import com.leofanti.gat.model.SeList;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import static com.leofanti.gat.R.layout.mfg_op_list_card;
import static com.leofanti.gat.R.layout.recein_item_row;


public class ReciperRecyclerViewAdapter extends RecyclerView.Adapter<ReciperRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Rece> receList;
    private Context context;
    private TextView desc, type, aler, label, tstamp;
    public ReciperRecyclerViewAdapter(Context context, ArrayList<Rece> reci) {
        this.receList = reci;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //public TextView desc;


        public ViewHolder(View itemView){
            super(itemView);
            desc = (TextView)itemView.findViewById(R.id.recein_descripcion);
            type = (TextView)itemView.findViewById(R.id.recein_type);
            aler = (TextView)itemView.findViewById(R.id.recein_alergen);
            label = (TextView)itemView.findViewById(R.id.recein_label);
            tstamp = (TextView) itemView.findViewById(R.id.recein_timestamp);

        }
    }

    @Override
    public ReciperRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(recein_item_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ReciperRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Rece itemCard = receList.get(position);
        String descripcion = itemCard.getDescripcion();
        desc.setText(descripcion);
        String type = itemCard.getReceType();
        String alerg = itemCard.getAlergenType();
        String tstamp = itemCard.getTimestamp();
        String label = itemCard.getLabel();
    }

    @Override
    public int getItemCount() {
        return this.receList.size();
    }
}