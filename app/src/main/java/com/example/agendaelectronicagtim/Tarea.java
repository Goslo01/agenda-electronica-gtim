package com.example.agendaelectronicagtim;

import androidx.annotation.NonNull;

// Clase Tarea con ID y todos los campos
public class Tarea {

    private long id;
    private String titulo;
    private String fechaInicio;
    private String horaInicio;
    private String fechaFin;
    private String horaFin;
    private String responsable;
    private String descripcion;

    // Constructor de una tarea
    public Tarea(long id, String titulo, String fechaInicio, String horaInicio,
                 String fechaFin, String horaFin, String responsable, String descripcion) {
        this.id = id;
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.fechaFin = fechaFin;
        this.horaFin = horaFin;
        this.responsable = responsable;
        this.descripcion = descripcion;
    }

    // *** NUEVO: Getter para ID ***
    public long getId() {
        return id;
    }

    // Métodos para obtener valores (getters)
    public String getTitulo() {
        return titulo;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public String getResponsable() {
        return responsable;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Métodos para cambiar valores (setters)
    public void setId(long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Método para mostrar la tarea en la lista
    @NonNull
    @Override
    public String toString() {
        String texto = titulo + "\nInicio: " + fechaInicio + " " + horaInicio +
                "\nFin: " + fechaFin + " " + horaFin +
                "\nResponsable: " + responsable;

        // Solo agregar descripción si no está vacía
        if (!descripcion.isEmpty()) {
            texto += "\nDescripción: " + descripcion;
        }

        return texto;
    }
}