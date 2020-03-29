package com.leofanti.gat;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.leofanti.gat.adapters.ExpAuditRecyclerViewAdapter;
import com.leofanti.gat.adapters.PtProdRecyclerView;
import com.leofanti.gat.model.PtProducido;
import com.leofanti.gat.utils.DatesHelper;
import com.leofanti.gat.utils.DbGetDataListener;
import com.leofanti.gat.utils.ExpenseHelper;
import com.leofanti.gat.utils.PtProdHelper;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HojaProdAuditor.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HojaProdAuditor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HojaProdAuditor extends DialogFragment {
    // Listado de hoja de produccion por segments de tiempo
    // por todo el segmento o por dia (scrollable table)
    //TODO ¿agrupar por algun criterio adicional?
    private static final int TODAY = 0;
    private static final int WTD = 1;
    private static final int MTD = 2;
    private static final int LMONTH = 3;
    private static  final int SIXTYD = 4 ;
    private static final int COLLAPSE = 0;
    private static final int EXPAND = 1;


    private GatTon gatTon = GatTon.getInstance();
    private PtProdHelper ptProdHelper = new PtProdHelper();
    private View baseView ;
    private static String hoyEsHoy;

    private static RecyclerView rv;
    private static PtProdRecyclerView adapter;

    private OnFragmentInteractionListener mListener;

    public HojaProdAuditor() {
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
    public static HojaProdAuditor newInstance(String param1, String param2) {
        HojaProdAuditor fragment = new HojaProdAuditor();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
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
        DateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd");
        Date today = new Date();
        hoyEsHoy = timestampFormat.format(today);
        ptProdHelper.setHoyEsHoy( hoyEsHoy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        baseView = inflater.inflate(R.layout.card_ptprod_audit, container, false);
        Toolbar expAuditToolbar = (Toolbar) baseView.findViewById(R.id.ptprodaudit_toolbar);
        expAuditToolbar.setTitle("AUDITOR DE PRODUCIDO");
        expAuditToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return baseView;
    }

    ArrayList<PtProducido> hojaProducido = new ArrayList<>();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        //Configura los listeners de lso filtros
        ChipGroup toDate = (ChipGroup) view.findViewById(R.id.ptprodaudit_to_date);
        final TextView dateMode = (TextView) view.findViewById(R.id.ptprodaudit_datemode);
        final TextView groupMode = (TextView) view.findViewById(R.id.ptprodaudit_groupmode);
        final TextView montoTotal = (TextView) view.findViewById(R.id.ptprodaudit_total) ;
        rv = (RecyclerView) baseView.findViewById(R.id.ptprodaudit_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);
        rv.setLayoutManager(mLayoutManager);
        DatesHelper datesHelper = new DatesHelper();
        String[] rangoDate = datesHelper.dateUntil(hoyEsHoy,-60);
        gatTon.setProducidoMatrix(rangoDate[1], hoyEsHoy, new DbGetDataListener< Map<String, Map<String, Long>> > () {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(final Map<String, Map<String, Long>> mtx) {
                ptProdHelper.setMatrix(mtx);
                ptProdHelper.setDateMode(MTD);
                dateMode.setText(ptProdHelper.getfD());
                groupMode.setText(ptProdHelper.getfH());
                hojaProducido = ptProdHelper.getHojaProdReport();
                String total = ptProdHelper.getGrandTotal();
                montoTotal.setText( total );
                adapter = new PtProdRecyclerView(getContext(), hojaProducido);
                rv.setAdapter(adapter);
                rv.setItemAnimator(new DefaultItemAnimator());
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
            }

        });

        toDate.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                switch( group.getCheckedChipId()) {
                    case R.id.ptprodaudit_today:
                        ptProdHelper.setDateMode(TODAY);
                        break;

                    case R.id.ptprodaudit_wtd:
                        ptProdHelper.setDateMode(WTD);
                        break;

                    case R.id.ptprodaudit_pastmonth:
                        ptProdHelper.setDateMode(LMONTH);
                        break;

                    case R.id.ptprodaudit_mtd:
                        ptProdHelper.setDateMode(MTD);
                        break;

                }
                dateMode.setText(ptProdHelper.getfD());
                groupMode.setText(ptProdHelper.getfH());
                hojaProducido = ptProdHelper.getHojaProdReport();
                String total = ptProdHelper.getGrandTotal();
                montoTotal.setText( total );
                adapter.notifyDataSetChanged();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        mListener = null;
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

}

