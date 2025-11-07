package DAO;

import config.DatabaseConnection;
import models.Microchips;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class GestorMicrochips implements GenericDao<Microchips> {

    // ===============================================
    // MÉTODOS TRANSACCIONALES (Aceptan Connection conn)
    // ===============================================
    @Override
    public Long crear(Connection conn, Microchips microchip) throws SQLException {
        String sql = "INSERT INTO Microchips (codigo, fechaImplantacion, veterinaria, observaciones, mascota_id) VALUES (?, ?, ?, ?, ?)";
        Long generatedId = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDate localDate = microchip.getFechaImplantacion();
            java.sql.Date sqlDate = localDate != null ? java.sql.Date.valueOf(localDate) : null;

            stmt.setString(1, microchip.getCodigo());
            stmt.setDate(2, sqlDate);
            stmt.setString(3, microchip.getVeterinaria());
            stmt.setString(4, microchip.getObservaciones());
            stmt.setLong(5, microchip.getMascotaId()); // Clave Foránea

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Fallo al crear el Microchip.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getLong(1);
                }
            }
            return generatedId;

        } catch (IllegalArgumentException e) {
            throw new SQLException("Error de formato de fecha en el campo Microchip: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Connection conn, Microchips microchip) throws SQLException {
        String sql = "UPDATE Microchips SET codigo = ?, fechaImplantacion = ?, veterinaria = ?, observaciones = ?, mascota_id = ? WHERE id = ? AND eliminado = FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDate localDate = microchip.getFechaImplantacion();
            java.sql.Date sqlDate = localDate != null ? java.sql.Date.valueOf(localDate) : null;

            stmt.setString(1, microchip.getCodigo());
            stmt.setDate(2, sqlDate);
            stmt.setString(3, microchip.getVeterinaria());
            stmt.setString(4, microchip.getObservaciones());
            stmt.setLong(5, microchip.getMascotaId());
            stmt.setLong(6, microchip.getId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No se encontró el Microchip ID " + microchip.getId() + " para actualizar o estaba eliminado.");
            }
        } catch (IllegalArgumentException e) {
            throw new SQLException("Error de formato de fecha al actualizar Microchip: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(Connection conn, long id) throws SQLException {
        String sql = "UPDATE Microchips SET eliminado = TRUE WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No se encontró el Microchip ID " + id + " para eliminar.");
            }
        }
    }

    // ===============================================
    // MÉTODOS NO TRANSACCIONALES (Lectura)
    // ===============================================
    @Override
    public Microchips leer(long id) throws SQLException {
        String sql = "SELECT * FROM Microchips WHERE id = ? AND eliminado = FALSE";

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
    public List<Microchips> leerTodos() throws SQLException {
        String sql = "SELECT * FROM Microchips WHERE eliminado = FALSE ORDER BY id";
        List<Microchips> microchips = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                microchips.add(crearObjetoMicrochip(rs));
            }
        }
        return microchips;
    }

    // Método helper para mapear ResultSet al objeto (constructor de persistencia)
    private Microchips crearObjetoMicrochip(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("fechaImplantacion");
        LocalDate localDate = sqlDate != null ? sqlDate.toLocalDate() : null;

        return new Microchips(
                rs.getLong("id"),
                rs.getBoolean("eliminado"),
                rs.getString("codigo"),
                localDate,
                rs.getString("veterinaria"),
                rs.getString("observaciones"),
                rs.getLong("mascota_id")
        );
    }

    // Método auxiliar para ser llamado desde el Service (Hydration)
    public Microchips leerPorMascotaId(long mascotaId) throws SQLException {
        String sql = "SELECT * FROM Microchips WHERE mascota_id = ? AND eliminado = FALSE";

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
