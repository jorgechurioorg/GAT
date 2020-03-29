package com.leofanti.gat.adapters;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.leofanti.gat.R;
import com.leofanti.gat.model.ProductoTerminadoOut;

import java.util.ArrayList;

public class PtOutScanItemAdapter extends RecyclerView.Adapter<PtOutScanItemAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ProductoTerminadoOut> ptOutList;

    public PtOutScanItemAdapter(Context context, ArrayList <ProductoTerminadoOut> ptOutList ){
        this.ptOutList = ptOutList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView descripcion;
        public TextView cantidad;
        public TextView lote;

        public ViewHolder(View itemView){
            super(itemView);
            descripcion= (TextView) itemView.findViewById(R.id.ptoutscan_entry_descripcion);
            cantidad = (TextView) itemView.findViewById(R.id.ptoutscan_entry_cantidad);
            lote = (TextView) itemView.findViewById(R.id.ptoutscan_entry_loteint);
        }
    }

    @Override
    public PtOutScanItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.ptoutscan_item_row, parent, false);

        // Return a new holder instance
        PtOutScanItemAdapter.ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PtOutScanItemAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ProductoTerminadoOut itemRow = ptOutList.get(position);
        //Log.d("JCHPTOUT_RV", "Bind View Holder position:" + Integer.toString(position) + itemRow.getDescripcion() + Integer.toString(itemRow.getCantidad())) ;
        // Set item views based on your views and data model
        TextView descripcion = viewHolder.descripcion;
        String d_desc = itemRow.getDescripcion();
        descripcion.setText(d_desc);
        TextView lotInt =viewHolder.lote;
        lotInt.setText(itemRow.getLote());
        TextView cantView = viewHolder.cantidad;
        cantView.setText(Integer.toString(itemRow.getCantidad()));
    }

    @Override
    public int getItemCount() {
        return ptOutList.size();
    }

}

