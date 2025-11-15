package DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface GenericDAO<T> {

    Long crear(Connection conn, T entidad) throws SQLException; 
    void actualizar(Connection conn, T entidad) throws SQLException;
    void eliminar(Connection conn, long id) throws SQLException;

    T leer(long id) throws SQLException;
    List<T> leerTodos() throws SQLException;
}
