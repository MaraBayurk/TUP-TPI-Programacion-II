package DAO;

import config.DatabaseConnection;
import models.Mascotas;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class GestorMascotas implements GenericDao<Mascotas> {


    // MÉTODOS TRANSACCIONALES (Aceptan Connection conn)

    @Override
    public Long crear(Connection conn, Mascotas mascota) throws SQLException {
        String sql = "INSERT INTO Mascotas (nombre, especie, raza, fechaNacimiento, duenio) VALUES (?, ?, ?, ?, ?)";
        Long generatedId = null;

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
                }
            }
            return generatedId;

        } catch (IllegalArgumentException e) {
            throw new SQLException("Error de formato de fecha en el campo Nacimiento: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Connection conn, Mascotas mascota) throws SQLException {
        String sql = "UPDATE Mascotas SET nombre = ?, especie = ?, raza = ?, fechaNacimiento = ?, duenio = ? WHERE id = ? AND eliminado = FALSE";

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
        }
    }

    @Override
    public void eliminar(Connection conn, long id) throws SQLException {
        String sql = "UPDATE Mascotas SET eliminado = TRUE WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No se encontró la Mascota ID " + id + " para eliminar.");
            }
        }
    }


    // MÉTODOS NO TRANSACCIONALES (Lectura)

    @Override
    public Mascotas leer(long id) throws SQLException {
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
    public List<Mascotas> leerTodos() throws SQLException {
        String sql = "SELECT * FROM Mascotas WHERE eliminado = FALSE ORDER BY id";
        List<Mascotas> mascotas = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                mascotas.add(crearObjetoMascota(rs));
            }
        }
        return mascotas;
    }

    // Método helper para mapear ResultSet al objeto (constructor de persistencia)
    private Mascotas crearObjetoMascota(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("fechaNacimiento");
        LocalDate localDate = sqlDate != null ? sqlDate.toLocalDate() : null;

        return new Mascotas(
                rs.getLong("id"),
                rs.getBoolean("eliminado"),
                rs.getString("nombre"),
                rs.getString("especie"),
                rs.getString("raza"),
                localDate,
                rs.getString("duenio")
        );
    }
}
