package service;

import DAO.GestorMicrochips;
import config.DatabaseConnection;
import models.Microchip;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MicrochipsServiceImpl implements GenericService<Microchip> {

    private final GestorMicrochips microchipDAO = new GestorMicrochips();

    @Override
    public Microchip insertar(Microchip microchip) {
        throw new UnsupportedOperationException("Crear microchips debe hacerse en la transacción Mascota+Microchip.");
    }

    @Override
    public Microchip actualizar(Microchip microchip) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            microchipDAO.actualizar(conn, microchip);
            return microchip;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar microchip: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            microchipDAO.eliminar(conn, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Microchip getById(Long id) {
        try { return microchipDAO.leer(id); } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Microchip> getAll() {
        try { return microchipDAO.leerTodos(); } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
