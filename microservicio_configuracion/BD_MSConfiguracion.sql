-- ============================================================
-- SCRIPT DB: db_configuracion  –  Datos reales Piedrazul
-- Fecha carga: 2026-06-05
-- ============================================================

CREATE TABLE IF NOT EXISTS dominio_especialidad (
    id_especialidad SERIAL PRIMARY KEY,
    nombre          VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS jornada_laboral (
    id_jornada                 SERIAL PRIMARY KEY,
    id_usuario                 INT NOT NULL,
    dia_semana                 VARCHAR(20) NOT NULL,
    hora_inicio                TIME NOT NULL,
    hora_fin                   TIME NOT NULL,
    id_estado                  INT DEFAULT 1,
    duracion_estimada_atencion INT DEFAULT 30
);

-- ─────────────────────────────────────────
-- ESPECIALIDADES
-- (deben coincidir en orden con las de db_usuarios)
-- ─────────────────────────────────────────

INSERT INTO dominio_especialidad (nombre) VALUES
('Terapia Neural'),   -- 1
('Fisioterapia'),     -- 2
('Quiropraxia'),      -- 3
('Medicina General'), -- 4
('Psicología'),       -- 5
('Nutrición'),        -- 6
('Cardiología'),      -- 7
('Neurología'),       -- 8
('Pediatría'),        -- 9
('Ortopedia');        -- 10

-- ─────────────────────────────────────────
-- JORNADAS LABORALES
--
-- id_usuario referencia el id_usuario de db_usuarios:
--   4  = clara.cordoba      (Terapia Neural)
--   5  = jose.garcia        (Terapia Neural)
--   6  = ibis.gonzalez      (Terapia Neural)
--   7  = christian.gonzalez (Terapia Neural)
--   8  = zarama.velasco     (Fisioterapia)
--   9  = armando.pena       (Quiropraxia)
--
-- duracion_estimada_atencion en minutos:
--   Terapia Neural  → 30 min por turno
--   Fisioterapia    → 45 min por turno
--   Quiropraxia     → 30 min por turno
-- ─────────────────────────────────────────

-- ── Clara Inés Córdoba (usuario 4) ─────────────────────────
-- Lunes a Viernes 07:00-12:00, turnos de 30 min (10 turnos/día)
INSERT INTO jornada_laboral (id_usuario, dia_semana, hora_inicio, hora_fin, id_estado, duracion_estimada_atencion) VALUES
(4, 'Lunes',    '07:00', '12:00', 1, 30),
(4, 'Martes',   '07:00', '12:00', 1, 30),
(4, 'Miércoles','07:00', '12:00', 1, 30),
(4, 'Jueves',   '07:00', '12:00', 1, 30),
(4, 'Viernes',  '07:00', '12:00', 1, 30);

-- ── Jose Ignacio Garcia (usuario 5) ────────────────────────
-- Lunes a Viernes 14:00-18:00, turnos de 30 min (8 turnos/día)
INSERT INTO jornada_laboral (id_usuario, dia_semana, hora_inicio, hora_fin, id_estado, duracion_estimada_atencion) VALUES
(5, 'Lunes',    '14:00', '18:00', 1, 30),
(5, 'Martes',   '14:00', '18:00', 1, 30),
(5, 'Miércoles','14:00', '18:00', 1, 30),
(5, 'Jueves',   '14:00', '18:00', 1, 30),
(5, 'Viernes',  '14:00', '18:00', 1, 30);

-- ── Ibis Gonzalez (usuario 6) ──────────────────────────────
-- Lunes, Miércoles, Viernes 08:00-13:00, turnos de 30 min (10 turnos/día)
INSERT INTO jornada_laboral (id_usuario, dia_semana, hora_inicio, hora_fin, id_estado, duracion_estimada_atencion) VALUES
(6, 'Lunes',    '08:00', '13:00', 1, 30),
(6, 'Miércoles','08:00', '13:00', 1, 30),
(6, 'Viernes',  '08:00', '13:00', 1, 30);

-- ── Christian Gonzalez (usuario 7) ─────────────────────────
-- Martes, Jueves, Sábado 09:00-14:00, turnos de 30 min (10 turnos/día)
INSERT INTO jornada_laboral (id_usuario, dia_semana, hora_inicio, hora_fin, id_estado, duracion_estimada_atencion) VALUES
(7, 'Martes',   '09:00', '14:00', 1, 30),
(7, 'Jueves',   '09:00', '14:00', 1, 30),
(7, 'Sábado',   '09:00', '14:00', 1, 30);

-- ── Zarama Velasco (usuario 8) – Fisioterapia ───────────────
-- Lunes a Viernes 07:00-13:00, turnos de 45 min (8 turnos/día)
INSERT INTO jornada_laboral (id_usuario, dia_semana, hora_inicio, hora_fin, id_estado, duracion_estimada_atencion) VALUES
(8, 'Lunes',    '07:00', '13:00', 1, 45),
(8, 'Martes',   '07:00', '13:00', 1, 45),
(8, 'Miércoles','07:00', '13:00', 1, 45),
(8, 'Jueves',   '07:00', '13:00', 1, 45),
(8, 'Viernes',  '07:00', '13:00', 1, 45);

-- ── Armando Peña (usuario 9) – Quiropraxia ─────────────────
-- Lunes, Martes, Jueves, Viernes 15:00-19:00, turnos de 30 min (8 turnos/día)
INSERT INTO jornada_laboral (id_usuario, dia_semana, hora_inicio, hora_fin, id_estado, duracion_estimada_atencion) VALUES
(9, 'Lunes',    '15:00', '19:00', 1, 30),
(9, 'Martes',   '15:00', '19:00', 1, 30),
(9, 'Jueves',   '15:00', '19:00', 1, 30),
(9, 'Viernes',  '15:00', '19:00', 1, 30);
