package com.leofanti.gat.utils;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.ChipGroup;
import com.leofanti.gat.GatTon;
import com.leofanti.gat.R;
import com.leofanti.gat.adapters.ExpAuditRecyclerViewAdapter;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpenseAuditor.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpenseAuditor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseAuditor extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int TODAY = 0;
    private static final int WTD = 1;
    private static final int MTD = 2;
    private static final int LMONTH = 3;
    private static  final int SIXTYD = 4 ;
    private static final int ITEM = 0;
    private static final int PROV = 1;
    private static final int RUBRO = 2;


    private GatTon gatTon = GatTon.getInstance();
    private ExpenseHelper expHelper = new ExpenseHelper();
    private View baseView ;
    private static String hoyEsHoy;

    private static RecyclerView rv;
    private static ExpAuditRecyclerViewAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public ExpenseAuditor() {
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
    public static ExpenseAuditor newInstance(String param1, String param2) {
        ExpenseAuditor fragment = new ExpenseAuditor();
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
        //String diaDesde = expHelper.dateUntil(hoyEsHoy, -60);
        //TODO mejorar esto
        //expHelper.setExpenseList(getContext(), SIXTYD );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        baseView = inflater.inflate(R.layout.card_exp_audit, container, false);
        Toolbar expAuditToolbar = (Toolbar) baseView.findViewById(R.id.expaudit_toolbar);
        expAuditToolbar.setTitle("AUDITOR DE GASTOS");
        expAuditToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        expHelper.normalizeExpenses();
        return baseView;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Configura los listeners de lso filtros
        final DatesHelper datesHelper = new DatesHelper();
        ChipGroup toDate = (ChipGroup) view.findViewById(R.id.expaudit_to_date);
        ChipGroup groupBy = (ChipGroup) view.findViewById(R.id.expaudit_groupby);
        final TextView dateMode = (TextView) view.findViewById(R.id.expaudit_datemode);
        final TextView groupMode = (TextView) view.findViewById(R.id.expaudit_groupmode);
        final TextView montoTotal = (TextView) view.findViewById(R.id.expaudit_total) ;
        rv = (RecyclerView) baseView.findViewById(R.id.expaudit_recycler_view);
        // 2. set layoutManger
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(mLayoutManager);

        expHelper.setDateMode(TODAY);
        expHelper.setGroupMode(ITEM);
        expHelper.setExpenseList(getContext(), TODAY);
        expHelper.setExpenseReport();

        adapter = new ExpAuditRecyclerViewAdapter(getContext(), expHelper.getExpenseList());
        rv.setAdapter(adapter);
        rv.setItemAnimator(new DefaultItemAnimator());

        montoTotal.setText( expHelper.getMontoTotalAsString());
        dateMode.setText(datesHelper.getDateHumanReadable(expHelper.getDateFrom()));
        groupMode.setText(datesHelper.getDateHumanReadable(expHelper.getDateTo()));

        toDate.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                switch( group.getCheckedChipId()) {
                    case R.id.expaudit_today:
                        expHelper.setDateMode(TODAY);
                        expHelper.setExpenseList(getContext(), TODAY);
                        break;

                    case R.id.expaudit_wtd:
                        expHelper.setDateMode(WTD);
                        expHelper.setExpenseList(getContext(), WTD);
                        break;

                    case R.id.expaudit_pastmonth:
                        expHelper.setDateMode(LMONTH);
                        expHelper.setExpenseList(getContext(), LMONTH);
                        break;

                    case R.id.expaudit_mtd:
                        expHelper.setDateMode(MTD);
                        expHelper.setExpenseList(getContext(), MTD);
                        break;

                }
                expHelper.setExpenseReport();
                montoTotal.setText( expHelper.getMontoTotalAsString());
                dateMode.setText(datesHelper.getDateHumanReadable(expHelper.getDateFrom()));
                groupMode.setText(datesHelper.getDateHumanReadable(expHelper.getDateTo()));
                adapter.notifyDataSetChanged();
            }
        });
        groupBy.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                switch( group.getCheckedChipId()) {
                    case R.id.expaudit_item:
                        expHelper.setGroupMode(ITEM);
                        break;

                    case R.id.expaudit_proveedor:
                        expHelper.setGroupMode(PROV);
                        break;

                    case R.id.expaudit_ccosto:
                        expHelper.setGroupMode(RUBRO);
                        break;

                }
                expHelper.setExpenseReport();
                montoTotal.setText(expHelper.getMontoTotalAsString());
                dateMode.setText(expHelper.getDateFrom());
                groupMode.setText(expHelper.getDateTo());
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



    private void getExpensesFromDatabase(int mode){
        //Por default trae los ultimos 60 dias
        //gatTon.getExInList(60);



    }

}

