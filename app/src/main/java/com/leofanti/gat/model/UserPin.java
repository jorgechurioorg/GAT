package com.leofanti.gat.model;

public class UserPin {
    /**
     * Modelo de datos del usuario OPERADOR (no es el que autentica en la base de datos
     * Estrategia de acceso: avatar + PIN
     */
    private String nick;
    private String fullname;
    private Long pin;
    private String pincrypto;
    private String role;
    private boolean active = true;
    private String thisKey;

    public UserPin() {

    }

    public String getFullname() {

        return fullname;
    }

    public String getNick() {

        return nick;
    }

    public String getPin() {
        if( pin == null)
            return "0000";
        else
            return pin.toString();
    }

    public String getRole() {
        return role;
    }

    public String getPincrypto() {
        return pincrypto;
    }


    public void setThisKey(String thisKey) {

        this.thisKey = thisKey;
    }

    public String getThisKey() {
        return thisKey;
    }

    public void setPincrypto(String pincrypto) {
        https:
//stackoverflow.com/questions/4846484/md5-hashing-in-android
        this.pincrypto = pincrypto;
    }

    public boolean checkPin(String pin) {

        String pinString = this.pin.toString();
        return pinString.equalsIgnoreCase(pin);
    }
}