package com.leofanti.gat.model;

import com.google.firebase.database.Exclude;

public class Labels {
    /**
     * Modelo de datos para etiquetas/rotulos
     */
    private String name;
    private String fullname;
    private String labelUrl;
    private String grupo;
    private String fileName;
    private String fileExt;
    private boolean active = true;
    private Long fileDate;
    private Long fileSize;

    private String thisKey;

    public Labels() {
    }

    public Long getFileDate() {
        return fileDate;
    }

    public void setFileDate(Long fileDate) {
        this.fileDate = fileDate;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getLabelUrl() {
        return labelUrl;
    }

    public void setLabelUrl(String labelUrl) {
        this.labelUrl = labelUrl;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
    @Exclude
    public void setThisKey(String thisKey) {
        this.thisKey = thisKey;
    }
    @Exclude
    public String getThisKey() {
        return thisKey;
    }

}