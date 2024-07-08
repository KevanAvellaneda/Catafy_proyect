package com.example.winestastic;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;

public class Opinion {
    private String idUsuario;
    private String comentario;
    private float calificacion;
    private Object timestamp;
    private String idVinedos;
    private String idEvento;
    private Date fecha;

    // Constructor vacío requerido para Firestore
    public Opinion() {
    }

    // Constructor para comentarios de viñedos o eventos
    public Opinion(String idUsuario, String comentario, float calificacion, String idVinedos, String idEvento) {
        this.idUsuario = idUsuario;
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.idVinedos = idVinedos;
        this.idEvento = idEvento;
        this.timestamp = FieldValue.serverTimestamp();
    }

    // Constructor para comentarios de viñedos o eventos
    public Opinion(String idUsuario, String comentario, float calificacion, String idVinedos, String idEvento, Date fecha) {
        this.idUsuario = idUsuario;
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.idVinedos = idVinedos;
        this.idEvento = idEvento;
        this.timestamp = FieldValue.serverTimestamp();
        this.fecha = fecha;
    }

    // Getter and setter methods for the fields

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public String getIdVinedos() {
        return idVinedos;
    }

    public void setIdVinedos(String idVinedos) {
        this.idVinedos = idVinedos;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public Date obtenerFecha(){
        return fecha;
    }
}
