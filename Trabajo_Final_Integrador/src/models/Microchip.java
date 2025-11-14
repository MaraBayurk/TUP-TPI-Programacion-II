package models;

import java.time.LocalDate;

public class Microchip {

    private Long id;
    private Boolean eliminado;
    private String codigo;
    private LocalDate fechaImplantacion; // coincide con la columna DB
    private String veterinaria;
    private String observaciones;
    private Long mascotaId; // FK (puede ser null hasta que la mascota exista)

    // ---------------------------
    // Constructor vacío (por si hace falta)
    // ---------------------------
    public Microchip() {
        this.eliminado = false;
    }

    // ---------------------------
    // Constructor para crear (usado por el menú antes de persistir)
    // ---------------------------
    public Microchip(String codigo, LocalDate fechaImplantacion, String veterinaria, String observaciones) {
        this.codigo = codigo;
        this.fechaImplantacion = fechaImplantacion;
        this.veterinaria = veterinaria;
        this.observaciones = observaciones;
        this.eliminado = false;
    }

    // ---------------------------
    // Constructor completo (usado por el DAO al mapear ResultSet)
    // ---------------------------
    public Microchip(Long id, Boolean eliminado, String codigo,
            LocalDate fechaImplantacion, String veterinaria,
            String observaciones, Long mascotaId) {
        this.id = id;
        this.eliminado = eliminado;
        this.codigo = codigo;
        this.fechaImplantacion = fechaImplantacion;
        this.veterinaria = veterinaria;
        this.observaciones = observaciones;
        this.mascotaId = mascotaId;
    }

    // ---------------------------
    // Getters y Setters
    // ---------------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getFechaImplantacion() {
        return fechaImplantacion;
    }

    public void setFechaImplantacion(LocalDate fechaImplantacion) {
        this.fechaImplantacion = fechaImplantacion;
    }

    public String getVeterinaria() {
        return veterinaria;
    }

    public void setVeterinaria(String veterinaria) {
        this.veterinaria = veterinaria;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(Long mascotaId) {
        this.mascotaId = mascotaId;
    }

    @Override
    public String toString() {
        return "Microchip{"
                + "id=" + id
                + ", codigo='" + codigo + '\''
                + ", fechaImplantacion=" + fechaImplantacion
                + ", veterinaria='" + veterinaria + '\''
                + ", observaciones='" + observaciones + '\''
                + ", mascotaId=" + mascotaId
                + ", eliminado=" + eliminado
                + '}';
    }
}
