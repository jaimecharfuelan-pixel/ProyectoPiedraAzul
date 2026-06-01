
-- ============================================================
-- DB 3: db_agendamiento
-- ============================================================

-- ============================================================
-- DB 3: db_agendamiento - CREACIÓN DE TABLAS
-- ============================================================

CREATE TABLE IF NOT EXISTS dominio_estado_cita (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS cita (
    id SERIAL PRIMARY KEY,
    id_paciente INT NOT NULL,
    id_medico INT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    id_estado_cita INT REFERENCES dominio_estado_cita(id)
);

-- ============================================================
-- DATOS INICIALES (Tu código original empieza aquí abajo)
-- ============================================================

INSERT INTO dominio_estado_cita (nombre) VALUES
('Cancelada'),   -- 1
('Pendiente'),   -- 2
('Confirmada'),  -- 3
('Completada'),  -- 4
('No Asistió'); -- 5

-- ─────────────────────────────────────────
-- CITAS ACTIVAS: una por paciente (10-24)
-- Distribuidas en la semana del 27 mayo al 1 jun 2026
-- ─────────────────────────────────────────
INSERT INTO cita (id_paciente, id_medico, fecha, hora_inicio, hora_fin, id_estado_cita) VALUES

-- Miércoles 27 mayo
(10, 3, '2026-05-27', '07:00', '07:30', 3),   -- Juan Pérez       → García (Medicina General) Confirmada
(11, 4, '2026-05-27', '08:00', '08:45', 2),   -- María Gómez      → López (Fisioterapia) Pendiente
(12, 5, '2026-05-27', '09:00', '10:00', 3),   -- Pedro Herrera     → Torres (Psicología) Confirmada
(13, 6, '2026-05-27', '07:30', '08:00', 2),   -- Ana Jiménez      → Ramírez (Nutrición) Pendiente
(14, 7, '2026-05-27', '06:00', '06:30', 3),   -- Luis Mendoza     → Vargas (Cardiología) Confirmada

-- Jueves 28 mayo
(15, 8, '2026-05-28', '10:00', '10:45', 2),   -- Paola Ríos       → Moreno (Neurología) Pendiente
(16, 9, '2026-05-28', '07:00', '07:30', 3),   -- Diego Silva      → Castro (Pediatría) Confirmada
(17, 3, '2026-05-28', '14:00', '14:30', 2),   -- Natalia Rojas    → García (Medicina General) Pendiente
(18, 4, '2026-05-28', '08:00', '08:45', 3),   -- Sebastián Ortiz  → López (Fisioterapia) Confirmada

-- Viernes 29 mayo
(19, 7, '2026-05-29', '06:00', '06:30', 2),   -- Daniela Núñez    → Vargas (Cardiología) Pendiente
(20, 5, '2026-05-29', '09:00', '10:00', 3),   -- Felipe Vega      → Torres (Psicología) Confirmada
(21, 6, '2026-05-29', '07:30', '08:00', 2),   -- Alejandra Soto   → Ramírez (Nutrición) Pendiente

-- Sábado 30 mayo
(22, 9, '2026-05-30', '07:00', '07:30', 3),   -- Mateo Reyes      → Castro (Pediatría) Confirmada
(23, 8, '2026-05-30', '10:00', '10:45', 2),   -- Valeria Mora     → Moreno (Neurología) Pendiente
(24, 3, '2026-05-30', '07:30', '08:00', 3),   -- Tomás León       → García (Medicina General) Confirmada

-- ─────────────────────────────────────────
-- CITAS HISTÓRICAS: para probar auditoría
-- Estados: Completada (4), Cancelada (1), No Asistió (5)
-- ─────────────────────────────────────────
(10, 3, '2026-05-20', '07:00', '07:30', 4),   -- Juan Pérez       → Completada (reagendada antes)
(11, 4, '2026-05-19', '08:00', '08:45', 1),   -- María Gómez      → Cancelada
(12, 5, '2026-05-18', '09:00', '10:00', 5),   -- Pedro Herrera    → No Asistió
(13, 6, '2026-05-15', '07:30', '08:00', 4),   -- Ana Jiménez      → Completada
(14, 7, '2026-05-14', '06:00', '06:30', 1),   -- Luis Mendoza     → Cancelada
(15, 9, '2026-05-13', '07:00', '07:30', 4),   -- Paola Ríos       → Completada
(16, 3, '2026-05-12', '14:00', '14:30', 1),   -- Diego Silva      → Cancelada
(17, 4, '2026-05-11', '08:00', '08:45', 4),   -- Natalia Rojas    → Completada
(18, 6, '2026-05-10', '07:30', '08:00', 5),   -- Sebastián Ortiz  → No Asistió
(19, 7, '2026-05-09', '06:00', '06:30', 4);   -- Daniela Núñez    → Completada