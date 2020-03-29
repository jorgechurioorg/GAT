package com.leofanti.gat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.adapters.MpLotRecyclerView;
import com.leofanti.gat.adapters.PtProdRecyclerView;
import com.leofanti.gat.model.MpLotSheet;
import com.leofanti.gat.model.PtProducido;
import com.leofanti.gat.utils.DbGetDataListener;
import com.leofanti.gat.utils.PtProdHelper;
import com.leofanti.gat.utils.RecyclerViewGestureListener;
import com.leofanti.gat.utils.RecyclerViewTouchListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class tabFragmentPtProd extends Fragment {
    private static View baseView;
    public static GatTon gatTon = GatTon.getInstance();
    public static String TAG = "JCHPTPROD";
    private static RecyclerView recyclerView ;
    private static PtProdRecyclerView adapter;
    private String hoyEsHoy;
    private PtProdHelper ptProdHelper = new PtProdHelper();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("JCHFRAG","On create");
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("JCHFRAG","On CreateView");
        baseView= inflater.inflate(R.layout.tab_fragment_ptprod, container, false);
        final TextView todayLotSheet = (TextView) baseView.findViewById(R.id.ptprod_today) ;
        final EditText setLotSheet = (EditText) baseView.findViewById(R.id.ptprod_sethojadate);
        DateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd");
        Date today = new Date();
        hoyEsHoy = timestampFormat.format(today);
        ptProdHelper.setHoyEsHoy( hoyEsHoy);
        //TODO sacar despues de poner al dia la base
        todayLotSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todayLotSheet.setVisibility(View.GONE);
                setLotSheet.setVisibility(View.VISIBLE);
                setLotSheet.requestFocus();
                setLotSheet.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager keyboard = (InputMethodManager) baseView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.showSoftInput(setLotSheet, 0);
                    }
                }, 0);
                setLotSheet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        hoyEsHoy = setLotSheet.getText().toString();
                        ptProdHelper.setHoyEsHoy( hoyEsHoy);
                        todayLotSheet.setText(hoyEsHoy);
                        todayLotSheet.setVisibility(View.VISIBLE);
                        setLotSheet.setVisibility(View.GONE);
                        InputMethodManager imm = (InputMethodManager) baseView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(baseView.getWindowToken(), 0);
                        return false;
                    }

                });
            }
        });
        todayLotSheet.setText(ptProdHelper.getHoyEsHoyHumanReadable());
        recyclerView = (RecyclerView) baseView.findViewById(R.id.ptprod_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);
            gatTon.getHojaProd( this.hoyEsHoy, new DbGetDataListener<ArrayList<PtProducido>> ()  {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess( final ArrayList<PtProducido> hojaProd){
                    adapter = new PtProdRecyclerView(getContext(), hojaProd);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setMotionEventSplittingEnabled(false);
                    recyclerView.addOnItemTouchListener(new RecyclerViewGestureListener(getContext(), recyclerView, new RecyclerViewTouchListener() {
                        @Override
                        public void onClick(View view, final int pos) {
                            final Long cantidad = hojaProd.get(pos).getQty();
                            final String key = hojaProd.get(pos).getPtName();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle(key + "    AGREGAR");
                            //https://stackoverflow.com/questions/10903754/input-text-dialog-android
                            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.ptproddialog_qty, (ViewGroup) getView(), false);
                            final TextView pfx = (TextView) viewInflated.findViewById(R.id.ptproddialog_prefix);
                            final EditText qty = (EditText) viewInflated.findViewById(R.id.ptproddialog_qty);
                            pfx.setText(cantidad.toString() + " + ");
                            qty.setText("0");
                            qty.requestFocus();
                            qty.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    InputMethodManager keyboard = (InputMethodManager) baseView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    keyboard.showSoftInput(qty, 0);
                                }
                            }, 0);
                            builder.setView(viewInflated);
                            builder.setPositiveButton("GUARDAR", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Long cantIn = Long.valueOf(qty.getText().toString());
                                    hojaProd.get(pos).setQty(cantidad + cantIn);
                                    gatTon.saveItemProd( pos, hoyEsHoy, hojaProd.get(pos));
                                    dialog.dismiss();
                                    // notifica al adapter
                                    adapter.notifyDataSetChanged();
                                    //TODO Impresion de rotulos
                                    //Snackbar.make(baseView, "Cantidad guardada", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                            final AlertDialog alert = builder.create();
                            alert.show();
                        }

                        @Override
                        public void onLongClick( View view, final int pos) {
                            if( hojaProd.get(pos).getQty()!=0) {
                                final String key = hojaProd.get(pos).getPtName();
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle(key + "  BORRAR");
                                builder.setNeutralButton("BORRAR", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        hojaProd.get(pos).setQty(0L);
                                        gatTon.saveItemProd(pos, hoyEsHoy, hojaProd.get(pos));
                                        adapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                });
                                final AlertDialog alert = builder.create();
                                alert.show();
                            }

                        }
                    }));
                }
                @Override
                public void onFailed(DatabaseError databaseError) {
                    //DO SOME THING WHEN GET DATA FAILED HERE
                }
            });
        return baseView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("JCHFRAG","On Attach");

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d("JCHFRAG","On destroy view");

    }

}

