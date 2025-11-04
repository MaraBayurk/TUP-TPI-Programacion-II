package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import config.DatabaseConnection;

public class TestConexion {

    public static void main(String[] args) {

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("✅ Conexión establecida con éxito.");

                // 🔹 La consulta a 'mascotas' es correcta ahora
                String sql = "SELECT * FROM mascotas";

                // Usar try-with-resources anidado para PreparedStatement y ResultSet
                try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

                    // ⚠️ Cambiado: Título para reflejar el contenido
                    System.out.println("📋 Listado de mascotas:");

                    while (rs.next()) {
                        // ⚠️ CORRECCIÓN CLAVE: Leer las columnas que SÍ existen en la tabla 'mascotas'
                        int id = rs.getInt("id");
                        String nombre = rs.getString("nombre");
                        String especie = rs.getString("especie"); // Nueva columna
                        String duenio = rs.getString("duenio");   // Nueva columna

                        // ⚠️ Cambiado: Imprimir los datos reales de la mascota
                        System.out.println("ID: " + id
                                + ", Nombre: " + nombre
                                + ", Especie: " + especie
                                + ", Dueño: " + duenio);
                    }
                }
            } else {
                System.out.println("❌ No se pudo establecer la conexión.");
            }
        } catch (SQLException e) {
            // ... manejo de errores ...
            System.err.println("⚠️ Error al ejecutar consulta o conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
