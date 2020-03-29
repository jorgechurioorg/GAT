package com.leofanti.gat.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.leofanti.gat.R;
import com.leofanti.gat.model.MpLotSheet;

import java.util.ArrayList;

public class MpLotRecyclerView extends RecyclerView.Adapter<MpLotRecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<MpLotSheet> mpInList;

    public MpLotRecyclerView(Context context, ArrayList <MpLotSheet> mpInListIn ){
        this.mpInList = mpInListIn;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreMp;
        public ToggleButton lot1;
        public ToggleButton lot2;
        public ToggleButton lot3;

        public ViewHolder(View itemView){
            super(itemView);

            nombreMp = (TextView)itemView.findViewById(R.id.plot_mpnombre);
            lot1 = (ToggleButton) itemView.findViewById(R.id.plot_button1);
            lot2 = (ToggleButton) itemView.findViewById(R.id.plot_button2);
            lot3 = (ToggleButton) itemView.findViewById(R.id.plot_button3);
        }
    }

    @Override
    public MpLotRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.plot_item_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(MpLotRecyclerView.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        MpLotSheet itemRow = mpInList.get(position);
        // Set item views based on your views and data model
        TextView textView = viewHolder.nombreMp;
        textView.setText(itemRow.getMpName());
        ToggleButton lot1 = viewHolder.lot1;
        ToggleButton lot2 = viewHolder.lot2;
        ToggleButton lot3 = viewHolder.lot3;
        String lt1 = itemRow.getLotSlashed(0);
        String lt2 = itemRow.getLotSlashed(1);
        lot1.setTextOn(lt1);
        lot1.setTextOff(lt1);
        lot1.setChecked(true);
        lot1.setVisibility(View.VISIBLE);
        if( lt2 == null ){
            lot2.setVisibility(View.INVISIBLE);
        } else {
            lot2.setTextOn( lt2 );
            lot2.setTextOff(lt2);
            lot2.setChecked(true);
        }
        lot3.setVisibility(View.INVISIBLE);

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        Log.d("JCHMPLOTRV","mplotsheet RV itemcount " + mpInList.size());
        return mpInList.size();
    }

}
