package com.leofanti.gat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.adapters.ExInRecyclerViewAdapter;
import com.leofanti.gat.model.ExpRegistro;
import com.leofanti.gat.utils.DbGetDataListener;
import com.leofanti.gat.utils.ExpenseHelper;
import com.leofanti.gat.utils.ExpenseInForm;
import com.leofanti.gat.utils.RecyclerViewGestureListener;
import com.leofanti.gat.utils.RecyclerViewTouchListener;

import java.util.ArrayList;

//import android.widget.Toolbar;


public class tabFragmentExIn extends Fragment {
    /** UI de gestion de materia prima
     * @author JCH
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */

    private static int IMAGE_CAPTURE_CODE = 10001;
    private static ImageView imagenRotulo;
    public static GatTon gatTon = GatTon.getInstance();
    public static String TAG = "JCHEXIN";
    public static String selectedProv, selectedMp;
    private static Bitmap bp;
    private static View baseView;
    private static final int  VIEWITEM = 0;
    private static final int  INPUTMP = 1 ;
    private static final int  INPUTEXP = 2 ;
    private static final int  VIEWEXP = 3 ;
    private static ArrayList<ExpRegistro> mpInList;
    private static ExInRecyclerViewAdapter adapter;
    private static RecyclerView recyclerView ;
    private static ExpenseHelper expenseHelper = new ExpenseHelper();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView= inflater.inflate(R.layout.tab_fragment_exin, container, false);

        FloatingActionButton fabEx = (FloatingActionButton) baseView.findViewById(R.id.mpin_fab_ex);
        fabEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp = null;
                ExpenseInForm dialog = new ExpenseInForm();
                /*dialog.setFromExpIn(true);
                dialog.setView(baseView);
                dialog.setRecyclerView(recyclerView);
                dialog.setAdapter(adapter);*/
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, TAG );

            }
        });

        //TODO swipe to file record
        //https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/?utm_source=recyclerview&utm_medium=site&utm_campaign=refer_article
        //https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        // 1. get a reference to recyclerView
        recyclerView = (RecyclerView) baseView.findViewById(R.id.mpin_recycler_view);
        // 2. set layoutManger
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        //TODO pasar la lectura de los gastos a Expensehelper
        recyclerView.setLayoutManager(mLayoutManager);
        gatTon.getExInList(getContext(), new DbGetDataListener<ArrayList<ExpRegistro>>() {
            @Override
            public void onStart() {
                progressDialog.setTitle("Cargando...");
                progressDialog.show();
            }

            @Override
            public void onSuccess(final ArrayList<ExpRegistro> listaObj) {
                Log.d("JCH", "#### interface on success in");
                // 3. create an adapter
                //adapter = new mpInRecyclerViewAdapter(getContext(), dummyMpList);
                adapter = new ExInRecyclerViewAdapter(getContext(),listaObj);
                // 4. set adapter
                recyclerView.setAdapter(adapter);
                // 5. set item animator to DefaultAnimator
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                recyclerView.addOnItemTouchListener(new RecyclerViewGestureListener(getContext(), recyclerView, new RecyclerViewTouchListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Log.d(TAG, "Objeto :" + listaObj.get(position).toString());
                        MpRegView mpRegView = MpRegView.newInstance(listaObj.get(position));
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        mpRegView.show(ft, TAG );
                    }

                    @Override
                    public void onLongClick(View view, final int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        builder.setTitle("ARCHIVA");
                        builder.setMessage("El registro de gasto  ");
                        builder.setPositiveButton("ARCHIVA", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(TAG, " ");
                                        gatTon.exInSetVisible(listaObj.get(position), false);
                                        //eliminar de la lista de recycler view
                                        listaObj.remove(position);
                                        dialog.dismiss();
                                        // notifica al adapter
                                        adapter.notifyItemRemoved(position);
                                        Snackbar.make(baseView, "Ingreso archivado", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                        builder.setNegativeButton("CANCELA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Log.d(TAG, "No cambian el estado del remito");
                            }
                        });
                        builder.create().show();
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

    public static class MpRegView extends DialogFragment  {
        //https://guides.codepath.com/android/using-dialogfragment
        //Mej0r este:
        //https://zocada.com/android-full-screen-dialogs-using-dialogfragment/

        public MpRegView() {

        }

        static ExpRegistro expense = new ExpRegistro();

        public static MpRegView newInstance (ExpRegistro expReg) {
		    MpRegView frag = new MpRegView();
		    expense = expReg;
		    Bundle args = new Bundle();
		    args.putString("mpName", expReg.getItemName());
            args.putString("provName", expReg.getProveedor());
            args.putString("imgUrl", expReg.getThumbUrl());
            args.putString("monto", expReg.getMontoAsString());
            frag.setArguments(args);
		    return frag;
	    }

        @Override
        public void onStart() {
            super.onStart();
            Dialog d = getDialog();
            if (d!=null){
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                d.getWindow().setLayout(width, height);
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }
//TODO seguir desde aca
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            View view = inflater.inflate(R.layout.card_mp_view, container, true);
            //Configurar el toolbar
            Toolbar mpInToolbar = view.findViewById(R.id.mpin_toolbar);
            mpInToolbar.setTitle(expense.getItemName());
            mpInToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            TextView prov = (TextView) view.findViewById(R.id.mpin_detalle_proveedor) ;
            TextView lotint = (TextView) view.findViewById(R.id.mpin_detalle_loteinterno) ;
            TextView timest = (TextView) view.findViewById(R.id.mpin_detalle_timestamp) ;
            TextView crej = (TextView) view.findViewById(R.id.mpin_detalle_cantrej);
            TextView cOk = (TextView) view.findViewById(R.id.mpin_detalle_cantok) ;
            TextView regKey = (TextView) view.findViewById(R.id.mpin_regKey);
            ImageView imgRotulo = (ImageView) view.findViewById(R.id.mpin_detalle_imagen) ;

            regKey.setText(expense.getThisKey());
            prov.setText("Proveedor :" + expense.getProveedor());
            lotint.setText("Rubro :" + expense.getcCosto());
            cOk.setText("Fecha :" + expense.getTimestamp());
            String factu = expense.getFactura()? "Factura" : "Remito";
            crej.setText( factu );
            timest.setText("Monto :  $" + Float.toString(expense.getMonto()));
            String url = expense.getThumbUrl();
            imgRotulo.requestLayout();
            if( url == null || url.contains("no")) {
                //imgRotulo.setImageResource(R.drawable.no_image_3);
                //imgRotulo.getLayoutParams().width =400;
                return view;
            }
            //TODO mostrar el progress dialog dentro del etxt view
            //https://stackoverflow.com/questions/25740492/show-progressbar-while-loadig-images-in-each-image-view-in-picasso
            //TODO make image zoomable on pinch
            //https://medium.com/quick-code/pinch-to-zoom-with-multi-touch-gestures-in-android-d6392e4bf52d
            Glide.with(getContext()).load(url).into(imgRotulo);
            return view;
        }


        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }
    }

    //TODO pasar esto a una clase separada

    public static boolean numIsZero( float num ) {
        return ( num == 0 );
    }

    public static void setEditTextError( EditText field, String msg) {
        field.setError(msg);
        field.requestFocus();
    }

    public static boolean strIsNull( String str) {
        return (str==null) ;
    }

    public static void setActvError( AutoCompleteTextView actv, String msg) {
        actv.setError(msg);
        actv.requestFocus();
    }
}




