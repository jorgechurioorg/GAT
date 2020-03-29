package com.leofanti.gat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.adapters.*;
import com.leofanti.gat.model.Cliente;
import com.leofanti.gat.model.ClienteOut;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.ProductoTerminado;
import com.leofanti.gat.model.ProductoTerminadoOut;
import com.leofanti.gat.model.UserPin;
import com.leofanti.gat.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class tabFragmentPtOut extends Fragment {


    public static GatTon gatTon = GatTon.getInstance();
    public static String TAG = "JCHPTOUT";
    public static String selectedCliente;
    private static View baseView;
    private static ArrayList<ProductoTerminadoOut> ptOutList;
    private static PtOutRecyclerViewAdapter adapter;
    private static RecyclerView recyclerView ;
    private static RecyclerView ptOutScanItemRv;
    public  static ClienteOut clienteOut;
    private PtOutHelper ptOutHelper = new PtOutHelper();
    private ArrayList<ClienteOut> displayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.tab_fragment_ptout, container, false);
        ChipGroup chipGroup = (ChipGroup) baseView.findViewById(R.id.tabptout_chipgroup) ;
        final Chip chipOpen = (Chip) baseView.findViewById(R.id.tabptout_open);
        chipOpen.setChecked(true);
        final Chip chipShipped = (Chip) baseView.findViewById(R.id.tabptout_shipped);
        chipShipped.setChecked(false);
        final Chip chipFiled = (Chip) baseView.findViewById(R.id.tabptout_filed);
        chipFiled.setChecked(false);
        CompoundButton.OnCheckedChangeListener filterChipListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                displayList = ptOutHelper.getDisplayList(chipOpen.isChecked(), chipShipped.isChecked(), chipFiled.isChecked());
                final PtOutRecyclerViewAdapter adap = new PtOutRecyclerViewAdapter(getContext(),displayList);
                recyclerView.setAdapter(adap);
            }
        };
        chipOpen.setOnCheckedChangeListener(filterChipListener);
        chipShipped.setOnCheckedChangeListener(filterChipListener);
        chipFiled.setOnCheckedChangeListener(filterChipListener);

        recyclerView = (RecyclerView) baseView.findViewById(R.id.ptout_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        gatTon.getRemitosMain(getContext(), new DbGetDataListener<ArrayList<ClienteOut>>() {
            @Override
            public void onStart() {

            }
            @Override
            public void onSuccess(final ArrayList<ClienteOut> listaObj) {
                final String[] canales = gatTon.getCanales();
                ptOutHelper.setWholeList( listaObj);
                displayList = ptOutHelper.getDisplayList( chipOpen.isChecked(), chipShipped.isChecked(),chipFiled.isChecked()  );
                adapter = new PtOutRecyclerViewAdapter(getContext(),displayList);
                recyclerView.setAdapter(adapter);
                recyclerView.setMotionEventSplittingEnabled(false);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addOnItemTouchListener(new RecyclerViewGestureListener(getContext(), recyclerView, new RecyclerViewTouchListener() {
                    @Override
                    /**
                     * Long click de la lista de remitos, segun el estado hace diferentes cosas
                     *<li>Const.OPEN</li>  edita el remito ya ingresado puede estra vacio)
                     * <li>Const.SHIPPED</li> reasigna la ruta
                     * <li>Const.CLOSED</li> reasigna la ruta
                     * <li>Const.DELIVRD</li> no se muestra en la lista
                     * <li>Const.RETURNED</li> no se muestra en la lista
                     * <b>Todas las ocpiones tienen el boton de cancelar remito </b>
                     */
                    public void onClick(View view, int position) {

                        clienteOut = displayList.get(position);
                        Snackbar.make(baseView, "item clicked" + String.valueOf(position)+clienteOut.getName(), Snackbar.LENGTH_SHORT).show();
                        String status = clienteOut.getStatus().toUpperCase();
                        switch( status) {
                            case Const.NEW:
                            case Const.OPEN:
                                clienteOut = new ClienteOut();
                                clienteOut = displayList.get(position);
                                //TODO usar el fragmentmanager de Activity Main
                                PtOutDialog ptOutDialog = PtOutDialog.newInstance(clienteOut);
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ptOutDialog.show(ft, TAG );
                                break;
                            case Const.CLOSED:
                            case Const.SHIPPED:
                                //TODO MOSTRAR READ ONLY EL REMITO
                                PtOutDialog dialog = PtOutDialog.newInstance(clienteOut);
                                FragmentTransaction ftro = getFragmentManager().beginTransaction();
                                dialog.show(ftro, TAG );
                                break;

                        }
                    }

                    @Override
                    /**
                     * Long click de la lista de remitos, segun el estado hace diferentes cosas
                     *<li>Const.OPEN</li> nada
                     * <li>Const.SHIPPED</li> nada
                     * <li>Const.DISPATCHED/li> archiva el remito
                     * <li>Const.RETURNED</li> no se muestra en la lista
                     */
                    public void onLongClick(View view, final int position) {
                        clienteOut = displayList.get(position);

                        String status = clienteOut.getStatus();
                        switch (status) {
                            case Const.OPEN:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("DESPACHA REMITO");
                                clienteOut.setRuta(canales[0]);
                                builder.setSingleChoiceItems(canales,  0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        clienteOut.setRuta(canales[i]);
                                    }
                                });
                                builder.setPositiveButton("DESPACHA", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        DatesHelper datesHelper = new DatesHelper();
                                        String despachado = datesHelper.getTimestamp();
                                        clienteOut.setDespachado(despachado);
                                        clienteOut.setDispatchOperador(gatTon.getUserLogged());
                                        clienteOut.setStatus(Const.SHIPPED);
                                        //TODO cargar la base de trazabilidad
                                        gatTon.shipRemito(clienteOut);
                                        adapter.notifyDataSetChanged();
                                        Snackbar.make(baseView, "Remito despachadoo".toUpperCase(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                                builder.create().show();
                                break;

                            case Const.CLOSED:
                            case Const.SHIPPED:
                            case Const.DISPD:
                                builder = new AlertDialog.Builder(getContext());
                                if( clienteOut.getRuta()==null){
                                    clienteOut.setRuta("***");
                                }
                                final String ruta = clienteOut.getRuta();
                                builder.setTitle("CAMBIA RUTA de " + ruta + " para :");
                                builder.setSingleChoiceItems(canales,  -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        clienteOut.setRuta(canales[i]);
                                    }
                                });
                                builder.setPositiveButton("CAMBIA", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String newRuta = clienteOut.getRuta();
                                        if( !ruta.equalsIgnoreCase(newRuta)) {
                                            dialog.dismiss();
                                            DatesHelper datesHelper = new DatesHelper();
                                            String despachado = datesHelper.getTimestamp();
                                            clienteOut.setDespachado(despachado);
                                            clienteOut.setDispatchOperador(gatTon.getUserLogged());
                                            clienteOut.setStatus(Const.SHIPPED);
                                            gatTon.reShipRemito(ruta, clienteOut);
                                            adapter.notifyDataSetChanged();
                                            Snackbar.make(baseView, "Ruta reasignada".toUpperCase(), Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                String role = gatTon.getUserRole();
                                if( role.equalsIgnoreCase(Const.ADMIN) || role.equalsIgnoreCase(Const.ROOT)){
                                    builder.setNeutralButton("BORRAR",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            clienteOut.setStatus(Const.DEL);
                                            gatTon.fileRemito(clienteOut);
                                            //TODO ARREGLAR EL TOUCH/VIEW
                                            ArrayList<ClienteOut> clout = ptOutHelper.getDisplayList(chipOpen.isChecked(), chipShipped.isChecked(), chipFiled.isChecked());
                                            final PtOutRecyclerViewAdapter newAdap = new PtOutRecyclerViewAdapter(getContext(),clout);
                                            recyclerView.setAdapter(newAdap);
                                        }
                                    });
                                }
                                builder.create().show();
                                break;

                            default:
                                break;
                        }
                    }
                }));
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                //DO SOME THING WHEN GET DATA FAILED HERE
            }
        });

        FloatingActionButton fab = (FloatingActionButton) baseView.findViewById(R.id.ptout_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clienteOut = new ClienteOut();
                clienteOut.setStatus(Const.NEW);
                PtOutDialog dialog = PtOutDialog.newInstance(clienteOut);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, TAG );
            }
        });
        return baseView;
    }

    public static class PtOutDialog extends DialogFragment {

        public PtOutDialog() { }
        Toolbar ptOutToolbar;
        private static ClienteOut cOut;
        private EditText shipNotesET;
        private Boolean edited = false;

        public static PtOutDialog newInstance (ClienteOut mpInReg) {
            PtOutDialog frag = new PtOutDialog();
            //TODO poner esto como metodo de guardar todo el objeto
            cOut = mpInReg;
            Bundle args = new Bundle();
            Log.d(TAG, "pt status :" + mpInReg.getStatus());
            args.putString("cliName", mpInReg.getName());
            args.putString("cliStatus", mpInReg.getStatus());
            args.putString("remitoKey", mpInReg.getRemitoKey());
            args.putString("cliTimestamp", mpInReg.getTimestamp());
            frag.setArguments(args);
            return frag;
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }




        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            final String status = getArguments().getString("cliStatus", "none").toUpperCase();
            final String cliName = getArguments().getString("cliName", "none");
            final String remitoKey = getArguments().getString("remitoKey", "none");
            final View view = inflater.inflate(R.layout.card_pt_out, container, false);

            ptOutToolbar = view.findViewById(R.id.ptout_toolbar);
            switch( status) {
                case "CLOSED":
                case "FILED":
                    //TODO borrar el menu de opciones del toolbar
            }
            ptOutToolbar.setNavigationOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch( status.toUpperCase()){
                        case Const.OPEN:
                        case Const.NEW:
                            if( edited) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("ATENCION");
                                builder.setMessage("Cancela los items  ingresados?");
                                builder.setPositiveButton("CANCELAR", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Snackbar.make(view, "Ingreso cancelado", Snackbar.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            dismiss();
                                        }
                                    });
                                builder.create().show();
                            } else {
                                dismiss();
                            }
                            break;
                        case Const.CLOSED:
                        case Const.SHIPPED:
                            dismiss();
                            break;
                    }
                }
            });

            ptOutToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if( status.equalsIgnoreCase(Const.OPEN)||status.equalsIgnoreCase(Const.NEW)) {
                        int id = item.getItemId();
                        String shipNote = shipNotesET.getText().toString();
                        if(id == R.id.ptout_save){
                            if( !ptOutScanList.isEmpty() || !shipNote.isEmpty()) {
                                if( ! shipNote.isEmpty()) {
                                    clienteOut.setShipNote(shipNote);
                                    clienteOut.setStatus(Const.OPEN);
                                    gatTon.rewriteClienteOut(clienteOut);
                                }
                                gatTon.savePtOutList(clienteOut.getRemitoKey(), ptOutScanList);
                                dismiss();
                                Snackbar.make(baseView, "Remito guardado en estado abierto", Snackbar.LENGTH_SHORT);
                            } else {
                                dismiss();
                                Snackbar.make(baseView, "Remito vacio: no se guarda", Snackbar.LENGTH_SHORT);
                            }
                        }
                    }
                    dismiss();
                    return true;
                }
            });

            ptOutToolbar.inflateMenu(R.menu.menu_ptout);
            ptOutToolbar.setNavigationIcon(R.drawable.cancela);
            ptOutToolbar.setTitle("Salida de Producto Terminado");
            return view;
        }

        ArrayAdapter<String> clienteTextList;
        ArrayList<ProductoTerminado> ptList = new ArrayList<>();
        final ArrayList<ProductoTerminadoOut> ptOutScanList = new ArrayList<>();
        final ArrayList<ProductoTerminadoOut> ptOutListTotal = new ArrayList<>();
        ArrayList<ProductoTerminadoOut> ptOutDisplay = new ArrayList<>();
        int CODPTLEN = 3;
        //TRES DIGITOS POR EL FORMATO DE LA IMPRESORA
        int LOTPTLEN = 3;
        String qty = "0";
        String des;
        String key;
        String loti;
        boolean totalViewMode = true;


        @Override
        public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            String status = getArguments().getString("cliStatus", "none");
            String cliName = getArguments().getString("cliName", "none");
            String remitoKey = getArguments().getString("remitoKey", "none");

            totalViewMode = true;

            //Log.d(TAG, "status y cliente name " + cliName +"|"+ status);

            clienteTextList = new ArrayAdapter<String>(getActivity(), R.layout.automplete_layout, R.id.auto_complete_item, gatTon.getClienteList());

            final TextView ptOutNewTimestamp = (TextView) view.findViewById(R.id.ptout_scan_created);
            final TextView ptOutCloseTimestamp = (TextView) view.findViewById(R.id.ptout_scan_closed);
            final TextView ptOutCanal = (TextView) view.findViewById(R.id.ptout_scan_canal);
            shipNotesET = (EditText) view.findViewById(R.id.ptout_shipnote);
            String shipN = clienteOut.getShipNote();
            if( shipN != null){
                if( !shipN.isEmpty()){
                    shipNotesET.setText(clienteOut.getShipNote());
                }
            }
            final AutoCompleteTextView mpSelect = (AutoCompleteTextView) view.findViewById(R.id.ptout_cliente);
            final ImageButton newCustomerButton = (ImageButton) view.findViewById(R.id.ptout_newclient);
            final LinearLayout ptOutScan = (LinearLayout) view.findViewById(R.id.ptOutScanScreen);
            final LinearLayout ptOutScanScannerWindow= (LinearLayout) view.findViewById(R.id.ptOutScanScannerWindow);
            final EditText scanCode = (EditText) view.findViewById(R.id.scanner_window);
            final TextView qtyItem = (TextView) view.findViewById(R.id.qtyField);
            final TextView prodName = (TextView) view.findViewById(R.id.pt_name);
            final TextView lotNum = (TextView) view.findViewById(R.id.pt_lote);
            final ChipGroup viewMode = (ChipGroup) view.findViewById(R.id.ptoutscan_chips);
            final Chip chipLote = (Chip) view.findViewById(R.id.ptout_bylot);
            final Chip chipTotal = (Chip) view.findViewById(R.id.ptout_byproduct);
            final TextView editMode = (TextView) view.findViewById(R.id.ptscan_editmsg) ;
            viewMode.setVisibility(view.VISIBLE);
            //final Button viewMode = (Button) view.findViewById(R.id.scanDisplayMode);
            //Sets recycler view de productos ingresados
            ptOutScanItemRv = view.findViewById(R.id.ptoutscan_recycler_view);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            ptOutScanItemRv.setLayoutManager(mLayoutManager);
            final PtOutScanItemAdapter ptoia = new PtOutScanItemAdapter(getContext(), ptOutListTotal);
            final PtOutScanItemAdapter ptoib = new PtOutScanItemAdapter(getContext(), ptOutScanList);
            ptOutScanItemRv.setAdapter(ptoia);
            if( status.equalsIgnoreCase(Const.OPEN)) {
                ptOutScanItemRv.addOnItemTouchListener(new RecyclerViewGestureListener(getContext(), ptOutScanItemRv, new RecyclerViewTouchListener() {
                    @Override
                    public void onClick(View view, int position) {
                        if( viewMode.getCheckedChipId() == R.id.ptout_bylot) {
                            ProductoTerminadoOut prodTermOut  = ptOutScanList.get(position);
                            //elimina d ela lista de cargados
                            ptOutScanList.remove(position);
                            //despuenta de la lista x producto
                            int ix = 0 ;
                            boolean remove = false;
                            for( ProductoTerminadoOut ptx: ptOutListTotal){
                                if( prodTermOut.getDescripcion().equalsIgnoreCase(ptx.getDescripcion())){
                                    int cantAnte = prodTermOut.getCantidad();
                                    int cantActu = cantAnte - prodTermOut.getCantidad();
                                    if( cantActu != 0 ) {
                                        ptOutListTotal.get(ix).setCantidad(cantActu);
                                    } else {
                                        remove = true;
                                    }
                                    break;
                                }
                                ix++;
                            }
                            if( remove )
                                ptOutListTotal.remove(ix);
                            ptoia.notifyDataSetChanged();
                            prodName.setText(prodTermOut.getDescripcion());
                            qtyItem.setText(String.valueOf(prodTermOut.getCantidad()));
                            lotNum.setText(prodTermOut.getLote());
                            String scCode = prodTermOut.getPtKey() + "000";
                            scanCode.setHint(scCode);
                            editMode.setVisibility(View.VISIBLE );
                            editMode.setText("MODIFICA LOTE");
                            editMode.setBackgroundColor(getResources().getColor(R.color.colorClose));
                            //deberia ir ptoib??? y refrescar ptoia
                            scanCode.setSelection(0);
                            scanItem(prodName, qtyItem, lotNum, scanCode, ptoia, position);
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        if( viewMode.getCheckedChipId() == R.id.ptout_byproduct) {
                            chipLote.setChecked(true);
                            Collections.sort(ptOutScanList, new Comparator<ProductoTerminadoOut>() {
                                @Override
                                public int compare(ProductoTerminadoOut o1, ProductoTerminadoOut o2) {
                                    return o2.getDescripcion().compareToIgnoreCase(o1.getDescripcion());
                                }
                            });

                            ptOutScanItemRv.swapAdapter(ptoib, true);
                            ptoib.notifyDataSetChanged();
                            editMode.setVisibility(View.VISIBLE );
                            editMode.setText("ELEGIR EL LOTE A MODIFICAR");
                        }

                    }
                }));
            }
            //FILTRO PARA MOSTRAR POR LOTE O POR TOTALES
            viewMode.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(ChipGroup group, int checkedId) {
                    switch( group.getCheckedChipId()) {
                        case R.id.ptout_bylot:
                            Collections.sort(ptOutScanList, new Comparator<ProductoTerminadoOut>() {
                                @Override
                                public int compare(ProductoTerminadoOut o1, ProductoTerminadoOut o2) {
                                    return o2.getDescripcion().compareToIgnoreCase(o1.getDescripcion());
                                }
                            });
                            final PtOutScanItemAdapter ptoib = new PtOutScanItemAdapter(getContext(), ptOutScanList);
                            ptOutScanItemRv.swapAdapter(ptoib, true);
                            ptoib.notifyDataSetChanged();
                            break;

                        case R.id.ptout_byproduct:
                            //final PtOutScanItemAdapter ptoia = new PtOutScanItemAdapter(getContext(), ptOutListTotal);
                            Collections.sort(ptOutListTotal, new Comparator<ProductoTerminadoOut>() {
                                @Override
                                public int compare(ProductoTerminadoOut o1, ProductoTerminadoOut o2) {
                                    return o2.getDescripcion().compareToIgnoreCase(o1.getDescripcion());
                                }
                            });
                            ptOutScanItemRv.swapAdapter(ptoia, true);
                            ptoia.notifyDataSetChanged();
                            break;
                    }
                }
            });

            if( status.equalsIgnoreCase(Const.OPEN)) {
                ptOutToolbar.setTitle(cliName);
            }
            if( status.equalsIgnoreCase(Const.CLOSED)||status.equalsIgnoreCase(Const.SHIPPED)){
                Log.d(TAG, "status closed shows read only");
                ptOutToolbar.setTitle(cliName);
                ptOutNewTimestamp.setVisibility(View.VISIBLE);
                //String creado = "Creado el : " +  gatTon.clienteOut.getTimestamp();
                //ptOutNewTimestamp.setText(creado);
                //String despachado = "Despachado el : " + gatTon.clienteOut.getDespachado();
                ptOutCloseTimestamp.setVisibility(View.VISIBLE);
                //ptOutCloseTimestamp.setText(despachado);
                //String ruta = "Canal : " + gatTon.clienteOut.getRuta();
                //ptOutCanal.setText( ruta );
                ptOutCanal.setVisibility(View.VISIBLE);
                shipNotesET.setVisibility(View.VISIBLE);
                mpSelect.setVisibility(View.GONE);
                newCustomerButton.setVisibility(View.GONE);
                ptOutScan.setVisibility(View.VISIBLE);
                ptOutScanScannerWindow.setVisibility(View.GONE);
                ptOutDisplay = ptOutScanList;
                gatTon.getRemitoList(remitoKey, getContext(), new DbGetDataListener<ArrayList<ProductoTerminadoOut>>() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "OPEN cargando detalle de remitos ");
                    }

                    @Override
                    public void onSuccess(final ArrayList<ProductoTerminadoOut> itemList) {
                        ptOutScanList.clear();
                        ptOutListTotal.clear();
                        for( ProductoTerminadoOut item: itemList) {
                            ptOutScanList.add(item);
                            ProductoTerminadoOut totalIt = new ProductoTerminadoOut(item.getDescripcion(), "000000", "TOTAL", 0) ;
                            boolean doNotExist = true;
                            int position = 0 ;
                            for( ProductoTerminadoOut subtotal: ptOutListTotal){
                                if( subtotal.getDescripcion().equalsIgnoreCase(item.getDescripcion())) {
                                    int cantidad = subtotal.getCantidad() + item.getCantidad();
                                    ptOutListTotal.get(position).setCantidad(cantidad);
                                    doNotExist = false;
                                }
                                position++;
                            }
                            if( doNotExist) {
                                totalIt.setCantidad(item.getCantidad());
                                ptOutListTotal.add(totalIt);
                            }
                        }
                        Collections.sort(ptOutScanList, new Comparator<ProductoTerminadoOut>() {
                            @Override
                            public int compare(ProductoTerminadoOut o1, ProductoTerminadoOut o2) {
                                return o2.getDescripcion().compareToIgnoreCase(o1.getDescripcion());
                            }
                        });
                        Collections.sort(ptOutListTotal, new Comparator<ProductoTerminadoOut>() {
                            @Override
                            public int compare(ProductoTerminadoOut o1, ProductoTerminadoOut o2) {
                                return o2.getDescripcion().compareToIgnoreCase(o1.getDescripcion());
                            }
                        });
                        mpSelect.setVisibility(View.GONE);
                        newCustomerButton.setVisibility(View.GONE);
                        ptOutScan.setVisibility(View.VISIBLE);
                        ptoia.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailed(DatabaseError databaseError) {
                        //DO SOME THING WHEN GET DATA FAILED HERE
                    }

                });

            }
            if( status.equalsIgnoreCase(Const.NEW) || status.equalsIgnoreCase(Const.OPEN)) {
                ptList.clear();
                ptList.addAll(gatTon.getPtListRo());
                ptOutListTotal.clear();
                switch ( status) {
                    case Const.NEW:
                        mpSelect.setVisibility(View.VISIBLE);
                        newCustomerButton.setVisibility(View.VISIBLE);
                        ptOutScan.setVisibility(View.GONE);
                        mpSelect.setThreshold(2);
                        mpSelect.setAdapter(clienteTextList);
                        mpSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                DatesHelper datesHelper = new DatesHelper();
                                String selectedCliente = (String) parent.getItemAtPosition(position);
                                Log.d(TAG, "selectedCliente " + selectedCliente);
                                String address = gatTon.getClienteAddress(selectedCliente);
                                ptOutToolbar.setTitle(selectedCliente);
                                clienteOut.setAddress(address);
                                clienteOut.setName(selectedCliente);
                                clienteOut.setTimestamp(datesHelper.getHoyEsHoy());
                                clienteOut.setStatus(Const.OPEN);
                                clienteOut.setOperador(gatTon.getUserLogged());
                                String remitoKey = gatTon.setClienteOut(clienteOut);
                                clienteOut.setRemitoKey(remitoKey);
                                mpSelect.setVisibility(View.GONE);
                                newCustomerButton.setVisibility(View.GONE);
                                viewMode.setVisibility(View.VISIBLE);
                                shipNotesET.setVisibility(View.VISIBLE);
                                ptOutScan.setVisibility(View.VISIBLE);
                                scanItem(prodName, qtyItem, lotNum, scanCode, ptoia,-1);
                            }
                        });
                        newCustomerButton.setOnClickListener((new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //https://stackoverflow.com/questions/4016313/how-to-keep-an-alertdialog-open-after-button-onclick-is-fired
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Nuevo Cliente");
                                final View customLayout = getLayoutInflater().inflate(R.layout.dialog_newclient, null);
                                builder.setView(customLayout);
                                builder.setPositiveButton("CREAR CLIENTE",null);
                                final AlertDialog alertDialog = builder.create();
                                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                        b.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View view) {
                                                Cliente nuevoCliente = new Cliente();
                                                // send data from the AlertDialog to the Activity
                                                EditText nameEt = customLayout.findViewById(R.id.newclient_name);
                                                EditText addrEt = customLayout.findViewById(R.id.newclient_direccion);
                                                EditText locaEt = customLayout.findViewById(R.id.newclient_localidad);
                                                EditText celuEt = customLayout.findViewById(R.id.newclient_celu);
                                                String cName = nameEt.getText().toString();
                                                String cAddress = addrEt.getText().toString();
                                                String cLocal = locaEt.getText().toString();
                                                String cCelu = celuEt.getText().toString();
                                                if( cName.isEmpty()) {
                                                    nameEt.setError("Ingresar nombre");
                                                    nameEt.requestFocus();
                                                } else {
                                                    nuevoCliente.setName(cName);
                                                    nuevoCliente.setAddress(cAddress);
                                                    nuevoCliente.setMovil(cCelu);
                                                    nuevoCliente.setLocalidad(cLocal);
                                                    gatTon.setNewCustomer(nuevoCliente);
                                                    //TODO llamar a una funcion que comparta con cliente existentes
                                                    alertDialog.dismiss();
                                                    DatesHelper datesHelper = new DatesHelper();
                                                    clienteOut.setName(cName);
                                                    clienteOut.setAddress(cAddress +","+cAddress);
                                                    clienteOut.setTimestamp(datesHelper.getHoyEsHoy());
                                                    clienteOut.setStatus(Const.OPEN);
                                                    clienteOut.setOperador(gatTon.getUserLogged());
                                                    String remitoKey = gatTon.setClienteOut(clienteOut);
                                                    clienteOut.setRemitoKey(remitoKey);
                                                    //Setting de la interfaz
                                                    mpSelect.setVisibility(View.GONE);
                                                    newCustomerButton.setVisibility(View.GONE);
                                                    viewMode.setVisibility(View.VISIBLE);
                                                    ptOutScan.setVisibility(View.VISIBLE);
                                                    ptOutToolbar.setTitle(cName);
                                                    shipNotesET.setVisibility(View.VISIBLE);
                                                    scanItem(prodName, qtyItem, lotNum, scanCode, ptoia, -1);
                                                }
                                            }
                                        });
                                    }
                                });
                                alertDialog.show();
                            }
                        }));
                    break;
                    case Const.OPEN:
                        Log.d(TAG, "loading remitos OPEN");
                        gatTon.getRemitoList(remitoKey, getContext(), new DbGetDataListener<ArrayList<ProductoTerminadoOut>>() {
                            @Override
                            public void onStart() {
                                Log.d(TAG, "OPEN cargando detalle de remitos ");
                            }

                            @Override
                            public void onSuccess(final ArrayList<ProductoTerminadoOut> itemList) {
                                ptOutScanList.clear();
                                ptOutListTotal.clear();
                                for( ProductoTerminadoOut item: itemList) {
                                    ptOutScanList.add(item);
                                    ProductoTerminadoOut totalIt = new ProductoTerminadoOut(item.getDescripcion(), "000000", "TOTAL", 0) ;
                                    boolean doNotExist = true;
                                    int ix = 0 ;
                                    for( ProductoTerminadoOut subtotal: ptOutListTotal){
                                        if( subtotal.getDescripcion().equalsIgnoreCase(item.getDescripcion())) {
                                            totalIt.setCantidad(subtotal.getCantidad() + item.getCantidad());
                                            ptOutListTotal.set(ix, totalIt);
                                            doNotExist = false;
                                        }
                                        ix++;
                                    }
                                    if( doNotExist) {
                                        totalIt.setCantidad(item.getCantidad());
                                        ptOutListTotal.add(totalIt);
                                    }
                                }
                                mpSelect.setVisibility(View.GONE);
                                newCustomerButton.setVisibility(View.GONE);
                                shipNotesET.setVisibility(View.VISIBLE);
                                shipNotesET.setText(cOut.getShipNote());
                                ptOutScan.setVisibility(View.VISIBLE);
                                ptoia.notifyDataSetChanged();
                                scanItem(prodName, qtyItem, lotNum, scanCode, ptoia, -1);
                            }

                            @Override
                            public void onFailed(DatabaseError databaseError) {
                                //DO SOME THING WHEN GET DATA FAILED HERE
                            }

                        });
                }
            }

        }

        private void scanItem(final TextView prodName, final TextView qtyItem, final TextView lotNum,final EditText scanCode, final PtOutScanItemAdapter ptoia, final int pos ){
            //Log.d( "JCHSCANITEM", "scan item function" );
            String origProd = prodName.getText().toString();
            scanCode.requestFocus();
            scanCode.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager keyboard = (InputMethodManager) getDialog().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(scanCode, 0);
                }
            }, 0);
            scanCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        int cantidad = Integer.valueOf(qty);
                        edited = true;
                        //Log.d("JCHPTOUT", "IME ACTION DONE cant:" + Integer.toString(cantidad)  );
                        if (cantidad > 0) {
                            boolean doNotExist = true;
                            for (ProductoTerminadoOut ptItem : ptOutScanList) {
                                //Log.d("JCHSCAN", "controlo si ya existe producto y lote :" + ptItem.getDescripcion() + " . " + ptItem.getLote());
                                String listKey = ptItem.getPtKey();
                                String listLot = ptItem.getLote();
                                if (key.equals(ptItem.getPtKey())) {
                                    if (loti.equals(ptItem.getLote())) {
                                        if( pos >=0 ) {
                                            ptItem.setCantidad(cantidad);
                                        } else {
                                            ptItem.addCantidad(cantidad);
                                        }
                                        doNotExist = false;
                                        break;
                                    }
                                }
                            }
                            if (doNotExist) {
                                Log.d("JCHSCAN", "No existe en la lista de carga");
                                final ProductoTerminadoOut pt = new ProductoTerminadoOut(des, key, loti, cantidad);
                                ptOutScanList.add(pt);
                                int position = ptOutScanItemRv.getAdapter().getItemCount() - 1;
                                if (position >= 0) {
                                    ptOutScanItemRv.smoothScrollToPosition(position);
                                }
                                ptoia.notifyDataSetChanged();
                            }

                            doNotExist = true;
                            ProductoTerminadoOut totalIt = new ProductoTerminadoOut(des, "000000", "TOTAL", 0) ;
                            int ix =0 ;
                            for( ProductoTerminadoOut subtotal: ptOutListTotal) {
                                if (subtotal.getDescripcion().equalsIgnoreCase(des)) {
                                    int newCant = subtotal.getCantidad() + cantidad;
                                    subtotal.setCantidad(subtotal.getCantidad() + cantidad);
                                    ptOutListTotal.get(ix).setCantidad(newCant);
                                    doNotExist = false;
                                }
                                ix++;
                            }
                            if (doNotExist) {
                                totalIt.setCantidad(cantidad);
                                ptOutListTotal.add(totalIt);
                            }
                            //todo si pos -1 poner el view mode en modo total y elimina rmensaje de edicion
                            ptoia.notifyDataSetChanged();
                            scanCode.getText().clear();

                        } else {
                            //Log.d("JCHPTOUT", "cantidad cero");
                            Snackbar.make(baseView, "INGRESAR CANTIDAD", Snackbar.LENGTH_SHORT).show();
                        }
                        scanCode.requestFocus();
                        scanCode.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager keyboard = (InputMethodManager) getDialog().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                keyboard.showSoftInput(scanCode, 0);
                            }
                        }, 100);
                        if( pos >=0  ) {

                        } else {
                            if( cantidad == 0 ) {
                                //registro modificado, lo borra

                            }
                        }
                    }

                    return false;
                }
            });
            scanCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //Log.d(TAG, "afterTextChanged:\t" +s.toString());
                    if (s.length() == CODPTLEN) {
                        des = gatTon.getPt(s.toString());
                        prodName.setText(des);
                        if (des.contains("NO EXISTE")) {
                            scanCode.setText("");
                            scanCode.setSelection(0);
                        } else {
                            key = s.toString();
                        }
                        loti = "";
                    }

                    if (s.length() == (CODPTLEN + LOTPTLEN)) {
                        String greg = s.toString().substring(CODPTLEN,(CODPTLEN+LOTPTLEN));
                        loti = new DatesHelper().julianToDdMm(greg);
                        lotNum.setText(loti);
                        qtyItem.setText("");
                    }

                    if (s.length() > (CODPTLEN + LOTPTLEN)) {
                        qty = s.toString().substring((CODPTLEN + LOTPTLEN), s.length());
                        qtyItem.setText(qty);
                        //Log.d("JCHPTOUT", "Cantidad =" + qty);
                    }
                    if (s.length() < (CODPTLEN + LOTPTLEN)) {
                        lotNum.setText("xx/xx");
                        qtyItem.setText("");
                        if (s.length() < 3) {
                            lotNum.setText("");
                            loti = "";
                            key = "";
                            //prodName.setText("");
                            des = " ";
                        }
                    }
                }
            });

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            return true;
        }


        public void hideSoftKeyboard() {
            //https://stackoverflow.com/questions/41725817/hide-to-show-and-hide-keyboard-in-dialogfragment
            try {
                View windowToken = getDialog().getWindow().getDecorView().getRootView();
                InputMethodManager imm = (InputMethodManager) getDialog().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow( windowToken.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception ex) {
                Log.d(TAG, "Error en hide keyboard");
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            Dialog dialog = getDialog();
            if (dialog != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setLayout(width, height);
            }

        }
    }
}
