package models;

import java.time.LocalDate;

public class Mascota extends Base {

    // Atributos Propios (Mapean a columnas en la tabla Mascotas)
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private String duenio;

    // 💡 REFERENCIA UNIDIRECCIONAL 1:1
    // La Mascota (A) conoce a su Microchip (B)
    private Microchip microchip;

    // ===========================================
    // CONSTRUCTORES
    // ===========================================
    // 1. Constructor Vacío
    public Mascota() {
        super(); // Llama al constructor de Base() para inicializar 'eliminado = false'
    }

    // 2. Constructor de Creación (Desde la Capa Service/UI, sin ID)
    public Mascota(String nombre, String especie, String raza, LocalDate fechaNacimiento, String duenio, Microchip microchip) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.duenio = duenio;
        this.microchip = microchip;
    }

    // 3. Constructor de Persistencia (Usado por el DAO al leer de la DB)
    public Mascota(Long id, Boolean eliminado, String nombre, String especie, String raza, LocalDate fechaNacimiento, String duenio) {
        super(id, eliminado); // Llama al constructor base para ID y eliminado
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.duenio = duenio;
    }

    // ===========================================
    // GETTERS Y SETTERS
    // ===========================================
    // Nota: Los métodos getId() y getEliminado() son heredados de Base.
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
        return "Mascota{" + "id=" + getId() + ", nombre='" + nombre + '\'' + ", especie='" + especie + '\'' + ", microchip=" + (microchip != null ? microchip.getCodigo() : "N/A") + ", eliminado=" + getEliminado() + '}';
    }
}
