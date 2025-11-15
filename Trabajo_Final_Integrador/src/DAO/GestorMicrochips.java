package DAO;

import config.DatabaseConnection;
import models.Microchip;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorMicrochips implements GenericDAO<Microchip> {

    // ----------------------------------------------------------
    // MAPEAR RESULTSET -> Microchip
    // ----------------------------------------------------------
    private Microchip crearObjetoMicrochip(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("fechaImplantacion");
        LocalDate fecha = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        Long mascotaId = rs.getLong("mascota_id");
        if (rs.wasNull()) {
            mascotaId = null;
        }

        return new Microchip(
                rs.getLong("id"),
                rs.getBoolean("eliminado"),
                rs.getString("codigo"),
                fecha,
                rs.getString("veterinaria"),
                rs.getString("observaciones"),
                mascotaId
        );
    }

    @Override
    public Long crear(Connection conn, Microchip microchip) throws SQLException {
        throw new UnsupportedOperationException("Use crear(conn, microchip, mascotaId) para insertar microchip con FK.");
    }

    public Long crear(Connection conn, Microchip microchip, Long mascotaId) throws SQLException {
        String sql = "INSERT INTO Microchips (codigo, fechaImplantacion, veterinaria, observaciones, mascota_id) VALUES (?, ?, ?, ?, ?)";
        Long generatedId = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, microchip.getCodigo());

            if (microchip.getFechaImplantacion() != null) {
                stmt.setDate(2, Date.valueOf(microchip.getFechaImplantacion()));
            } else {
                stmt.setNull(2, Types.DATE);
            }

            stmt.setString(3, microchip.getVeterinaria());
            stmt.setString(4, microchip.getObservaciones());

            if (mascotaId != null) {
                stmt.setLong(5, mascotaId);
            } else {
                stmt.setNull(5, Types.BIGINT);
            }

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("No se insertó microchip.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    generatedId = keys.getLong(1);
                    microchip.setId(generatedId);
                    microchip.setMascotaId(mascotaId);
                }
            }
        }

        return generatedId;
    }

    @Override
    public void actualizar(Connection conn, Microchip microchip) throws SQLException {
        boolean closeConn = (conn == null);
        if (closeConn) {
            conn = DatabaseConnection.getConnection();
        }

        String sql = "UPDATE Microchips SET codigo = ?, fechaImplantacion = ?, veterinaria = ?, observaciones = ? WHERE id = ? AND (eliminado = FALSE OR eliminado IS NULL)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, microchip.getCodigo());

            if (microchip.getFechaImplantacion() != null) {
                stmt.setDate(2, Date.valueOf(microchip.getFechaImplantacion()));
            } else {
                stmt.setNull(2, Types.DATE);
            }

            stmt.setString(3, microchip.getVeterinaria());
            stmt.setString(4, microchip.getObservaciones());
            stmt.setLong(5, microchip.getId());

            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new SQLException("No se actualizó microchip ID " + microchip.getId());
            }
        } finally {
            if (closeConn && conn != null) {
                conn.close();
            }
        }
    }

    @Override
    public void eliminar(Connection conn, long id) throws SQLException {
        boolean closeConn = (conn == null);
        if (closeConn) {
            conn = DatabaseConnection.getConnection();
        }

        String sql = "UPDATE Microchips SET eliminado = TRUE WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } finally {
            if (closeConn && conn != null) {
                conn.close();
            }
        }
    }

    @Override
    public Microchip leer(long id) throws SQLException {
        String sql = "SELECT id, eliminado, codigo, fechaImplantacion, veterinaria, observaciones, mascota_id FROM Microchips WHERE id = ? AND (eliminado = FALSE OR eliminado IS NULL)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return crearObjetoMicrochip(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Microchip> leerTodos() throws SQLException {
        String sql = "SELECT id, eliminado, codigo, fechaImplantacion, veterinaria, observaciones, mascota_id FROM Microchips WHERE eliminado = FALSE OR eliminado IS NULL ORDER BY id";
        List<Microchip> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(crearObjetoMicrochip(rs));
            }
        }
        return lista;
    }

    public Microchip leerPorMascotaId(long mascotaId) throws SQLException {
        String sql = "SELECT id, eliminado, codigo, fechaImplantacion, veterinaria, observaciones, mascota_id FROM Microchips WHERE mascota_id = ? AND (eliminado = FALSE OR eliminado IS NULL)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, mascotaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return crearObjetoMicrochip(rs);
                }
            }
        }
        return null;
    }
}
