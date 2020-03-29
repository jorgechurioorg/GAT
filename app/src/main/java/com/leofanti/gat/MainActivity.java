package com.leofanti.gat;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.leofanti.gat.model.MateriaPrimaIn;
import com.leofanti.gat.utils.AdminTools;
import com.leofanti.gat.utils.DbReadOnlyTables;
import com.leofanti.gat.utils.ExpenseAuditor;
import com.leofanti.gat.utils.ExpenseInForm;
import com.leofanti.gat.utils.MpInAuditor;
import com.leofanti.gat.utils.SalesTon;
import com.leofanti.gat.utils.configHelper;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    PagerAdapter adapter;
    Integer tabPos = 0;
    Context context;

    final int MPINTAB = 1 ;
    final int EXINTAB = 0 ;
    final int PLOTTAB = 2 ;
    final int MPOUTTAB = 3 ;


    GatTon gatTon = GatTon.getInstance();

    //https://stackoverflow.com/questions/9132027/very-simple-code-but-got-error-activity-has-been-destroyed-when-use-fragment
    static FragmentManager fragmentManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String userNick = intent.getStringExtra("username");
        String userRole = intent.getStringExtra("role");
        gatTon.setUserLogged(userNick);
        gatTon.setUserRole(userRole);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setLogo(R.drawable.user_circle);
        setSupportActionBar(toolbar);
        TextView usuario = (TextView) findViewById(R.id.main_usuario);
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
        getSupportActionBar().setTitle(null);

        //Copia una coleccion en otro child
        //gatTon.copyRecord("expensetest", "expenses");

        //http://codetheory.in/difference-between-setdisplayhomeasupenabled-sethomebuttonenabled-and-setdisplayshowhomeenabled/
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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
                //TODO ver toolbar contextual
                switch( tabPos ) {
                    case MPINTAB:

                        break;
                    case EXINTAB:
                        if( gatTon.getUserRole().equalsIgnoreCase("AUDIT")){
                            if( auditIcon != null ){
                                auditIcon.setVisible(false);
                            }
                        }
                        break;
                    case PLOTTAB:

                        break;
                    case MPOUTTAB:

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
    MenuItem mfgIcon;
    MenuItem printIcon;
    MenuItem setupIcon;
    MenuItem collectIcon;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_admin, menu);
        MenuItem opcionUser = menu.findItem(R.id.blocked_icon);
        auditIcon = menu.findItem(R.id.menuadmin_audit);
        printIcon = menu.findItem(R.id.menuadmin_labels);
        mfgIcon = menu.findItem(R.id.menuadmin_mfg);
        setupIcon = menu.findItem(R.id.menuadmin_setup);
        collectIcon = menu.findItem(R.id.menuadmin_collect);
        printIcon.setVisible(true);
        mfgIcon.setVisible(false);
        auditIcon.setVisible(false);
        setupIcon.setVisible(false);
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
                    mfgIcon.setVisible(true);
                    setupIcon.setVisible(true); //sacarlo de aca
                    break;

                case "ROOT":
                    auditIcon.setVisible(true);
                    mfgIcon.setVisible(true);
                    collectIcon.setVisible(true);
                    setupIcon.setVisible(true);
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch( id) {
            case R.id.menuadmin_audit:
                switch( tabPos) {
                    case EXINTAB:

                        if( gatTon.getUserRole().equalsIgnoreCase("ADMIN") || gatTon.getUserRole().equalsIgnoreCase("ROOT")) {
                            ExpenseAuditor dialog = new ExpenseAuditor();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
                            dialog.show(ft, "EXPENSEAUDITOR");
                        }
                        break;
                    case MPINTAB:
                        MpInAuditor mpDialog = new MpInAuditor();
                        FragmentTransaction ft2 = fragmentManager.beginTransaction();
                        mpDialog.show(ft2, "MPINAUDITOR");
                        break;

                    case PLOTTAB:
                        HojaProdAuditor hojaProdAuditor = new HojaProdAuditor();
                        FragmentTransaction ft3 = fragmentManager.beginTransaction();
                        hojaProdAuditor.show(ft3, "HOJAPRODAUDIT") ;
                        break;
                }
                break;


            case R.id.menuadmin_labels:
                //Intent intentLabel = new Intent(MainActivity.this, labelPrint.class);
                //startActivity(intentlabel);
                break;

            case R.id.menuadmin_collect:
                Intent intentCollect = new Intent( MainActivity.this, MainActivityCollect.class);
                intentCollect.putExtra("username", gatTon.getUserLogged());
                intentCollect.putExtra("role", gatTon.getUserRole() );
                startActivity(intentCollect);
                break;

            case R.id.menuadmin_setup:
                Intent intentSetup = new Intent( MainActivity.this, MainActivitySetup.class);
                intentSetup.putExtra("username", gatTon.getUserLogged());
                intentSetup.putExtra("role", gatTon.getUserRole() );
                startActivity(intentSetup);
                break;

            case R.id.menuadmin_mfg:
                if(configHelper.isTablet(this)){
                Intent intentMfg = new Intent( MainActivity.this, MainActivityMfg.class);
                intentMfg.putExtra("username", gatTon.getUserLogged());
                intentMfg.putExtra("role", gatTon.getUserRole() );
                startActivity(intentMfg);
                } else {
                    Log.d("JCH Device type", "not tablet or desktop") ;
                    //Snackbar.make(View, "MFG Solo corre en tablet", Snackbar.LENGTH_SHORT).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void callExpenseInForm( MateriaPrimaIn mpIn){

        ExpenseInForm expDialog = new ExpenseInForm();

        Bundle bundle = new Bundle();
        bundle.putString("mpName", mpIn.getNombreMp());
        bundle.putString("provName", mpIn.getProveedor());
        bundle.putBoolean("fromMpIn", true );
        expDialog.setArguments(bundle);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        expDialog.show(ft, "ExpenseInForm" );

    }
}

