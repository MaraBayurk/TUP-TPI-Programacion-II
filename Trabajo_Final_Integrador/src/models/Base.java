package models;

//Clase base abstracta para todas las entidades.
public abstract class Base {

    private Long id;
    private boolean eliminado = false;

    protected Base(Long id, boolean eliminado) {
        this.id = id;
        this.eliminado = eliminado;
    }

    protected Base() {
        this.eliminado = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
}
