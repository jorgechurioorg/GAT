package com.leofanti.gat.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.leofanti.gat.model.Cliente;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.ExpRegistro;
import com.leofanti.gat.model.MateriaPrimaIn;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;



public class ExpenseInForm extends DialogFragment {

    public ExpenseInForm() {

    }


    private boolean fromExpIn = false;
    private String mpName;
    private String provName;
    private static int IMAGE_CAPTURE_CODE = 10111;
    private Bitmap bp;
    private static final String TAG = "JCHEXPINFRAG";
    private GatTon gatTon = GatTon.getInstance();
    private RecyclerView recyclerView;
    private ExInRecyclerViewAdapter adapter;
    private View baseView;
    private Context context;
    private ExpRegistro expReg = new ExpRegistro();
    private ArrayAdapter<String> mpTextList, provTextList;
    private AutoCompleteTextView ACTV, mpSelect;
    private ImageView imagenRotulo;
    private EditText montoE;
    private EditText inFecha;
    private RadioGroup radioFact;
    private boolean factura = true;
    private float monto;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mpName = getArguments().getString("mpName","");
            provName = getArguments().getString("provName","");
            fromExpIn = getArguments().getBoolean("fromMpIn");
        }
        expReg.setVisible(true);
        expReg.setThumbUrl("no_image");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.card_exp_input, container, false);

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
                builder.create().show();
            }
        });
        mpInToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean hiddenExpense = setHiddenExpense( expReg );

                int id = item.getItemId();
                if (id == R.id.mpin_save) {

                    String mont = montoE.getText().toString();
                    monto = (mont.isEmpty()) ? 0.0f : Float.parseFloat(mont);
                    boolean fact = (radioFact.getCheckedRadioButtonId() == R.id.exinexp_factura);
                    expReg.setMonto(monto, fact);
                    //String cCosto = gatTon.getCcosto(expReg.getItemName());
                    //expReg.setcCosto(cCosto);

                    boolean retErr = false;

                    if (monto == 0) {
                        setEditTextError(montoE, "monto?");
                        retErr = true;
                    }
                    /*
                    if (bp == null && !hiddenExpense ) {
                        imagenRotulo.setImageResource(R.drawable.no_image_3);
                        imagenRotulo.setColorFilter(R.color.colorPrimaryDark);
                        imagenRotulo.requestFocus();
                        retErr=true;
                    }*/
                    
                    if (strIsNull(expReg.getItemName())) {
                        String error1 = "Elegir un gasto";
                        setActvError(mpSelect, error1);
                        retErr = true;
                    } else {
                        String cCosto = gatTon.getCcosto(expReg.getItemName());
                        expReg.setcCosto(cCosto);
                    }
                    if (strIsNull(expReg.getProveedor())) {
                        setActvError(ACTV, "Elegir un proveedor de la lista");
                        retErr = true;
                    }
                    if (retErr) return true;

                    //Log.d(TAG, "saving " + expReg.getItemName());
                    String fechaGasto = inFecha.getText().toString();
                    if( !fechaGasto.isEmpty() && fechaGasto != null ){
                        expReg.setTimestamp(DatesHelper.humanToYYYYMMDD(fechaGasto));
                    }
                    expReg.setOperador(gatTon.getUserLogged());
                    gatTon.saveExp(expReg, bp, getContext(), baseView);
                    dismiss();
                    //REVISAR ESTO OJO AL PIOJO deveria ser con una señal
                    /*if( !fromExpIn ){
                        int position = recyclerView.getAdapter().getItemCount() - 1;
                        recyclerView.smoothScrollToPosition(position);
                        adapter.notifyDataSetChanged();
                    }*/
                    //Snackbar.make(baseView, "Gasto guardado ", Snackbar.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        mpInToolbar.inflateMenu(R.menu.menu_mpitem);
        String title = "REGISTRA GASTO";
        mpInToolbar.setTitle(title);
        return view;
    }


        @Override
        public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mpTextList = new ArrayAdapter<String>(getActivity(),R.layout.automplete_layout,R.id.auto_complete_item,  gatTon.getMpList());
            provTextList = new ArrayAdapter<String>(getActivity(),R.layout.automplete_layout, R.id.auto_complete_item, gatTon.getProvList());
            inFecha = (EditText) view.findViewById(R.id.exin_date_in) ;
            if( gatTon.getUserRole().equalsIgnoreCase("ADMIN") || gatTon.getUserRole().equalsIgnoreCase("ROOT") ){
                inFecha.setVisibility(View.VISIBLE);
            }
            inFecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatesHelper.DatePickerFragment newFragment = DatesHelper.DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            final String selectedDate = DatesHelper.setTimeStampFromDMY( true, day, (month), year);
                            inFecha.setText(selectedDate);
                        }
                    });
                    newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                }
            });
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
            final TextView itemName = (TextView) view.findViewById(R.id.exin_item_gasto_text);
            final TextView provNameTV = (TextView) view.findViewById(R.id.exin_item_prov_text);
            final ImageButton nuevoProv = (ImageButton) view.findViewById(R.id.exin_newprov);
            ACTV = (AutoCompleteTextView) view.findViewById(R.id.exin_proveedor);
            mpSelect = (AutoCompleteTextView) view.findViewById(R.id.exin_item_gasto);

            if( fromExpIn) {
                itemName.setText(mpName);
                expReg.setItemName(mpName);
                itemName.setVisibility(View.VISIBLE);
                mpSelect.setVisibility(View.GONE);
                provNameTV.setText(provName);
                expReg.setProveedor(provName);
                provNameTV.setVisibility(View.VISIBLE);
                ACTV.setVisibility(View.INVISIBLE);
            } else {
                mpSelect.setThreshold(2);
                mpSelect.setAdapter(mpTextList);
                mpSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        expReg.setItemName((String) parent.getItemAtPosition(position));
                    }
                });


                ACTV.setThreshold(2);
                ACTV.setAdapter(provTextList);
                ACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        expReg.setProveedor((String) parent.getItemAtPosition(position));
                    }
                });
            }
            montoE = (EditText) view.findViewById(R.id.exin_monto);
            radioFact = (RadioGroup) view.findViewById(R.id.exin_radio_group);
            radioFact.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);

                }
            });
            nuevoProv.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //https://stackoverflow.com/questions/4016313/how-to-keep-an-alertdialog-open-after-button-onclick-is-fired
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Nuevo Proveedor");
                    final View customLayout = getLayoutInflater().inflate(R.layout.dialog_newclient, null);
                    builder.setView(customLayout);
                    builder.setPositiveButton("CREAR PROVEEDOR",null);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            b.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    // send data from the AlertDialog to the Activity
                                    //TODO Solo guarda el nombre del proveedor por el momento
                                    EditText nameEt = customLayout.findViewById(R.id.newclient_name);
                                    EditText addrEt = customLayout.findViewById(R.id.newclient_direccion);
                                    EditText locaEt = customLayout.findViewById(R.id.newclient_localidad);
                                    EditText celuEt = customLayout.findViewById(R.id.newclient_celu);
                                    String cName = nameEt.getText().toString();
                                    String cAddress = addrEt.getText().toString();
                                    String cLocal = locaEt.getText().toString();
                                    String cCelu = celuEt.getText().toString();
                                    if( cName.isEmpty()) {
                                        nameEt.setError("Ingresar nombre");
                                        nameEt.requestFocus();
                                    } else {
                                        expReg.setProveedor(cName);
                                        gatTon.addProveedor(cName);
                                        //TODO llamar a una funcion que comparta con cliente existentes
                                        alertDialog.dismiss();
                                        DatesHelper datesHelper = new DatesHelper();
                                        //TODO Guardar el proveedor en cnamenuevoCliente);
                                        provNameTV.setText(cName);
                                        provNameTV.setVisibility(View.VISIBLE);
                                        ACTV.setVisibility(View.GONE);
                                        nuevoProv.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    });
                    alertDialog.show();
                }
            }));
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

    private boolean  setHiddenExpense( ExpRegistro expReg ) {
        boolean hidden = false ;
        if( expReg.getcCosto()!=null) {
            hidden = expReg.getcCosto().equalsIgnoreCase("RRHH");
        }
        return hidden;

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

