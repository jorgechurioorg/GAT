package com.leofanti.gat.adapters;


import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leofanti.gat.R;
import com.leofanti.gat.model.*;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.leofanti.gat.R.drawable.circle_blue;

public class PtOutRecyclerViewAdapter extends RecyclerView.Adapter<PtOutRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ClienteOut> clOutList;

    public PtOutRecyclerViewAdapter(Context context, ArrayList <ClienteOut> clOutList ){
        this.clOutList = clOutList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreCliente;
        public TextView timeStamp;
        public TextView address;
        public ImageView status;
        public CardView cardView;
        public LinearLayout lLay;
        public TextView ruta;
        public TextView field30Tv;
        public TextView field31Tv;

        public ViewHolder(View itemView){
            super(itemView);
            nombreCliente= (TextView) itemView.findViewById(R.id.ptout_cliente);
            timeStamp = (TextView) itemView.findViewById(R.id.ptout_timestamp);
            address = (TextView) itemView.findViewById(R.id.ptout_adrress) ;
            status = (ImageView) itemView.findViewById(R.id.ptout_status);
            cardView = (CardView) itemView.findViewById(R.id.ptout_row);
            lLay = (LinearLayout) itemView.findViewById(R.id.ptout_LL);
            ruta = (TextView) itemView.findViewById(R.id.ptout_ruta);
            field30Tv = (TextView) itemView.findViewById(R.id.ptout_field30);
            field31Tv = (TextView) itemView.findViewById(R.id.ptout_field31);

        }
    }

    @Override
    public PtOutRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.ptout_item_row, parent, false);

        // Return a new holder instance
        PtOutRecyclerViewAdapter.ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PtOutRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ClienteOut itemRow = clOutList.get(position);
        TextView descripcion = viewHolder.nombreCliente;
        String d_desc = itemRow.getName();
        descripcion.setText(d_desc);
        TextView add = viewHolder.address;
        String addSt = itemRow.getAddress();
        add.setText(addSt);
        TextView lotInt = viewHolder.timeStamp;
        lotInt.setText(itemRow.getShipNote());
        String status = itemRow.getStatus().toUpperCase();
        ImageView ptOutIcon = viewHolder.status;
        TextView rutaName = viewHolder.ruta;
        String ruta = itemRow.getRuta();
        String field30 = " ";
        String field31 = " " ;
        TextView field_30 = viewHolder.field30Tv;
        field_30.setText(field30);
        TextView field_31 = viewHolder.field31Tv;
        field_31.setText(field31);
        switch( status) {
            case "NEW":
            case "OPEN":
                viewHolder.status.setColorFilter(ContextCompat.getColor(context, R.color.colorOpen));
                //viewHolder.status.setBackground(ContextCompat.getDrawable(context, circle_blue));
                ptOutIcon.setImageResource(R.drawable.edit);
                rutaName.setText("OPEN");
                break;
            case "CLOSED":
            case "SHIPPED":
                ptOutIcon.setImageResource(R.drawable.canal);
                ptOutIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorNeutral));
                rutaName.setVisibility(View.VISIBLE);
                rutaName.setText(ruta);
                break;
            case Const.FILED:
            case Const.DEL:
                ptOutIcon.setImageResource(R.drawable.filed);
                ptOutIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorDeleted));
                break;
            case "DELIVERED":
                ptOutIcon.setImageResource(R.drawable.home);
                ptOutIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorFine));
                break;
            case "RETURNED":
                ptOutIcon.setImageResource(R.drawable.returned);
                ptOutIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorError));
                break;

        }
    }

    public void updateList(ArrayList<ClienteOut> data) {
        clOutList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return clOutList.size();
    }

}

