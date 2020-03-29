package com.leofanti.gat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.adapters.ReciperRecyclerViewAdapter;
import com.leofanti.gat.adapters.mpInRecyclerViewAdapter;
import com.leofanti.gat.model.MateriaPrima;
import com.leofanti.gat.model.MateriaPrimaIn;
import com.leofanti.gat.model.Rece;
import com.leofanti.gat.model.SeList;
import com.leofanti.gat.utils.DatesHelper;
import com.leofanti.gat.utils.DbGetDataListener;
import com.leofanti.gat.utils.MfgTon;
import com.leofanti.gat.utils.ReciperForm;
import com.leofanti.gat.utils.RecyclerViewGestureListener;
import com.leofanti.gat.utils.RecyclerViewTouchListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

//import android.widget.Toolbar;


public class tabFragmentSetupReciper extends Fragment {
    /** UI de gestion de recetas
     * @author JCH
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */

    public static MfgTon mfgTon = MfgTon.getInstance();
    public static String TAG = "JCH CFGReciper";
    public static String selectedProv, selectedMp;
    private static View baseView;
    private static ArrayList<SeList> seList = new ArrayList<>();
    private static ReciperRecyclerViewAdapter adapter;
    private static RecyclerView recyclerView ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView= inflater.inflate(R.layout.tab_fragment_cfg_rece, container, false);


        FloatingActionButton fabMp = (FloatingActionButton) baseView.findViewById(R.id.cfg_rece_fab);
        fabMp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //launch RECIPER
                ReciperForm dialog = new ReciperForm();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "reciper form new recipe" );

            }
        });
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        // 1. get a reference to recyclerView
        recyclerView = (RecyclerView) baseView.findViewById(R.id.cfg_rece_recycler_view);
        // 2. set layoutManger
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        //mLayoutManager.setReverseLayout(true);
        //mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        //https://stackoverflow.com/questions/33723139/wait-firebase-async-retrieve-data-in-android
        mfgTon.getReceList(getContext(), new DbGetDataListener<ArrayList<Rece>>() {
            @Override
            public void onStart() {
                progressDialog.setTitle("Cargando...");
                progressDialog.show();
            }

            @Override
            public void onSuccess(final ArrayList<Rece> listaObj) {
                // 3. create an adapter
                //adapter = new mpInRecyclerViewAdapter(getContext(), dummyMpList);
                adapter = new ReciperRecyclerViewAdapter(getContext(),listaObj);
                // 4. set adapter
                recyclerView.setAdapter(adapter);
                // 5. set item animator to DefaultAnimator
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                //http://www.androidtutorialshub.com/android-recyclerview-click-listener-tutorial/
                recyclerView.addOnItemTouchListener(new RecyclerViewGestureListener(getContext(), recyclerView, new RecyclerViewTouchListener() {
                    @Override
                    public void onClick(View view, int position) {
                        //TODO edita
                        ReciperForm reciperForm;
                        reciperForm = ReciperForm.newInstance(listaObj.get(position));
                        //TODO otra opcion
                        //https://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
                        //definir metof¿do setObject en el fragment
                        //reciperForm reciperForm = new ReciperForm();
                        //reciperorm.setRecipe( Rece rece)
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        reciperForm.show(ft, TAG );
                    }

                    @Override
                    public void onLongClick(View view, final int position) {
                        //TODO BORRA (archiva)
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("ARCHIVAR");
                        builder.setMessage("Archiva la receta");
                        builder.setPositiveButton("ARCHIVA", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(TAG, " ");
                                        mfgTon.receSetVisible(listaObj.get(position),false);
                                        listaObj.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        Snackbar.make(baseView, "Ingreso archivado", Snackbar.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                }));

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                //DO SOME THING WHEN GET DATA FAILED HERE
            }
        });
        return baseView;
    }

}




