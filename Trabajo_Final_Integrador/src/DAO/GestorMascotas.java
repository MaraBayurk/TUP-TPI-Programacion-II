package DAO;

import config.DatabaseConnection;
import models.Mascota;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorMascotas implements GenericDAO<Mascota> {

    // 💡 Método helper para mapear ResultSet al objeto
    private Mascota crearObjetoMascota(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("fechaNacimiento");
        LocalDate localDate = sqlDate != null ? sqlDate.toLocalDate() : null;

        // Asumiendo que Mascota tiene el constructor de persistencia
        return new Mascota(
                rs.getLong("id"),
                rs.getBoolean("eliminado"),
                rs.getString("nombre"),
                rs.getString("especie"),
                rs.getString("raza"),
                localDate,
                rs.getString("duenio")
        );
    }

    // ===============================================
    // MÉTODOS TRANSACCIONALES (Aceptan Connection conn)
    // ===============================================
    @Override
    public Long crear(Connection conn, Mascota mascota) throws SQLException {
        String sql = "INSERT INTO Mascotas (nombre, especie, raza, fechaNacimiento, duenio) VALUES (?, ?, ?, ?, ?)";
        Long generatedId = null;

        // Usa la conexión recibida (de la transacción)
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDate localDate = mascota.getFechaNacimiento();
            java.sql.Date sqlDate = localDate != null ? java.sql.Date.valueOf(localDate) : null;

            stmt.setString(1, mascota.getNombre());
            stmt.setString(2, mascota.getEspecie());
            stmt.setString(3, mascota.getRaza());
            stmt.setDate(4, sqlDate);
            stmt.setString(5, mascota.getDuenio());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Fallo al crear la Mascota.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getLong(1);
                    // 💥 SOLUCIÓN CRÍTICA: Setea el ID en el objeto Mascota en memoria.
                    mascota.setId(generatedId);
                }
            }
            return generatedId;
        } catch (IllegalArgumentException e) {
            throw new SQLException("Error de formato de fecha en el campo Nacimiento: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Connection conn, Mascota mascota) throws SQLException {
        String sql = "UPDATE Mascotas SET nombre = ?, especie = ?, raza = ?, fechaNacimiento = ?, duenio = ? WHERE id = ? AND eliminado = FALSE";
        boolean closeConn = (conn == null);
        if (closeConn) {
            conn = DatabaseConnection.getConnection(); // Abre si es null
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            LocalDate localDate = mascota.getFechaNacimiento();
            java.sql.Date sqlDate = localDate != null ? java.sql.Date.valueOf(localDate) : null;

            stmt.setString(1, mascota.getNombre());
            stmt.setString(2, mascota.getEspecie());
            stmt.setString(3, mascota.getRaza());
            stmt.setDate(4, sqlDate);
            stmt.setString(5, mascota.getDuenio());
            stmt.setLong(6, mascota.getId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No se encontró la Mascota ID " + mascota.getId() + " para actualizar o estaba eliminada.");
            }
        } catch (IllegalArgumentException e) {
            throw new SQLException("Error de formato de fecha al actualizar: " + e.getMessage());
        } finally {
            if (closeConn && conn != null) {
                conn.close(); // Cierra si se abrió aquí
            }
        }
    }

    @Override
    public void eliminar(Connection conn, long id) throws SQLException {
        boolean closeConn = (conn == null);
        if (closeConn) {
            conn = DatabaseConnection.getConnection();
        }

        String sql = "UPDATE Mascotas SET eliminado = TRUE WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No se encontró la Mascota ID " + id + " para eliminar.");
            }
        } finally {
            if (closeConn && conn != null) {
                conn.close();
            }
        }
    }

    // ===============================================
    // MÉTODOS NO TRANSACCIONALES (Lectura)
    // ===============================================
    @Override
    public Mascota leer(long id) throws SQLException {
        String sql = "SELECT * FROM Mascotas WHERE id = ? AND eliminado = FALSE";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return crearObjetoMascota(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Mascota> leerTodos() throws SQLException {
        String sql = "SELECT * FROM Mascotas WHERE eliminado = FALSE ORDER BY id";
        List<Mascota> mascotas = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                mascotas.add(crearObjetoMascota(rs));
            }
        }
        return mascotas;
    }
}
