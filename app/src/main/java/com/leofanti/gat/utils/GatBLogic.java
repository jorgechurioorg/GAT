package com.leofanti.gat.utils;

import java.util.Date;

//Esta clase tiene todo lo que hace la logica del negocio
// 1) Accesos a la base de datos
// 2) manejo del usuario (PIN) y persistencia
// 3) ingreso y recupero de MPIN


public class GatBLogic {

    private int icon;
    private String title;
    private String url;
    private String loteInterno;
    private Date fechaIngreso;
    private String proveedorMp;
    private String loteFab;
    private String unidadProd;
    private Date fechaVenc;
    private Date aptitudDias;
    private Date fechaElab;
    private int imagenRotulo;
    private boolean userIn;
    private String userName, userProfile;

    //constructor
    public void GatBlogic()
    {
        this.userIn = false;
        this.userName = null;
        this.userProfile = "user";
    }

    //Guarda en base de datos el ingreso de materia prima
    public void saveItem( String nombreMp, String loteInterno, String loteFab, String  fechaElab, String fechaVenc){}


    /*
    public void setThumbnail(int thumbnail) {
        this.imagenRotulo = thumbnail;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }*/
}
