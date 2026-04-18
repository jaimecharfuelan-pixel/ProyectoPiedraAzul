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

INSERT INTO cita (id_paciente, id_medico, fecha, hora_inicio, hora_fin, id_estado_cita) VALUES
(10, 3, '2026-04-21', '07:00', '07:30', 3),
(11, 4, '2026-04-21', '08:00', '08:45', 2),
(12, 5, '2026-04-22', '09:00', '10:00', 3),
(13, 6, '2026-04-22', '07:30', '08:00', 2),
(14, 7, '2026-04-21', '06:00', '06:30', 4),
(15, 8, '2026-04-22', '10:00', '10:45', 3),
(16, 9, '2026-04-21', '07:00', '07:30', 2),
(17, 3, '2026-04-23', '07:00', '07:30', 2),
(18, 4, '2026-04-23', '08:00', '08:45', 3),
(19, 5, '2026-04-23', '09:00', '10:00', 2),
(20, 6, '2026-04-24', '07:30', '08:00', 3),
(21, 7, '2026-04-21', '06:30', '07:00', 4),
(22, 8, '2026-04-24', '10:00', '10:45', 2),
(23, 9, '2026-04-21', '07:30', '08:00', 3),
(24, 3, '2026-04-25', '07:00', '07:30', 2),
(25, 4, '2026-04-21', '08:45', '09:30', 4),
(26, 5, '2026-04-22', '10:00', '11:00', 1),
(27, 6, '2026-04-22', '08:00', '08:30', 3),
(28, 7, '2026-04-23', '06:00', '06:30', 2),
(29, 8, '2026-04-24', '10:45', '11:30', 3),
(10, 9, '2026-04-23', '08:00', '08:30', 4),
(11, 3, '2026-04-25', '07:30', '08:00', 2),
(12, 4, '2026-04-25', '09:30', '10:15', 3),
(13, 5, '2026-04-24', '09:00', '10:00', 5),
(14, 6, '2026-04-25', '07:30', '08:00', 2),
(15, 7, '2026-04-23', '06:30', '07:00', 3),
(16, 8, '2026-04-25', '11:30', '12:15', 2),
(17, 9, '2026-04-24', '07:00', '07:30', 4),
(18, 3, '2026-04-28', '14:00', '14:30', 2),
(19, 4, '2026-04-28', '14:00', '14:45', 3);
