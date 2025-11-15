# Trabajo Final Integrador (TPI) - Gestión de Mascotas y Microchips (1:1)

**Materia:** Programación II - UTN
**Comisión:** 10
**Alumnos:**
* Berrone Lanza Lina Lucía
* Bayurk Mara Valentina
* Cabrera Darío Ezequiel 

---

## 1. Dominio Elegido y Justificación

El dominio elegido es **Mascota → Microchip** (Relación 1 a 1 Unidireccional).

* **Clase A:** `Mascota` (contiene referencia a `Microchip`).
* **Clase B:** `Microchip`.
* **Justificación:** Permite modelar que una mascota puede tener **cero o un** microchip, y que cada microchip pertenece a **una sola** mascota, ideal para practicar la cardinalidad **1:1**.

## 2. Arquitectura del Proyecto

El proyecto sigue una arquitectura de capas clara:

* `config/`: Contiene la clase `DatabaseConnection` para centralizar la configuración de MySQL.
* `models/`: Clases de dominio (`Mascota`, `Microchip`) y la clase base para `id` y `eliminado` (baja lógica).
* `DAO/`: Contiene la interfaz genérica (`GenericDAO`) y los DAOs concretos (`GestorMascotas`, `GestorMicrochips`) que utilizan **JDBC** y `PreparedStatement` para la persistencia.
* `service/`: Contiene la lógica de negocio, validaciones y la orquestación de **Transacciones** (gestión de `commit`/`rollback`).
* `main/`: Contiene el punto de arranque de la aplicación y el menú de consola (`AppMenu`).

## 3. Requisitos y Configuración de la Base de Datos

### Requisitos

* **Lenguaje:** Java (Se recomienda Java 21).
* **Base de Datos:** MySQL Workbench (se recomienda la versión 8.0+).
* **Driver:** Conector MySQL JDBC (`mysql-connector-j-9.5.0`).

### Pasos para Crear la Base de Datos

1.  Asegúrate de que tu servidor MySQL esté en funcionamiento con XAMPP. 
2.  Accede a tu herramienta de gestión de base de datos (MySQL Workbench, DBeaver, etc.).
3.  Ejecuta el script **`SQL/Tabla_Mascota_Microchip`**. Este archivo creará la base de datos `db_veterinaria_tfi` y las tablas `Mascotas` y `Microchips`.
    **Relación 1:1:** Implementada con una **Foreign Key (FK) única** en la tabla `Microchips` (`mascota_id`) referenciando a `Mascotas(id)`. Y además cargará 3 mascotas iniciales y dos microchips para realizar pruebas. 

### Credenciales de Conexión

Las credenciales están definidas en `src/config/DatabaseConnection.java`:

| Parámetro | Valor |
| :--- | :--- |
| **URL** |`jdbc:mysql://127.0.0.1:3306/db_veterinaria_tfi?serverTimezone=UTC&useSSL=false` |
| **USER** | `root` |
| **PASSWORD** | `Colocar tu contraseña` |

## 4. Compilación y Ejecución

1.  Asegúrate de que el driver JDBC de MySQL esté en el **classpath** de tu proyecto.
2.  Compila y ejecuta la clase principal (`main/Main.java`).
3.  **Flujo de Uso Sugerido:**
    * Seleccionar `1. Crear Mascota y Microchip` (opción transaccional).
    * Seleccionar `5. Listar Mascotas Activas` para ver el registro.
    * Seleccionar `3. Actualizar Mascota` para modificar los datos.
    * Seleccionar `4. Eliminar Mascota (Baja Lógica)` para probar la baja transaccional.

