package com.leofanti.gat.utils;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.R;
import com.leofanti.gat.ShipperTon;
import com.leofanti.gat.adapters.ExInRecyclerViewAdapter;
import com.leofanti.gat.adapters.PtOutScanItemAdapter;
import com.leofanti.gat.model.ClienteOut;
import com.leofanti.gat.model.CollectRegistro;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.MateriaPrimaIn;
import com.leofanti.gat.tabFragmentMpIn;

import java.util.ArrayList;
import java.util.Collection;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class CollectInForm extends DialogFragment {



    private String remitoKey;
    private static CollectRegistro collectRegistro;
    //private ShipperTon shipperTon = ShipperTon.getInstance();
    private View baseView;
    private float saldoAnt;
    private TextView saldoAntT;
    private float monto;
    private EditText montoE;
    private float pago;
    private EditText pagoE;
    private float cheque;
    private EditText chequeE;
    private String chequeDia;
    private String notes;
    private TextView saldoFinal;
    private Float saldoFinalFloat;
    private EditText notesE;
    private ImageView imagenRotulo;
    private LinearLayout saldoFinalLL, incomeBlockLL, saldoAnteriorLL;
    private RelativeLayout imageBlockRL;

    private static int IMAGE_CAPTURE_CODE = 10111;
    private Bitmap bp;
    private static final String TAG = "GATCollectIn";

    //private RecyclerView recyclerView;
    //private ExInRecyclerViewAdapter adapter;

    private Context context;
    private ArrayAdapter<String> cliente;
    private AutoCompleteTextView ACTV, mpSelect;

    private Boolean someInput;

    private DatesHelper datesHelper = new DatesHelper();
    private SalesHelper salesHelper = new SalesHelper();
    private SalesTon salesTon = SalesTon.getInstance();

    public CollectInForm() {

    }

    public static CollectInForm newInstance (CollectRegistro reg) {
        CollectInForm frag = new CollectInForm();
        //collectRegistro = reg;
        collectRegistro = new CollectRegistro();
        /*Bundle args = new Bundle();
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
        mpInMode = VIEWITEM;*/
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ///if (getArguments() != null) {
        //    remitoKey = getArguments().getString("remitoKey","");
        //    clienteOut = shipperTon.getClienteOut(remitoKey);
        //}
        someInput = false;
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
                        String total = montoE.getText().toString();
                        monto = (total.isEmpty() ? 0.0f : Float.parseFloat(total));
                        String mont = pagoE.getText().toString();
                        pago = (mont.isEmpty()) ? 0.0f : Float.parseFloat(mont);
                        String check = chequeE.getText().toString();
                        cheque = (check.isEmpty()) ? 0.0f : Float.parseFloat(check);
                        String notas = notesE.getText().toString();

                        boolean retErr = false;


                        if (pago == 0.0f && cheque == 0.0f) {
                            setEditTextError(pagoE, "monto?");
                            setEditTextError(chequeE, "monto?");
                            retErr = true;
                        }

                        if (monto == 0.0f &&
                                collectRegistro.getSaldo() == 0.0f) {
                            setEditTextError(montoE, "monto?");
                            retErr = true;
                        }

                        if (strIsNull(collectRegistro.getCliente())) {
                            String error1 = "seleccionar cliente";
                            setActvError(mpSelect, error1);
                            retErr = true;
                        }

                        if (cheque != 0.0f) {
                            //TODO abrir ventana de fecha de cheque
                        }
                        /*if (clienteOut.getShipImageUrl().contains(Const.IMAGENOIMAGE)   ) {
                            imagenRotulo.setImageResource(R.drawable.no_image_3);
                            imagenRotulo.setColorFilter(R.color.colorPrimaryDark);
                            imagenRotulo.requestFocus();
                            retErr=true;
                        }*/

                        if (retErr) return true;

                        collectRegistro.setTimestamp(datesHelper.getTimestamp());
                        collectRegistro.setOperador(salesTon.getUserLogged());
                        collectRegistro.setMontoFact(monto);
                        collectRegistro.setPago(pago);
                        collectRegistro.setCheque(cheque);
                        collectRegistro.setNotas(notas);
                        //collectRegistro.setSaldo( saldoAnt - monto +pago +cheque -debe);
                        //TODO salesHelper consolida las ventas deuda, etc y guarda el registro
                        salesHelper.saveCollection(collectRegistro);

                    }
                }
                dismiss();
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

            imagenRotulo = (ImageView) view.findViewById(R.id.cardcollectin_image);
            imagenRotulo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cInt,IMAGE_CAPTURE_CODE);

                }
            });

            saldoAntT = (TextView) baseView.findViewById(R.id.cardcollectin_saldo_anterior);
            montoE = (EditText) baseView.findViewById(R.id.cardcollectin_info);
            pagoE = (EditText) baseView.findViewById(R.id.cardcollectin_cash);
            chequeE = (EditText) baseView.findViewById(R.id.cardcollectin_cheque);
            saldoFinal = (TextView) baseView.findViewById(R.id.cardcollectin_saldo_actual);
            notesE = (EditText) baseView.findViewById(R.id.cardcollectin_notes);

            imageBlockRL = (RelativeLayout) baseView.findViewById(R.id.cardcollectin_imageblock);
            imageBlockRL.setVisibility(View.INVISIBLE);

            saldoFinalLL = (LinearLayout) baseView.findViewById(R.id.cardcollectin_saldofinal_block);
            saldoFinalLL.setVisibility(View.INVISIBLE);
            incomeBlockLL = (LinearLayout) baseView.findViewById(R.id.cardcollectin_incomeblock);
            incomeBlockLL.setVisibility(View.INVISIBLE);
            saldoAnteriorLL = (LinearLayout) baseView.findViewById(R.id.cardcollectin_saldoanterior_block);
            saldoAnteriorLL.setVisibility(View.INVISIBLE);


            montoE.addTextChangedListener(new TextChange(montoE));
            pagoE.addTextChangedListener(new TextChange(pagoE));
            chequeE.addTextChangedListener(new TextChange(chequeE));

            salesTon.getClienteList( new DbGetDataListener<ArrayList<String>>() {
                @Override
                public void onStart() {
                }
                @Override
                public void onSuccess(final ArrayList<String> cliList) {
                    ArrayAdapter clis = new ArrayAdapter<String>(getActivity(), R.layout.automplete_layout, R.id.auto_complete_item, cliList);
                    ACTV = (AutoCompleteTextView)baseView.findViewById(R.id.cardcollectin_cliente);
                    ACTV.setThreshold(2);
                    ACTV.setAdapter(clis);
                    ACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String cliente = (String)parent.getItemAtPosition(position);
                            collectRegistro.setCliente ( cliente );

                            salesTon.getCustomerCollect( cliente, new DbGetDataListener<Float>() {
                                @Override
                                public void onStart() {
                                }
                                @Override
                                public void onSuccess(final Float saldo) {
                                    //TODO configura el campo de saldo anterior
                                    saldoAnt = saldoFinalFloat = saldo;
                                    saldoAntT.setText(String.format("%2f", saldo));
                                    saldoFinal.setText(String.format("%2f", saldoFinalFloat));
                                    saldoAnteriorLL.setVisibility(View.VISIBLE);
                                    imageBlockRL.setVisibility(View.VISIBLE);
                                    saldoFinalLL.setVisibility(View.VISIBLE);
                                    incomeBlockLL.setVisibility(View.VISIBLE);

                                }
                                @Override
                                public void onFailed(DatabaseError error) {

                                }
                            });

                        }
                    });
                }
                @Override
                public void onFailed(DatabaseError dbe) {
                }
            });

        }

    //inner class

    private int textBefLength = 0;
    private Float inputBefore ;
    private class TextChange implements TextWatcher {

        View view;
        private TextChange (View v) {
            view = v;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            String input = s.toString();
            textBefLength = s.length();
            try
            {
                inputBefore = Float.valueOf(input);

            }
            catch(NumberFormatException ex)
            {
                inputBefore = 0.0f;
            }

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ;
        }

        @Override
        public void afterTextChanged(Editable s){

            String input = s.toString();
            someInput = true;
            try
            {
                inF = Float.valueOf(input);

            }
            catch(NumberFormatException ex)
            {
                inF = 0.0f;
            }
            Boolean moreDigit = input.length() > textBefLength;
            switch (view.getId()) {
                case R.id.cardcollectin_info:
                    //Monto de la factura actua
                        saldoFinalFloat += inputBefore;
                        saldoFinalFloat -= inF;
                    break;

                case R.id.cardcollectin_cash:
                case R.id.cardcollectin_cheque:
                    saldoFinalFloat -= inputBefore;
                    saldoFinalFloat += inF;
                    break;


            }
            saldoFinal.setText(Float.toString(saldoFinalFloat));
        }
    }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == IMAGE_CAPTURE_CODE) {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Imagen tomada");
                    bp = (Bitmap) data.getExtras().get("data");
                    imagenRotulo.setImageBitmap(bp);
                } else if (resultCode == RESULT_CANCELED) {
                    //clienteOut.setShipImageUrl(Const.IMAGESETERROR);
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
    Float inF;
    Float saldo;

    private void scanNumber(final EditText captureDigits, final TextView saldoFinal, final int pos ){
        //TODO ir cargando numeros, parsearlos y actualizar el campo de saldo final


        captureDigits.requestFocus();
        captureDigits.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager) getDialog().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(captureDigits, 0);
            }
        }, 0);

        captureDigits.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //TODO aca valida el campo numerico ingresado
                    switch( pos ) {
                        case 0: //cash
                            collectRegistro.setPago(inF);
                            break;

                        case 1: //cheque
                            collectRegistro.setCheque(inF);
                            break;

                    }
                }
                return false;
            }
        });

        captureDigits.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO aca va el parser
                String input = s.toString();
                inF = Float.parseFloat(input);
                saldo = collectRegistro.getMontoFact() - inF;
                saldoFinal.setText(String.format("%1$,.2f", saldo.toString()));
            }
        });


    }



    private Boolean checkInput() {
           return !((pagoE.getText().toString().length() == 0) &&
                    (chequeE.getText().toString().length() == 0) );
    }
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

