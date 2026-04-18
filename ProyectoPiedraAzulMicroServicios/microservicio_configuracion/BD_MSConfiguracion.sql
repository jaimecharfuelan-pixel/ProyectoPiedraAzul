-- ============================================================
-- SCRIPT DB: db_configuracion
-- Microservicio: ms-configuracion (puerto 8083)
-- Contiene: jornadas laborales y especialidades
-- NOTA: id_medico referencia al id_persona del médico en
--       db_usuarios. No hay FK cruzada entre BDs — es
--       responsabilidad de la aplicación mantener consistencia.
-- ============================================================

CREATE TABLE IF NOT EXISTS dominio_especialidad (
    id_especialidad SERIAL PRIMARY KEY,
    nombre          VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS jornada_laboral (
    id_jornada                  SERIAL PRIMARY KEY,
    id_usuario                  INT NOT NULL,   -- id del médico en ms-usuarios
    dia_semana                  VARCHAR(20) NOT NULL,
    hora_inicio                 TIME NOT NULL,
    hora_fin                    TIME NOT NULL,
    id_estado                   INT DEFAULT 1,
    duracion_estimada_atencion  INT DEFAULT 30
);

-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

-- Especialidades (mismas que ms-usuarios para consistencia de UI)
INSERT INTO dominio_especialidad (nombre) VALUES
('Medicina General'), ('Fisioterapia'), ('Psicología'), ('Nutrición'),
('Cardiología'), ('Neurología'), ('Pediatría'), ('Dermatología'),
('Ortopedia'), ('Terapia Ocupacional');

-- Jornadas laborales
-- id_usuario corresponde al id_persona del médico en db_usuarios:
--   3 = Andrés García   (Medicina General)
--   4 = Sofía López     (Fisioterapia)
--   5 = Miguel Torres   (Psicología)
--   6 = Valentina Ramírez (Nutrición)
--   7 = Julián Vargas   (Cardiología)
--   8 = Camila Moreno   (Neurología)
--   9 = Ricardo Castro  (Pediatría)

INSERT INTO jornada_laboral (id_usuario, dia_semana, hora_inicio, hora_fin, id_estado, duracion_estimada_atencion) VALUES
-- García - Medicina General
(3, 'Lunes',     '07:00', '12:00', 1, 30),
(3, 'Miércoles', '07:00', '12:00', 1, 30),
(3, 'Viernes',   '07:00', '12:00', 1, 30),
(3, 'Martes',    '14:00', '18:00', 1, 30),
(3, 'Jueves',    '14:00', '18:00', 1, 30),
-- López - Fisioterapia
(4, 'Lunes',     '08:00', '13:00', 1, 45),
(4, 'Martes',    '08:00', '13:00', 1, 45),
(4, 'Jueves',    '08:00', '13:00', 1, 45),
(4, 'Viernes',   '14:00', '17:00', 1, 45),
(4, 'Miércoles', '14:00', '17:00', 1, 45),
-- Torres - Psicología
(5, 'Lunes',     '09:00', '14:00', 1, 60),
(5, 'Miércoles', '09:00', '14:00', 1, 60),
(5, 'Jueves',    '15:00', '19:00', 1, 60),
(5, 'Viernes',   '09:00', '14:00', 1, 60),
-- Ramírez - Nutrición
(6, 'Martes',    '07:30', '12:30', 1, 30),
(6, 'Jueves',    '07:30', '12:30', 1, 30),
(6, 'Sábado',    '14:00', '18:00', 1, 30),
(6, 'Lunes',     '07:30', '12:30', 1, 30),
-- Vargas - Cardiología
(7, 'Lunes',     '06:00', '11:00', 1, 30),
(7, 'Miércoles', '06:00', '11:00', 1, 30),
(7, 'Viernes',   '06:00', '11:00', 1, 30),
(7, 'Martes',    '14:00', '18:00', 1, 30),
-- Moreno - Neurología
(8, 'Martes',    '10:00', '15:00', 1, 45),
(8, 'Jueves',    '10:00', '15:00', 1, 45),
(8, 'Sábado',    '10:00', '14:00', 1, 45),
-- Castro - Pediatría
(9, 'Lunes',     '07:00', '12:00', 1, 30),
(9, 'Martes',    '07:00', '12:00', 1, 30),
(9, 'Miércoles', '07:00', '12:00', 1, 30),
(9, 'Jueves',    '07:00', '12:00', 1, 30),
(9, 'Viernes',   '07:00', '12:00', 1, 30);
