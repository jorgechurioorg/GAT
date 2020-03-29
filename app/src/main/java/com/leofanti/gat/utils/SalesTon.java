package com.leofanti.gat.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.leofanti.gat.model.Cliente;
import com.leofanti.gat.model.ClienteOut;
import com.leofanti.gat.model.CollectRegistro;

import java.util.ArrayList;
import java.util.Map;

// singleton para ventas, distribucion
public class SalesTon {
    private static SalesTon ourInstance;

    public static SalesTon getInstance() {

        if (ourInstance == null){
            ourInstance = new SalesTon();
        }
        return ourInstance;
    }

    private SalesTon() {
    }

    private String userLogged= null;
    private String userRole = null;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbCollections = database.getReference("sales/main/collections");
    private DatabaseReference dbCollectionsTrace = database.getReference("sales/trace/collections");
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private final static String TAG = "JCH SalesTon";
    private DatabaseReference dbClienteOk = database.getReference("clientes/");
    private ArrayList<String> cliListDesc = new ArrayList<>(); //para el autocomlpete textview
    private ArrayList<Cliente> clienteList = new ArrayList<>();
    ArrayList<CollectRegistro> collectList = new ArrayList<>();


    public void getCustomerCollect( String cliente, final DbGetDataListener<Float> listener  ) {
        //TODO limitar la busqueda???? ahora barre todo el cliente
        listener.onStart();

        dbCollections.orderByChild("cliente").equalTo(cliente).addListenerForSingleValueEvent  (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Float saldoCheck = 0.0f;
                for (DataSnapshot collRec : dataSnapshot.getChildren()) {
                    CollectRegistro cll =  new CollectRegistro();
                    cll = collRec.getValue(CollectRegistro.class);
                    saldoCheck += -cll.getMontoFact() + cll.getPago() + cll.getCheque();
                }
                listener.onSuccess(saldoCheck);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    public void setUserLogged(String userLogged) {
        this.userLogged = userLogged;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getUserLogged() { return this.userLogged;}

    public void getClienteList (final DbGetDataListener<ArrayList<String>> listener) {
        listener.onStart();
        dbClienteOk.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot provSnapshot) {
                clienteList.clear();
                cliListDesc.clear();
                for (DataSnapshot prov : provSnapshot.getChildren()) {
                    try {
                        Cliente cliente = prov.getValue(Cliente.class);
                        String cliName = cliente.getName();
                        cliListDesc.add(cliName);
                        clienteList.add(cliente);
                    } catch (Exception e) {
                        //Error de lectura
                    }

                }
                listener.onSuccess(cliListDesc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    public void saveCollection(CollectRegistro collectRec){
        String collKey = dbCollections.push().getKey();

        dbCollections.child(collKey).setValue(collectRec);
        dbCollectionsTrace.child(collectRec.getCliente()).push().setValue(collKey);

    }

    public void getCollections( String fromDate, String toDate, final DbGetDataListener<ArrayList<CollectRegistro>> listener ){

        listener.onStart();

        dbCollections.startAt(fromDate).endAt(toDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(collectList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }                                                               }
        );
    }
}
