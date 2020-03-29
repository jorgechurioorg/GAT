package com.leofanti.gat;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.util.NumberUtils;
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
import com.leofanti.gat.model.MateriaPrima;
import com.leofanti.gat.model.MateriaPrimaIn;
import com.leofanti.gat.model.MpDailySheet;
import com.leofanti.gat.model.MpLotSheet;
import com.leofanti.gat.model.ProductoTerminado;
import com.leofanti.gat.model.ProductoTerminadoOut;
import com.leofanti.gat.model.*;
import com.leofanti.gat.utils.*;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

// Singleton para la logica de negocios
public class GatTon {

    private static GatTon ourInstance;

    public static GatTon getInstance() {
        if (ourInstance == null){ //if there is no instance available... create new one
            ourInstance = new GatTon();
        }
        return ourInstance;
    }

    public void setPersistence() {
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
    }

    private GatTon() {

    }


    private String userLogged= null;
    private String userRole = null;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbProv = database.getReference("proveedores");
    private DatabaseReference dbMp = database.getReference("mp");
    private DatabaseReference dbMpIn = database.getReference("mpin");
    private DatabaseReference dbTraceProd = database.getReference("trace");
    private ArrayList<MateriaPrima> mpList = new ArrayList<>();
    private ArrayList<String> cliListDesc = new ArrayList<>();
    private ArrayList<String> provList = new ArrayList<>();
    private ArrayList<MateriaPrimaIn> mpInLista = new ArrayList<>(), mpInListaAudit = new ArrayList<>();
    private DatabaseReference dbCliente;
    private DatabaseReference dbClienteOk = database.getReference("clientes/");
    private DatabaseReference dbPtOutCliente  = database.getReference("ptout/cliente/");
    private DatabaseReference dbPtOutRemito = database.getReference("ptout/remito/");
    private DatabaseReference dbPtOutDespachado = database.getReference("ptout/shipper/");
    private DatabaseReference dbPt = database.getReference("pt");
    private DatabaseReference dbTrace = database.getReference("trace");
    private DatabaseReference dbTraceMpIn = database.getReference("trace/mpin");
    private DatabaseReference dbExIn = database.getReference("expenses");
    private DatabaseReference dbPtProducido = database.getReference("ptproducido");
    private ArrayList<Cliente> clienteList = new ArrayList<>();
    private ArrayList<String> ptListDesc = new ArrayList<>();
    private ArrayList<ProductoTerminado> ptList = new ArrayList<>();
    private ArrayList<String> mpListDesc = new ArrayList<>();
    private ArrayList<String> mpListNameProd = new ArrayList<>();
    private ArrayList<ClienteOut> ptOutMain = new ArrayList<>();
    private ArrayList<ExpRegistro> exInList = new ArrayList<>(), exInAudit = new ArrayList<>();
    private String remitoPtOutKey = null;

    private FirebaseStorage storage = FirebaseStorage.getInstance();


    private HashMap<String, String> prodTerm = new HashMap<>();

    public ArrayList<String> getMpListNameProd() {return mpListNameProd;}

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

    public void setNewCustomer( Cliente newCustomer){
        if( newCustomer.getName().isEmpty()){
            //mensaje de error diciendo que esta vacio el nombre
        }
        dbClienteOk.push().setValue(newCustomer);
    }



    // metodos de salida de producto terminado
    public String getPt( String prodCode) {
        if( prodTerm.get(prodCode) == null ) {
            return "COD NO EXISTE";
        } else {
            return prodTerm.get(prodCode);
        }
    }

    public void copyRecord(String fromPath, String toPath) {
        DatabaseReference fPath = database.getReference(fromPath);
        final DatabaseReference tPath = database.getReference(toPath);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Log.d("JCH copy", "Success!");
                        } else {
                            Log.d("JCH copy", "Copy failed!");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        fPath.addListenerForSingleValueEvent(valueEventListener);
    }



    public String getCcosto( String mpName) {
        Log.d("JCHEXREG", "Busca el centro de costos de " + mpName);

            for (MateriaPrima item : mpList) {
                if (item.getDescripcion().toUpperCase().equals(mpName.toUpperCase())) {
                    String cc = item.getCcosto();
                    String ccosto1 = (cc == null) ? "******" : cc.toUpperCase();
                    return ccosto1;
                }
            }
            return "n/d";
    }

    public ArrayList<ExpRegistro> getExpAuditList() {
        return exInAudit;
    }

    public void getExInList( Context context, final DbGetDataListener<ArrayList<ExpRegistro>> listener ) {
        listener.onStart();
        DatesHelper dh = new DatesHelper();
        String today = dh.getHoyEsHoy();
        String[] sixty= dh.dateUntil(today,-60);
        String startAtDate = sixty[1] + "-00:00:00";
        String endAtDate = sixty[0] + "23:59:59";
        dbExIn.orderByChild("timestamp").startAt(startAtDate).endAt(endAtDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                exInList.clear();
                exInAudit.clear();
                for( DataSnapshot exInSnap: dataSnapshot.getChildren()){
                    ExpRegistro exReg = exInSnap.getValue(ExpRegistro.class);
                    exReg.setThisKey( exInSnap.getKey() ) ;
                    exInAudit.add(exReg);
                    boolean hidden = exReg.getcCosto().equalsIgnoreCase("RRHH");
                    if( exReg.getVisible() && !hidden)
                        exInList.add(exReg);
                    Log.d( "JCHEXIN", "Gasto cargado " + exReg.getItemName());
                }
                listener.onSuccess(exInList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("JCHEXIN", "getExInList cancelled error :" + databaseError.toException());
                listener.onFailed(databaseError);
            }
        });
    }

    public void exInSetVisible(ExpRegistro exReg, boolean estado){
        //Cambia el estado visible a false
        ;
    }

    public void updateExpense( final ExpRegistro exReg, final String field, final String value ){
        String thisKey = exReg.getThisKey();
        dbExIn.child(thisKey).child(field).setValue(value);
    }

    public void saveExp( final ExpRegistro exReg, Bitmap bp, Context context, View view){

        Log.d("JCH", "saveExp " + exReg.getItemName());
        String urltoimage = "no_image";
        final String exInKey = dbExIn.getRef().push().getKey();
        if( bp == null) {
            //From https://gist.github.com/nglauber/5aa7ed41da8264881b7c40f5e8ab7a60
            Log.d("JCHSAVEEXP", "null image");
            dbExIn.child(exInKey).setValue(exReg);
            exInList.add(exReg);
            return;
        }
        urltoimage = "img_expenses/" + exInKey + ".jpg";
        // TODO usar esto para el download URL
        //https://code.tutsplus.com/tutorials/firebase-for-android-file-storage--cms-27376
        //final ProgressDialog progressDialog = new ProgressDialog(context);
        //progressDialog.setTitle("Guardando...");
        //progressDialog.show();

        StorageReference storageRef = storage.getReferenceFromUrl("gs://villate3165.appspot.com/");
        final StorageReference imagesRef = storageRef.child(urltoimage);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("JCH", "Saving image error no image saved");
                dbExIn.child(exInKey).setValue(exReg);
                exReg.setThumbUrl("no_image");
                exReg.setThisKey(exInKey);
                exInList.add(exReg);
                //progressDialog.dismiss();
                //todo falta devolver el key
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // getting image uri and converting into string
                        Uri downloadUrl = uri;
                        String url = downloadUrl.toString();
                        //progressDialog.dismiss();
                        exReg.setThumbUrl(url);
                        dbExIn.child(exInKey).setValue(exReg);
                        exReg.setThisKey(exInKey);
                        exInList.add(exReg);
                    }
                });
            }
        });
    }

    public String[] getCanales() {
        String[] cans = new String[outRutas.size()];
        int x = 0 ;
        for( Ruta rt: outRutas){
            cans[x++] = rt.getNombreRuta();
        }
        return cans;
    }

    private ArrayList<Ruta> outRutas = new ArrayList<>();

    public void getRemitosMain(Context context, final DbGetDataListener<ArrayList<ClienteOut>> listener) {
        //TODO cargar los casos de update, removed etc
        //TODO limitar el query a las ultimas 100 entradas (ordenadas or timestamp o key
        //DatabaseReference dbPtOutCliente  = database.getReference("ptout/cliente/");
        final DatabaseReference dbPtOutRuta = database.getReference("ptout/ruta/");
        Query query = dbPtOutCliente.orderByKey().limitToLast(100);
        listener.onStart();
        //dbPtOutCliente.addValueEventListener(new ValueEventListener() {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ptOutMain.clear();
                for (DataSnapshot mpInSnap: dataSnapshot.getChildren()) {
                    //null object exception
                    ClienteOut mpIn = mpInSnap.getValue(ClienteOut.class);
                    String key = mpInSnap.getKey();
                    mpIn.setRemitoKey( key );
                    ptOutMain.add(mpIn);
                }
                dbPtOutRuta.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        outRutas.clear();
                        for( DataSnapshot canal: dataSnapshot.getChildren() ){
                            Ruta ruta = new Ruta();
                            ruta = canal.getValue(Ruta.class);
                            outRutas.add(ruta);
                        }
                        listener.onSuccess(ptOutMain);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onFailed(databaseError);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("JCH", "getMpInList cancelled error :"+databaseError.toException());
                listener.onFailed(databaseError);
            }
        });

    }


    public ArrayList<String> getClienteList(){
        return cliListDesc;
    }

    public void fileRemito(ClienteOut clienteOut){
        String TAG = "GAT fileRemito";
        String key = clienteOut.getRemitoKey();
        String ruta = clienteOut.getRuta();
        if( key != null && ruta!= null) {
            DatabaseReference traceOut = database.getReference("trace/remitos");
            dbPtOutCliente.child(key).setValue(clienteOut);
            traceOut.setValue(key);
            dbPtOutDespachado.child(ruta).child(key).removeValue();
        } else {
            Log.e(TAG, "remito key:" + key + " ruta:" + ruta);
        }
    }


    public void shipRemito(ClienteOut clienteOut ) {
        String TAG = "GAT shipRemito";
        String key = clienteOut.getRemitoKey();
        String ruta = clienteOut.getRuta();
        if( key != null && ruta!= null) {
            dbPtOutCliente.child(key).setValue(clienteOut);
            dbPtOutDespachado.child(ruta).child(key).setValue(clienteOut);
        } else {
            Log.e(TAG, "remito key:" + key + " ruta:" + ruta);
        }
    }

    public void reShipRemito(String canalAnt, ClienteOut clienteOut ) {
        String TAG = "GAT reShipRemito";
        String key = clienteOut.getRemitoKey();
        String ruta = clienteOut.getRuta();
        if( key != null && ruta!= null) {
            dbPtOutCliente.child(key).setValue(clienteOut);
            dbPtOutDespachado.child(ruta).child(key).setValue(clienteOut);
            dbPtOutDespachado.child(canalAnt).child(key).removeValue();
        } else {
            Log.e(TAG, "remito key:" + key + " ruta:" + ruta);
        }
    }


    public String setClienteOut( ClienteOut clienteOut) {
        String remitoKey = dbPtOutCliente.push().getKey();
        remitoPtOutKey = remitoKey;
        dbPtOutCliente.child(remitoKey).setValue(clienteOut);
        return remitoKey;
    }

    public void rewriteClienteOut( ClienteOut clienteOut){
        String key = clienteOut.getRemitoKey();
        dbPtOutCliente.child(key).setValue(clienteOut);
    }

    /** Guarda el monto cobrado, ene ll key del remito original
     * en la fecha de collection y solo se hace desde la interfaz de admin
     * para los remitos delivered
     */
    public void setCollected( ClienteOut clienteOut){

    }

    public void getRemitoList( String remitoKey, Context context, final DbGetDataListener<ArrayList<ProductoTerminadoOut>> listener){
        final ArrayList<ProductoTerminadoOut> remitoList = new ArrayList<>();
        if( remitoKey != null) {
            dbPtOutRemito.child(remitoKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot provSnapshot) {
                    remitoList.clear();
                    for (DataSnapshot itemRemito: provSnapshot.getChildren()) {
                        ProductoTerminadoOut item = itemRemito.getValue(ProductoTerminadoOut.class);
                        remitoList.add(item);
                        Log.d("JCHPTOUT", "remito item loaded "+ item.getDescripcion() +"|"+item.getLote()+"|"+item.getCantidad());
                    }
                    listener.onSuccess(remitoList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("JCHPTOUT", "GATON getClienteAll error onCancelled :"+databaseError.toException());
                }
            });
        }
    }


    public void savePtOutList(String remitoKey, ArrayList<ProductoTerminadoOut> listOut){
        //TODO escribe ptOutList en remito en estado Open
        Log.d("JCHPTOUT", "guarda remito en key:" + remitoPtOutKey );
        dbPtOutRemito.child(remitoKey).setValue(listOut);
    }


    private MpDailySheet mpDailySheet = new MpDailySheet();


    public void getMpLot(Context context, final DbGetDataListener<ArrayList<MpLotSheet>> listener)  {
        listener.onStart();
        final DatabaseReference dailySheet = database.getReference("tracetest/dailysheet");
        //1) si el Daily sheet no existe lo crea
        //2) si en Daily sheet ya existe lo carga
        //Carga los ultimos dos lotes de cada MP
        //TODO getMpLot: listeners cuando cambia algun lote actualizar la lista y marcar  en el TAB para actualizarlo (o no)
        final ArrayList<MpLotSheet> listaLots = new ArrayList<>();
        dbTraceMpIn.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for( DataSnapshot lot: dataSnapshot.getChildren()) {
                    HashMap<String, String> lots;
                    MpLotSheet lotItem = new MpLotSheet();
                    String itName = lot.getKey();
                    lotItem.setMpName(itName);
                    //Chequear si esta activo este lote
                    lots = (HashMap<String, String>) lot.getValue();
                    lotItem.setLotes(lots);
                    mpDailySheet.setMpLotSheet(lotItem);
                }
                listaLots.addAll(mpDailySheet.getMpLotSheet());
                Log.d("JCHTRACEMPIN", "mpDailySheet creada :" + mpDailySheet.toString() ) ;
                //TODO funcion para guardar el daily sheet
                //dailySheet.push().setValue(mpDailySheet);
                listener.onSuccess(listaLots);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("JCH", "trace mpin cancelled error :"+databaseError.toException());
            }
        });
        //Log.d("JCHTRACE", nombreMp + "|" + loteIntKey +"|" + mpIn.getLoteInterno() +"|" + mpIn.getTimestamp());
    }

    public String getLotSheetToday(){
            String t =  mpDailySheet.getToday();
            return t.substring(4,6) + "/" + t.substring(2,4);
    }

    public void mpinSaveVisible( MateriaPrimaIn mpRec){
        String key = mpRec.getKey() + "/visible";
        dbMpIn.child(key).setValue(false);
    }

    private ArrayList<PtProducido> lista= new ArrayList<>();

    public void getHojaProd( final String fecha, final DbGetDataListener<ArrayList<PtProducido>> listener) {

        lista.clear();
        DatabaseReference hojaProd = dbPtProducido.child(fecha).getRef();

        hojaProd.addValueEventListener(new ValueEventListener() {
            private Map<String, Long> listaPos = new HashMap<>();
            @Override
            public void onDataChange(DataSnapshot hojaProdSnap) {
                lista.clear();
                if(!hojaProdSnap.exists()) {
                    Collections.sort(ptListDesc);
                    for( String nomPt: ptListDesc){
                        listaPos.put(nomPt,0L);
                        PtProducido ptProducido = new PtProducido();
                        ptProducido.setPtName(nomPt);
                        ptProducido.setQty(0L);
                        lista.add(ptProducido);
                    }
                    dbPtProducido.child(fecha).setValue(listaPos);

                } else {
                    //https://markojerkic.com/firebase-database-working-with-maps-and-lists/
                    listaPos = (Map<String,Long>) hojaProdSnap.getValue();
                    Map<String,Long> treeMap = new TreeMap<String,Long>(listaPos);
                    for (Map.Entry<String,Long> mapEntry: treeMap.entrySet()) {
                        PtProducido ptProducido = new PtProducido();
                        ptProducido.setPtName(mapEntry.getKey());
                        Long cant = mapEntry.getValue();
                        ptProducido.setQty(cant);
                        lista.add(ptProducido);
                    }

                }
                listener.onSuccess(lista);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
                Log.d("JCHPTOUT", "GATON getClienteAll error onCancelled :"+databaseError.toException());
            }
        });

    }

    public void  setProducidoMatrix( final String fd, final String fh, final DbGetDataListener<Map<String, Map<String, Long>> > listener) {

        listener.onStart();

        final Map<String, Map<String, Long>> matrix= new HashMap<>();
        final ArrayList<PtProducido> lista = new ArrayList<>();

        dbPtProducido.orderByKey().startAt(fd).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot hojaProdSnap) {
                lista.clear();
                Map<String, Long> listaPos = new HashMap<>();
                for( DataSnapshot ptpr: hojaProdSnap.getChildren()){
                    String key = ptpr.getKey();
                    listaPos = (Map<String,Long>) ptpr.getValue();
                    matrix.put(key, listaPos);
                }

               /* Map<String, Long> grouper = new HashMap<>();
                Long grandTotal = 0L;
                for (Map.Entry<String,Map<String,Long>> entry : matrix.entrySet()) {
                    Map<String, Long> value = entry.getValue();
                    String key = entry.getKey();
                    for( Map.Entry<String,Long> pt: value.entrySet()){
                        grandTotal += pt.getValue();
                        if( grouper.containsKey(pt.getKey())){
                            Long qty = grouper.get(pt.getKey()) + pt.getValue();
                            grouper.put(pt.getKey(),qty);
                        } else {
                            grouper.put(pt.getKey(), pt.getValue());
                        }
                    }
                }

                Map<String,Long> treeMap = new TreeMap<String,Long>(grouper);
                for( Map.Entry<String, Long> total: treeMap.entrySet()) {
                    PtProducido report = new PtProducido();
                    report.setPtName(total.getKey());
                    report.setQty(total.getValue());
                    lista.add(report);
                }*/
                listener.onSuccess(matrix);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
                Log.d("JCHPTOUT", "GATON getClienteAll error onCancelled :"+databaseError.toException());
            }
        });

    }



    public void saveItemProd( Integer pos, String hoyEsHoy, PtProducido ptProducido){
        String hashKey = ptProducido.getPtName();
        Long qty = ptProducido.getQty();
        dbPtProducido.child(hoyEsHoy).child(hashKey).setValue(qty);
    }

    public String getClienteAddress( String cliName){
            boolean notFound = true;
            for( Cliente cliente: clienteList){
                if( cliente.getName().equalsIgnoreCase(cliName)){
                    notFound = false;
                    String add = cliente.getAddress() + "," + cliente.getLocalidad();
                    if( add.isEmpty())
                        add = "Sin direccion";
                    return add;
                }

            }
            if( notFound){
                return "Sin direccion NF";
            }
            return "---";
    }

    public void addProveedor( String ficha){
        String clienteKey = dbProv.push().getKey();
        dbProv.child(clienteKey).setValue(ficha);
    }

     public void getReadOnlyList(final DbReadOnlyTables listener){
        listener.onStart();
        //dbCliente = database.getReference("cliente");
        dbClienteOk.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot provSnapshot) {
                clienteList.clear();
                for (DataSnapshot prov: provSnapshot.getChildren()) {
                    try {
                        Cliente cliente = prov.getValue(Cliente.class);
                        String cliName = cliente.getName();
                        cliListDesc.add(cliName);
                        clienteList.add(cliente);
                    } catch (Exception e ){
                        //Error de lectura
                    }

                }
                dbProv.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot provSnapshot) {
                        provList.clear();
                        for (DataSnapshot prov: provSnapshot.getChildren()) {
                            String mpName = prov.getValue(String.class);
                            provList.add(mpName);
                        }
                        dbPt.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot mpSnapshot) {
                                ptList.clear();
                                ptListDesc.clear();
                                for( DataSnapshot mpItem: mpSnapshot.getChildren()){
                                    ProductoTerminado mp = mpItem.getValue(ProductoTerminado.class);
                                    ptList.add(mp);
                                    ptListDesc.add(mp.getDescripcion());
                                    prodTerm.put(mpItem.getKey(), mp.getDescripcion());
                                }

                                dbMp.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot mpSnapshot) {
                                        mpList.clear();
                                        mpListDesc.clear();
                                        //https://www.captechconsulting.com/blogs/firebase-realtime-database-android-tutorial
                                        for( DataSnapshot mpItem: mpSnapshot.getChildren()){
                                            MateriaPrima mp = mpItem.getValue(MateriaPrima.class);
                                            String key = mpItem.getKey();
                                            mpList.add(mp);
                                            mpListDesc.add(mp.getDescripcion());
                                            //Log.d("JCHMPget" ,"MP Get list " + key + ":"+ mp.getDescripcion());
                                        }
                                        listener.onSuccess();
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Getting Post failed, log a message
                                        Log.d("JCH", "getMpListAll error:onCancelled", databaseError.toException());
                                        listener.onFailed("Error getting MP list");
                                    }
                                });
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed("Error getting clilist");
            }
        });
    }


     public ArrayList<ProductoTerminado> getPtListRo () {
            return ptList;
        }

    public void getMfgMpActiva() {
        //TODO version mejorada de lista de MP ingresada, usando la lista de MP Activa de MFG
        final DatabaseReference dbMfgMpActiva = database.getReference("/mfg/mpactiva");
        mpInLista.clear();
        dbMfgMpActiva.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<HashMap<String,String>> mpKeys = new ArrayList<>();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    //mpKeys.add( item.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("JCH", "getMpInList cancelled error :" + databaseError.toException());
                //listener.onFailed(databaseError);
            }
        });
    }
    public void getMpInList(Context context, final DbGetDataListener<ArrayList<MateriaPrimaIn>> listener) {
        //TODO Cambiar a la tabla de MP activa????
        listener.onStart();
        //Carga la base (lista) de ingreso de MP (poner un limite de ultimos 50 registros???)
        DatabaseReference mpMfgMpInActiva = database.getReference();
        //DatabaseReference dbMpIn2 = database.getReference("mpintest");
        DatabaseReference dbMpIn2 = database.getReference("mpin");
        Query query =  dbMpIn2.orderByKey().limitToLast(50);
        //el problema que no vcarga la lista ver: https://stackoverflow.com/questions/47219128/firebase-ondatachange-not-working
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot mpinSnapshot) {

                //https://stackoverflow.com/questions/49852777/retrieve-arraylist-from-firebase-and-display-it-in-listview
                //https://firebase.google.com/docs/database/android/lists-of-data?hl=es-419
                mpInLista.clear();
                mpInListaAudit.clear();
                for (DataSnapshot mpInSnap: mpinSnapshot.getChildren()) {
                        //null object exception
                        String snapKey = mpInSnap.getKey();
                        MateriaPrimaIn mpIn = mpInSnap.getValue(MateriaPrimaIn.class);
                        mpIn.setKey(snapKey);
                        mpInListaAudit.add(mpIn);
                        if( mpIn.getVisible() ) {
                            mpInLista.add(mpIn);
                        }
                        //Normaliza la tracelist
                        //setMpInTrace(mpIn, snapKey);
                        //Normaliza el timestamp
                        /*if (mpIn.getTimestamp().contains("/")) {
                            String ts = mpIn.getTimestamp();
                            String newTimestamp = ts.substring(8,10) + ts.substring(3,5) + ts.substring(0,2);
                            DatabaseReference dbMpInTout = dbMpIn.child(snapKey);
                            mpIn.setTimestamp(newTimestamp);
                            dbMpInTout.getRef().setValue(mpIn);
                            Log.d("JCHMPIN", "Normaliza timestamp " + mpIn.getTimestamp() + " to " + newTimestamp );
                        }
                        if( mpIn.timestamp.startsWith("19")) {
                            String ts = mpIn.getTimestamp();
                            int largo = ts.length();
                            String newTimestamp = "20" + ts;
                            DatabaseReference dbMpInN2 = dbMpIn.child(snapKey);
                            mpIn.setTimestamp(newTimestamp);
                            dbMpInN2.getRef().setValue(mpIn);
                            Log.d("JCHMPIN", "Normaliza timestamp " + mpIn.getTimestamp() + " to " + newTimestamp );
                        }*/
                    }
                    Log.d("JCHMPIN", "GATON getMpInList datasnapshot read");
                    listener.onSuccess(mpInLista);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("JCH", "getMpInList cancelled error :"+databaseError.toException());
                    listener.onFailed(databaseError);
                }
            });

    }



    public void saveMp(final MateriaPrimaIn mp, Bitmap img, Context context){

        //TODO ahora la MP debe editar la be de dato de MP Activa que es la que cuenta
        //Log.d("JCH", "saveMp " + mp.getNombreMp());
        String urltoimage = "no_image";
        final String key = dbMpIn.push().getKey();
        mp.setKey(key);
        setMpInTrace(mp, key);
        setMfgMpActiva(mp,key);
        if( img == null) {
            //From https://gist.github.com/nglauber/5aa7ed41da8264881b7c40f5e8ab7a60
            Log.d("JCH", "saveMp null image");
            //TODO descomentar esto para produccion
            dbMpIn.child(key).setValue(mp);
            mpInLista.add(mp);
            mpInListaAudit.add(mp);
            return;
        }
        urltoimage = "img_rotulos/" + key + ".jpg";
        // TODO usar esto para el download URL
        //https://code.tutsplus.com/tutorials/firebase-for-android-file-storage--cms-27376
        //final ProgressDialog progressDialog = new ProgressDialog(context);
        //progressDialog.setTitle("Guardando...");
        //progressDialog.show();

        StorageReference storageRef = storage.getReferenceFromUrl("gs://villate3165.appspot.com/");
        final StorageReference imagesRef = storageRef.child(urltoimage);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("JCH", "Saving image error no image saved");
                //TODO descomentar esto para produccion
                dbMpIn.child(key).setValue(mp);
                mp.setThumbUrl("no_image_ERROR");
                mpInLista.add(mp);
                mpInListaAudit.add(mp);
                //progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // getting image uri and converting into string
                        Uri downloadUrl = uri;
                        String url = downloadUrl.toString();
                        //progressDialog.dismiss();
                        mp.setThumbUrl(url);
                        //TODO descomentar esto para produccion
                        dbMpIn.child(key).setValue(mp);
                        mpInLista.add(mp);
                        mpInListaAudit.add(mp);
                    }
                });
            }
        });
    }



    private void setMfgMpActiva( final MateriaPrimaIn mpIn, String key){
        final DatabaseReference dbMfgMpActiva = database.getReference("/mfg/mpactiva"+mpIn.getNombreMp());
        dbMfgMpActiva.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot item:dataSnapshot.getChildren()) {
                    TreeMap<String, String> mpEntry = new TreeMap<>();
                    mpEntry = (TreeMap<String, String>) item.getValue();
                    if (mpEntry.size() >= 2) {
                        mpEntry.remove(mpEntry.firstKey());
                    }
                    mpEntry.put(mpIn.getTimestampChopped(), mpIn.getKey());
                    dbMfgMpActiva.setValue(mpEntry);
                }
                //TODO Notificar al RV de mpin
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void setMpInTrace( MateriaPrimaIn mpIn, String key){
        String nombreMp = mpIn.getNombreMp().toUpperCase();
        String loteIntKey = mpIn.getTimestampChopped();
        dbTraceProd.child("/mpin/" + nombreMp).child(loteIntKey).getRef().setValue(key);
        //Log.d("JCHTRACE", nombreMp + "|" + loteIntKey +"|" + mpIn.getLoteInterno() +"|" + mpIn.getTimestamp());
    }

    public void getMpInTrace(String mpName, final int cant, final DbGetDataListener<ArrayList<TraceLinkedList>> listener ){
        //final ArrayList<MateriaPrimaIn> gotList = new ArrayList<MateriaPrimaIn>();
        final ArrayList<TraceLinkedList> traceList = new ArrayList<>();
        Query  query ;
        listener.onStart();
        if( !mpName.equalsIgnoreCase("all")){
            query = dbTraceMpIn.orderByKey().equalTo(mpName);
        }else{
            query = dbTraceMpIn.orderByKey();
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    for( DataSnapshot rec: dataSnapshot.getChildren() ) {
                        String mpName = rec.getKey();
                        Map<String, String> lotList = new HashMap<>();
                        lotList = (HashMap<String,String>) rec.getValue();
                        TreeMap<String, String> treeMap = new TreeMap<String, String>(Collections.reverseOrder());
                        treeMap.putAll(lotList);
                        int ix = 0;
                        for(Map.Entry<String,String> entry : treeMap.entrySet()) {
                            if( ix++ < cant || cant == 999 ) {
                                TraceLinkedList recList = new TraceLinkedList();
                                recList.setItem(mpName, entry.getKey(), entry.getValue());
                                traceList.add(recList);
                            } else {
                                break;
                            }
                        }

                    }

                listener.onSuccess(traceList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }


    public ArrayList<String> getProvList(){
        return provList;
    }

    public ArrayList<String> getMpList(){
        return mpListDesc;
    }

    public String getMpUnidad( String descripcion){
        for( MateriaPrima item: mpList){
            if (item.getDescripcion().equals(descripcion)) {
                return item.getUnidad();
            }
        }
        return "**";
    }

    public void readGroupingDb( final DbGetDataListener<HashMap<String,String>> listener) {
        final HashMap<String,String> gr = new HashMap<>();
        listener.onStart();
        DatabaseReference dbGroup = database.getReference("eerr/grouping");
        dbGroup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot grp: dataSnapshot.getChildren()){
                    String key = grp.getKey();
                    String value = grp.getValue(String.class);
                    gr.put(key,value);
                }
                listener.onSuccess(gr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("JCH", "getMpInList cancelled error :"+databaseError.toException());
                listener.onFailed(databaseError);
            }
        });
    }

}



