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
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.leofanti.gat.utils.AdminTools;
import com.leofanti.gat.utils.DbReadOnlyTables;
import com.leofanti.gat.utils.ExpenseAuditor;
import com.leofanti.gat.utils.MpInAuditor;


public class MainActivitySetup extends AppCompatActivity {

    ViewPager viewPager;
    PagerAdapterSetup adapter;
    Integer tabPos = 0;
    Context context;


    final int PRINTAB = 0 ;
    final int RECETAB = 1 ;
    final int USERTAB = 2 ;


    GatTon gatTon = GatTon.getInstance();

    //https://stackoverflow.com/questions/9132027/very-simple-code-but-got-error-activity-has-been-destroyed-when-use-fragment
    static FragmentManager fragmentManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_setup);
        Intent intent = getIntent();
        String userNick = intent.getStringExtra("username");
        String userRole = intent.getStringExtra("role");
        gatTon.setUserLogged(userNick);
        gatTon.setUserRole(userRole);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainsetup_toolbar);
        toolbar.setLogo(R.drawable.user_circle);
        setSupportActionBar(toolbar);
        TextView usuario = (TextView) findViewById(R.id.mainsetup_usuario);
        usuario.setText(userNick);

        context = this;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        gatTon.getReadOnlyList(new DbReadOnlyTables(){
            @Override
            public void onStart() {
                //progressDialog.setTitle("Inicializando");
                //progressDialog.show();
            }
            public void onSuccess() {
                //if (progressDialog != null && progressDialog.isShowing()) {
                //    progressDialog.dismiss();
                //}
            }
            public void onFailed( String error){
                Log.d("JCH", "Error de getReadOnly " + error);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //Snackbar.make(, error, Snackbar.LENGTH_SHORT).show();
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
                        Intent returnIntent = new Intent();
                        setResult(UserPinInput.RESULT_OK,returnIntent);
                        finish();
                    }
                });
                builder.create().show();

            }
        });
        getSupportActionBar().setTitle("MASTER SETUP");

        //Copia una coleccion en otro child
        //gatTon.copyRecord("expensetest", "expenses");

        //http://codetheory.in/difference-between-setdisplayhomeasupenabled-sethomebuttonenabled-and-setdisplayshowhomeenabled/
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsetup_layout);
        viewPager = (ViewPager) findViewById(R.id.mainsetup_pager);
        adapter = new PagerAdapterSetup(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if( auditIcon != null ){
                    auditIcon.setVisible(true);
                }
                tabPos = tab.getPosition();
                switch( tabPos ) {
                    case PRINTAB:
                        break;
                    case RECETAB:
                        //TODO populate rece list
                        break;
                    case USERTAB:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    MenuItem auditIcon;
    MenuItem collectIcon;
    MenuItem abmUserIcon;
    MenuItem printIcon;
    MenuItem mfgIcon;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.menu_main_admin, menu);
        MenuItem opcionUser = menu.findItem(R.id.blocked_icon);
        abmUserIcon = menu.findItem(R.id.menuadmin_nuevocli);
        collectIcon = menu.findItem(R.id.menuadmin_collect);
        auditIcon = menu.findItem(R.id.menuadmin_audit);
        printIcon = menu.findItem(R.id.menuadmin_labels);
        mfgIcon = menu.findItem(R.id.menuadmin_mfg);
        abmUserIcon.setVisible(false);
        auditIcon.setVisible(false);
        printIcon.setVisible(true);
        mfgIcon.setVisible(false);
        collectIcon.setVisible(false);

        String role = gatTon.getUserRole().toUpperCase();
        if( role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROOT")||role.equalsIgnoreCase("AUDIT")) {
            //getMenuInflater().inflate(R.menu.menu_main_admin, menu);

            switch (role) {
                case "AUDIT":
                    auditIcon.setVisible(false);
                    break;
                case "ADMIN":
                    auditIcon.setVisible(true);
                    collectIcon.setVisible(true);
                case "ROOT":
                    auditIcon.setVisible(true);
                    abmUserIcon.setVisible(true);
                    mfgIcon.setVisible(true);
                    collectIcon.setVisible(true);
                    break;
            }
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


}

