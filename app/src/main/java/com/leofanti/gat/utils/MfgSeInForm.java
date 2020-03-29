package com.leofanti.gat.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.R;
import com.leofanti.gat.UserPinInput;
import com.leofanti.gat.adapters.MfgOpListRecyclerView;
import com.leofanti.gat.adapters.UserPinRecyclerViewAdapter;
import com.leofanti.gat.model.CollectRegistro;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.MfgItem;
import com.leofanti.gat.model.Rece;
import com.leofanti.gat.model.SeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class MfgSeInForm extends DialogFragment implements View.OnClickListener {



    private static MfgItem mfgItem;
    //private ShipperTon shipperTon = ShipperTon.getInstance();
    private View baseView;


     private static final String TAG = "JCH GAT MFG_IN_OP";

    //private RecyclerView recyclerView;
    //private ExInRecyclerViewAdapter adapter;

    private Context context;

    private Boolean someInput;
    private ArrayList<Rece> seList = new ArrayList<>();
    private DatesHelper datesHelper = new DatesHelper();
    private MfgHelper mfgHelper = new MfgHelper();
    private MfgTon mfgTon = MfgTon.getInstance();
    private GridLayoutManager seListGrid;
    private Map<String, Float> dosisButt = new HashMap<>();
    private TextView alergenType, prodType, seleccion, cantidad;
    private LinearLayout buttonPanel;
    private Button[] buttons;
private String loteLabel;
    private Rece seItem;
    private String dosis;

    public MfgSeInForm() {

    }


    @Override
    public void onClick(View v) {
        someInput = true;
        loteLabel = (String) v.getTag();
        Float loteProd = dosisButt.get(loteLabel);
        String loteProdS = Float.toString(loteProd);
        cantidad.setText(loteProdS);
    }

    public static MfgSeInForm newInstance () {
        MfgSeInForm frag = new MfgSeInForm();
        //collectRegistro = reg;
        mfgItem = new MfgItem();
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        someInput = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        baseView = inflater.inflate(R.layout.mfg_card_op_input_form, container, false);


        //set to adjust screen height automatically, when soft keyboard appears on screen
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Toolbar mpInToolbar = (Toolbar) baseView.findViewById(R.id.mfg_card_op_input_form_toolbar);
        mpInToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //someInput = checkInput();
                if (someInput) {

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
                } else {
                    dismiss();
                }

            }
        });
        mpInToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (someInput) {
                    int id = item.getItemId();
                    if (id == R.id.menushipper_save) {
                        //TODO check if Se and dosis are selected

                        String seSel = seleccion.getText().toString();
                        String seDosis = cantidad.getText().toString();
                        boolean retErr = false;
                        if( seSel.isEmpty()){
                            seleccion.setError("Elegir SE");
                            retErr = true;
                        }
                        if( seDosis.isEmpty()){
                            cantidad.setError("Elegir Cantidad");
                            retErr = true;
                        }
                        if( !retErr ) {
                            //TODO calcula las cnatidade spor lote y las guarda en OP estado Op
                            //TODO guarda la orden de produccion
                            Float loteProd = Float.parseFloat(seDosis);
                            mfgHelper.saveMfgRec(Const.MFG_OP, loteProd, loteLabel, seItem);
                            //notify RV de OP??
                        }
                    }
                }
                dismiss();
                return true;
            }
        });
        mpInToolbar.inflateMenu(R.menu.menu_shipper);
        String title = "";
        mpInToolbar.setTitle(title);
        return baseView;
    }


        @Override
        public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            alergenType = (TextView) baseView.findViewById(R.id.mfg_card_op_input_form_alergen);
            prodType = (TextView) baseView.findViewById(R.id.mfg_card_op_input_form_type);
            seleccion = (TextView) baseView.findViewById(R.id.mfg_card_op_input_form_seleccion);
            cantidad = (TextView) baseView.findViewById(R.id.mfg_card_op_input_form_cantidad);
            buttonPanel = (LinearLayout) baseView.findViewById(R.id.mfg_card_op_input_form_qty_button_panel);
            buttons = new Button[] {
                    (Button) baseView.findViewById(R.id. mfg_card_op_input_form_qty_button_1),
                    (Button) baseView.findViewById(R.id. mfg_card_op_input_form_qty_button_2),
                    (Button) baseView.findViewById(R.id. mfg_card_op_input_form_qty_button_3),
                    (Button) baseView.findViewById(R.id. mfg_card_op_input_form_qty_button_4),
                    (Button) baseView.findViewById(R.id. mfg_card_op_input_form_qty_button_5),
                    (Button) baseView.findViewById(R.id. mfg_card_op_input_form_qty_button_6)
            };
            for( int ix = 0 ; ix<buttons.length; ix++) {
                buttons[ix].setOnClickListener(this);
            }
            seListGrid= new GridLayoutManager(getContext(), 4 , RecyclerView.VERTICAL, false );

            final RecyclerView rView = (RecyclerView)baseView.findViewById(R.id.mfg_card_op_input_form_rv);

            rView.setLayoutManager(seListGrid);

            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Cargando...");
            progressDialog.show();

            mfgTon.getReceList(getContext(), new DbGetDataListener<ArrayList<Rece>> () {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(final ArrayList<Rece> list) {
                    seList.clear();
                    seList.addAll(list);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    MfgOpListRecyclerView rcAdapter = new MfgOpListRecyclerView(getContext(), seList);
                    rView.setAdapter(rcAdapter);
                    rView.addOnItemTouchListener(new RecyclerViewGestureListener(getContext(), rView, new RecyclerViewTouchListener() {
                        @Override
                        public void onClick(View view, int position) {
                            someInput = true;
                            seItem = list.get(position);
                            alergenType.setText(seItem.getAlergenType());
                            prodType.setText(seItem.getReceType());
                            seleccion.setText(seItem.getDescripcion());
                            int ix = 0;
                            dosisButt = seItem.getLoteProd();
                            for (Map.Entry<String, Float> entry : seItem.getLoteProd().entrySet()) {
                                String key = entry.getKey();
                                Float value = entry.getValue();
                                buttons[ix].setText(key);
                                buttons[ix].setVisibility(View.VISIBLE);
                                buttons[ix].setTag(key);
                                ix++;
                            }
                            buttonPanel.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLongClick(View view, final int position) {
                        }
                    }));

                }
                @Override
                public void onFailed( DatabaseError error){
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    //Snackbar.make(, error, Snackbar.LENGTH_SHORT).show();
                }
            });

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

