package com.leofanti.gat.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.leofanti.gat.R;
import com.leofanti.gat.model.SeList;

import java.util.ArrayList;
import java.util.HashMap;

import static com.leofanti.gat.R.layout.mfg_op_list_card;


public class SeReceRecyclerViewAdapter extends RecyclerView.Adapter<SeReceRecyclerViewAdapter.ViewHolder> {

    private ArrayList<SeList> seList;
    private Context context;
    private TextView desc;

    public SeReceRecyclerViewAdapter(Context context, HashMap<String, Float> reci) {
        this.seList = seList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //public TextView desc;


        public ViewHolder(View itemView){
            super(itemView);
            desc = (TextView)itemView.findViewById(R.id.mfg_op_list_desc);
        }
    }

    @Override
    public SeReceRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(mfg_op_list_card, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(SeReceRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        SeList itemCard = seList.get(position);
        //Set item views based on your views and data model
        String descripcion = itemCard.getDescripcion();
        desc.setText(descripcion);
    }

    @Override
    public int getItemCount() {
        return this.seList.size();
    }
}