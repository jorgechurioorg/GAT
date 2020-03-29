package com.leofanti.gat;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.leofanti.gat.utils.DbReadOnlyTables;


public class MainActivityCanal extends AppCompatActivity {

    ViewPager viewPager;
    PagerAdapterCanal adapter;
    Integer tabPos = 0;
    Context context;

    final int PDVSALE = 1 ;
    final int ALMACEN = 0 ;
    final int REMITOS = 2 ;


    GatTon gatTon = GatTon.getInstance();
    //https://stackoverflow.com/questions/9132027/very-simple-code-but-got-error-activity-has-been-destroyed-when-use-fragment
    static FragmentManager fragmentManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_mfg);
        Intent intent = getIntent();
        String userNick = intent.getStringExtra("username");
        String userRole = intent.getStringExtra("role");
        gatTon.setUserLogged(userNick);
        gatTon.setUserRole(userRole);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.maincanal_toolbar);
        //toolbar.setLogo(R.drawable.user_circle);
        //setSupportActionBar(toolbar);
        //TextView usuario = (TextView) findViewById(R.id.maincanal_usuario);
        //usuario.setText(userNick);

        context = this;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        gatTon.getReadOnlyList(new DbReadOnlyTables(){
            @Override
            public void onStart() {
                progressDialog.setTitle("Inicializando");
                progressDialog.show();
            }
            public void onSuccess() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
            public void onFailed( String error){
                Log.d("JCH", "Error de getReadOnly " + error);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //Snackbar.make(, error, Snackbar.LENGTH_SHORT).show();
            }
        });

        /*usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("ATENCION");
                builder.setMessage("Sale del sistema?");
                builder.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent returnIntent = new Intent();
                        setResult(UserPinInput.RESULT_OK,returnIntent);
                        finish();
                    }
                });
                builder.create().show();

            }
        });
        getSupportActionBar().setTitle(null);

        //Copia una coleccion en otro child
        //gatTon.copyRecord("expensetest", "expenses");

*/
    }

    MenuItem menuItem;
    MenuItem auditIcon;
    MenuItem abmUserIcon;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String role = gatTon.getUserRole().toUpperCase();
        /*if( role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROOT")||role.equalsIgnoreCase("AUDIT")) {
            getMenuInflater().inflate(R.menu.menu_main_admin, menu);
            abmUserIcon = menu.findItem(R.id.menuadmin_nuevocli);
            auditIcon = menu.findItem(R.id.menuadmin_audit);
            switch (role) {
                case "CANAL":
                    auditIcon.setVisible(false);
                    abmUserIcon.setVisible(false);
                    break;
                case "ADMIN":
                    auditIcon.setVisible(true);
                    break;
                case "ROOT":
                    auditIcon.setVisible(true);
                    break;
            }
        } else {
            getMenuInflater().inflate(R.menu.menu_main_user, menu);
            MenuItem opcionUser = menu.findItem(R.id.blocked_icon);
            opcionUser.setVisible(false);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}

