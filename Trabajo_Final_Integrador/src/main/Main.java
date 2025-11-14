package main;

public class Main {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("<<< Iniciando Sistema de Veterinaria TFI >>>");
        System.out.println("=========================================");

        MenuHandlers handlers = new MenuHandlers();
        handlers.startApplication();
    }
}
