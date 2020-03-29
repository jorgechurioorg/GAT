package com.leofanti.gat.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leofanti.gat.R;
import com.leofanti.gat.model.*;

import java.util.ArrayList;

public class mpInRecyclerViewAdapter extends RecyclerView.Adapter<mpInRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MateriaPrimaIn> mpInList;

    public mpInRecyclerViewAdapter(Context context, ArrayList <MateriaPrimaIn> mpInListIn ){
        this.mpInList = mpInListIn;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgRotulo;
        public TextView nombreMp;
        public TextView nombreProv;
        public TextView loteInterno;

        public ViewHolder(View itemView){
            super(itemView);

            imgRotulo = (ImageView)itemView.findViewById(R.id.rotulo_thumb);
            nombreMp = (TextView)itemView.findViewById(R.id.ruta_cliente);
            nombreProv = (TextView) itemView.findViewById(R.id.ruta_address);
            loteInterno = (TextView) itemView.findViewById(R.id.ruta_map);
        }
    }

    @Override
    public mpInRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.mpin_item_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(mpInRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        MateriaPrimaIn itemRow = mpInList.get(position);
        // Set item views based on your views and data model
        TextView textView = viewHolder.nombreMp;
        textView.setText(itemRow.getNombreMp());
        TextView provView = viewHolder.nombreProv;
        provView.setText(itemRow.getProveedor());
        TextView lotint = viewHolder.loteInterno;
        lotint.setText(itemRow.getLoteInterno());
        ImageView imgRotulo = viewHolder.imgRotulo;
        String url = itemRow.getThumbUrl();
        Log.d("JCH", "ViewHolder geturl() " + url );
        if( url == null) {
            imgRotulo.setImageResource(R.drawable.no_image_3);
            Log.d("JCH", "ViewHolder NULL not loaded");
            return;
        }
        if (url.contentEquals("no_image") || url.contentEquals("no image")) {
            imgRotulo.setImageResource(R.drawable.no_image_3);
            Log.d("JCH", "ViewHolder not loaded");
            return;
        }
        //Glide.with(context).load(url).into(viewHolder.imgRotulo);
        imgRotulo.setImageResource(R.drawable.image_white);


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mpInList.size();
    }

}
