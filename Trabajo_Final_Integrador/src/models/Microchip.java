package models;

import java.time.LocalDate;

public class Microchip extends Base {

    private String codigo;
    private LocalDate fechaImplantacion;
    private String veterinaria;
    private String observaciones;

    // Constructores
    public Microchip() { super(); }

    // Constructor de Creación (Sin FK)
    public Microchip(String codigo, LocalDate fechaImplantacion, String veterinaria, String observaciones) {
        super();
        this.codigo = codigo;
        this.fechaImplantacion = fechaImplantacion;
        this.veterinaria = veterinaria;
        this.observaciones = observaciones;
    }

    // Constructor de Persistencia (Usado por el DAO, NO incluye mascotaId)
    public Microchip(Long id, Boolean eliminado, String codigo, LocalDate fechaImplantacion, String veterinaria, String observaciones) {
        super(id, eliminado);
        this.codigo = codigo;
        this.fechaImplantacion = fechaImplantacion;
        this.veterinaria = veterinaria;
        this.observaciones = observaciones;
    }

    // Getters Y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public LocalDate getFechaImplantacion() { return fechaImplantacion; }
    public void setFechaImplantacion(LocalDate fechaImplantacion) { this.fechaImplantacion = fechaImplantacion; }
    public String getVeterinaria() { return veterinaria; }
    public void setVeterinaria(String veterinaria) { this.veterinaria = veterinaria; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return "Microchip{" + "id=" + getId() + ", codigo='" + codigo + '\'' + ", eliminado=" + getEliminado() + '}';
    }
}