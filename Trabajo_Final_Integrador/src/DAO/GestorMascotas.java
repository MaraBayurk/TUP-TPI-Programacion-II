package DAO;

import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestorMascotas {

    // 📌 Agregar una nueva mascota
    // Nota: fechaNacimiento ahora espera java.sql.Date o una cadena que pueda ser convertida
    public void agregarMascota(String nombre, String especie, String raza, String fechaNacimientoStr, String duenio) {
        String sql = "INSERT INTO Mascotas (nombre, especie, raza, fechaNacimiento, duenio) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Conversión de String a java.sql.Date
            java.sql.Date sqlDate = java.sql.Date.valueOf(fechaNacimientoStr);

            stmt.setString(1, nombre);
            stmt.setString(2, especie);
            stmt.setString(3, raza);
            stmt.setDate(4, sqlDate); // Usamos setDate para el tipo DATE
            stmt.setString(5, duenio);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                // ... (Obtención de ID y mensaje de éxito)
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        System.out.println("✅ Mascota agregada con ID: " + generatedKeys.getInt(1));
                    }
                }
            } else {
                System.out.println("❌ No se pudo agregar la mascota.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error SQL al agregar la mascota: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error: Formato de fecha de nacimiento inválido (Debe ser YYYY-MM-DD).");
        }
    }

    // 📌 Listar mascotas activas
    public void listarMascotas() {
        String sql = "SELECT id, nombre, especie, raza, duenio, fechaNacimiento FROM Mascotas WHERE eliminado = FALSE ORDER BY id";
        List<String> mascotas = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Uso de getBoolean para el campo eliminado (aunque no lo seleccionamos aquí, es útil saberlo)
                mascotas.add(String.format("ID: %d | Nombre: %s | Especie: %s | Raza: %s | Dueño: %s | Nac: %s",
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("especie"),
                        rs.getString("raza"),
                        rs.getString("duenio"),
                        rs.getDate("fechaNacimiento").toString())); // Usamos getDate
            }
        } catch (SQLException e) {
            System.err.println("❌ Error SQL al listar las mascotas: " + e.getMessage());
        }

        if (mascotas.isEmpty()) {
            System.out.println("📭 No hay mascotas activas registradas.");
        } else {
            System.out.println("\n📋 LISTA DE MASCOTAS ACTIVAS:");
            mascotas.forEach(System.out::println);
        }
    }

    // 📌 Eliminar una mascota (Borrado Lógico)
    public void eliminarMascota(String id) {
        // Borrado Lógico: Establece 'eliminado' a TRUE (o 1)
        String sql = "UPDATE Mascotas SET eliminado = TRUE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(id));
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Mascota " + id + " eliminada (lógicamente) con éxito.");
            } else {
                System.out.println("❌ No se encontró la mascota con ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error SQL al eliminar la mascota: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("❌ ID inválido. Debe ser un número: " + e.getMessage());
        }
    }
    // ... (Faltan mostrarMascota y actualizarMascota, que siguen la misma lógica de setXxx/getXxx)
  
}
