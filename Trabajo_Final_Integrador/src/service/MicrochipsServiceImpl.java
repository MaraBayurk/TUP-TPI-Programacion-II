package service;

import DAO.GestorMicrochips;
import models.Microchips;

import java.sql.SQLException;
import java.util.List;

public class MicrochipsServiceImpl implements GenericService<Microchips> {

    private final GestorMicrochips microchipDAO = new GestorMicrochips();

    // Las operaciones CRUD de B son simples llamadas al DAO.
    @Override
    public Microchips insertar(Microchips microchip) {
        try {
            validarMicrochip(microchip);
            // Pasa null indicando que NO es parte de una transacción externa (usa el AutoCommit del DAO)
            Long id = microchipDAO.crear(null, microchip);
            microchip.setId(id);
            return microchip;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar Microchip: " + e.getMessage(), e);
        }
    }

    @Override
    public Microchips actualizar(Microchips microchip) {
        try {
            validarMicrochip(microchip);
            microchipDAO.actualizar(null, microchip);
            return microchip;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar Microchip: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            microchipDAO.eliminar(null, id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar Microchip: " + e.getMessage(), e);
        }
    }

    @Override
    public Microchips getById(Long id) {
        try {
            return microchipDAO.leer(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer Microchip: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Microchips> getAll() {
        try {
            return microchipDAO.leerTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar Microchips: " + e.getMessage(), e);
        }
    }

    // 📌 Validaciones (Reglas de Negocio)
    private void validarMicrochip(Microchips microchip) {
        if (microchip.getCodigo() == null || microchip.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("La validación falló: El código del microchip es obligatorio.");
        }
        if (microchip.getFechaImplantacion() == null) {
            throw new IllegalArgumentException("La validación falló: La fecha de implantación es obligatoria.");
        }
    }
}
