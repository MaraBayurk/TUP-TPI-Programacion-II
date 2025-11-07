package service;

import DAO.GestorMascotas;
import DAO.GestorMicrochips;
import config.DatabaseConnection;
import models.Mascotas;
import models.Microchips;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MascotasServiceImpl implements GenericService<Mascotas> {

    // Instancias de DAOs (Gestores)
    private final GestorMascotas mascotaDAO = new GestorMascotas();
    private final GestorMicrochips microchipDAO = new GestorMicrochips();

    //  CREAR (INSERTAR) - TRANSACCIÓN OBLIGATORIA (A + B)
    @Override
    public Mascotas insertar(Mascotas mascota) {

        // 1. Validaciones (Regla de Negocio)
        validarMascota(mascota);

        Connection conn = null;

        try {
            // 2. Abrir Transacción y obtener la Conexión Compartida
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // <--- INICIO de la Transacción

            // 3. Ejecutar Operación Compuesta (Crear Mascota y Microchip)
            // 3.1. Crear Mascota (A) para obtener el ID generado
            Long mascotaId = mascotaDAO.crear(conn, mascota);
            mascota.setId(mascotaId);

            // 3.2. Crear Microchip (B) usando el ID de Mascota como FK
            Microchips microchip = mascota.getMicrochip();
            if (microchip != null && mascotaId != null) {
                // Prepara el Microchip con la FK recién generada
                microchip.setMascotaId(mascotaId);

                // Insertar el microchip. La DB garantiza el 1:1 con la FK UNIQUE.
                Long microchipId = microchipDAO.crear(conn, microchip);
                microchip.setId(microchipId);
            }

            // 4. Commit si todo fue OK
            conn.commit();
            return mascota;

        } catch (Exception e) {
            // 5. Rollback ante cualquier error (SQL, Formato, Validación)
            try {
                if (conn != null) {
                    conn.rollback(); // <--- ROLLBACK
                }
            } catch (SQLException rollbackEx) {
                // Log: Error al intentar Rollback
            }
            // Re-lanzar para que la Capa Main/UI lo maneje
            throw new RuntimeException("Fallo en la transacción de inserción de Mascota-Microchip. Causa: " + e.getMessage(), e);
        } finally {
            // 6. Restablecer y Cerrar Recursos
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Restablecer AutoCommit
                    conn.close();             // Cerrar la conexión
                }
            } catch (SQLException closeEx) {
                // Log: Error al cerrar la conexión
            }
        }
    }

    //  ACTUALIZAR - Transacción Simple (Actualiza A y B si existen)
    @Override
    public Mascotas actualizar(Mascotas mascota) {
        validarMascota(mascota);
        Connection conn = null;
        try {
            // Inicia la transacción para actualizar A y B
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Actualizar Mascota (A)
            mascotaDAO.actualizar(conn, mascota);

            // 2. Actualizar Microchip asociado si existe
            Microchips microchip = mascota.getMicrochip();
            if (microchip != null && microchip.getId() != null) {
                microchipDAO.actualizar(conn, microchip);
            }

            conn.commit();
            return mascota;

        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                /* log */ }
            throw new RuntimeException("Fallo en la transacción de actualización.", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                /* log */ }
        }
    }

    //  ELIMINAR, LEER y LISTAR (No Transaccional)
    @Override
    public void eliminar(Long id) {
        try {
            // El Microchip (B) se eliminará automáticamente por el ON DELETE CASCADE de la DB.
            mascotaDAO.eliminar(null, id); // Pasa null porque no es parte de una transacción externa
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar Mascota ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Mascotas getById(Long id) {
        try {
            // 1. Leer Mascota (A)
            Mascotas mascota = mascotaDAO.leer(id);

            if (mascota != null) {
                // 2. Cargar Microchip (B) para completar la relación (Hydration)
                Microchips microchip = microchipDAO.leerPorMascotaId(id);
                mascota.setMicrochip(microchip);
            }
            return mascota;
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer Mascota ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<Mascotas> getAll() {
        try {
            return mascotaDAO.leerTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar Mascotas: " + e.getMessage(), e);
        }
    }

    // 📌 Validaciones (Reglas de Negocio)
    private void validarMascota(Mascotas mascota) {
        if (mascota.getNombre() == null || mascota.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        }
        if (mascota.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La validación falló: La fecha de nacimiento es obligatoria.");
        }
        // Validación 1->1: Impedir crear un microchip si la mascota ya tiene uno (aunque la DB lo garantiza)
        if (mascota.getMicrochip() != null && mascota.getMicrochip().getId() != null) {
            // Esta validación se aplica en 'insertar' antes de la DB.
            // Aquí se puede verificar que el código no esté vacío.
            if (mascota.getMicrochip().getCodigo() == null || mascota.getMicrochip().getCodigo().isEmpty()) {
                throw new IllegalArgumentException("El código del microchip asociado no puede estar vacío.");
            }
        }
    }
}
