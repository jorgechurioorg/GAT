package com.leofanti.gat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.adapters.LabelsRecyclerViewAdapter;
import com.leofanti.gat.adapters.UserPinRecyclerViewAdapter;
import com.leofanti.gat.model.Labels;
import com.leofanti.gat.model.UserPin;
import com.leofanti.gat.utils.DbGetDataListener;
import com.leofanti.gat.utils.RecyclerViewGestureListener;
import com.leofanti.gat.utils.RecyclerViewTouchListener;
import com.leofanti.gat.utils.Users;

import java.util.ArrayList;

/**
 * A login screen that offers login via pin and avatar per user
 */
public class LabelActivity extends AppCompatActivity  {



    //TODO labels es un singleton helper de etiquetas

    private static LabelTon labelTon = LabelTon.getInstance();
    private GridLayoutManager usersGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.card_labelmain);
        //TODO helper singleton que maneja la impresora
        labelTon.startPrinterService();
        SharedPreferences sharedPreferences = getSharedPreferences("GATCFG", MODE_PRIVATE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.labelmain_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("IMPRESION DE ROTULOS");
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.cancela);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        usersGrid = new GridLayoutManager(LabelActivity.this, 2 , RecyclerView.VERTICAL, false );
        final RecyclerView rView = (RecyclerView)findViewById(R.id.labelmain_rv);
        rView.setLayoutManager(usersGrid);

        labelTon.getAllLabels(new DbGetDataListener<ArrayList<Labels>>()  {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess( final ArrayList<Labels> labelsList){
                LabelsRecyclerViewAdapter rcAdapter = new LabelsRecyclerViewAdapter(LabelActivity.this, labelsList);
                rView.setAdapter(rcAdapter);
                rView.addOnItemTouchListener(new RecyclerViewGestureListener(LabelActivity.this, rView, new RecyclerViewTouchListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Labels labelToPrint = labelsList.get(position);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(LabelActivity.this);
                        builder.setTitle("IMPRIMIR " + labelToPrint.getName());
                        /*
                        qty.setText("0");
                        qty.requestFocus();
                        qty.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager keyboard = (InputMethodManager) baseView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                keyboard.showSoftInput(qty, 0);
                            }
                        }, 0);
                        builder.setView(viewInflated);*/
                        builder.setPositiveButton("IMPRIMIR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Snackbar.make(, "IMPRESION DE ROTULO", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }

                    @Override
                    public void onLongClick(View view, final int position) {
                    }

                }));

            }
            @Override
            public void onFailed(DatabaseError databaseError) {
                //DO SOME THING WHEN GET DATA FAILED HERE
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("JCH Activity", "return from activity");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return true;
    }



    public static class LabelsPrinter extends DialogFragment {

        private static Labels labelToPrint = new Labels();

        public LabelsPrinter() {

        }

        public static LabelsPrinter newInstance(Labels label) {
            LabelsPrinter frag = new LabelsPrinter();
            labelToPrint = label ;
            return frag;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            final View view = inflater.inflate(R.layout.labelprinter, container, false);

           //Nombre de la etiqueta y preguntala cantidad

            return view;
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
    }
}

