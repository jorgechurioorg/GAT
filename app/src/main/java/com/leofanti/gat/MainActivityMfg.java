package com.leofanti.gat;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.allyants.boardview.BoardView;
import com.allyants.boardview.SimpleBoardAdapter;
import com.allyants.boardview.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leofanti.gat.model.CollectRegistro;
import com.leofanti.gat.model.MfgItem;
import com.leofanti.gat.utils.DbReadOnlyTables;
import com.leofanti.gat.utils.MfgSeInForm;
import com.leofanti.gat.utils.MfgTon;

import java.util.ArrayList;


public class MainActivityMfg extends AppCompatActivity {


    ArrayList<String> list = new ArrayList<>();


    //private MainActivityBinding mainActivityBinding;
    ArrayList<MfgItem> opList, seList, ptList;
    private Context context;
    private static MfgTon mfgTon = MfgTon.getInstance();
    //https://stackoverflow.com/questions/9132027/very-simple-code-but-got-error-activity-has-been-destroyed-when-use-fragment
    private static FragmentManager fragmentManager = null;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //mfgTon.initDb();


        fragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_mfg);
        Intent intent = getIntent();
        String userNick = intent.getStringExtra("username");
        String userRole = intent.getStringExtra("role");
        mfgTon.setUserLogged(userNick);
        mfgTon.setUserRole(userRole);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mfg_main_toolbar);
        toolbar.setLogo(R.drawable.user_circle);
        setSupportActionBar(toolbar);
        final TextView usuario = (TextView) findViewById(R.id.mfg_main_usuario);
        usuario.setText(userNick);

        context = this;

        final BoardView boardView = (BoardView)findViewById(R.id.boardView);
        ArrayList<SimpleBoardAdapter.SimpleColumn> data = new ArrayList<>();
        ArrayList<String> list = new ArrayList<String>();
        list.add("Item 1");
        list.add("Item 2");
        list.add("Item 3");
        list.add("Item 4");
        data.add(new SimpleBoardAdapter.SimpleColumn("Column 1",(ArrayList)list));
        data.add(new SimpleBoardAdapter.SimpleColumn("Column 2",(ArrayList)list));
        data.add(new SimpleBoardAdapter.SimpleColumn("Column 3",(ArrayList)list));
        SimpleBoardAdapter boardAdapter = new SimpleBoardAdapter(this,data);
        boardView.setAdapter(boardAdapter);

        boardView.setOnDoneListener(new BoardView.DoneListener() {
            @Override
            public void onDone() {
                Log.e("scroll","done");
            }
        });

        boardView.setOnDragItemListener(new BoardView.DragItemStartCallback() {
            @Override
            public void startDrag(View view, int startItemPos, int startColumnPos) {
                Log.e("boardView","startDrag");
            }

            @Override
            public void changedPosition(View view, int startItemPos, int startColumnPos, int newItemPos, int newColumnPos) {
                Log.e("boardView","changedPosition");
            }

            @Override
            public void dragging(View itemView, MotionEvent event) {
                Log.e("boardView","dragging");
            }

            @Override
            public void endDrag(View view, int startItemPos, int startColumnPos, int endItemPos, int endColumnPos) {
                Log.e("boardView","endDrag");
            }
        });


        FloatingActionButton fabMfg = (FloatingActionButton) this.findViewById(R.id.mfg_main_op_fab);
        fabMfg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO  dispara el fragment de ingreso de OP
                MfgItem mfgItem = new MfgItem();
                MfgSeInForm mfgSeInForm;
                mfgSeInForm = MfgSeInForm.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                mfgSeInForm.show(ft, "MfgInForm" );

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
        getSupportActionBar().setTitle("");


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


}

