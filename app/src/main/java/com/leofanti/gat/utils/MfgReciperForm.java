package com.leofanti.gat.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.R;
import com.leofanti.gat.adapters.MfgOpListRecyclerView;
import com.leofanti.gat.adapters.SeReceRecyclerViewAdapter;
import com.leofanti.gat.adapters.mpInRecyclerViewAdapter;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.MateriaPrima;
import com.leofanti.gat.model.MateriaPrimaIn;
import com.leofanti.gat.model.MfgItem;
import com.leofanti.gat.model.SeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MfgReciperForm extends DialogFragment implements View.OnClickListener {

//* carga o edita una receta

    private static MfgItem mfgItem;
    //private ShipperTon shipperTon = ShipperTon.getInstance();
    private View baseView;


     private static final String TAG = "JCH GAT MFG_IN_OP";

    //private RecyclerView recyclerView;
    //private ExInRecyclerViewAdapter adapter;

    private Context context;

    private SeReceRecyclerViewAdapter receRv;

    private ArrayList<String> mpTextList;
    private Boolean someInput;

    private ArrayList<SeList> seList = new ArrayList<>();
    private DatesHelper datesHelper = new DatesHelper();
    private MfgHelper mfgHelper = new MfgHelper();
    private MfgTon mfgTon = MfgTon.getInstance();
    private LinearLayoutManager seListGrid;

    private AutoCompleteTextView mpSelect;
    private TextView seleccion, cantidad;
    private Button enterDosis;
    private EditText mpDosis;
    private ArrayList<MateriaPrima> mpListArray = new ArrayList<>();
    private HashMap<String, Float> recipeF ;
    private HashMap<String, String> recipeS ;
    private SeList seItem = new SeList();
    private String dosis;
    private String ingredient;
    private Float  qty;

    public MfgReciperForm() {

    }


    @Override
    public void onClick(View v) {
        //Boton de enter de ingrediente
        someInput = true;
        String tagButt = (String) v.getTag();
        cantidad.setText(dosis);
    }

    public static MfgReciperForm newInstance (SeList seItem) {
        //Copiar seItem
        MfgReciperForm fragment = new MfgReciperForm();
        return fragment;
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
                            //TODO guarda la orden de produccion
                            mfgHelper.saveRecipe(seItem);
                            //notify RV de OP??
                        }
                    }
                }
                dismiss();
                return true;
            }
        });
        mpInToolbar.inflateMenu(R.menu.menu_shipper);
        String title = "RECIPER";
        mpInToolbar.setTitle(title);
        return baseView;
    }


        @Override
        public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            seleccion = (TextView) baseView.findViewById(R.id.mfg_card_reciper_form_seleccion);
            cantidad = (TextView) baseView.findViewById(R.id.mfg_card_reciper_form_xunit);
            mpSelect = (AutoCompleteTextView) baseView.findViewById(R.id.mfg_card_reciper_mp_select);
            mpDosis = (EditText) baseView.findViewById(R.id.mfg_card_reciper_mp_dosis);
            enterDosis = (Button) baseView.findViewById(R.id.mfg_card_reciper_input_mp);


            final RecyclerView rView = (RecyclerView)baseView.findViewById(R.id.mfg_card_reciper_recipe_rv);

            rView.setLayoutManager(seListGrid);
            HashMap<String, Float> receF = new HashMap<>();
            //receF = mfgHelper.receStoF( seItem.getRece());
            receRv = new SeReceRecyclerViewAdapter(getContext(),receF);
            // 4. set adapter
            rView.setAdapter(receRv);
            // 5. set item animator to DefaultAnimator
            rView.setItemAnimator(new DefaultItemAnimator());

            //http://www.androidtutorialshub.com/android-recyclerview-click-listener-tutorial/
            rView.addOnItemTouchListener(new RecyclerViewGestureListener(getContext(), rView, new RecyclerViewTouchListener() {
                @Override
                public void onClick(View view, int position) {
                    //EDITA EL INGREDIENTE (lo carga en el fragmento de entrada;
                }

                @Override
                public void onLongClick(View view, int position) {
                    //BORRA
                    ;
                }
            }));


            mfgTon.getMpList(new DbGetDataListener<ArrayList<MateriaPrima>> () {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess( final ArrayList<MateriaPrima> lista) {
                    mpListArray = lista;
                    mpTextList= new ArrayList();
                    for( MateriaPrima mpItem: mpListArray){
                        mpTextList.add( mpItem.getDescripcion());
                    }
                    ArrayAdapter<String> mpArrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.automplete_layout,R.id.auto_complete_item,  mpTextList);
                    mpSelect.setThreshold(2);
                    mpSelect.setAdapter(mpArrayAdapter);
                    mpSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                           ingredient = (String)parent.getItemAtPosition(position);
                        }
                    });

                }
                @Override
                public void onFailed( DatabaseError error){

                    //Snackbar.make(baseView, error, Snackbar.LENGTH_SHORT).show();
                }
            });

        }


        @Override
        public void onStart() {
            super.onStart();
            /*Dialog dialog = getDialog();
            if (dialog != null) {
                //int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setLayout(width, height);
            }
            */
        }

}

