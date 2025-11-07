package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import config.DatabaseConnection;

public class TestConexion {

    public static void main(String[] args) {
        // La clase actúa como un tester de infraestructura y lectura básica
        String sql = "SELECT id, nombre, especie, duenio, fechaNacimiento FROM mascotas WHERE eliminado = FALSE";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn != null) {
                System.out.println("✅ Conexión a la base de datos establecida con éxito.");

                // Usar try-with-resources anidado para PreparedStatement y ResultSet
                try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

                    System.out.println("\n📋 Listado de Mascotas (Prueba de Lectura):");
                    System.out.println("------------------------------------------------------------------");

                    while (rs.next()) {

                        int id = rs.getInt("id");
                        String nombre = rs.getString("nombre");
                        String especie = rs.getString("especie");
                        String duenio = rs.getString("duenio");
                        // 🔹 LECTURA MEJORADA: Incluye el campo DATE
                        Date fechaNacimiento = rs.getDate("fechaNacimiento");

                        // Impresión con formato limpio
                        System.out.printf("ID: %d | Nombre: %s | Especie: %s | Duenio: %s | Nacimiento: %s%n",
                                id, nombre, especie, duenio, fechaNacimiento != null ? fechaNacimiento.toString() : "N/A");
                    }
                    System.out.println("------------------------------------------------------------------");

                }
            } else {
                System.out.println("❌ No se pudo establecer la conexión.");
            }

        } catch (SQLException e) {
            // Este error puede indicar que la tabla 'mascotas' no existe, las columnas fallan o el servidor DB está abajo.
            System.err.println("⚠️ Error al ejecutar consulta o conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
