package com.leofanti.gat.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.leofanti.gat.GatTon;
import com.leofanti.gat.R;
import com.leofanti.gat.adapters.ExInRecyclerViewAdapter;
import com.leofanti.gat.model.ExpRegistro;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class SetPage extends DialogFragment {

    public SetPage() {

    }

    public static SetPage newInstance() {

        Bundle args = new Bundle();
        SetPage fragment = new SetPage();
        fragment.setArguments(args);
        return fragment;
    }

    public void setView( View view){
        this.baseView=view;
    }

    public void setRecyclerView( RecyclerView recyclerView){
        this.recyclerView = recyclerView;
    }

    public void setAdapter(ExInRecyclerViewAdapter adapter){
        this.adapter = adapter;
    }

    
    private boolean fromExpIn = true;
    private static int IMAGE_CAPTURE_CODE = 10111;
    private Bitmap bp;
    private static final String TAG = "JCHEXPINFRAG";
    private GatTon gatTon = GatTon.getInstance();
    private RecyclerView recyclerView;
    private ExInRecyclerViewAdapter adapter;
    private static View baseView;
    private static Context context;
    private ExpRegistro expReg = new ExpRegistro();
    private ArrayAdapter<String> mpTextList, provTextList;
    private AutoCompleteTextView ACTV, mpSelect;
    private ImageView imagenRotulo;
    private EditText montoE;
    private RadioGroup radioFact;
    private boolean factura = true;
    private float monto;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        expReg.setVisible(true);
        expReg.setThumbUrl("no_image");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.card_new_customer, container, false);

        //set to adjust screen height automatically, when soft keyboard appears on screen
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Toolbar mpInToolbar = (Toolbar) view.findViewById(R.id.mpin_toolbar);
        mpInToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("ATENCION");
                builder.setMessage("Cancelar el ingreso?");
                builder.setPositiveButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "ingreso eliminado ");
                        dialog.dismiss();
                        dismiss();
                    }
                });
                builder.setNeutralButton("CONTINUAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        mpInToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.mpin_save) {

                    //Controla que el cliente no exista

                    Log.d(TAG, "saving " + expReg.getItemName());
                    String cCosto = gatTon.getCcosto(expReg.getItemName());
                    expReg.setcCosto(cCosto);
                    gatTon.saveExp(expReg, bp, getContext(), baseView);
                    dismiss();
                    if( fromExpIn ){
                        int position = recyclerView.getAdapter().getItemCount() - 1;
                        //Log.d(TAG, "Adapter itemcount = " + position);
                        recyclerView.smoothScrollToPosition(position);
                        adapter.notifyDataSetChanged();
                    }
                    Snackbar.make(baseView, "Gasto guardado ", Snackbar.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        mpInToolbar.inflateMenu(R.menu.menu_mpitem);
        String title = "ABM de Cliente";
        mpInToolbar.setTitle(title);
        return view;
    }


        @Override
        public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mpTextList = new ArrayAdapter<String>(getActivity(),R.layout.automplete_layout,R.id.auto_complete_item,  gatTon.getMpList());
            provTextList = new ArrayAdapter<String>(getActivity(),R.layout.automplete_layout, R.id.auto_complete_item, gatTon.getProvList());

            imagenRotulo = (ImageView) view.findViewById(R.id.imagen_ticket);
            imagenRotulo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cInt,IMAGE_CAPTURE_CODE);

                }
            });

            final TextView textoExpense = (TextView)view.findViewById(R.id.mpin_expense_info) ;
            textoExpense.setVisibility(View.VISIBLE);

            mpSelect =  (AutoCompleteTextView)view.findViewById(R.id.exin_item_gasto);
            mpSelect.setThreshold(2);
            mpSelect.setAdapter(mpTextList);
            mpSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    expReg.setItemName((String)parent.getItemAtPosition(position));
                }
            });

            ACTV = (AutoCompleteTextView)view.findViewById(R.id.exin_proveedor);;
            ACTV.setThreshold(2);
            ACTV.setAdapter(provTextList);
            ACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    expReg.setProveedor((String)parent.getItemAtPosition(position));
                }
            });
            montoE = (EditText) view.findViewById(R.id.exin_monto);
            radioFact = (RadioGroup) view.findViewById(R.id.exin_radio_group);
            radioFact.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);

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

