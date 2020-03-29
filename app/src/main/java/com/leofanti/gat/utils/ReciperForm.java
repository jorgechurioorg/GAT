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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.leofanti.gat.R;
import com.leofanti.gat.ShipperTon;
import com.leofanti.gat.adapters.ExInRecyclerViewAdapter;
import com.leofanti.gat.model.ClienteOut;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.Rece;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class ReciperForm extends DialogFragment {

    public ReciperForm() {

    }

    private String remitoKey;
    private ClienteOut clienteOut;
    private ShipperTon shipperTon = ShipperTon.getInstance();
    private View baseView;
    private float pago;
    private EditText pagoE;
    private float debe;
    private EditText debeE;
    private String notes;
    private EditText notesE;
    private ImageView imagenRotulo;

    private static int IMAGE_CAPTURE_CODE = 10111;
    private Bitmap bp;
    private static final String TAG = "GATshipperForm";

    private RecyclerView recyclerView;
    private ExInRecyclerViewAdapter adapter;

    private Context context;
    private ArrayAdapter<String> mpTextList, provTextList;
    private AutoCompleteTextView ACTV, mpSelect;


    private static Rece rece = new Rece();

    public static ReciperForm newInstance(Rece receIn) {
        ReciperForm fragment = new ReciperForm();
        rece = receIn;
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

       baseView = inflater.inflate(R.layout.mfg_card_reciper, container, false);

        //set to adjust screen height automatically, when soft keyboard appears on screen
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Toolbar mpInToolbar = (Toolbar) baseView.findViewById(R.id.cardcollectin_toolbar);
        mpInToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("ATENCION");
                builder.setMessage("Cancelar el ingreso?");
                builder.setPositiveButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                if (id == R.id.menushipper_save) {

                    boolean retErr = false;


                    dismiss();

                }
                return true;
            }
        });
        mpInToolbar.inflateMenu(R.menu.menu_shipper);
        String title = "INGRESO DE RECETA";
        mpInToolbar.setTitle(title);
        return baseView;
    }


        @Override
        public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            //final TextView textoExpense = (TextView)view.findViewById(R.id.mpin_expense_info) ;
            //final TextView itemName = (TextView) view.findViewById(R.id.exin_item_gasto_text);
            //final TextView provNameTV = (TextView) view.findViewById(R.id.exin_item_prov_text);

/*
            montoE = (EditText) view.findViewById(R.id.exin_monto);
            radioFact = (RadioGroup) view.findViewById(R.id.exin_radio_group);
            radioFact.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);

                }
            });*/
        }



        @Override
        public void onStart() {
            super.onStart();
            Dialog dialog = getDialog();
            //TODO solo si esta en modo tablet landscape
            if (dialog != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setLayout(width, height);
            }

        }
}

