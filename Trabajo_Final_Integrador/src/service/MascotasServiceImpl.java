package service;

import DAO.GestorMascotas;
import DAO.GestorMicrochips;
import config.DatabaseConnection;
import models.Mascota;
import models.Microchip;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MascotasServiceImpl implements GenericService<Mascota> {

    private final GestorMascotas mascotaDAO = new GestorMascotas();
    private final GestorMicrochips microchipDAO = new GestorMicrochips();

    @Override
    public Mascota insertar(Mascota mascota) {
        validarMascota(mascota);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Long mascotaId = mascotaDAO.crear(conn, mascota);

            Microchip microchip = mascota.getMicrochip();
            if (microchip != null && mascotaId != null) {
                Long microchipId = microchipDAO.crear(conn, microchip, mascotaId);
                microchip.setId(microchipId);
                microchip.setMascotaId(mascotaId);
            }

            conn.commit();
            return mascota;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ignored) {
            }
            throw new RuntimeException("Error en transacción Mascota+Microchip: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    @Override
    public Mascota actualizar(Mascota mascota) {
        validarMascota(mascota);
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            mascotaDAO.actualizar(conn, mascota);

            Microchip microchip = mascota.getMicrochip();
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
            } catch (SQLException ignored) {
            }
            throw new RuntimeException("Error al actualizar: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            mascotaDAO.eliminar(null, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mascota getById(Long id) {
        try {
            Mascota mascota = mascotaDAO.leer(id);
            if (mascota != null) {
                Microchip microchip = microchipDAO.leerPorMascotaId(id);
                mascota.setMicrochip(microchip);
            }
            return mascota;
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer mascota: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Mascota> getAll() {
        try {
            List<Mascota> mascotas = mascotaDAO.leerTodos();

            for (Mascota m : mascotas) {
                Microchip chip = microchipDAO.leerPorMascotaId(m.getId());
                m.setMicrochip(chip);
            }

            return mascotas;

        } catch (SQLException e) {
            throw new RuntimeException("Error al leer todas las mascotas: " + e.getMessage(), e);
        }
    }

    private void validarMascota(Mascota mascota) {
        if (mascota.getNombre() == null || mascota.getNombre().isEmpty()) {
            throw new IllegalArgumentException("Nombre obligatorio.");
        }
        if (mascota.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("Fecha nacimiento obligatoria.");
        }
        if (mascota.getMicrochip() != null && (mascota.getMicrochip().getCodigo() == null || mascota.getMicrochip().getCodigo().isEmpty())) {
            throw new IllegalArgumentException("Código microchip obligatorio.");
        }
    }
}
