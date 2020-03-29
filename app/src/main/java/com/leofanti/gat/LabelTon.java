package com.leofanti.gat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leofanti.gat.model.Cliente;
import com.leofanti.gat.model.ClienteOut;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.ExpRegistro;
import com.leofanti.gat.model.Labels;
import com.leofanti.gat.model.ProductoTerminado;
import com.leofanti.gat.utils.ComparatorCol1;
import com.leofanti.gat.utils.ComparatorCol2;
import com.leofanti.gat.utils.ComparatorLabelGroup;
import com.leofanti.gat.utils.ComparatorLabelName;
import com.leofanti.gat.utils.DbGetDataListener;
import com.leofanti.gat.utils.DbReadOnlyTables;
import com.leofanti.gat.utils.LabelChainedComparator;
import com.leofanti.gat.utils.ReportChainedComparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

// Singleton para la logica de negocios impresion de etiquetas
public class LabelTon {

    private static LabelTon ourInstance;

    public static LabelTon getInstance() {
        if (ourInstance == null){ //if there is no instance available... create new one
            ourInstance = new LabelTon();
        }
        return ourInstance;
    }


    private LabelTon() {

    }


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbLabels = database.getReference("labels");
    private ArrayList<Labels> labels = new ArrayList<>();
    private HashMap<String, Integer> grupos = new HashMap<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private final static String TAG = "GAT labelTon";



    /**Sube el archivo prn en formato binario
     *
     */
    public void upload(final File file, final Labels label, final boolean labelExist){

        String fileName = file.getName();
        String urltoimage = "labels/" + fileName;
        StorageReference storageRef = storage.getReferenceFromUrl("gs://villate3165.appspot.com/");
        final StorageReference imagesRef = storageRef.child(urltoimage);
        //StorageReference storageRef = FirebaseStorage.getInstance().reference().child("folderName/file.jpg");
        //Uri file = Uri.fromFile(new File("path/to/folderName/file.jpg"));
        //UploadTask uploadTask = storageRef.putFile(file);
        Uri archi = Uri.fromFile(file);
        final UploadTask uploadTask = imagesRef.putFile(archi);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //TODO mensaje de error que no se pudo guardar el archivo de etiqueta
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        String url = downloadUrl.toString();
                        label.setLabelUrl(url);
                        //todo si existe la etiqueta reescribe
                        saveLabelEntry(label, labelExist);
                    }
                });
            }
        });
    }

    private void saveLabelEntry( Labels label, boolean labelExist ) {
        String thisKey = dbLabels.push().getKey();
        dbLabels.child(thisKey).setValue(label);
    }

    private void removeLabelEntry(String key) {
        //https://stackoverflow.com/questions/42930619/how-to-delete-image-from-firebase-storage
        dbLabels.child(key).removeValue();
    }

    public ArrayList<Labels> getByGroup( String group){
        ArrayList<Labels> labelist = new ArrayList<>();
        for(Labels lab: labels ){
            if( lab.getGrupo().equalsIgnoreCase(group)){
                labelist.add(lab);
            }
        }
        return labelist;
    }

    public void getAllLabels(final DbGetDataListener<ArrayList<Labels>> listener) {
        listener.onStart();
        if( !labels.isEmpty()) {
            listener.onSuccess(labels);
        } else {
            dbLabels.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    labels.clear();
                    for (DataSnapshot labelOb : dataSnapshot.getChildren()) {
                        Labels lab = labelOb.getValue(Labels.class);
                        lab.setThisKey(labelOb.getKey());
                        labels.add(lab);
                    }
                    Collections.sort(labels, new LabelChainedComparator(
                            new ComparatorLabelGroup(),
                            new ComparatorLabelName()));
                    grupos.clear();
                    int pos = 0;
                    for (Labels labe : labels) {
                        String key = labe.getGrupo();
                        if (!grupos.containsKey(key)) {
                            grupos.put(labe.getGrupo(), pos);
                        }
                        pos++;
                    }
                    listener.onSuccess(labels);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "getlabelList cancelled error :" + databaseError.toException());
                    listener.onFailed(databaseError);
                }
            });
        }

    }
    public void startPrinterService() {
        //SharedPreferences sharedPreferences = getSharedPreferences("GATCFG", MODE_PRIVATE);
        //abrir el port y lannzar un thread monitoreando el estado de la impresora
    }
}



