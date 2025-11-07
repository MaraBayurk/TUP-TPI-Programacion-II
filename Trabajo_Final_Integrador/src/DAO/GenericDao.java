package DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz genérica que define las operaciones CRUD básicas 
 */
public interface GenericDao<T> {

    // Métodos Transaccionales (Aceptan Connection conn)
    Long crear(Connection conn, T entidad) throws SQLException; 
    void actualizar(Connection conn, T entidad) throws SQLException;
    void eliminar(Connection conn, long id) throws SQLException;

    // Métodos de Lectura (Abren su propia conexión)
    T leer(long id) throws SQLException;
    List<T> leerTodos() throws SQLException;
}