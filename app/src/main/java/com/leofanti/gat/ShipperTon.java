package com.leofanti.gat;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leofanti.gat.model.Cliente;
import com.leofanti.gat.model.ClienteOut;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.ExpRegistro;
import com.leofanti.gat.model.MateriaPrima;
import com.leofanti.gat.model.MateriaPrimaIn;
import com.leofanti.gat.model.MpDailySheet;
import com.leofanti.gat.model.MpLotSheet;
import com.leofanti.gat.model.ProductoTerminado;
import com.leofanti.gat.model.ProductoTerminadoOut;
import com.leofanti.gat.model.PtProducido;
import com.leofanti.gat.model.Ruta;
import com.leofanti.gat.utils.DbGetDataListener;
import com.leofanti.gat.utils.DbReadOnlyTables;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

// Singleton para la logica de negocios en modo shipper
public class ShipperTon {

    private static ShipperTon ourInstance;

    public static ShipperTon getInstance() {
        if (ourInstance == null){ //if there is no instance available... create new one
            ourInstance = new ShipperTon();
        }
        return ourInstance;
    }

    public void setPersistence() {
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
    }

    private ShipperTon() {

    }


    private String userLogged= null;
    private String userRole = null;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbPtOutShipper = database.getReference("ptout/shipper/");
    private DatabaseReference dbPtOutCliente  = database.getReference("ptout/cliente/");
    private DatabaseReference dbPtOutRemito = database.getReference("ptout/remito/");
    private DatabaseReference dbPt = database.getReference("pt");
    private ArrayList<ClienteOut> ptOutMain = new ArrayList<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private final static String TAG = "shipperTon";



    private ArrayList<ProductoTerminado> ptList = new ArrayList<>();

    private ArrayList<String> cliListDesc = new ArrayList<>();

    private DatabaseReference dbCliente;

    private ArrayList<Cliente> clienteList = new ArrayList<>();
    private ArrayList<String> ptListDesc = new ArrayList<>();


    private ArrayList<ExpRegistro> exInList = new ArrayList<>(), exInAudit = new ArrayList<>();
    private HashMap<String, String> prodTerm = new HashMap<>();

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

    // metodos de salida de producto terminado
    public String getPt( String prodCode) {
        if( prodTerm.get(prodCode) == null ) {
            return "COD NO EXISTE";
        } else {
            return prodTerm.get(prodCode);
        }
    }

    /**guarda los datos de la entrega al cliente, el monto y la foto del remito
     *
     */
    public void delivered(final ClienteOut clienteOut, Bitmap bp, Context context, View view){
        //TODO shipper.delivered: definir como manejar los rgistros de salida de producto, si se vuelven a copiar o se sacan del canal.
        clienteOut.setShipImageUrl(Const.IMAGENOIMAGE);
        final String remitoKey = clienteOut.getRemitoKey();
        if( bp == null) {
            Log.d(TAG, "shipperTon delivered: null image");
            saveDeliveredRecord(clienteOut);
            return;
        }

        //https://code.tutsplus.com/tutorials/firebase-for-android-file-storage--cms-27376
        //final ProgressDialog progressDialog = new ProgressDialog(context);
        //progressDialog.setTitle("Guardando...");
        //progressDialog.show();
        String urltoimage = "img_shipper/" + remitoKey + ".jpg";
        StorageReference storageRef = storage.getReferenceFromUrl("gs://villate3165.appspot.com/");
        final StorageReference imagesRef = storageRef.child(urltoimage);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "shipperTon delivered Saving image error no image saved");
                    clienteOut.setShipImageUrl(Const.IMAGESETERROR);
                    saveDeliveredRecord(clienteOut);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        String url = downloadUrl.toString();
                        clienteOut.setShipImageUrl(url);
                        saveDeliveredRecord(clienteOut);
                    }
                });
            }
        });
    }

    private void saveDeliveredRecord(ClienteOut clienteOut){
        //TODO guardo en clientes o en shipper o en los dos???
    }


    public void setRemitosAllList( Map<String,Map<String, ProductoTerminadoOut>> remitosAll){


    }

    public void getRemitosAll(Context context, final DbGetDataListener<Map<String, Map<String, ProductoTerminadoOut>> > listener) {
        listener.onStart();
        final Map<String, Map<String, ProductoTerminadoOut>> shipperListAll= new HashMap<>();
        dbPtOutShipper.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for( DataSnapshot shipperList:dataSnapshot.getChildren()){

                    }
                    listener.onSuccess(shipperListAll);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onFailed(databaseError);
                }
            });

    }
    public void getRemitosMain(Context context, final DbGetDataListener<ArrayList<ClienteOut>> listener) {
        //final DatabaseReference dbPtOutShipper = database.getReference("ptout/shipper/" + userLogged );
        listener.onStart();
        /*esta es la version para cargar las vebtas del dia (todos los shippers)*/

        /* esta es la version que aplica para cada usuario
        //filtrar por estado OPEN
        dbPtOutShipper.child(userLogged).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ptOutMain.clear();
                for (DataSnapshot remitoToShip : dataSnapshot.getChildren()) {
                    ClienteOut clO = remitoToShip.getValue(ClienteOut.class);
                    clO.setRemitoKey(remitoToShip.getKey());
                    ptOutMain.add(clO);
                }
                listener.onSuccess(ptOutMain);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("JCH", "getMpInList cancelled error :"+databaseError.toException());
                listener.onFailed(databaseError);
            }
        });*/

    }

    public ClienteOut getClienteOut( String remitoKey){
        for( ClienteOut clout: ptOutMain){
            if( remitoKey.equals(clout.getRemitoKey())){
                return clout;
            }
        }
        return null;
    }

    public ArrayList<String> getClienteList(){
        return cliListDesc;
    }




    public void rewriteClienteOut( ClienteOut clienteOut) {
        String path = clienteOut.getRemitoKey();
        dbPtOutCliente.child(path).setValue(clienteOut);
    }


    public void updateRemitoStatus( ClienteOut clienteOut, String status) {
        String path = clienteOut.getRemitoKey() + "/status";
        dbPtOutCliente.child(path).setValue(status.toUpperCase());
    }




    //TODO por el momento solo lee la lista de clientes OJO que lo lee a todos
    //Y la lista de prodcuto terminado
    // ¿lee los remitos despachados del usuari logeado (en la version final)
    // ahora lee tods lso remitos abiertos
     public void getReadOnlyList(final DbReadOnlyTables listener) {
         listener.onStart();
         dbCliente = database.getReference("cliente");
         dbCliente.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot provSnapshot) {
                 for (DataSnapshot prov : provSnapshot.getChildren()) {
                     String cliName = prov.getValue(String.class);
                     cliListDesc.add(cliName);
                 }
                 dbPt.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot mpSnapshot) {
                         for (DataSnapshot mpItem : mpSnapshot.getChildren()) {
                             ProductoTerminado mp = mpItem.getValue(ProductoTerminado.class);
                             ptList.add(mp);
                             ptListDesc.add(mp.getDescripcion());
                             prodTerm.put(mpItem.getKey(), mp.getDescripcion());
                         }
                         listener.onSuccess();
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {
                         listener.onFailed("Error getting Pt");
                     }
                 });
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
                 listener.onFailed("Error getting Provlist");
             }
         });
     }

}



