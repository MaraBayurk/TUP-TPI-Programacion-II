package service;

import java.util.List;
import models.Mascota; // Usamos Mascota como ejemplo
import models.Microchip; // Usamos Microchip como ejemplo

public interface GenericService<T> {
    
    // Métodos Transaccionales/Operacionales
    T insertar(T entidad); 
    T actualizar(T entidad);
    void eliminar(Long id);

    // Métodos de Lectura
    T getById(Long id);
    List<T> getAll();
}