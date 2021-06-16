package com.example.fcontreras.augercsachecker;

public class AlarmItemList {
    int ID;
    String nombre;
    String detalle;
    String last;
    int estado;
    String hijos;
    String ack; // It is the name of the user who makes the ack
    boolean silent; // It says if the alarm is silent by the user

    public AlarmItemList(int ID, String nombre, String detalle, int estado, String last, String hijos, String ack, boolean silent ) {
        this.ID = ID;
        this.nombre = nombre;
        this.detalle = detalle;
        this.estado = estado;
        this.last = last;
        this.hijos = hijos;
        this.ack = ack;
        this.silent = silent;
    }
}
