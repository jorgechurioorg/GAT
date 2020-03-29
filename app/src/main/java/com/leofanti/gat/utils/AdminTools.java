package com.leofanti.gat.utils;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leofanti.gat.GatTon;
import com.leofanti.gat.LabelActivity;
import com.leofanti.gat.LabelTon;
import com.leofanti.gat.R;
import com.leofanti.gat.adapters.FilesRecyclerViewAdapter;
import com.leofanti.gat.adapters.PtOutRecyclerViewAdapter;
import com.leofanti.gat.model.Labels;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;


import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminTools.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminTools#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminTools extends DialogFragment {

    private View baseView ;
    private SharedPreferences sharedPreferences;
    private String installMode = null;
    private String printerOneIp = null;
    private String printerOnePort = null;
    private String sdPath = null;
    private RecyclerView filesRv;
    private FilesRecyclerViewAdapter adapter;
    private static final int  REQUEST_READ_EXTERNAL_STORAGE = 9001;
    private static LabelTon labelTon = LabelTon.getInstance();
    private AdminHelper adminHelper = new AdminHelper();

    public AdminTools() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseAuditor.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminTools newInstance(String param1, String param2) {
        AdminTools fragment = new AdminTools();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
        sharedPreferences = getContext().getSharedPreferences("GATCFG", MODE_PRIVATE);

        sdPath = sharedPreferences.getString("SdPath", null);
        if( sdPath==null){
            File extStore = Environment.getExternalStorageDirectory();
            sdPath = extStore.getAbsolutePath() + "/Download/" ;
        }

        installMode = sharedPreferences.getString("InstallMode", null);
        if( installMode==null ) {
            installMode = "FABRICA";
            sharedPreferences.edit().putString("InstallMode", installMode).apply();
        }
        printerOneIp = sharedPreferences.getString("PrinterOneIp", null);
        printerOnePort = sharedPreferences.getString("PrinterOnePort", null);

        if( printerOneIp == null) {
            printerOneIp = "192.169.1.100";
            printerOnePort ="9100";
            sharedPreferences.edit()
                    .putString("PrinterOneIp", printerOneIp)
                    .putString("PrinterOnePort", printerOnePort)
                    .apply();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        baseView = inflater.inflate(R.layout.card_admin_tools, container, false);
        Toolbar expAuditToolbar = (Toolbar) baseView.findViewById(R.id.admintools_toolbar);
        expAuditToolbar.setTitle("ADMIN TOOLS");
        expAuditToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return baseView;
    }





    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RadioGroup radioFact = (RadioGroup) view.findViewById(R.id.cardadmin_install_radio_group);
        final RadioButton radioFabrica = (RadioButton) view.findViewById(R.id.cardadmin_modo_fabrica) ;
        final RadioButton radioCanal = (RadioButton) view.findViewById(R.id.cardadmin_modo_canal);
        final EditText printerOneIpET = (EditText) view.findViewById(R.id.cardadmin_printer_1_ip) ;
        printerOneIpET.setText(printerOneIp);
        final EditText printerOnePortET = (EditText) view.findViewById(R.id.cardadmin_printer_1_port) ;
        printerOnePortET.setText(printerOnePort);
        final ImageButton printerOneBtn = (ImageButton) view.findViewById(R.id.cardadmin_printer_1_save);

        final EditText externalStoragePath = (EditText) view.findViewById(R.id.cardadmin_sdpath);
        externalStoragePath.setText(sdPath);
        final ImageButton getSdFilesButton = (ImageButton) view.findViewById(R.id.cardadmin_getsdfiles);
        filesRv = (RecyclerView) view.findViewById(R.id.cardadmin_filesview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        filesRv.setLayoutManager(mLayoutManager);

        getSdFilesButton.setOnClickListener((new View.OnClickListener() {
            //muestra el listado de archivos de impresora
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions( //Method of Fragment
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    String path = externalStoragePath.getText().toString();
                    File newDir = new File(path);
                    if (newDir.exists() && newDir.isDirectory()) {
                        if (!path.equalsIgnoreCase(sdPath)) {
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("GATCFG", MODE_PRIVATE);
                            sdPath = path;
                            sharedPreferences.edit().putString("SdPath", sdPath).apply();
                        }
                    } else {
                        Snackbar.make(baseView, "Directorio invalido queda en " + sdPath, Snackbar.LENGTH_SHORT).show();
                    }
                    listExternalStorage();
            }
            }
        }));

        switch ( installMode.toUpperCase()){
            case "FABRICA":
                radioCanal.setChecked(false);
                radioFabrica.setChecked(true);
                break;

            case "CANAL":
                radioCanal.setChecked(true);
                radioFabrica.setChecked(false);
                break;

        }
        radioFact.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                String installMode = null;
                switch (radioFact.getCheckedRadioButtonId()) {
                    case R.id.cardadmin_modo_canal:
                        radioCanal.setChecked(true);
                        radioFabrica.setChecked(false);
                        installMode = "CANAL";
                        break;
                    case R.id.cardadmin_modo_fabrica:
                        radioCanal.setChecked(false);
                        radioFabrica.setChecked(true);
                        installMode = "FABRICA";
                        break;
                }
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("GATCFG", MODE_PRIVATE);
                sharedPreferences.edit().putString("InstallMode", installMode).apply();

            }
        });
        printerOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO vakidar IP y port
                printerOneIp = printerOneIpET.getText().toString();
                printerOnePort = printerOnePortET.getText().toString();
                sharedPreferences.edit()
                        .putString("PrinterOneIp", printerOneIp)
                        .putString("PrinterOnePort", printerOnePort)
                        .apply();
                printerOneBtn.setBackgroundResource(R.drawable.filed);
                Snackbar.make(baseView, "Printer guardado como " +printerOneIp+":"+printerOnePort, Snackbar.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    ArrayList<File> fileList = new ArrayList<>();
    ArrayList<Labels> existingLabels = new ArrayList<>();
    //traer todas la etiquets que hay en la base de datos

    private void listExternalStorage(){
        //Armar aca el recyclerview
        fileList.clear();
        File externalStorage = new File(sdPath);
        File[] files = externalStorage.listFiles();
        if( files!=null) {
            for (int i = 0; i < files.length; i++) {
                if(files[i].getName().toUpperCase().endsWith("PRN")) {
                    fileList.add(files[i]);
                }
            }
        }
        adapter = new FilesRecyclerViewAdapter(fileList);
        filesRv.setAdapter(adapter);
        filesRv.setItemAnimator(new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
        filesRv.addOnItemTouchListener(new RecyclerViewGestureListener(getContext(), filesRv, new RecyclerViewTouchListener() {
            @Override
            public void onClick(View view, int position) {
                processFile(fileList.get(position));
            }

            @Override
            public void onLongClick(View view, final int position) {
            }

        }));
        //TODO touch listener para subir el archivo a la base de datos
        //Si mismo nombre sobreescribir el ya existente con nuevos datos
        //Si cambia nombre (no esta en la base) pedir grupo (autocomplete) y nombre
        //para la lista mostrada
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            //if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
            //        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //    ;
            //}
        }
    }


    private void processFile( final File file){
        labelTon.getAllLabels(new DbGetDataListener<ArrayList<Labels>>()  {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess( ArrayList<Labels> labelsList){
                boolean fileExist = false;
                for( Labels  dbLab: labelsList){
                    if( file.getName().equalsIgnoreCase(dbLab.getName())){
                        fileExist = true;
                        dbLab.setFileSize(file.length());
                        dbLab.setFileDate(file.lastModified());
                        labelTon.upload(file, dbLab, fileExist);
                        break;
                    }
                }
                if( !fileExist ) {
                    //TODO Abre un dialogo de carga de parametros (permite sobreescribir)
                    //Labels newLabel = new Labels();
                    //labelTon.upload(file, newLabel, false);
                    Snackbar.make(baseView, "ETIQUETA NUEVA", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                //DO SOME THING WHEN GET DATA FAILED HERE
            }
        });

    }



}


