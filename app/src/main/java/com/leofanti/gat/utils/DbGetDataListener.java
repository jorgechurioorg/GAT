package com.leofanti.gat.utils;

import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.model.MateriaPrimaIn;

import java.util.ArrayList;

public interface DbGetDataListener<O> {
    public void onStart();
    public void onSuccess(O list);
    public void onFailed(DatabaseError databaseError);
}