package main;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import models.Mascota;
import models.Microchip;
import service.MascotasServiceImpl;
import service.MicrochipsServiceImpl;

public class MenuHandlers {

    // Instancias de la Capa Service
    private final MascotasServiceImpl mascotaService = new MascotasServiceImpl();
    private final MicrochipsServiceImpl microchipService = new MicrochipsServiceImpl();

    // Scanner centralizado
    private final Scanner scanner = new Scanner(System.in);

    public void startApplication() {
        int opcion;
        do {
            MenuDisplay.showMainMenu();
            // Pasa la instancia del scanner para capturar la opción
            opcion = MenuDisplay.getMenuOption(scanner);
            handleOption(opcion);
        } while (opcion != 0);

        scanner.close(); // Cerrar el scanner al finalizar
    }

    private void handleOption(int opcion) {
        try {
            switch (opcion) {
                case 1:
                    createMascotaTransaccional();
                    break;
                case 2:
                    listMascotas();
                    break;
                case 3:
                    findMascotaById();
                    break;
                case 4:
                    updateMascota();
                    break;
                case 5:
                    deleteMascota();
                    break;
                case 6:
                    listMicrochips();
                    break;
                case 0:
                    System.out.println("\n¡Saliendo del sistema! ¡Adiós!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } catch (RuntimeException e) {
            // Captura las excepciones de negocio
            System.err.println("⚠️ OPERACIÓN FALLIDA: " + e.getMessage());
        }
    }

    // ===============================================
    // HANDLERS ESPECÍFICOS (Se ajustaron las llamadas a MenuDisplay)
    // ===============================================
    private void createMascotaTransaccional() {
        System.out.println("\n--- CREAR MASCOTA Y ASOCIAR MICROCHIP ---");

        String nombre = MenuDisplay.getStringInput(scanner, "Nombre de la Mascota: ");
        String especie = MenuDisplay.getStringInput(scanner, "Especie: ");
        String raza = MenuDisplay.getStringInput(scanner, "Raza: ");

        String fechaNacimientoStr = MenuDisplay.getStringInput(scanner, "Fecha Nacimiento (AAAA-MM-DD): ");

        LocalDate fechaNacimiento = null;
        try {
            fechaNacimiento = LocalDate.parse(fechaNacimientoStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha de nacimiento incorrecto. Use AAAA-MM-DD.");
        }

        String duenio = MenuDisplay.getStringInput(scanner, "Dueño: ");

        System.out.println("\n--- DATOS DEL MICROCHIP (1:1) ---");
        String codigo = MenuDisplay.getStringInput(scanner, "Código ÚNICO del Microchip: ");
        String fechaImplantacionStr = MenuDisplay.getStringInput(scanner, "Fecha Implantación (AAAA-MM-DD): ");

        LocalDate fechaImplantacion = null;
        try {
            fechaImplantacion = LocalDate.parse(fechaImplantacionStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha de implantación incorrecto. Use AAAA-MM-DD.");
        }

        String veterinaria = MenuDisplay.getStringInput(scanner, "Veterinaria: ");
        String observaciones = MenuDisplay.getStringInput(scanner, "Observaciones: ");

        Microchip microchip = new Microchip(codigo, fechaImplantacion, veterinaria, observaciones, null);
        Mascota mascota = new Mascota(nombre, especie, raza, fechaNacimiento, duenio, microchip);

        Mascota mascotaCreada = mascotaService.insertar(mascota);
        System.out.println("\n✅ ÉXITO: Mascota y Microchip creados en una sola transacción.");
        System.out.println("   Mascota ID Generado: " + mascotaCreada.getId());
    }

    private void findMascotaById() {
        // Se ajustó la llamada a getLongInput para pasar el scanner
        Long id = MenuDisplay.getLongInput(scanner, "Ingrese el ID de la mascota a buscar: ");
        if (id == null) {
            return;
        }

        Mascota mascota = mascotaService.getById(id);

        if (mascota != null) {
            System.out.println("\n🐾 DETALLES DE LA MASCOTA:");
            System.out.println(mascota);
        } else {
            System.out.println("❌ Mascota con ID " + id + " no encontrada o eliminada.");
        }
    }

    // Métodos listMascotas, deleteMascota, updateMascota, listMicrochips, etc., requieren el mismo ajuste
    // al usar MenuDisplay.getLongInput(scanner, ...) o MenuDisplay.getStringInput(scanner, ...).
    private void listMascotas() {
        System.out.println("\n--- LISTADO DE MASCOTAS ACTIVAS ---");
        List<Mascota> mascotas = mascotaService.getAll();
        if (mascotas.isEmpty()) {
            System.out.println("📭 No hay mascotas registradas.");
            return;
        }
        mascotas.forEach(m -> System.out.println(m.toString()));
    }

    private void updateMascota() {
        // Se ajustó la llamada para usar el scanner
        Long id = MenuDisplay.getLongInput(scanner, "Ingrese el ID de la mascota a ACTUALIZAR: ");
        if (id == null) {
            return;
        }

        Mascota mascotaExistente = mascotaService.getById(id);
        if (mascotaExistente == null) {
            System.out.println("❌ Mascota con ID " + id + " no encontrada.");
            return;
        }

        System.out.println("\n--- Actualizando Mascota ID: " + id + " ---");
        System.out.println("Valor actual del Nombre: " + mascotaExistente.getNombre());
        // Se ajustó la llamada para usar el scanner
        String nuevoNombre = MenuDisplay.getStringInput(scanner, "Nuevo Nombre (Dejar vacío para mantener): ");

        if (!nuevoNombre.trim().isEmpty()) {
            mascotaExistente.setNombre(nuevoNombre);
        }

        // Lógica de actualización del microchip (si existe)
        Microchip microchip = mascotaExistente.getMicrochip();
        if (microchip != null) {
            System.out.println("\n--- Actualizando Microchip Código: " + microchip.getCodigo() + " ---");
            // Se ajustó la llamada para usar el scanner
            String nuevaVeterinaria = MenuDisplay.getStringInput(scanner, "Nueva Veterinaria (Dejar vacío para mantener): ");
            if (!nuevaVeterinaria.trim().isEmpty()) {
                microchip.setVeterinaria(nuevaVeterinaria);
            }
        }

        mascotaService.actualizar(mascotaExistente);
        System.out.println("✅ Mascota ID " + id + " actualizada con éxito (Transacción de actualización).");
    }

    private void deleteMascota() {
        // Se ajustó la llamada para usar el scanner
        Long id = MenuDisplay.getLongInput(scanner, "Ingrese el ID de la mascota para BAJA LÓGICA: ");
        if (id == null) {
            return;
        }

        mascotaService.eliminar(id);
        System.out.println("✅ Mascota ID " + id + " marcada como eliminada (Baja Lógica).");
    }

    private void listMicrochips() {
        System.out.println("\n--- LISTADO DE MICROCHIPS ---");
        List<Microchip> chips = microchipService.getAll();
        if (chips.isEmpty()) {
            System.out.println("📭 No hay microchips registrados.");
            return;
        }
        chips.forEach(c -> System.out.println(c.toString()));
    }
}
