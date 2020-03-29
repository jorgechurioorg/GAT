package com.leofanti.gat;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.adapters.PtOutRecyclerViewAdapter;
import com.leofanti.gat.model.CollectRegistro;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.utils.CollectInForm;

import com.leofanti.gat.utils.DbGetDataListener;
import com.leofanti.gat.utils.DbReadOnlyTables;
import com.leofanti.gat.utils.SalesHelper;
import com.leofanti.gat.utils.SalesTon;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainActivityCollect extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PtOutRecyclerViewAdapter adapter;
    private Context context;
    private static SalesTon salesTon = SalesTon.getInstance();
    private SalesHelper salesHelper = new SalesHelper();
    //https://stackoverflow.com/questions/9132027/very-simple-code-but-got-error-activity-has-been-destroyed-when-use-fragment
    private static FragmentManager fragmentManager = null;

    private TextView cb_inv_tdy;
    private  TextView cb_inv_wtd;
    private TextView cb_inv_mtd ;
    private TextView cb_cash_tdy ;
    private TextView cb_cash_wtd;
    private TextView cb_cash_mtd ;
    private TextView cb_check_tdy ;
    private TextView cb_check_wtd;
    private TextView cb_check_mtd;
    private TextView cb_debe_tdy ;
    private TextView cb_debe_wtd;
    private TextView cb_debe_mtd ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collect);
        Intent intent = getIntent();
        String userNick = intent.getStringExtra("username");
        String userRole = intent.getStringExtra("role");
        salesTon.setUserLogged(userNick);
        salesTon.setUserRole(userRole);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activitycollect_toolbar);
        toolbar.setLogo(R.drawable.user_circle);
        setSupportActionBar(toolbar);
        TextView usuario = (TextView) findViewById(R.id.activitycollect_usuario);
        usuario.setText(userNick);

        context = this;

        cb_inv_tdy = (TextView) findViewById(R.id.collectboard_inv_tdy);
        cb_inv_wtd = (TextView) findViewById(R.id.collectboard_inv_wtd);
        cb_inv_mtd = (TextView) findViewById(R.id.collectboard_inv_mtd);

        cb_cash_tdy = (TextView) findViewById(R.id.collectboard_cash_tdy);
        cb_cash_wtd = (TextView) findViewById(R.id.collectboard_cash_wtd);
        cb_cash_mtd = (TextView) findViewById(R.id.collectboard_cash_mtd);
        cb_check_tdy = (TextView) findViewById(R.id.collectboard_check_tdy);
        cb_check_wtd = (TextView) findViewById(R.id.collectboard_check_wtd);
        cb_check_mtd = (TextView) findViewById(R.id.collectboard_check_mtd);
        cb_debe_tdy = (TextView) findViewById(R.id.collectboard_debe_tdy);
        cb_debe_wtd = (TextView) findViewById(R.id.collectboard_debe_wtd);
        cb_debe_mtd = (TextView) findViewById(R.id.collectboard_debe_mtd);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        salesHelper.updateCollectionsBoard(new DbGetDataListener<Map<String, Float>> () {
            @Override
            public void onStart() {
                progressDialog.setTitle("Cargando...");
                progressDialog.show();
            }

            @Override
            public void onSuccess(final Map<String,Float> board) {

                setCollectionsBoard( board );

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onFailed( DatabaseError error){
                //Log.d("JCH", "Error de getReadOnly " + error);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //Snackbar.make(, error, Snackbar.LENGTH_SHORT).show();
            }
        });


        FloatingActionButton fabCollect = (FloatingActionButton) this.findViewById(R.id.activitycollect_fab);
        fabCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO  el objeto collectRegistro lo dejo para cuando ponga la lista de entregas
                CollectRegistro collectRegistro = new CollectRegistro();
                CollectInForm collectInForm;
                collectInForm = CollectInForm.newInstance( collectRegistro);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                collectInForm.show(ft, "CollectInForm" );

            }
        });

        usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("ATENCION");
                builder.setMessage("Sale del sistema?");
                builder.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //Intent returnIntent = new Intent();
                        //setResult(UserPinInput.RESULT_OK,returnIntent);
                        //TODO cerrar la app???
                        finish();
                    }
                });
                builder.create().show();

            }
        });
        getSupportActionBar().setTitle(null);


    }

    public void setCollectionsBoard( Map<String, Float> board){

        cb_inv_tdy.setText(Float.toString(board.get(Const.INV_TDY)));
        cb_inv_wtd.setText(Float.toString(board.get(Const.INV_WTD)));
        cb_inv_mtd.setText(Float.toString(board.get(Const.INV_MTD)));

        cb_cash_tdy.setText(Float.toString(board.get(Const.CSH_TDY)));
        cb_cash_wtd.setText(Float.toString(board.get(Const.CSH_WTD)));
        cb_cash_mtd.setText(Float.toString(board.get(Const.CSH_MTD)));

        cb_check_tdy.setText(Float.toString(board.get(Const.CHK_TDY)));
        cb_check_wtd.setText(Float.toString(board.get(Const.CHK_MTD)));
        cb_check_mtd.setText(Float.toString(board.get(Const.CHK_MTD)));


        cb_debe_tdy.setText(Float.toString(board.get(Const.DUE_TDY)));
        cb_debe_wtd.setText(Float.toString(board.get(Const.DUE_WTD)));
        cb_debe_mtd.setText(Float.toString(board.get(Const.DUE_MTD)));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch( id) {
            //case R.id.menucollectsave:
               //TODO guardar la cobranza
             //   break;
            case R.id.menucollectaudit:
                //TODO solo en los usuarios jerarquizados
                //if( userHelper.getPermission( Const.ADMIN) ) {
                //      TODO trigger collect auditor fragment
                //}
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public void callCollectForm(CollectRegistro collectRegistro){

        //TODO lanzar el form aca y no en el main
        //ShipperForm expDialog = new ShipperForm();
        //Bundle bundle = new Bundle();
        //bundle.putString("remitoKey", clienteOut.getRemitoKey());
        //expDialog.setArguments(bundle);
        //FragmentTransaction ft = fragmentManager.beginTransaction();
        //expDialog.show(ft, "CollectForm" );

    }

    private void saveMap(Map<String,Float> inputMap){
        //TODO usar esta funcion si quiero guardar en forma rapida el collectins board por ahora lo calcula
        SharedPreferences sharedPreferences = getSharedPreferences("GATCFG", MODE_PRIVATE);
        if (sharedPreferences != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("collections_board").commit();
            editor.putString("collections_board", jsonString);
            editor.commit();
        }
    }

    private Map<String,Float> loadMap(){

        Map<String,Float> outputMap = new HashMap<String,Float>();

        SharedPreferences pSharedPref = getSharedPreferences("GATCFG", MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("collections_board", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    Float value = (Float) jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }
}

