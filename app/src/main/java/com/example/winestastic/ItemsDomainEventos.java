package com.example.winestastic;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class ItemsDomainEventos implements Serializable {

    private String nombre_evento;
    private String ubicacion_evento;

    private String url;
    private Timestamp fecha_eventoo; // Cambiado a tipo Timestamp para almacenar la fecha como un objeto Timestamp


    public String getNombre_evento() {
        return nombre_evento;
    }

    public void setNombre_evento(String nombre_evento) {
        this.nombre_evento = nombre_evento;
    }

    public String getUbicacion_evento() {
        return ubicacion_evento;
    }

    public void setUbicacion_evento(String ubicacion_evento) {
        this.ubicacion_evento = ubicacion_evento;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getFecha_eventoo() {
        return fecha_eventoo;
    }


    public ItemsDomainEventos(String nombre_evento, String ubicacion_evento, String url) {
        this.nombre_evento = nombre_evento;
        this.ubicacion_evento = ubicacion_evento;
        this.url = url;
        this.fecha_eventoo = fecha_eventoo;
    }
    public ItemsDomainEventos() {
        // Constructor vacío requerido para Firestore
    }


}
