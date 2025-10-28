-- 1. CONFIGURACIÓN INICIAL (DDL)

-- 1.1. Creación y selección de la base de datos
CREATE DATABASE IF NOT EXISTS db_veterinaria_tfi;
USE db_veterinaria_tfi;

-- 1.2. Creación de la Tabla MASCOTAS (Clase A)
CREATE TABLE Mascotas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT UNIQUE NOT NULL,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nombre VARCHAR(60) NOT NULL,
    especie VARCHAR(30) NOT NULL,
    raza VARCHAR(60),
    fechaNacimiento DATE,
    duenio VARCHAR(120) NOT NULL
);

-- 1.3. Creación de la Tabla MICROCHIPS (Clase B)

CREATE TABLE Microchips (
    id BIGINT PRIMARY KEY AUTO_INCREMENT UNIQUE  NOT NULL,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    codigo VARCHAR(25) NOT NULL UNIQUE, 
    fechaImplantacion DATE,
    veterinaria VARCHAR(120),
    observaciones VARCHAR(255),
    mascota_id BIGINT UNIQUE,
    CONSTRAINT fk_microchip_mascota FOREIGN KEY (mascota_id) REFERENCES Mascotas(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 2. DATOS DE PRUEBA (DML Inicial)

-- 2.1. Insertar Mascotas
INSERT INTO Mascotas (nombre, especie, raza, fechaNacimiento, duenio) VALUES
('Fido', 'Perro', 'Labrador', '2020-05-10', 'Carlos Gómez'), -- ID: 1
('Garfield', 'Gato', 'Siamés', '2021-01-20', 'Ana Torres'),   -- ID: 2
('Bugs', 'Conejo', 'Belier', '2022-03-15', 'Martín Pérez');  -- ID: 3

-- 2.2. Insertar Microchips (Asociados a Fido y Garfield)
-- Se asocia el microchip a la mascota usando el ID generado automáticamente (mascota_id)
INSERT INTO Microchips (codigo, fechaImplantacion, veterinaria, observaciones, mascota_id) VALUES
('MC-123456789012345', '2023-08-01', 'Vet Central', 'Chip implantado en pata trasera.', 1), -- Asociado a Fido (id=1)
('MC-987654321098765', '2023-11-20', 'Clínica Patitas', NULL, 2); -- Asociado a Garfield (id=2)

SELECT * FROM Mascotas;
SELECT * FROM Microchips;
