package service;

import DAO.GestorMascotas;
import DAO.GestorMicrochips;
import config.DatabaseConnection;
import models.Mascota;
import models.Microchip;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MascotasServiceImpl implements GenericService<Mascota> {

    // Instancias de DAOs (Gestores)
    private final GestorMascotas mascotaDAO = new GestorMascotas();
    private final GestorMicrochips microchipDAO = new GestorMicrochips();

    // ====================================================================
    // 📌 CREAR (INSERTAR) - TRANSACCIÓN OBLIGATORIA (A + B)
    // ====================================================================
    @Override
    public Mascota insertar(Mascota mascota) {

        // 1. Validaciones (Requeridas por el TFI)
        validarMascota(mascota);

        Connection conn = null;

        try {
            // 2. INICIO de la Transacción
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 3. Crear Mascota (A): El DAO genera el ID y lo setea en el objeto 'mascota'
            Long mascotaId = mascotaDAO.crear(conn, mascota);

            // 4. Crear Microchip (B)
            Microchip microchip = mascota.getMicrochip();
            if (microchip != null && mascotaId != null) {
                // 💡 Solución de Unidireccionalidad y FK: Pasamos el ID generado al DAO.
                Long microchipId = microchipDAO.crear(
                        conn,
                        microchip,
                        mascotaId // Pasa el ID como argumento FK
                );
                microchip.setId(microchipId);
            }

            // 5. COMMIT
            conn.commit();
            return mascota;

        } catch (Exception e) {
            // 6. ROLLBACK
            try {
                if (conn != null) {
                    System.err.println("🚨 Ejecutando ROLLBACK...");
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Error al ejecutar ROLLBACK: " + rollbackEx.getMessage());
            }
            // Re-lanzar la excepción para que el Main/UI maneje el error.
            throw new RuntimeException("Fallo en la transacción de inserción Mascota-Microchip. Causa: " + e.getMessage(), e);
        } finally {
            // 7. Cierre de Recursos
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("❌ Error al cerrar la conexión: " + closeEx.getMessage());
            }
        }
    }

    // ====================================================================
    // 📌 RESTO DE MÉTODOS CRUD (Completos)
    // ====================================================================
    @Override
    public Mascota actualizar(Mascota mascota) {
        validarMascota(mascota);
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Actualizar Mascota (A)
            mascotaDAO.actualizar(conn, mascota);

            // 2. Actualizar Microchip asociado si existe
            Microchip microchip = mascota.getMicrochip();
            if (microchip != null && microchip.getId() != null) {
                // Notar que el DAO de Microchip no necesita el mascotaId aquí
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
            throw new RuntimeException("Fallo en la transacción de actualización. Causa: " + e.getMessage(), e);
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

    @Override
    public void eliminar(Long id) {
        try {
            // Pasa null indicando que el DAO debe manejar su propia conexión (operación simple)
            mascotaDAO.eliminar(null, id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar Mascota ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Mascota getById(Long id) {
        try {
            // 1. Leer Mascota (A)
            Mascota mascota = mascotaDAO.leer(id);

            if (mascota != null) {
                // 2. Cargar Microchip (B) para completar la relación (Hydration)
                Microchip microchip = microchipDAO.leerPorMascotaId(id);
                mascota.setMicrochip(microchip);
            }
            return mascota;
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer Mascota ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<Mascota> getAll() {
        try {
            return mascotaDAO.leerTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar Mascotas: " + e.getMessage(), e);
        }
    }

    // 📌 Validaciones (Reglas de Negocio Simples)
    private void validarMascota(Mascota mascota) {
        if (mascota.getNombre() == null || mascota.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        }
        if (mascota.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La validación falló: La fecha de nacimiento es obligatoria.");
        }
        if (mascota.getMicrochip() != null && (mascota.getMicrochip().getCodigo() == null || mascota.getMicrochip().getCodigo().isEmpty())) {
            throw new IllegalArgumentException("El código del microchip asociado no puede estar vacío.");
        }
    }
}
