package com.leofanti.gat.utils;


public interface DbReadOnlyTables {
    public void onStart();
    public void onSuccess();
    public void onFailed(String error);
}