-- ============================================================
-- SCRIPT DB: db_agendamiento
-- Microservicio: ms-agendamiento (puerto 8082)
-- Contiene: citas y estados de cita
-- NOTA: id_paciente e id_medico referencian id_persona en
--       db_usuarios. No hay FK cruzada entre BDs.
-- ============================================================

CREATE TABLE IF NOT EXISTS dominio_estado_cita (
    id_estado_cita SERIAL PRIMARY KEY,
    nombre         VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS cita (
    id_cita        SERIAL PRIMARY KEY,
    id_paciente    INT NOT NULL,   -- id_persona del paciente en db_usuarios
    id_medico      INT NOT NULL,   -- id_persona del médico en db_usuarios
    fecha          DATE NOT NULL,
    hora_inicio    TIME NOT NULL,
    hora_fin       TIME NOT NULL,
    id_estado_cita INT REFERENCES dominio_estado_cita(id_estado_cita)
);

-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

INSERT INTO dominio_estado_cita (nombre) VALUES
('Cancelada'), ('Pendiente'), ('Confirmada'), ('Completada'), ('No Asistió');

-- Citas de prueba
-- id_paciente: 10-29 (id_persona de pacientes en db_usuarios)
-- id_medico:   3-9   (id_persona de médicos en db_usuarios)
-- Estados: 1=Cancelada, 2=Pendiente, 3=Confirmada, 4=Completada, 5=No Asistió
-- Semana actual: lunes 11 may → sábado 16 may 2026

INSERT INTO cita (id_paciente, id_medico, fecha, hora_inicio, hora_fin, id_estado_cita) VALUES
-- Lunes 11 mayo
(10, 3, '2026-05-11', '07:00', '07:30', 3),   -- García - Medicina General
(11, 4, '2026-05-11', '08:00', '08:45', 3),   -- López  - Fisioterapia
(12, 5, '2026-05-11', '09:00', '10:00', 2),   -- Torres - Psicología
(13, 6, '2026-05-11', '07:30', '08:00', 3),   -- Ramírez - Nutrición
(14, 7, '2026-05-11', '06:00', '06:30', 3),   -- Vargas - Cardiología
(15, 9, '2026-05-11', '07:00', '07:30', 2),   -- Castro - Pediatría
-- Martes 12 mayo
(16, 3, '2026-05-12', '14:00', '14:30', 2),   -- García - tarde
(17, 4, '2026-05-12', '08:00', '08:45', 3),   -- López
(18, 6, '2026-05-12', '07:30', '08:00', 3),   -- Ramírez
(19, 7, '2026-05-12', '14:00', '14:30', 2),   -- Vargas - tarde
(20, 8, '2026-05-12', '10:00', '10:45', 3),   -- Moreno - Neurología
(21, 9, '2026-05-12', '07:00', '07:30', 2),   -- Castro
-- Miércoles 13 mayo
(22, 3, '2026-05-13', '07:00', '07:30', 3),   -- García
(23, 5, '2026-05-13', '09:00', '10:00', 2),   -- Torres
(24, 7, '2026-05-13', '06:00', '06:30', 3),   -- Vargas
(25, 9, '2026-05-13', '07:30', '08:00', 2),   -- Castro
-- Jueves 14 mayo
(26, 3, '2026-05-14', '14:00', '14:30', 2),   -- García - tarde
(27, 4, '2026-05-14', '08:00', '08:45', 3),   -- López
(28, 5, '2026-05-14', '15:00', '16:00', 2),   -- Torres - tarde
(29, 6, '2026-05-14', '07:30', '08:00', 3),   -- Ramírez
(10, 8, '2026-05-14', '10:00', '10:45', 2),   -- Moreno
(11, 9, '2026-05-14', '07:00', '07:30', 3),   -- Castro
-- Viernes 15 mayo
(12, 3, '2026-05-15', '07:00', '07:30', 2),   -- García
(13, 4, '2026-05-15', '14:00', '14:45', 3),   -- López - tarde
(14, 5, '2026-05-15', '09:00', '10:00', 2),   -- Torres
(15, 7, '2026-05-15', '06:00', '06:30', 3),   -- Vargas
(16, 9, '2026-05-15', '07:30', '08:00', 2),   -- Castro
-- Sábado 16 mayo
(17, 6, '2026-05-16', '14:00', '14:30', 2),   -- Ramírez - Nutrición (sábado tarde)
(18, 8, '2026-05-16', '10:00', '10:45', 3),   -- Moreno  - Neurología (sábado mañana)
(19, 6, '2026-05-16', '14:30', '15:00', 2);   -- Ramírez - segunda cita sábado
