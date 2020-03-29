package com.leofanti.gat.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.leofanti.gat.model.Cliente;
import com.leofanti.gat.model.CollectRegistro;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.MateriaPrima;
import com.leofanti.gat.model.MfgItem;
import com.leofanti.gat.model.Rece;
import com.leofanti.gat.model.SeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

// singleton para fabricacion
public class MfgTon {
    private static MfgTon ourInstance;

    public static MfgTon getInstance() {

        if (ourInstance == null){
            ourInstance = new MfgTon();
        }
        return ourInstance;
    }

    private MfgTon() {
    }

    private String userLogged= null;
    private String userRole = null;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static String MFG_ROOT = "test/mfg";
    private DatabaseReference dbMpList = database.getReference("/mp" );
    private DatabaseReference dbMpTrace = database.getReference("/trace/mpin");
    private DatabaseReference dbMfgDb = database.getReference(MFG_ROOT );
    private DatabaseReference dbMfgOp = database.getReference(MFG_ROOT + "/ordenprod");
    private DatabaseReference dbMfgPt = database.getReference(MFG_ROOT +"/pt");
    private DatabaseReference dbMfgSe = database.getReference(MFG_ROOT +"/se");
    private final static String TAG = "JCH mfgTon";
    private DatabaseReference dbSemielabList = database.getReference("mfg/selist");
    private DatabaseReference dbRece = database.getReference("mfg/rece/rece");
    private DatabaseReference dbReceTrace = database.getReference("mfg/rece/trace");
    public ArrayList<MfgItem> opList = new ArrayList<>();
    public ArrayList<MfgItem> seList = new ArrayList<>();
    public ArrayList<MfgItem> ptList = new ArrayList<>();

    public void initDb() {
        SeList seList = new SeList();
        seList.setDescripcion("primer registro");

        dbSemielabList.push().setValue(seList);

        dbMfgDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (! snapshot.hasChild("ordenProd")) {
                    //Sort of "touch" record
                    //Required really?
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void receSetVisible(Rece receItem, Boolean visible){
        String receItemKey = receItem.getThisKey();
        dbRece.child(receItemKey+"/visible").setValue(visible);
    }

    public void getReceList(Context context, final DbGetDataListener listener){
        listener.onStart();
        final ArrayList<Rece> receList = new ArrayList();
        dbRece.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for( DataSnapshot item: dataSnapshot.getChildren()){
                    Rece rece = item.getValue(Rece.class);
                    if( rece.getVisible()){
                        String key = item.getKey();
                        rece.setThisKey(key);
                        receList.add(rece);
                    }
                }
                listener.onSuccess(receList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    public void normalizeMpTable(){
        final DatabaseReference dbMfgMpActiva = database.getReference("/mfg/mpactiva");
        //final Hash
        final Map<String, Map<String,String>> traceTable = new HashMap<>();
        dbMpTrace.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //TODO aca tengo toda la base de trace in, tengo que llevarla a una base de los ultimos dos lote
                Map<String, String> listaPos = new HashMap<>();
                for(DataSnapshot item:dataSnapshot.getChildren()){
                    String key = item.getKey();
                    listaPos = (Map<String,String>) item.getValue();
                    Map<String, String> listaOrd = new TreeMap<String, String>(listaPos);
                    Map<String, String> lastTwo = new HashMap<>();
                    int ix = 0 ;
                    for( Map.Entry<String, String> mpSet: listaOrd.entrySet()){
                        if( ix++ >= listaOrd.size()-2){
                            lastTwo.put(mpSet.getKey(), mpSet.getValue());
                        }
                    }
                    traceTable.put(key, lastTwo);
                }
                dbMfgMpActiva.setValue(traceTable);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getMpList( final DbGetDataListener listener){

        listener.onStart();
        dbMpList.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                ArrayList<MateriaPrima> lista = new ArrayList<>();
                //TODO cargar la lista en el array
                listener.onSuccess( lista );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });

    }
    public void getProdTables(final DbReadOnlyTables listener){
        listener.onStart();
        dbMfgOp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                opList.clear();
                for( DataSnapshot item: dataSnapshot.getChildren()){
                    MfgItem mfIt = item.getValue(MfgItem.class);
                    String key = item.getKey();
                    mfIt.setThisKey(key);
                    opList.add(mfIt);

                }
                dbMfgSe.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        seList.clear();
                        for( DataSnapshot item: dataSnapshot.getChildren()){
                            MfgItem mfIt = item.getValue(MfgItem.class);
                            String key = item.getKey();
                            mfIt.setThisKey(key);
                            seList.add(mfIt);
                        }
                        dbMfgPt.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ptList.clear();
                                for( DataSnapshot item: dataSnapshot.getChildren()){
                                    MfgItem mfIt = item.getValue(MfgItem.class);
                                    String key = item.getKey();
                                    mfIt.setThisKey(key);
                                    ptList.add(mfIt);
                                }
                                listener.onSuccess();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                listener.onFailed("Error getting dbMfgSe");
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onFailed("Error getting dbMfgOp");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed("Error getting dbMfgSe");
            }
        });
    }

    public void getSemielabList( final DbGetDataListener<ArrayList<SeList>> listener  ) {
        listener.onStart();
        final ArrayList<SeList> seList = new ArrayList<>();
        dbSemielabList.addValueEventListener  (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    SeList seRec =  new SeList();
                    seRec = snap.getValue(SeList.class);
                    seRec.setThisKey(snap.getKey());
                    //TODO esto va donde abre la receta
                    //parseLeoString(seRec.getRece());
                    seList.add(seRec);
                }
                listener.onSuccess(seList);
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

    public void getOp (final DbGetDataListener<ArrayList<MfgItem>> listener) {
        listener.onStart();
        dbMfgOp.addValueEventListener(new ValueEventListener() {
            final ArrayList<MfgItem> mfgOpList = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot provSnapshot) {
                for (DataSnapshot prov : provSnapshot.getChildren()) {
                    try {

                    } catch (Exception e) {
                        //Error de lectura
                    }

                }
                listener.onSuccess(mfgOpList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    public void saveMfgItem(MfgItem mfgItem, String stage){

        switch( stage){
            //TODO activa pro el FAB
            case Const.MFG_OP:
                String thisKey = dbMfgOp.push().getKey();
                dbMfgOp.child(thisKey).setValue(mfgItem);
                break;
                //TODO activaos pro el drag and drop
            case Const.MFG_EL:
                //String key = mfgItem.getThisKey();
                //dbMfgEl.child(key).setValue(mfgItem);
                //dbMfgOp.child(key).removeValue();
            case Const.MFG_SE:
                //String key2 = mfgItem.getThisKey();
                //dbMfgSe.child(key2).setValue(mfgItem);
                //dbMfgEl.child(key2).removeValue();

        }
    }

}
