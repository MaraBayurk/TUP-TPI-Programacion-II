package DAO;

import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestorMicrochips {

    // 📌 Agregar un nuevo microchip
    // Los tipos DATE y BIGINT (mascota_id) se manejan específicamente
    public void agregarMicrochip(String codigo, String fechaImplantacionStr, String veterinaria, String observaciones, int mascotaId) {
        String sql = "INSERT INTO Microchips (codigo, fechaImplantacion, veterinaria, observaciones, mascota_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Conversión de String a java.sql.Date
            java.sql.Date sqlDate = java.sql.Date.valueOf(fechaImplantacionStr);

            stmt.setString(1, codigo);
            stmt.setDate(2, sqlDate);
            stmt.setString(3, veterinaria);
            stmt.setString(4, observaciones);
            stmt.setInt(5, mascotaId); // Usamos setInt/setLong para el BIGINT (mascota_id)

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Microchip " + codigo + " agregado a la Mascota ID: " + mascotaId);
            } else {
                System.out.println("❌ No se pudo agregar el microchip.");
            }
        } catch (SQLException e) {
            // Este catch atrapará errores como 'mascota_id' no existente o 'codigo' duplicado (UNIQUE)
            System.err.println("❌ Error SQL al agregar el microchip: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error: Formato de fecha de implantación inválido (Debe ser YYYY-MM-DD).");
        }
    }

    // 📌 Obtener Microchip por ID de Mascota (mascota_id es UNIQUE)
    public void mostrarMicrochipPorMascota(int mascotaId) {
        String sql = "SELECT * FROM Microchips WHERE mascota_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mascotaId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n🔎 Microchip Encontrado:");
                    System.out.println("Código: " + rs.getString("codigo"));
                    System.out.println("Veterinaria: " + rs.getString("veterinaria"));
                    System.out.println("Observaciones: " + rs.getString("observaciones"));
                    System.out.println("Fecha Implantación: " + rs.getDate("fechaImplantacion").toString());
                } else {
                    System.out.println("❌ No se encontró microchip para la Mascota ID: " + mascotaId);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error SQL al buscar el microchip: " + e.getMessage());
        }
    }

   // Listar todos los microchips
    public void listarMicrochips() {
        String sql = "SELECT id, codigo, fechaImplantacion, veterinaria, observaciones, mascota_id FROM Microchips ORDER BY id";
        List<String> microchips = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql); 
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                microchips.add(String.format(
                        "ID: %d | Código: %s | Fecha: %s | Veterinaria: %s | Mascota ID: %d | Obs: %s",
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getDate("fechaImplantacion").toString(),
                        rs.getString("veterinaria"),
                        rs.getInt("mascota_id"),
                        rs.getString("observaciones") != null ? rs.getString("observaciones") : "(sin observaciones)"
                ));
            }

        } catch (SQLException e) {
            System.err.println(" Error SQL al listar los microchips: " + e.getMessage());
        }

        if (microchips.isEmpty()) {
            System.out.println("No hay microchips registrados.");
        } else {
            System.out.println("\n LISTA DE MICROCHIPS REGISTRADOS:");
            microchips.forEach(System.out::println);
        }
    }

    // Actualizar un microchip
    public void actualizarMicrochip(int id, String codigo, String fechaImplantacionStr, String veterinaria, String observaciones, int mascotaId) {
        String sql = "UPDATE Microchips SET codigo = ?, fechaImplantacion = ?, veterinaria = ?, observaciones = ?, mascota_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            java.sql.Date sqlDate = java.sql.Date.valueOf(fechaImplantacionStr);

            stmt.setString(1, codigo);
            stmt.setDate(2, sqlDate);
            stmt.setString(3, veterinaria);
            stmt.setString(4, observaciones);
            stmt.setInt(5, mascotaId);
            stmt.setInt(6, id);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Microchip actualizado correctamente (ID: " + id + ").");
            } else {
                System.out.println("No se encontró un microchip con ese ID.");
            }

        } catch (SQLException e) {
            System.err.println("Error SQL al actualizar el microchip: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Formato de fecha inválido (Debe ser YYYY-MM-DD).");
        }
    }

    //  Eliminar un microchip
    public void eliminarMicrochip(int id) {
        String sql = "DELETE FROM Microchips WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Microchip eliminado correctamente (ID: " + id + ").");
            } else {
                System.out.println("No se encontró un microchip con ese ID.");
            }

        } catch (SQLException e) {
            System.err.println("Error SQL al eliminar el microchip: " + e.getMessage());
        }
    }
}
