package DAO;

import config.DatabaseConnection;
import models.Microchip;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class GestorMicrochips implements GenericDAO<Microchip> {

    // 💡 Método helper para mapear ResultSet al objeto (constructor de persistencia)
    private Microchip crearObjetoMicrochip(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("fechaImplantacion");
        LocalDate localDate = sqlDate != null ? sqlDate.toLocalDate() : null;

        // NOTA: El constructor de persistencia NO acepta mascotaId (por el diseño unidireccional)
        return new Microchip(
                rs.getLong("id"),
                rs.getBoolean("eliminado"),
                rs.getString("codigo"),
                localDate,
                rs.getString("veterinaria"),
                rs.getString("observaciones")
        );
    }

    // ===============================================
    // MÉTODOS TRANSACCIONALES (Completo y Robusto)
    // ===============================================
    // Método Sobrecargado para la Transacción (recibe la FK)
    public Long crear(Connection conn, Microchip microchip, Long mascotaId) throws SQLException {
        String sql = "INSERT INTO Microchips (codigo, fechaImplantacion, veterinaria, observaciones, mascota_id) VALUES (?, ?, ?, ?, ?)";
        Long generatedId = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDate localDate = microchip.getFechaImplantacion();
            java.sql.Date sqlDate = localDate != null ? java.sql.Date.valueOf(localDate) : null;

            stmt.setString(1, microchip.getCodigo());
            stmt.setDate(2, sqlDate);
            stmt.setString(3, microchip.getVeterinaria());
            stmt.setString(4, microchip.getObservaciones());
            stmt.setLong(5, mascotaId); // Clave Foránea

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

    // Implementación de la Interfaz (Lanza excepción)
    @Override
    public Long crear(Connection conn, Microchip microchip) throws SQLException {
        throw new UnsupportedOperationException("Error: La creación de Microchip debe incluir el ID de la Mascota. Use crear(conn, microchip, mascotaId).");
    }

    @Override
    public void actualizar(Connection conn, Microchip microchip) throws SQLException {
        String sql = "UPDATE Microchips SET codigo = ?, fechaImplantacion = ?, veterinaria = ?, observaciones = ? WHERE id = ? AND eliminado = FALSE";
        boolean closeConn = (conn == null);
        if (closeConn) {
            conn = DatabaseConnection.getConnection();
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            LocalDate localDate = microchip.getFechaImplantacion();
            java.sql.Date sqlDate = localDate != null ? java.sql.Date.valueOf(localDate) : null;

            stmt.setString(1, microchip.getCodigo());
            stmt.setDate(2, sqlDate);
            stmt.setString(3, microchip.getVeterinaria());
            stmt.setString(4, microchip.getObservaciones());
            stmt.setLong(5, microchip.getId()); // ID del microchip

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No se encontró el Microchip ID " + microchip.getId() + " para actualizar o estaba eliminado.");
            }
        } catch (IllegalArgumentException e) {
            throw new SQLException("Error de formato de fecha al actualizar Microchip: " + e.getMessage());
        } finally {
            if (closeConn) {
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
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No se encontró el Microchip ID " + id + " para eliminar.");
            }
        } finally {
            if (closeConn) {
                conn.close();
            }
        }
    }

    // ===============================================
    // MÉTODOS NO TRANSACCIONALES (Lectura)
    // ===============================================
    // 💡 CORRECCIÓN CRÍTICA: Uso de SELECT explícito para evitar problemas con mascota_id
    @Override
    public List<Microchip> leerTodos() throws SQLException {
        String sql = "SELECT id, eliminado, codigo, fechaImplantacion, veterinaria, observaciones FROM Microchips WHERE eliminado = FALSE ORDER BY id";
        List<Microchip> microchips = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                microchips.add(crearObjetoMicrochip(rs));
            }
        }
        return microchips;
    }

    // Método auxiliar para ser llamado desde el Service (Hydration - R)
    public Microchip leerPorMascotaId(long mascotaId) throws SQLException {
        String sql = "SELECT id, eliminado, codigo, fechaImplantacion, veterinaria, observaciones FROM Microchips WHERE mascota_id = ? AND eliminado = FALSE";

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

    @Override
    public Microchip leer(long id) throws SQLException {
        String sql = "SELECT id, eliminado, codigo, fechaImplantacion, veterinaria, observaciones FROM Microchips WHERE id = ? AND eliminado = FALSE";

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
}
