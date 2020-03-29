package com.leofanti.gat.utils;

import com.leofanti.gat.model.ClienteOut;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.MfgItem;
import com.leofanti.gat.model.Rece;
import com.leofanti.gat.model.SeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MfgHelper {

    private MfgTon mfgTon = MfgTon.getInstance();
    private DatesHelper datesHelper = new DatesHelper();

    public MfgHelper() {;}

    public void saveMfgRec(String stage, Float cantidad, String loteLabel, Rece rece){
        MfgItem mfgItem = new MfgItem();
        HashMap<String, Float> receProd = new HashMap<>();
        receProd = this.setReceta(cantidad, rece);
        mfgItem.setReceKey(rece.getThisKey());
        mfgItem.setReceta(receProd);
        mfgItem.setTimestamp(Const.MFG_OP, datesHelper.getTimestamp());
        mfgItem.setDescripcion(rece.getDescripcion());
        mfgItem.setStatus(stage);
        mfgItem.setVisible(true);
        mfgItem.setCantidad(cantidad);
        mfgItem.setLoteLabel(loteLabel);
        mfgItem.setAlergenType(rece.getAlergenType());
        // calcular la receta y guardar
        mfgTon.saveMfgItem(mfgItem, Const.MFG_OP);

    }

    public void saveRecipe( SeList seItem ){

        setRecipeTrace( seItem);
    }

    //Guarda en la base de trace la receta
    private void setRecipeTrace( SeList seItem){

    }


    private HashMap<String, Float> setReceta(Float loteFab, Rece rece ){
        //TODO parser de dosis especiales (x, etc)
        HashMap<String, Float> receta = new HashMap<>();
        for (Map.Entry<String, Float> entry : rece.getRece().entrySet()) {
            float dosis;
            try{
                dosis = entry.getValue()*rece.getXunit()*Const.RECE_BASE;
            }
            catch (NumberFormatException ex) {
                //TODO aca va el parser especial
                dosis = 0.0F;
            }
            float cantF = dosis * loteFab;
            if( entry.getKey().equalsIgnoreCase("HUEVO FRESCO")){
                int huevo = Math.round(cantF/Const.RECE_HUEVO);
                if( huevo == 0 ) huevo = 1 ;
                cantF = (float) huevo;
            }



            receta.put( entry.getKey(), cantF);
        }
        return receta;
    }

}
