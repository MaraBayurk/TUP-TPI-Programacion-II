package main;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import models.Mascotas;
import models.Microchips;
import service.MascotasServiceImpl;
import service.MicrochipsServiceImpl;

/**
 * Clase que gestiona el menú de consola y la interacción con el usuario.
 * Llama directamente a la Capa Service.
 */
public class AppMenu {

    private final MascotasServiceImpl mascotaService = new MascotasServiceImpl();
    private final MicrochipsServiceImpl microchipService = new MicrochipsServiceImpl();
    private final Scanner scanner = new Scanner(System.in);

    // ===============================================
    // DISPLAY (Mostrar Menú)
    // ===============================================

    public void displayMenu() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ PRINCIPAL VETERINARIA TFI ---");
            System.out.println("1.  [C] Crear Mascota (Transaccional)");
            System.out.println("2.  [R] Listar Mascotas");
            System.out.println("3.  [R] Buscar Mascota por ID");
            System.out.println("4.  [U] Actualizar Mascota");
            System.out.println("5.  [D] Eliminar Mascota (Baja Lógica)");
            System.out.println("6.  [R] Listar Microchips");
            System.out.println("0. Salir");
            System.out.println("----------------------------------------");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
                handleOption(opcion);
            } catch (NumberFormatException e) {
                System.err.println("❌ Error: Ingrese un número válido.");
                opcion = -1;
            }
        } while (opcion != 0);
    }

    // ===============================================
    // HANDLERS (Manejo de Opciones)
    // ===============================================

    private void handleOption(int opcion) {
        try {
            switch (opcion) {
                case 1:
                    crearMascotaTransaccional();
                    break;
                case 2:
                    listarMascotas();
                    break;
                case 3:
                    buscarMascotaPorId();
                    break;
                case 4:
                    // actualizarMascota(); // Pendiente de implementación completa en el Service
                    System.out.println("Opción 4: Actualizar Mascota (En desarrollo).");
                    break;
                case 5:
                    eliminarMascota();
                    break;
                case 6:
                    listarMicrochips();
                    break;
                case 0:
                    System.out.println("¡Saliendo del sistema! ¡Adiós!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } catch (RuntimeException e) {
            // Captura las excepciones de negocio lanzadas por la Capa Service
            System.err.println("⚠️ OPERACIÓN FALLIDA: " + e.getMessage());
        }
    }

    // ===============================================
    // LÓGICA DE NEGOCIO (Usa la Capa Service)
    // ===============================================

    /**
     * Crea Mascota y su Microchip asociado en una ÚNICA transacción.
     */
    private void crearMascotaTransaccional() {
        System.out.println("\n--- CREAR MASCOTA Y ASOCIAR MICROCHIP ---");
        
        // 1. Recolección de datos de Mascota (Clase A)
        System.out.print("Nombre de la Mascota: ");
        String nombre = scanner.nextLine();
        System.out.print("Especie: ");
        String especie = scanner.nextLine();
        System.out.print("Raza: ");
        String raza = scanner.nextLine();
        System.out.print("Fecha Nacimiento (AAAA-MM-DD): ");
        LocalDate fechaNacimiento = LocalDate.parse(scanner.nextLine());
        System.out.print("Dueño: ");
        String duenio = scanner.nextLine();

        // 2. Recolección de datos de Microchip (Clase B)
        System.out.println("\n--- DATOS DEL MICROCHIP ---");
        System.out.print("Código ÚNICO del Microchip: ");
        String codigo = scanner.nextLine();
        System.out.print("Fecha Implantación (AAAA-MM-DD): ");
        LocalDate fechaImplantacion = LocalDate.parse(scanner.nextLine());

        // 3. Construir objetos
        Microchips microchip = new Microchips(codigo, fechaImplantacion, "Veterinaria TFI", "Implante inicial", null);
        Mascotas mascota = new Mascotas(nombre, especie, raza, fechaNacimiento, duenio, microchip);

        // 4. Llamar al Service (aquí ocurre la transacción)
        Mascotas mascotaCreada = mascotaService.insertar(mascota);
        System.out.println("\n✅ ÉXITO: Mascota y Microchip creados en una sola transacción.");
        System.out.println("   Mascota ID Generado: " + mascotaCreada.getId());
        System.out.println("   Microchip Código: " + mascotaCreada.getMicrochip().getCodigo());
    }

    private void listarMascotas() {
        System.out.println("\n--- LISTADO DE MASCOTAS ACTIVAS ---");
        List<Mascotas> mascotas = mascotaService.getAll();
        if (mascotas.isEmpty()) {
            System.out.println("📭 No hay mascotas registradas.");
            return;
        }
        mascotas.forEach(m -> System.out.println(
            "ID: " + m.getId() + 
            " | Nombre: " + m.getNombre() + 
            " | Especie: " + m.getEspecie() +
            " | Dueño: " + m.getDuenio()
        ));
    }

    private void buscarMascotaPorId() {
        System.out.print("Ingrese el ID de la mascota a buscar: ");
        try {
            long id = Long.parseLong(scanner.nextLine());
            Mascotas mascota = mascotaService.getById(id);
            
            if (mascota != null) {
                System.out.println("\n🐾 DETALLES DE LA MASCOTA:");
                System.out.println(mascota); // Usa el toString() del modelo
            } else {
                System.out.println("❌ Mascota con ID " + id + " no encontrada o eliminada.");
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ ID inválido. Debe ser un número entero.");
        }
    }

    private void eliminarMascota() {
        System.out.print("Ingrese el ID de la mascota para BAJA LÓGICA: ");
        try {
            long id = Long.parseLong(scanner.nextLine());
            mascotaService.eliminar(id);
            System.out.println("✅ Mascota ID " + id + " marcada como eliminada (Baja Lógica).");
        } catch (NumberFormatException e) {
            System.err.println("❌ ID inválido.");
        }
    }
    
    private void listarMicrochips() {
        System.out.println("\n--- LISTADO DE MICROCHIPS ---");
        List<Microchips> chips = microchipService.getAll();
        if (chips.isEmpty()) {
            System.out.println("📭 No hay microchips registrados.");
            return;
        }
        chips.forEach(c -> System.out.println(
            "ID: " + c.getId() + 
            " | Código: " + c.getCodigo() + 
            " | Mascota ID: " + c.getMascotaId()
        ));
    }
}