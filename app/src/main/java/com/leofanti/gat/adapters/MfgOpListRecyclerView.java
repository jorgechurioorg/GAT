package com.leofanti.gat.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.leofanti.gat.R;
import static com.leofanti.gat.R.layout.mfg_op_list_card;
import com.leofanti.gat.model.MfgItem;
import com.leofanti.gat.model.Rece;
import com.leofanti.gat.model.SeList;
import com.leofanti.gat.model.UserPin;

import java.util.ArrayList;

import static com.leofanti.gat.R.layout.card_userpin_card;


public class MfgOpListRecyclerView extends RecyclerView.Adapter<MfgOpListRecyclerView.ViewHolder> {

    private ArrayList<Rece> seList;
    private Context context;
    private TextView desc;

    public MfgOpListRecyclerView(Context context, ArrayList<Rece> seList) {
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
    public MfgOpListRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(MfgOpListRecyclerView.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Rece itemCard = seList.get(position);
        //Set item views based on your views and data model
        String descripcion = itemCard.getDescripcion();
        desc.setText(descripcion);
    }

    @Override
    public int getItemCount() {
        return this.seList.size();
    }
}