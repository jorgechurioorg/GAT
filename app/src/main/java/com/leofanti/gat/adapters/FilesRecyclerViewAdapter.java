package com.leofanti.gat.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.leofanti.gat.R;
import com.leofanti.gat.model.MateriaPrimaIn;
import com.leofanti.gat.utils.DatesHelper;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FilesRecyclerViewAdapter extends RecyclerView.Adapter<FilesRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<File> filesList;

    public FilesRecyclerViewAdapter(ArrayList<File> files){
        this.filesList = files;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fileName;
        public TextView fileSize;
        public TextView fileDate;
        public ImageView fileExist;

        public ViewHolder(View itemView){
            super(itemView);

            fileName = (TextView)itemView.findViewById(R.id.filelist_col1);
            fileSize = (TextView)itemView.findViewById(R.id.filelist_col2);
            fileDate = (TextView) itemView.findViewById(R.id.filelist_col3);
            fileExist = (ImageView) itemView.findViewById(R.id.filelist_icon);
        }
    }

    @Override
    public FilesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.filelist_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(FilesRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        File itemRow = filesList.get(position);
        TextView textView = viewHolder.fileName;
        textView.setText(itemRow.getName());
        TextView provView = viewHolder.fileSize;
        provView.setText(String.valueOf((long)itemRow.length()));
        //TODO Pasarlo al dateHelper
        DatesHelper datesHelper = new DatesHelper();
        TextView lotint = viewHolder.fileDate;
        lotint.setText(datesHelper.epochToHuman(itemRow.lastModified()));
        ImageView imgRotulo = viewHolder.fileExist;
        imgRotulo.setImageResource(R.drawable.circle_white);

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return filesList.size();
    }

}
