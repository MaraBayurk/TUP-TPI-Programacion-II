package service;

import DAO.GestorMicrochips;
import models.Microchip;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MicrochipsServiceImpl implements GenericService<Microchip> {
    
    private final GestorMicrochips microchipDAO = new GestorMicrochips();

    @Override
    public Microchip insertar(Microchip microchip) {
        try {
            // Pasa null como ID de Mascota, lo que forzará un error FK si se llama incorrectamente.
            Long id = microchipDAO.crear(null, microchip, 0L); 
            microchip.setId(id);
            return microchip;
        } catch (SQLException e) {
             throw new RuntimeException("Error al insertar Microchip: " + e.getMessage(), e);
        }
    }
    
    @Override public Microchip actualizar(Microchip microchip) { return null; }
    @Override public void eliminar(Long id) { /* ... */ }
    @Override public Microchip getById(Long id) { return null; }
    @Override public List<Microchip> getAll() { return new ArrayList<>(); }
}