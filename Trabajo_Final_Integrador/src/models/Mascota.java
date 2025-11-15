package models;

import java.time.LocalDate;

public class Mascota extends Base {

    // Atributos Propios
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private String duenio;

    // RELACIÓN UNIDIRECCIONAL: Solo A conoce a B
    private Microchip microchip;

    // Constructores
    public Mascota() {
        super();
    }

    // Constructor de Creación (usado por el Service/UI)
    public Mascota(String nombre, String especie, String raza, LocalDate fechaNacimiento, String duenio, Microchip microchip) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.duenio = duenio;
        this.microchip = microchip;
    }

    // Constructor de Persistencia (usado por el DAO)
    public Mascota(Long id, Boolean eliminado, String nombre, String especie, String raza, LocalDate fechaNacimiento, String duenio) {
        super(id, eliminado);
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.duenio = duenio;
    }

    // Getters Y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDuenio() {
        return duenio;
    }

    public void setDuenio(String duenio) {
        this.duenio = duenio;
    }

    public Microchip getMicrochip() {
        return microchip;
    }

    public void setMicrochip(Microchip microchip) {
        this.microchip = microchip;
    }

    @Override
    public String toString() {
        return "Mascota{"
                + "id=" + getId()
                + ", nombre='" + nombre + '\''
                + ", especie='" + especie + '\''
                + ", microchip=" + (microchip != null ? microchip.getCodigo() : "N/A")
                + ", eliminado=" + getEliminado() + '}';
    }
}
