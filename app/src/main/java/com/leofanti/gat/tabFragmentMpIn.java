package com.leofanti.gat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.adapters.mpInRecyclerViewAdapter;
import com.leofanti.gat.utils.*;
import com.leofanti.gat.model.*;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class tabFragmentMpIn extends Fragment {
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
    public static String TAG = "JCHMPIN";
    public static String selectedProv, selectedMp;
    private static Bitmap bp;
    private static View baseView;
    private static final int  VIEWITEM = 0;
    private static final int  INPUTMP = 1 ;
    private static final int  INPUTEXP = 2 ;
    private static final int  VIEWEXP = 3 ;
    private static int mpInMode;
    private static ArrayList<MateriaPrimaIn> dummyMpList, mpInList;
    private static mpInRecyclerViewAdapter adapter;
    private static RecyclerView recyclerView ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView= inflater.inflate(R.layout.tab_fragment_mpin, container, false);


        FloatingActionButton fabMp = (FloatingActionButton) baseView.findViewById(R.id.mpin_fab_mp);
        fabMp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpInMode = INPUTMP;
                bp = null;
                MpinDialog dialog = new MpinDialog();
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
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        https://stackoverflow.com/questions/33723139/wait-firebase-async-retrieve-data-in-android
        gatTon.getMpInList(getContext(), new DbGetDataListener<ArrayList<MateriaPrimaIn>>() {
            @Override
            public void onStart() {
                progressDialog.setTitle("Cargando...");
                progressDialog.show();
            }

            @Override
            public void onSuccess(final ArrayList<MateriaPrimaIn> listaObj) {
                Log.d("JCH", "#### interface on success in");
                // 3. create an adapter
                //adapter = new mpInRecyclerViewAdapter(getContext(), dummyMpList);
                adapter = new mpInRecyclerViewAdapter(getContext(),listaObj);
                // 4. set adapter
                recyclerView.setAdapter(adapter);
                // 5. set item animator to DefaultAnimator
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                //http://www.androidtutorialshub.com/android-recyclerview-click-listener-tutorial/
                recyclerView.addOnItemTouchListener(new RecyclerViewGestureListener(getContext(), recyclerView, new RecyclerViewTouchListener() {
                    @Override
                    public void onClick(View view, int position) {
                        //Log.d(TAG, "Objeto :" + listaObj.get(position).toString());
                        MpRegView mpRegView;
                        mpRegView = MpRegView.newInstance(listaObj.get(position));
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        mpRegView.show(ft, TAG );
                    }

                    @Override
                    public void onLongClick(View view, final int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("ARCHIVA");
                        builder.setMessage("Esta accion archiva este lote de MP");
                        builder.setPositiveButton("ARCHIVA", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Log.d(TAG, " ");
                                        gatTon.mpinSaveVisible(listaObj.get(position));
                                        listaObj.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        //nackbar.make(baseView, "Ingreso archivado", Snackbar.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                });
                        /*builder.setNeutralButton("CANCELA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Log.d(TAG, "No cambian el estado del remito");
                            }
                        });*/
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

    public static class MpRegView extends DialogFragment  {
        //https://guides.codepath.com/android/using-dialogfragment
        //Mej0r este:
        //https://zocada.com/android-full-screen-dialogs-using-dialogfragment/
        public MpRegView() {

        }

        public static MpRegView newInstance (MateriaPrimaIn mpInReg) {
		    MpRegView frag = new MpRegView();
		    Bundle args = new Bundle();
		    args.putString("mpName", mpInReg.getNombreMp());
            args.putString("provName", mpInReg.getProveedor());
            args.putString("loteInt", mpInReg.getLoteInterno());
            args.putString("cantOk", mpInReg.getCantOk());
            args.putString("cantRej", mpInReg.getCantRech());
            args.putString("timestamp", mpInReg.getTimestamp());
            args.putString("loteFabricante", mpInReg.loteFabricante);
            args.putString("imgUrl", mpInReg.getThumbUrl());
            args.putString("unidad", mpInReg.unidadMp);
            frag.setArguments(args);
            mpInMode = VIEWITEM;
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            View view = inflater.inflate(R.layout.card_mp_view, container, true);
            //Configurar el toolbar
            Toolbar mpInToolbar = view.findViewById(R.id.mpin_toolbar);
            String title = getArguments().getString("mpName", "default value");
            mpInToolbar.setTitle(title);
            mpInToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            //mpInToolbar.inflateMenu(R.menu.menu_mpitem);
            //TODO mpin view eliminar el action menu (GUARDAR)

            TextView prov = (TextView) view.findViewById(R.id.mpin_detalle_proveedor) ;
            TextView lotint = (TextView) view.findViewById(R.id.mpin_detalle_loteinterno) ;
            TextView timest = (TextView) view.findViewById(R.id.mpin_detalle_timestamp) ;
            TextView loteprov = (TextView) view.findViewById(R.id.mpin_detalle_loteproveedor) ;
            TextView cOk = (TextView) view.findViewById(R.id.mpin_detalle_cantok) ;
            ImageView imgRotulo = (ImageView) view.findViewById(R.id.mpin_detalle_imagen) ;

            loteprov.setText("Lote Fabrticante: " + getArguments().getString("loteFabricante", "VACIO"));
            prov.setText("proveedor: " + getArguments().getString("provName", "_proveedor"));
            lotint.setText("Lote interno: " + getArguments().getString("loteInt", "_01/01"));
            timest.setText("Timestamp: " + getArguments().getString("timestamp", "_00/00/00"));
            cOk.setText("Cantidad recibida: " + getArguments().getString("cantOk", "_00/00/00"));
            String url = getArguments().getString("imgUrl", "no_image");
            //Log.d("JCH", "MP IN view geturl() " + url );
            imgRotulo.requestLayout();
            if( url == null || url.contains("no")) {
                return view;
            }
            //TODO mostrar el progress dialog dentro del etxt view
            //https://stackoverflow.com/questions/25740492/show-progressbar-while-loadig-images-in-each-image-view-in-picasso
            //TODO make image zoomable on pinch
            //https://medium.com/quick-code/pinch-to-zoom-with-multi-touch-gestures-in-android-d6392e4bf52d
            imgRotulo.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(url).into(imgRotulo);
            return view;
        }


        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }
    }




    public static class MpinDialog extends DialogFragment {
        /** UI de alta de un item de materia prima
         * Se lanza desde el FAB
         * @param savedInstanceState
         */


        ArrayAdapter<String> mpTextList,provTextList;
        private final MateriaPrimaIn materiaPrimaIn = new MateriaPrimaIn();
        AutoCompleteTextView ACTV, mpSelect;
        EditText cantNoE, cantOkE, montoIn, loteFabE,fechaElabE,fechaVencE;
        float ok, rej, monto;
        String unidad;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            final View view = inflater.inflate(R.layout.card_mp_input, container, false);
            //set to adjust screen height automatically, when soft keyboard appears on screen
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            Toolbar mpInToolbar = (Toolbar) view.findViewById(R.id.mpin_toolbar);
            mpInToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("ATENCION");
                    builder.setMessage("Cancela el ingreso?");
                    builder.setPositiveButton("CANCELAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "ingreso eliminado ");
                            dialog.dismiss();
                            dismiss();
                        }
                    });
                    builder.create().show();
                }
            });
            mpInToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if(id == R.id.mpin_save){
                        String okS = cantOkE.getText().toString();
                       ok = (okS.isEmpty())? 0.0f : Float.parseFloat(okS);
                       materiaPrimaIn.cantIngresada = ok;
                       String rejS = cantNoE.getText().toString();
                       rej = (rejS.isEmpty())? 0.0f : Float.parseFloat(rejS);
                       materiaPrimaIn.cantRechazada = rej;
                       String lFE= loteFabE.getText().toString();
                       String fEL = fechaElabE.getText().toString();
                       String fVE = fechaVencE.getText().toString();

                       materiaPrimaIn.loteFabricante = lFE;
                       materiaPrimaIn.fechaElab = fEL;
                       materiaPrimaIn.fechaVenc = fVE;

                       boolean retErr = false;

                        if( strIsNull(materiaPrimaIn.loteFabricante)){
                            setEditTextError(loteFabE, "Completar!!!");
                            retErr = true;
                        }

                        if( numIsZero(ok) && numIsZero(rej) && (mpInMode==INPUTMP)) {
                            setEditTextError(cantNoE, "valor?");
                            setEditTextError(cantOkE, "Valor?");
                            retErr = true;
                        }
                        if( strIsNull(materiaPrimaIn.getProveedor())){
                            setActvError( ACTV, "Elegir un proveedor de la lista" );
                            retErr = true;
                        }
                        if( strIsNull(materiaPrimaIn.getNombreMp())){
                            String error1 = "Elegir materia prima de la lista";
                            setActvError(mpSelect, error1);
                            retErr = true;
                        }
                        if( retErr ) return true;
                        Log.d("JCH", "saving " + materiaPrimaIn.getNombreMp());
                        materiaPrimaIn.setTimestamp();
                        materiaPrimaIn.setOperador(gatTon.getUserLogged());
                        gatTon.saveMp(materiaPrimaIn, bp, getContext());
                        int position = recyclerView.getAdapter().getItemCount()-1;
                        recyclerView.smoothScrollToPosition(position);
                        adapter.notifyDataSetChanged();
                        dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("GASTO");
                        builder.setMessage("Si el item tiene un pago asociado lo carga ahora?");
                        builder.setPositiveButton("CARGAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dismiss();
                                new MainActivity().callExpenseInForm(materiaPrimaIn);

                            }
                        });
                        builder.create().show();
                    }
                    return true;
                }
            });

            mpInToolbar.inflateMenu(R.menu.menu_mpitem);
            String title = "INGRESO MP";
            mpInToolbar.setTitle(title);
            return view;
        }

        @Override
        public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            //materiaPrimaIn.clear();

            mpTextList = new ArrayAdapter<String>(getActivity(),R.layout.automplete_layout,R.id.auto_complete_item,  gatTon.getMpList());
            provTextList = new ArrayAdapter<String>(getActivity(),R.layout.automplete_layout, R.id.auto_complete_item, gatTon.getProvList());

            imagenRotulo = (ImageView) view.findViewById(R.id.imagen_rotulo);
            imagenRotulo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cInt,IMAGE_CAPTURE_CODE);

                }
            });

            final TextView unidadMedida = (TextView) view.findViewById(R.id.mpin_unidad);
            loteFabE = (EditText)view.findViewById(R.id.mpin_lotefab2);
            fechaElabE = (EditText)view.findViewById(R.id.mpin_elab2);
            fechaVencE = (EditText)view.findViewById(R.id.mpin_vence2);
            //inal TextView textoExpense = (TextView)view.findViewById(R.id.mpin_expense_info) ;

            cantOkE = (EditText)view.findViewById(R.id.mpin_cant_ok) ;
            cantNoE = (EditText)view.findViewById(R.id.mpin_cant_no) ;
            mpSelect =  (AutoCompleteTextView)view.findViewById(R.id.mpin_materia_prima);
            mpSelect.setThreshold(2);
            mpSelect.setAdapter(mpTextList);
            mpSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    materiaPrimaIn.setNombreMp((String)parent.getItemAtPosition(position));
                    unidad = gatTon.getMpUnidad(materiaPrimaIn.getNombreMp());
                    unidadMedida.setText(unidad);
                }
            });

            ACTV = (AutoCompleteTextView)view.findViewById(R.id.mpin_proveedor);;
            ACTV.setThreshold(2);
            ACTV.setAdapter(provTextList);
            ACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    materiaPrimaIn.setProveedor((String)parent.getItemAtPosition(position));
                }
            });

            fechaElabE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatesHelper.DatePickerFragment newFragment = DatesHelper.DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            // +1 because January is zero
                            final String selectedDate = DatesHelper.setTimeStampFromDMY( true, day, (month), year);
                            fechaElabE.setText(selectedDate);
                            materiaPrimaIn.fechaElab = selectedDate;
                        }
                    });
                    newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                }
            });

            fechaVencE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == IMAGE_CAPTURE_CODE) {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Imagen tomada");
                    bp = (Bitmap) data.getExtras().get("data");
                    imagenRotulo.setImageBitmap(bp);
                } else if (resultCode == RESULT_CANCELED) {
                    Log.d(TAG, "Imagen cancelada");
                }
            }
        }


        @Override
        public void onStart() {
            super.onStart();
            Dialog dialog = getDialog();
            if (dialog != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setLayout(width, height);
            }

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
        return (str==null || str.isEmpty()) ;
    }

    public static void setActvError( AutoCompleteTextView actv, String msg) {
        actv.setError(msg);
        actv.requestFocus();
    }
}




