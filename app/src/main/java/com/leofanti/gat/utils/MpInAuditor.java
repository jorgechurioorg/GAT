package com.leofanti.gat.utils;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import com.leofanti.gat.GatTon;
import com.leofanti.gat.R;
import com.leofanti.gat.adapters.Col4RecyclerViewAdapter;
import com.leofanti.gat.model.StrFourColReport;
import com.leofanti.gat.model.TraceLinkedList;

import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MpInAuditor.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MpInAuditor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MpInAuditor extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER



    private GatTon gatTon = GatTon.getInstance();
    private MpInHelper mpInHelper = new MpInHelper();
    private View baseView ;
    private static String hoyEsHoy;

    private static RecyclerView rv;
    private static Col4RecyclerViewAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public MpInAuditor() {
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
    public static MpInAuditor newInstance(String param1, String param2) {
        MpInAuditor fragment = new MpInAuditor();
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
        DateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd");
        Date today = new Date();
        hoyEsHoy = timestampFormat.format(today);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        baseView = inflater.inflate(R.layout.card_mpin_audit, container, false);
        Toolbar expAuditToolbar = (Toolbar) baseView.findViewById(R.id.mpinaudit_toolbar);
        expAuditToolbar.setTitle("AUDITOR MPIN");
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

        //ChipGroup toDate = (ChipGroup) view.findViewById(R.id.mpinaudit_grouper);
        ChipGroup groupBy = (ChipGroup) view.findViewById(R.id.mpinaudit_cant);
        final TextView bread1 = (TextView) view.findViewById(R.id.mpinaudit_bread1);
        final TextView bread2 = (TextView) view.findViewById(R.id.mpinaudit_bread2);
        final TextView bread3 = (TextView) view.findViewById(R.id.mpinaudit_bread3) ;
        rv = (RecyclerView) baseView.findViewById(R.id.mpinaudit_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);
        rv.setLayoutManager(mLayoutManager);

        gatTon.getMpInTrace("all", 6, new DbGetDataListener<ArrayList<TraceLinkedList>>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(final ArrayList<TraceLinkedList> gotList) {
                mpInHelper.setTraceData(gotList);
                mpInHelper.arrangeReport();
                ArrayList<StrFourColReport> listToDisplay = new ArrayList<>();
                listToDisplay = mpInHelper.get4ColReport();
                adapter = new Col4RecyclerViewAdapter(getContext(), listToDisplay );
                rv.setAdapter(adapter);
                rv.setItemAnimator(new DefaultItemAnimator());
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                //DO SOME THING WHEN GET DATA FAILED HERE
            }
        });


        groupBy.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                switch( group.getCheckedChipId()) {
                    case R.id.expaudit_item:
                        //pHelper.setGroupMode(ITEM);
                        break;

                    case R.id.expaudit_proveedor:
                        //expHelper.setGroupMode(PROV);
                        break;

                    case R.id.expaudit_ccosto:
                        //expHelper.setGroupMode(RUBRO);
                        break;

                }
                //expHelper.setExpenseReport();
                //montoTotal.setText( Float.toString(expHelper.getMontoTotal()));
                //dateMode.setText(expHelper.getDateFrom());
                //groupMode.setText(expHelper.getDateTo());
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

