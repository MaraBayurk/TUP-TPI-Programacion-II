package DAO;

import config.DatabaseConnection;
import models.Mascota;
import models.Microchip;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorMascotas implements GenericDAO<Mascota> {

    private final GestorMicrochips microchipDAO = new GestorMicrochips();

    // ==========================================================
    // MAPEO DE MASCOTA
    // ==========================================================
    private Mascota crearObjetoMascota(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("fechaNacimiento");
        LocalDate localDate = sqlDate != null ? sqlDate.toLocalDate() : null;

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

    // ==========================================================
    // CRUD TRANSACCIONAL
    // ==========================================================
    @Override
    public Long crear(Connection conn, Mascota mascota) throws SQLException {

        String sql = "INSERT INTO Mascotas (nombre, especie, raza, fechaNacimiento, duenio) VALUES (?, ?, ?, ?, ?)";

        Long generatedId;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDate ld = mascota.getFechaNacimiento();
            Date sqlDate = (ld != null ? Date.valueOf(ld) : null);

            stmt.setString(1, mascota.getNombre());
            stmt.setString(2, mascota.getEspecie());
            stmt.setString(3, mascota.getRaza());
            stmt.setDate(4, sqlDate);
            stmt.setString(5, mascota.getDuenio());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("No se generó ID");
                }
                generatedId = rs.getLong(1);
                mascota.setId(generatedId);
            }
        }

        return generatedId;
    }

    @Override
    public void actualizar(Connection conn, Mascota mascota) throws SQLException {

        String sql = "UPDATE Mascotas SET nombre=?, especie=?, raza=?, fechaNacimiento=?, duenio=? "
                + "WHERE id=? AND eliminado=FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDate ld = mascota.getFechaNacimiento();
            Date sqlDate = (ld != null ? Date.valueOf(ld) : null);

            stmt.setString(1, mascota.getNombre());
            stmt.setString(2, mascota.getEspecie());
            stmt.setString(3, mascota.getRaza());
            stmt.setDate(4, sqlDate);
            stmt.setString(5, mascota.getDuenio());
            stmt.setLong(6, mascota.getId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Mascota no encontrada.");
            }
        }
    }

    @Override
    public void eliminar(Connection conn, long id) throws SQLException {
        String sql = "UPDATE Mascotas SET eliminado=TRUE WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    // ==========================================================
    // LECTURAS
    // ==========================================================
    @Override
    public Mascota leer(long id) throws SQLException {
        String sql = "SELECT * FROM Mascotas WHERE id=? AND eliminado=FALSE";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Mascota mascota = crearObjetoMascota(rs);

                    // Hidratar Microchip (1:1)
                    Microchip microchip = microchipDAO.leerPorMascotaId(id);
                    mascota.setMicrochip(microchip);

                    return mascota;
                }
            }
        }

        return null;
    }

    @Override
    public List<Mascota> leerTodos() throws SQLException {

        String sql = "SELECT * FROM Mascotas WHERE eliminado=FALSE";

        List<Mascota> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Mascota mascota = crearObjetoMascota(rs);

                // ⭐ HIDRATAR MICROCHIP AQUÍ ⭐
                Microchip microchip = microchipDAO.leerPorMascotaId(mascota.getId());
                mascota.setMicrochip(microchip);

                lista.add(mascota);
            }
        }

        return lista;
    }
}
