package com.leofanti.gat;

import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.utils.configHelper;


public class MainLauncher extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button  btnLogin, btnReset;


    private int LOGINPIN = 1001;
    private int AUTHAPP  = 2001;
    //https://www.tutorialspoint.com/java/java_using_singleton.htm


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO Set Firebase local persistence
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();



        //PRIMERA AUTHENTICATION ES DE LA APP EN FIREBASE
        if (auth.getCurrentUser() != null) {
            //TODO Si es una tablet o desktop dar la opcion de entrar en modo mfg directo (y recordar la opcion)


            //TODO determinar el customer claim????
            Intent intentUser = new Intent(MainLauncher.this, UserPinInput.class);
            startActivity(intentUser);
            finish();
        }

        setContentView(R.layout.activity_app_login);
        final View logScreenView = findViewById(R.id.app_log_screen);

        //TODO setup inicial: aca se debe configurar el tipo de instalacion
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO reset del password de app
                Snackbar.make(logScreenView, "La clave solo la cambia el admin", Snackbar.LENGTH_LONG).show();

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(logScreenView, "Enter email address!", Snackbar.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Snackbar.make(logScreenView, "Enter password!", Snackbar.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)

                        .addOnCompleteListener(MainLauncher.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult>  task) {
                                Log.d("JCH", "signed ok ");
                                //public void onComplete(Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError("password minimo de seis caracteres!!");
                                    } else {
                                        Snackbar.make(logScreenView, "Error de autenticacion", Snackbar.LENGTH_LONG).show();
                                        //Toast.makeText(MainLauncher.this, "No se pudo autenticar!", Toast.LENGTH_LONG).show();
                                        Log.d("JCH", "auth error ");
                                    }
                                } else {
                                    //TODO conseguir el customer claim?????
                                    Intent intentUser = new Intent(MainLauncher.this, UserPinInput.class);
                                    startActivity(intentUser);
                                    finish();
                                }
                            }
                        });
            }
        });


    }

}