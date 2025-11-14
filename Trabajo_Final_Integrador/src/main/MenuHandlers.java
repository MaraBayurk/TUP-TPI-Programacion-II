package main;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import models.Mascota;
import models.Microchip;
import service.MascotasServiceImpl;
import service.MicrochipsServiceImpl;

public class MenuHandlers {

    private final MascotasServiceImpl mascotaService = new MascotasServiceImpl();
    private final MicrochipsServiceImpl microchipService = new MicrochipsServiceImpl();
    private final Scanner scanner = new Scanner(System.in);

    public void startApplication() {
        int opcion;
        do {
            AppMenu.showMainMenu();
            opcion = AppMenu.getMenuOption(scanner);
            handleOption(opcion);
        } while (opcion != 0);
        scanner.close();
    }

    private void handleOption(int opcion) {
        try {
            switch (opcion) {
                case 1 ->
                    createMascotaTransaccional();
                case 2 ->
                    listMascotas();
                case 3 ->
                    findMascotaById();
                case 4 ->
                    updateMascota();
                case 5 ->
                    deleteMascota();
                case 6 ->
                    listMicrochips();
                case 0 ->
                    System.out.println("\n¡Saliendo del sistema! ¡Adiós!");
                default ->
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } catch (RuntimeException e) {
            System.err.println("⚠️ OPERACIÓN FALLIDA: " + e.getMessage());
        }
    }

    private void createMascotaTransaccional() {
        System.out.println("\n--- CREAR MASCOTA Y ASOCIAR MICROCHIP ---");

        String nombre = AppMenu.getStringInput(scanner, "Nombre de la Mascota: ");
        String especie = AppMenu.getStringInput(scanner, "Especie: ");
        String raza = AppMenu.getStringInput(scanner, "Raza: ");

        String fechaNacimientoStr = AppMenu.getStringInput(scanner, "Fecha Nacimiento (AAAA-MM-DD): ");
        LocalDate fechaNacimiento;
        try {
            fechaNacimiento = LocalDate.parse(fechaNacimientoStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato fecha nacimiento incorrecto. AAAA-MM-DD.");
        }

        String duenio = AppMenu.getStringInput(scanner, "Dueño: ");

        System.out.println("\n--- DATOS DEL MICROCHIP (1:1) ---");
        String codigo = AppMenu.getStringInput(scanner, "Código ÚNICO del Microchip: ");
        String fechaImplantacionStr = AppMenu.getStringInput(scanner, "Fecha Implantación (AAAA-MM-DD): ");
        LocalDate fechaImplantacion;
        try {
            fechaImplantacion = LocalDate.parse(fechaImplantacionStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato fecha implantación incorrecto. AAAA-MM-DD.");
        }

        String veterinaria = AppMenu.getStringInput(scanner, "Veterinaria: ");
        String observaciones = AppMenu.getStringInput(scanner, "Observaciones: ");

        Microchip microchip = new Microchip(codigo, fechaImplantacion, veterinaria, observaciones);
        Mascota mascota = new Mascota(nombre, especie, raza, fechaNacimiento, duenio, microchip);

        Mascota mascotaCreada = mascotaService.insertar(mascota);

        System.out.println("\n✅ ÉXITO: Mascota y Microchip creados en una sola transacción.");
        System.out.println("   Mascota ID Generado: " + mascotaCreada.getId());
    }

    private void updateMascota() {
        Long id = AppMenu.getLongInput(scanner, "Ingrese el ID de la mascota a ACTUALIZAR: ");
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
        String nuevoNombre = AppMenu.getStringInput(scanner, "Nuevo Nombre (Dejar vacío para mantener): ");
        if (!nuevoNombre.trim().isEmpty()) {
            mascotaExistente.setNombre(nuevoNombre);
        }

        Microchip microchip = mascotaExistente.getMicrochip();
        if (microchip != null) {
            System.out.println("\n--- Actualizando Microchip Código: " + microchip.getCodigo() + " ---");
            String nuevaVeterinaria = AppMenu.getStringInput(scanner, "Nueva Veterinaria (Dejar vacío para mantener): ");
            if (!nuevaVeterinaria.trim().isEmpty()) {
                microchip.setVeterinaria(nuevaVeterinaria);
            }
        }

        mascotaService.actualizar(mascotaExistente);
        System.out.println("✅ Mascota ID " + id + " actualizada con éxito.");
    }

    private void findMascotaById() {
        Long id = AppMenu.getLongInput(scanner, "Ingrese el ID de la mascota a buscar: ");
        if (id == null) {
            return;
        }

        Mascota mascota = mascotaService.getById(id);
        if (mascota != null) {
            System.out.println("\n🐾 DETALLES DE LA MASCOTA (Hidratado con Microchip):");
            System.out.println(mascota);
            if (mascota.getMicrochip() != null) {
                System.out.println(mascota.getMicrochip());
            }
        } else {
            System.out.println("❌ Mascota con ID " + id + " no encontrada o eliminada.");
        }
    }

    private void listMascotas() {
        System.out.println("\n--- LISTADO DE MASCOTAS ACTIVAS ---");
        List<Mascota> mascotas = mascotaService.getAll();
        if (mascotas.isEmpty()) {
            System.out.println("📭 No hay mascotas registradas.");
            return;
        }
        mascotas.forEach(m -> System.out.println(m.toString()));
    }

    private void deleteMascota() {
        Long id = AppMenu.getLongInput(scanner, "Ingrese el ID de la mascota para BAJA LÓGICA: ");
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
