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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class ShipperForm extends DialogFragment {

    public ShipperForm() {

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





    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            remitoKey = getArguments().getString("remitoKey","");
            clienteOut = shipperTon.getClienteOut(remitoKey);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

       baseView = inflater.inflate(R.layout.card_collect_input, container, false);

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

                    String mont = pagoE.getText().toString();
                    pago = (mont.isEmpty()) ? 0.0f : Float.parseFloat(mont);
                    String deb = debeE.getText().toString();
                    debe = (deb.isEmpty()) ? 0.0f : Float.parseFloat(mont);

                    boolean retErr = false;

                    if (pago == 0.0f && debe == 0.0f) {
                        setEditTextError(pagoE, "monto?");
                        setEditTextError(debeE, "monto?");
                        retErr = true;
                    }
                    if (clienteOut.getShipImageUrl().contains(Const.IMAGENOIMAGE)   ) {
                        imagenRotulo.setImageResource(R.drawable.no_image_3);
                        imagenRotulo.setColorFilter(R.color.colorPrimaryDark);
                        imagenRotulo.requestFocus();
                        retErr=true;
                    }

                    if (retErr) return true;


                    shipperTon.delivered(clienteOut, bp, getContext(), baseView);
                    dismiss();

                }
                return true;
            }
        });
        mpInToolbar.inflateMenu(R.menu.menu_shipper);
        String title = "ENTREGA A CLIENTE";
        mpInToolbar.setTitle(title);
        return baseView;
    }


        @Override
        public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            imagenRotulo = (ImageView) view.findViewById(R.id.imagen_ticket);
            imagenRotulo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cInt,IMAGE_CAPTURE_CODE);

                }
            });

            final TextView textoExpense = (TextView)view.findViewById(R.id.mpin_expense_info) ;
            final TextView itemName = (TextView) view.findViewById(R.id.exin_item_gasto_text);
            final TextView provNameTV = (TextView) view.findViewById(R.id.exin_item_prov_text);

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
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == IMAGE_CAPTURE_CODE) {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Imagen tomada");
                    bp = (Bitmap) data.getExtras().get("data");
                    imagenRotulo.setImageBitmap(bp);
                } else if (resultCode == RESULT_CANCELED) {
                    clienteOut.setShipImageUrl(Const.IMAGESETERROR);
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

