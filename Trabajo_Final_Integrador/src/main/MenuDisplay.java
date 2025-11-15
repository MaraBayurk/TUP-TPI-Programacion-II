package main;

import java.util.Scanner;

// Clase estática responsable de la salida a consola (display) y la validación de la opción de menú del usuario.

public class MenuDisplay {

    // Usaremos un Scanner local dentro de MenuHandlers para más seguridad.
    
    public static void showMainMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1.  [C] Crear Mascota y Microchip");
        System.out.println("2.  [R] Listar Mascotas Activas");
        System.out.println("3.  [R] Buscar Mascota por ID y ver detalle");
        System.out.println("4.  [U] Actualizar Mascota y Microchip");
        System.out.println("5.  [D] Eliminar Mascota");
        System.out.println("6.  [R] Listar Microchips");
        System.out.println("0. Salir del Sistema");
        System.out.println("----------------------------------------");
        System.out.print("Seleccione una opcion: ");
    }
    
    public static String getStringInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static int getMenuOption(Scanner scanner) {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return -1; // Retorna un valor inválido
        }
    }

    public static Long getLongInput(Scanner scanner, String prompt) {
        String input = getStringInput(scanner, prompt);
        try {
            return Long.parseLong(input.trim());
        } catch (NumberFormatException e) {
            System.err.println("Error de entrada: Ingrese un numero valido.");
            return null;
        }
    }
}