package service;

import java.util.List;

public interface GenericService<T> {
    
    // 📌 Métodos CRUD estándar
    
    // Inserta una entidad. Maneja la transacción si es compuesta (A + B).
    T insertar(T entidad); 

    // Actualiza una entidad. Maneja la transacción si es compuesta.
    T actualizar(T entidad);
    
    // Elimina (lógicamente) una entidad.
    void eliminar(Long id);

    // Obtiene una entidad por su ID (sin transacción).
    T getById(Long id);

    // Obtiene todas las entidades activas (sin transacción).
    List<T> getAll();
}
