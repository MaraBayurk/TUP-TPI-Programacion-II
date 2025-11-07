/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author facu3
 */

import service.MascotasServiceImpl;

public class Main {
    public static void main(String[] args) {
        MascotasServiceImpl service = new MascotasServiceImpl();

        service.registrarMascotaConMicrochip(
                "Luna",
                "Gato",
                "Gris",
                "2020-05-15",
                "Mara Bayurk",
                "CHIP-999999999999",
                "2025-11-07",
                "Veterinaria San Pablo",
                "Implantación sin complicaciones"
        );
    }
}
