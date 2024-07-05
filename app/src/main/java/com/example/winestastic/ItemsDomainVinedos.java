package com.example.winestastic;

import java.io.Serializable;

public class ItemsDomainVinedos implements Serializable {
    private String idVinedos;
    private String nombre_vinedos;
    private String ubicacion_vinedos;
    private String horario_vinedos;
    private String url;


    public String getIdVinedos() {
        return idVinedos;
    }
    public String getNombre_vinedos() {
        return nombre_vinedos;
    }

    public void setNombre_vinedos(String nombre_vinedos) {
        this.nombre_vinedos = nombre_vinedos;
    }

    public String getUbicacion_vinedos() {
        return ubicacion_vinedos;
    }

    public void setUbicacion_vinedos(String ubicacion_vinedos) {
        this.ubicacion_vinedos = ubicacion_vinedos;
    }
    public String getHorario_vinedos() {
        return horario_vinedos;
    }

    public void setHorario_vinedos(String horario_vinedos) {
        this.horario_vinedos = horario_vinedos;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public ItemsDomainVinedos(String idVinedos, String nombre_vinedos, String ubicacion_vinedos, String horario_vinedos, String url) {
        this.idVinedos = idVinedos;
        this.nombre_vinedos = nombre_vinedos;
        this.ubicacion_vinedos = ubicacion_vinedos;
        this.horario_vinedos = horario_vinedos;
        this.url = url;
    }
    public ItemsDomainVinedos() {
        // Constructor vacío requerido para Firestore
    }


}
