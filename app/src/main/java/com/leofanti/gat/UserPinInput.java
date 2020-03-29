package com.leofanti.gat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.adapters.PtProdRecyclerView;
import com.leofanti.gat.adapters.UserPinRecyclerViewAdapter;
import com.leofanti.gat.model.ClienteOut;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.PtProducido;
import com.leofanti.gat.model.UserPin;
import com.leofanti.gat.utils.DbGetDataListener;
import com.leofanti.gat.utils.RecyclerViewGestureListener;
import com.leofanti.gat.utils.RecyclerViewTouchListener;
import com.leofanti.gat.utils.Users;
import com.leofanti.gat.utils.configHelper;

import java.util.ArrayList;

/**
 * A login screen that offers login via pin and avatar per user
 */
public class UserPinInput extends AppCompatActivity  {

    //Muestra la lista de avatars como un grid
    //al seleccionar un usuario se presenta un dialog para ingresar el pin numerico

    // cargar  lista de usuarios

    private static Users users = Users.getInstance();
    private GridLayoutManager usersGrid;
    private ArrayList<UserPin> usersList = new ArrayList();
    private String installMode = null;
    private int startup_mode = Const.MODE_GRAL;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.card_pin_login);

        pref = getApplicationContext().getSharedPreferences("LEOERP", MODE_PRIVATE); // 0 - for private mode
        editor = pref.edit();


        Toolbar toolbar = (Toolbar) findViewById(R.id.cardpin_toolbar);
        //toolbar.setLogo(R.drawable.user_circle);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("INGRESAR");


        usersGrid = new GridLayoutManager(UserPinInput.this, 2 , RecyclerView.VERTICAL, false );

        final RecyclerView rView = (RecyclerView)findViewById(R.id.userpingrid);

        rView.setLayoutManager(usersGrid);


        users.setUsersList( installMode, new DbGetDataListener<ArrayList<UserPin>>()  {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess( final ArrayList<UserPin> usersList){
                UserPinRecyclerViewAdapter rcAdapter = new UserPinRecyclerViewAdapter(UserPinInput.this, usersList);
                rView.setAdapter(rcAdapter);
                rView.addOnItemTouchListener(new RecyclerViewGestureListener(UserPinInput.this, rView, new RecyclerViewTouchListener() {
                    @Override
                    public void onClick(View view, int position) {
                        //TODO validar al usuario con pin
                        PinInput pinInput =PinInput.newInstance(usersList.get(position));
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        pinInput.show(ft, "PIN INPUT" );
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

    public static  int setMfgMode(SharedPreferences pref, SharedPreferences.Editor editor){
        return Const.MODE_MFG;
        //la otra ocpion es: MODE_GRAL
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("JCH Activity", "return from activity");
    }


    MenuItem auditIcon;
    MenuItem mfgIcon;
    MenuItem printIcon;
    MenuItem setupIcon;
    MenuItem collectIcon;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_admin, menu);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuadmin_labels:
                Intent intentLabel = new Intent(UserPinInput.this, LabelActivity.class);
                startActivity(intentLabel);
                break;

        }
        return true;
    }

    public static class PinInput extends DialogFragment {

        public PinInput() {

        }

        public static PinInput newInstance(UserPin userPin) {
            PinInput frag = new PinInput();
            Bundle args = new Bundle();
            args.putString("nick", userPin.getNick());
            args.putString("role", userPin.getRole());
            args.putString("thisKey", userPin.getThisKey());
            args.putString("pin", userPin.getPin());
            frag.setArguments(args);
            return frag;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
        }


        String pinInput;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            final View view = inflater.inflate(R.layout.activity_user_login, container, false);

            final String nick = getArguments().getString("nick", "none");
            final String pin = getArguments().getString("pin");
            final String role = getArguments().getString("role", "user");

            Toolbar inputPinToolbar = (Toolbar) view.findViewById(R.id.userpininput_toolbar);
            inputPinToolbar.setTitle(nick.toUpperCase());
            inputPinToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            TextView userNick = view.findViewById(R.id.userpininput_username);
            userNick.setText("INGRESAR PIN");
            final EditText inputPinField = (EditText) view.findViewById(R.id.userpininput_inputdigits) ;
            final ImageView i1 = (ImageView) view.findViewById(R.id.userpininput_digit1);
            final ImageView i2 = (ImageView) view.findViewById(R.id.userpininput_digit2);
            final ImageView i3 = (ImageView) view.findViewById(R.id.userpininput_digit3);
            final ImageView i4 = (ImageView) view.findViewById(R.id.userpininput_digit4);


            inputPinField.requestFocus();
            inputPinField.postDelayed(new Runnable() {
                @Override
                public void run() {

                    InputMethodManager keyboard = (InputMethodManager) getDialog().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(inputPinField, 0);
                }
            }, 0);
            inputPinField.setInputType(InputType.TYPE_CLASS_NUMBER);
            inputPinField.setFocusableInTouchMode(true);

            inputPinField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if( pinInput.equalsIgnoreCase(pin)) {
                        inputPinField.getText().clear();
                        int startupMod = Const.MODE_GRAL;
                        /*if (role.equalsIgnoreCase("CANAL")) {
                            intent = new Intent(getContext(), MainActivityCanal.class);
                        } else {
                            intent = new Intent(getContext(), MainActivity.class);
                        }*/
                        if(configHelper.isTablet(getContext())) {
                            if (!pref.contains("tablet_startup_mode")) {
                                startupMod = setMfgMode(pref, editor);
                            } else {
                                startupMod = pref.getInt("tablet_startup_mode", Const.MODE_GRAL);
                            }
                        }
                        Intent intent = new Intent();
                        switch( startupMod) {

                                case Const.MODE_MFG:
                                    intent = new Intent(getContext(), MainActivityMfg.class);
                                    break;
                                default:
                                    intent = new Intent(getContext(), MainActivity.class);
                                    break;
                        }
                        intent.putExtra("username", nick);
                        intent.putExtra("role", role );
                        dismiss();
                        startActivityForResult(intent, 1001);
                    } else {
                        Snackbar.make(view, "Clave Incorrecta " + pinInput, Snackbar.LENGTH_SHORT).show();
                        i4.setImageResource(R.drawable.circle_empty);
                        i4.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        i3.setImageResource(R.drawable.circle_empty);
                        i3.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        i2.setImageResource(R.drawable.circle_empty);
                        i2.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        i1.setImageResource(R.drawable.circle_empty);
                        i1.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                        inputPinField.getText().clear();
                        inputPinField.requestFocus();
                        inputPinField.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager keyboard = (InputMethodManager) getDialog().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                keyboard.showSoftInput(inputPinField, 0);
                            }
                        }, 0);
                    }
                    return false;
                }
            });
            inputPinField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    switch (s.length()) {
                        case 4:
                            i4.setImageResource(R.drawable.circle_white);
                            i4.setColorFilter(getResources().getColor(R.color.colorAccent));
                            pinInput = s.toString();
                            break;
                        case 3:
                            i4.setImageResource(R.drawable.circle_empty);
                            i3.setImageResource(R.drawable.circle_white);
                            i3.setColorFilter(getResources().getColor(R.color.colorAccent));
                            break;
                        case 2:
                            i3.setImageResource(R.drawable.circle_empty);
                            i2.setImageResource(R.drawable.circle_white);
                            i2.setColorFilter(getResources().getColor(R.color.colorAccent));
                            break;
                        case 1:
                            i2.setImageResource(R.drawable.circle_empty);
                            i1.setImageResource(R.drawable.circle_white);
                            i1.setColorFilter(getResources().getColor(R.color.colorAccent));
                            break;
                        default:
                            i1.setImageResource(R.drawable.circle_empty);
                    }
                }
            });
            return view;
        }

        @Override
        public void onStart() {
            super.onStart();
            Dialog d = getDialog();
            if (d!=null){
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                //d.getWindow().setLayout(width, height);
            }
        }
    }
}

