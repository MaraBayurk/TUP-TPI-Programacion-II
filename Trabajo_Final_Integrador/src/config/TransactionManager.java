package config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestiona la apertura y cierre de la conexión de forma segura para las
 * transacciones. Esta clase es usada por la capa Service para obtener la
 * conexión inicial.
 */
public class TransactionManager {

    /**
     * Obtiene una nueva conexión de la base de datos.
     *
     * @return Una nueva conexión de JDBC.
     * @throws SQLException Si hay un error al conectar.
     */
    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    /**
     * Ejecuta COMMIT y cierra la conexión.
     *
     * @param conn La conexión activa con autoCommit=false.
     */
    public static void commitAndClose(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                System.err.println("❌ ERROR al realizar COMMIT: " + e.getMessage());
            } finally {
                // Siempre restablece el autoCommit y cierra la conexión
                closeConnection(conn);
            }
        }
    }

    /**
     * Ejecuta ROLLBACK y cierra la conexión.
     *
     * @param conn La conexión activa con autoCommit=false.
     */
    public static void rollbackAndClose(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("❌ ERROR al realizar ROLLBACK: " + e.getMessage());
            } finally {
                // Siempre restablece el autoCommit y cierra la conexión
                closeConnection(conn);
            }
        }
    }

    /**
     * Método auxiliar para restablecer autoCommit y cerrar la conexión.
     */
    private static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.setAutoCommit(true); // Restablecer
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("❌ ERROR al cerrar la conexión: " + e.getMessage());
        }
    }
}
