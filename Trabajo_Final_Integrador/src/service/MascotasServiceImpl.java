/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import DAO.GestorMascotas;
import DAO.GestorMicrochips;
import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class MascotasServiceImpl {

    private GestorMascotas gestorMascotas = new GestorMascotas();
    private GestorMicrochips gestorMicrochips = new GestorMicrochips();

    /**
     * Registra una mascota y su microchip en una misma transacción.
     */
    public void registrarMascotaConMicrochip(
            String nombre, String especie, String raza, String fechaNacimientoStr, String duenio,
            String codigoChip, String fechaImplantacionStr, String veterinaria, String observaciones
    ) {
        Connection conn = null;

        try {
            // Obtener conexión y desactivar autocommit
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insertar mascota (y obtener el ID generado)
            int mascotaId = agregarMascotaConRetorno(conn, nombre, especie, raza, fechaNacimientoStr, duenio);

            // Insertar microchip asociado a la mascota
            agregarMicrochipConConexion(conn, codigoChip, fechaImplantacionStr, veterinaria, observaciones, mascotaId);

            // Confirmar toda la transacción
            conn.commit();
            System.out.println("Mascota y microchip registrados exitosamente en una sola transacción.");

        } catch (Exception e) {
            // Si algo falla, deshacer todo
            try {
                if (conn != null) conn.rollback();
                System.err.println("Error: se deshizo la transacción completa. Motivo: " + e.getMessage());
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }

        } finally {
            // Cerrar conexión
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Inserta una mascota dentro de una transacción y devuelve su ID generado.
     */
    private int agregarMascotaConRetorno(Connection conn, String nombre, String especie, String raza, String fechaNacimientoStr, String duenio) throws SQLException {
        String sql = "INSERT INTO Mascotas (nombre, especie, raza, fechaNacimiento, duenio) VALUES (?, ?, ?, ?, ?)";

        try (var stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nombre);
            stmt.setString(2, especie);
            stmt.setString(3, raza);
            stmt.setDate(4, java.sql.Date.valueOf(fechaNacimientoStr));
            stmt.setString(5, duenio);

            int filas = stmt.executeUpdate();
            if (filas == 0) throw new SQLException("No se pudo insertar la mascota.");

            try (var keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                else throw new SQLException("No se pudo obtener el ID generado de la mascota.");
            }
        }
    }

    /**
     * Inserta un microchip asociado a una mascota dentro de una transacción.
     */
    private void agregarMicrochipConConexion(Connection conn, String codigo, String fechaImplantacionStr, String veterinaria, String observaciones, int mascotaId) throws SQLException {
        String sql = "INSERT INTO Microchips (codigo, fechaImplantacion, veterinaria, observaciones, mascota_id) VALUES (?, ?, ?, ?, ?)";

        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            stmt.setDate(2, java.sql.Date.valueOf(fechaImplantacionStr));
            stmt.setString(3, veterinaria);
            stmt.setString(4, observaciones);
            stmt.setInt(5, mascotaId);
            stmt.executeUpdate();
        }
    }
}
