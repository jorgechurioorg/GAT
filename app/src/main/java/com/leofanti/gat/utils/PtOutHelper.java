package com.leofanti.gat.utils;

import com.leofanti.gat.model.ClienteOut;
import com.leofanti.gat.model.Const;

import java.util.ArrayList;

public class PtOutHelper {

    public PtOutHelper() {;}

    private ArrayList<ClienteOut> wholeList = new ArrayList<>();

    public void setWholeList(ArrayList<ClienteOut> wholeList){
        this.wholeList = wholeList;
    }

    public ArrayList<ClienteOut> getDisplayList( boolean op, boolean sh, boolean fl){
        ArrayList<ClienteOut> clienteOutList = new ArrayList<>();
        for( ClienteOut clienteOut: wholeList ) {
            switch (clienteOut.getStatus()){
                case Const.NEW:
                case Const.OPEN:
                    if( op ) clienteOutList.add(clienteOut);
                    break;

                case Const.CLOSED:
                case Const.SHIPPED:
                    if( sh ) clienteOutList.add(clienteOut);
                    break;

                default:
                    if( fl ) clienteOutList.add(clienteOut);
            }

        }
        return clienteOutList;
    }


}
