package main;

import java.util.Scanner;

public class AppMenu {

    public static void showMainMenu() {
        System.out.println("\n--- SISTEMA DE VETERINARIA TFI ---");
        System.out.println("1. Registrar Mascota y Microchip (Transacción)");
        System.out.println("2. Listar todas las Mascotas");
        System.out.println("3. Buscar Mascota por ID (con Microchip)");
        System.out.println("4. Actualizar Mascota y Microchip");
        System.out.println("5. Eliminar Mascota (Baja Lógica)");
        System.out.println("6. Listar todos los Microchips");
        System.out.println("0. Salir");
        System.out.println("------------------------------------");
    }

    public static int getMenuOption(Scanner scanner) {
        System.out.print("Seleccione una opción: ");
        if (scanner.hasNextInt()) {
            int option = scanner.nextInt();
            scanner.nextLine();
            return option;
        } else {
            System.err.println("Entrada inválida. Debe ingresar un número.");
            scanner.nextLine();
            return -1;
        }
    }

    public static String getStringInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input;
    }

    public static Long getLongInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();

        if (line.isEmpty()) {
            return null;
        }

        try {
            return Long.parseLong(line);
        } catch (NumberFormatException e) {
            System.err.println("Entrada inválida. Debe ingresar un número ID válido.");
            return null;
        }
    }
}
