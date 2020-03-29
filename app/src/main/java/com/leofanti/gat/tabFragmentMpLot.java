package com.leofanti.gat;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.adapters.MpLotRecyclerView;
import com.leofanti.gat.model.MpLotSheet;
import com.leofanti.gat.utils.DbGetDataListener;

import java.util.ArrayList;

public class tabFragmentMpLot extends Fragment {
    private static View baseView;
    public static GatTon gatTon = GatTon.getInstance();
    public static String TAG = "JCHMPLOT";
    private static RecyclerView recyclerView ;
    private static MpLotRecyclerView adapter;
    ArrayList<MpLotSheet> mpLotSheetAll = new ArrayList<>();
    ArrayList<MpLotSheet> mpLotSheetMin = new ArrayList<>();
    private boolean totalViewMode = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView= inflater.inflate(R.layout.tab_fragment_mplot, container, false);

        final TextView todayLotSheet = (TextView) baseView.findViewById(R.id.mplot_today) ;
        final Button viewMode = (Button) baseView.findViewById(R.id.mpLotDisplayMode);

        recyclerView = (RecyclerView) baseView.findViewById(R.id.mplot_rv);
        //LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        gatTon.getMpLot(getContext(), new DbGetDataListener<ArrayList<MpLotSheet>>() {
            @Override
            public void onStart() {
                //progressDialog.setTitle("Cargando...");
                //progressDialog.show();
            }

            @Override
            public void onSuccess(final ArrayList<MpLotSheet> listaObj) {
                todayLotSheet.setText("Dia de elaboracion :" + gatTon.getLotSheetToday());
                mpLotSheetAll.clear();
                mpLotSheetMin.clear();
                for( MpLotSheet mpLotItem: listaObj) {
                    mpLotSheetAll.add(mpLotItem);
                    if( mpLotItem.lotesL.size()>1){
                        mpLotSheetMin.add(mpLotItem);
                    }
                }

                // 3. create an adapter con el listado resumido
                adapter = new MpLotRecyclerView(getContext(), mpLotSheetMin);
                // 4. set adapter
                recyclerView.setAdapter(adapter);

                viewMode.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if( totalViewMode){
                            //cambia al modo x lote
                            final MpLotRecyclerView ptoia = new MpLotRecyclerView( getContext(), mpLotSheetAll);
                            recyclerView.swapAdapter(ptoia, true);
                            viewMode.setText("LISTA RESUMIDA");
                            totalViewMode = false;
                            ptoia.notifyDataSetChanged();
                        } else {
                            //cambia al modo total
                            final MpLotRecyclerView ptoia = new MpLotRecyclerView(getContext(), mpLotSheetMin);
                            recyclerView.swapAdapter(ptoia, true);
                            viewMode.setText("LISTA COMPLETA");
                            totalViewMode = true;
                            ptoia.notifyDataSetChanged();
                        }
                        Log.d(TAG, "Modo de display total es " + String.valueOf(totalViewMode));

                    }
                });

            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                //DO SOME THING WHEN GET DATA FAILED HERE
            }
        });
        return baseView;

    }



}

