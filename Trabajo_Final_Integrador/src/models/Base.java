package models;

/**
 * Clase base abstracta para todas las entidades del sistema (Mascotas,
 * Microchips, etc.). Implementa el patrón de borrado lógico (soft delete)
 * mediante los campos 'id' y 'eliminado'.
 */
public abstract class Base {

    /* Identificador único de la entidad (BIGINT en MySQL) */
    private Long id;

    /* Flag de eliminación lógica (soft delete). false por defecto. */
    private Boolean eliminado = false;

    // Constructores
    /**
     * Constructor usado por el DAO al leer una entidad existente de la base de
     * datos.
     *
     * @param id El ID de la entidad.
     * @param eliminado El estado de eliminación lógica.
     */
    protected Base(Long id, Boolean eliminado) {
        this.id = id;
        this.eliminado = eliminado;
    }

    /**
     * Constructor por defecto (usado para crear una nueva entidad en memoria).
     * Las entidades nuevas inician con eliminado = false.
     */
    protected Base() {
        this.eliminado = false;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    /**
     * Setea el estado de eliminación lógica (true para eliminar).
     *
     * @param eliminado Estado de baja lógica.
     */
    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }
}
