package models;

import java.time.LocalDate;

public class Microchips extends Base {

    // Atributos Propios (Mapean a columnas en la tabla Microchips)
    private String codigo;
    private LocalDate fechaImplantacion;
    private String veterinaria;
    private String observaciones;

    // 💡 Clave Foránea (FK) para el DAO
    // Este campo es crucial para que el DAO asocie el Microchip a la Mascota.
    private Long mascotaId;

    // ===========================================
    // CONSTRUCTORES
    // ===========================================
    // 1. Constructor Vacío
    public Microchips() {
        super();
    }

    // 2. Constructor de Creación
    public Microchips(String codigo, LocalDate fechaImplantacion, String veterinaria, String observaciones, Long mascotaId) {
        this.codigo = codigo;
        this.fechaImplantacion = fechaImplantacion;
        this.veterinaria = veterinaria;
        this.observaciones = observaciones;
        this.mascotaId = mascotaId;
    }

    // 3. Constructor de Persistencia (Usado por el DAO al leer de la DB)
    public Microchips(Long id, Boolean eliminado, String codigo, LocalDate fechaImplantacion, String veterinaria, String observaciones, Long mascotaId) {
        super(id, eliminado);
        this.codigo = codigo;
        this.fechaImplantacion = fechaImplantacion;
        this.veterinaria = veterinaria;
        this.observaciones = observaciones;
        this.mascotaId = mascotaId;
    }

    // ===========================================
    // GETTERS Y SETTERS
    // ===========================================
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
        return "Microchip{" + "id=" + getId() + ", codigo='" + codigo + '\'' + ", mascotaId=" + mascotaId + ", eliminado=" + getEliminado() + '}';
    }
}
