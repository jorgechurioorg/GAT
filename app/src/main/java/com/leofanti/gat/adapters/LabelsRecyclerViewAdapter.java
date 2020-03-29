package com.leofanti.gat.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.leofanti.gat.R;
import com.leofanti.gat.model.Labels;
import com.leofanti.gat.model.UserPin;

import java.util.ArrayList;

import static com.leofanti.gat.R.layout.card_labelgroup_card;
import static com.leofanti.gat.R.layout.card_userpin_card;


public class LabelsRecyclerViewAdapter extends RecyclerView.Adapter<LabelsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Labels> labelList;
    private Context context;

    public LabelsRecyclerViewAdapter(Context context, ArrayList<Labels> labelList) {
        this.labelList = labelList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView labelGroup;

        public ViewHolder(View itemView){
            super(itemView);
            labelGroup = (TextView)itemView.findViewById(R.id.labelgroup_name);
        }
    }

    @Override
    public LabelsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(card_labelgroup_card, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(LabelsRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Labels itemCard = labelList.get(position);

    }



    @Override
    public int getItemCount() {
        return this.labelList.size();
    }
}