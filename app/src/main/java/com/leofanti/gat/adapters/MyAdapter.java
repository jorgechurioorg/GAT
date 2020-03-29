package com.leofanti.gat.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leofanti.gat.R;
import com.leofanti.gat.utils.GatBLogic;

import java.util.List;

// eca va el obeto de Materia prima IN

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<GatBLogic> gatBLogics;

    // constructor
    public MyAdapter(List<GatBLogic> gatBLogics){
        this.gatBLogics = gatBLogics;
    }

    //@NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // inflate item_layout
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_mpin, null);

        MyViewHolder vh = new MyViewHolder(itemLayoutView);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //holder.itemIcon.setImageResource(gatBLogics.get(position).getIcon());
        //holder.itemTitle.setText(gatBLogics.get(position).getTitle());
        //holder.itemUrl.setText(gatBLogics.get(position).getUrl());
    }

    @Override
    public int getItemCount() {
        if(gatBLogics != null)
            return gatBLogics.size();
        else
            return 0;
    }

    // inner static class
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView itemTitle;
        public TextView itemUrl;
        public ImageView itemIcon;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemTitle = itemLayoutView.findViewById(R.id.item_title);
            //itemUrl = itemLayoutView.findViewById(R.id.item_url);
            itemIcon = itemLayoutView.findViewById(R.id.item_icon);
        }
    }
}
