-- ============================================================
-- SCRIPT DB: db_agendamiento  –  Datos reales Piedrazul
-- Fecha carga: 2026-06-05
-- ============================================================
-- Mapa id_medico (= id_persona en db_usuarios):
--   3  = Clara Inés Córdoba      (Terapia Neural)  Lun-Vie 07:00-12:00  30min
--   4  = Jose Ignacio Garcia     (Terapia Neural)  Lun-Vie 14:00-18:00  30min
--   5  = Ibis Gonzalez           (Terapia Neural)  Lun/Mié/Vie 08:00-13:00  30min
--   6  = Christian Gonzalez      (Terapia Neural)  Mar/Jue/Sáb 09:00-14:00  30min
--   7  = Zarama Velasco          (Fisioterapia)    Lun-Vie 07:00-13:00  45min
--   8  = Armando Peña            (Quiropraxia)     Lun/Mar/Jue/Vie 15:00-19:00  30min
--
-- Pacientes (id_persona en db_usuarios):
--   9..23 = Juan Torres, María Ramírez, Pedro Herrera, Ana Jiménez,
--            Luis Mendoza, Paola Ríos, Diego Silva, Natalia Rojas,
--            Sebastián Ortiz, Daniela Núñez, Felipe Vega, Alejandra Soto,
--            Mateo Reyes, Valeria Mora, Tomás León
--
-- Días con franjas completas cargadas:
--   Jueves  4 jun 2026
--   Viernes 5 jun 2026  ← HOY
--   Lunes   8 jun 2026
-- ============================================================

CREATE TABLE IF NOT EXISTS dominio_estado_cita (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS cita (
    id              SERIAL PRIMARY KEY,
    id_paciente     INT NOT NULL,
    id_medico       INT NOT NULL,
    fecha           DATE NOT NULL,
    hora_inicio     TIME NOT NULL,
    hora_fin        TIME NOT NULL,
    id_estado_cita  INT REFERENCES dominio_estado_cita(id)
);

-- ─────────────────────────────────────────
-- ESTADOS DE CITA
-- ─────────────────────────────────────────
INSERT INTO dominio_estado_cita (nombre) VALUES
('Cancelada'),   -- 1
('Pendiente'),   -- 2
('Confirmada'),  -- 3
('Completada'),  -- 4
('No Asistió'); -- 5

-- ============================================================
-- ──────────────────────────────────────────────────────────
-- JUEVES 4 JUNIO 2026  (ayer)
-- ──────────────────────────────────────────────────────────
-- Clara Inés Córdoba (id=3)  Jue 07:00-12:00  10 turnos de 30min
INSERT INTO cita (id_paciente, id_medico, fecha, hora_inicio, hora_fin, id_estado_cita) VALUES
(9,  3, '2026-06-04', '07:00', '07:30', 4),  -- Juan Torres      Completada
(10, 3, '2026-06-04', '07:30', '08:00', 4),  -- María Ramírez    Completada
(11, 3, '2026-06-04', '08:00', '08:30', 4),  -- Pedro Herrera    Completada
(12, 3, '2026-06-04', '08:30', '09:00', 4),  -- Ana Jiménez      Completada
(13, 3, '2026-06-04', '09:00', '09:30', 4),  -- Luis Mendoza     Completada
(14, 3, '2026-06-04', '09:30', '10:00', 4),  -- Paola Ríos       Completada
(15, 3, '2026-06-04', '10:00', '10:30', 4),  -- Diego Silva      Completada
(16, 3, '2026-06-04', '10:30', '11:00', 4),  -- Natalia Rojas    Completada
(17, 3, '2026-06-04', '11:00', '11:30', 4),  -- Sebastián Ortiz  Completada
(18, 3, '2026-06-04', '11:30', '12:00', 4),  -- Daniela Núñez    Completada

-- Jose Ignacio Garcia (id=4)  Jue 14:00-18:00  8 turnos de 30min
(19, 4, '2026-06-04', '14:00', '14:30', 4),  -- Felipe Vega      Completada
(20, 4, '2026-06-04', '14:30', '15:00', 4),  -- Alejandra Soto   Completada
(21, 4, '2026-06-04', '15:00', '15:30', 4),  -- Mateo Reyes      Completada
(22, 4, '2026-06-04', '15:30', '16:00', 4),  -- Valeria Mora     Completada
(23, 4, '2026-06-04', '16:00', '16:30', 4),  -- Tomás León       Completada
(9,  4, '2026-06-04', '16:30', '17:00', 4),  -- Juan Torres      Completada
(10, 4, '2026-06-04', '17:00', '17:30', 4),  -- María Ramírez    Completada
(11, 4, '2026-06-04', '17:30', '18:00', 4),  -- Pedro Herrera    Completada

-- Christian Gonzalez (id=6)  Jue 09:00-14:00  10 turnos de 30min
(12, 6, '2026-06-04', '09:00', '09:30', 4),  -- Ana Jiménez      Completada
(13, 6, '2026-06-04', '09:30', '10:00', 4),  -- Luis Mendoza     Completada
(14, 6, '2026-06-04', '10:00', '10:30', 4),  -- Paola Ríos       Completada
(15, 6, '2026-06-04', '10:30', '11:00', 4),  -- Diego Silva      Completada
(16, 6, '2026-06-04', '11:00', '11:30', 4),  -- Natalia Rojas    Completada
(17, 6, '2026-06-04', '11:30', '12:00', 4),  -- Sebastián Ortiz  Completada
(18, 6, '2026-06-04', '12:00', '12:30', 4),  -- Daniela Núñez    Completada
(19, 6, '2026-06-04', '12:30', '13:00', 4),  -- Felipe Vega      Completada
(20, 6, '2026-06-04', '13:00', '13:30', 4),  -- Alejandra Soto   Completada
(21, 6, '2026-06-04', '13:30', '14:00', 4),  -- Mateo Reyes      Completada

-- Zarama Velasco (id=7)  Jue 07:00-13:00  8 turnos de 45min
(22, 7, '2026-06-04', '07:00', '07:45', 4),  -- Valeria Mora     Completada
(23, 7, '2026-06-04', '07:45', '08:30', 4),  -- Tomás León       Completada
(9,  7, '2026-06-04', '08:30', '09:15', 4),  -- Juan Torres      Completada
(10, 7, '2026-06-04', '09:15', '10:00', 4),  -- María Ramírez    Completada
(11, 7, '2026-06-04', '10:00', '10:45', 4),  -- Pedro Herrera    Completada
(12, 7, '2026-06-04', '10:45', '11:30', 4),  -- Ana Jiménez      Completada
(13, 7, '2026-06-04', '11:30', '12:15', 4),  -- Luis Mendoza     Completada
(14, 7, '2026-06-04', '12:15', '13:00', 4),  -- Paola Ríos       Completada

-- Armando Peña (id=8)  Jue 15:00-19:00  8 turnos de 30min
(15, 8, '2026-06-04', '15:00', '15:30', 4),  -- Diego Silva      Completada
(16, 8, '2026-06-04', '15:30', '16:00', 4),  -- Natalia Rojas    Completada
(17, 8, '2026-06-04', '16:00', '16:30', 4),  -- Sebastián Ortiz  Completada
(18, 8, '2026-06-04', '16:30', '17:00', 4),  -- Daniela Núñez    Completada
(19, 8, '2026-06-04', '17:00', '17:30', 4),  -- Felipe Vega      Completada
(20, 8, '2026-06-04', '17:30', '18:00', 4),  -- Alejandra Soto   Completada
(21, 8, '2026-06-04', '18:00', '18:30', 4),  -- Mateo Reyes      Completada
(22, 8, '2026-06-04', '18:30', '19:00', 4);  -- Valeria Mora     Completada

-- ──────────────────────────────────────────────────────────
-- VIERNES 5 JUNIO 2026  ← HOY
-- ──────────────────────────────────────────────────────────
-- Clara Inés Córdoba (id=3)  Vie 07:00-12:00  10 turnos de 30min
INSERT INTO cita (id_paciente, id_medico, fecha, hora_inicio, hora_fin, id_estado_cita) VALUES
(9,  3, '2026-06-05', '07:00', '07:30', 3),  -- Juan Torres      Confirmada
(10, 3, '2026-06-05', '07:30', '08:00', 3),  -- María Ramírez    Confirmada
(11, 3, '2026-06-05', '08:00', '08:30', 3),  -- Pedro Herrera    Confirmada
(12, 3, '2026-06-05', '08:30', '09:00', 2),  -- Ana Jiménez      Pendiente
(13, 3, '2026-06-05', '09:00', '09:30', 3),  -- Luis Mendoza     Confirmada
(14, 3, '2026-06-05', '09:30', '10:00', 2),  -- Paola Ríos       Pendiente
(15, 3, '2026-06-05', '10:00', '10:30', 3),  -- Diego Silva      Confirmada
(16, 3, '2026-06-05', '10:30', '11:00', 2),  -- Natalia Rojas    Pendiente
(17, 3, '2026-06-05', '11:00', '11:30', 3),  -- Sebastián Ortiz  Confirmada
(18, 3, '2026-06-05', '11:30', '12:00', 3),  -- Daniela Núñez    Confirmada

-- Jose Ignacio Garcia (id=4)  Vie 14:00-18:00  8 turnos de 30min
(19, 4, '2026-06-05', '14:00', '14:30', 3),  -- Felipe Vega      Confirmada
(20, 4, '2026-06-05', '14:30', '15:00', 2),  -- Alejandra Soto   Pendiente
(21, 4, '2026-06-05', '15:00', '15:30', 3),  -- Mateo Reyes      Confirmada
(22, 4, '2026-06-05', '15:30', '16:00', 3),  -- Valeria Mora     Confirmada
(23, 4, '2026-06-05', '16:00', '16:30', 2),  -- Tomás León       Pendiente
(9,  4, '2026-06-05', '16:30', '17:00', 3),  -- Juan Torres      Confirmada
(10, 4, '2026-06-05', '17:00', '17:30', 3),  -- María Ramírez    Confirmada
(11, 4, '2026-06-05', '17:30', '18:00', 2),  -- Pedro Herrera    Pendiente

-- Ibis Gonzalez (id=5)  Vie 08:00-13:00  10 turnos de 30min
(12, 5, '2026-06-05', '08:00', '08:30', 3),  -- Ana Jiménez      Confirmada
(13, 5, '2026-06-05', '08:30', '09:00', 3),  -- Luis Mendoza     Confirmada
(14, 5, '2026-06-05', '09:00', '09:30', 2),  -- Paola Ríos       Pendiente
(15, 5, '2026-06-05', '09:30', '10:00', 3),  -- Diego Silva      Confirmada
(16, 5, '2026-06-05', '10:00', '10:30', 3),  -- Natalia Rojas    Confirmada
(17, 5, '2026-06-05', '10:30', '11:00', 2),  -- Sebastián Ortiz  Pendiente
(18, 5, '2026-06-05', '11:00', '11:30', 3),  -- Daniela Núñez    Confirmada
(19, 5, '2026-06-05', '11:30', '12:00', 3),  -- Felipe Vega      Confirmada
(20, 5, '2026-06-05', '12:00', '12:30', 2),  -- Alejandra Soto   Pendiente
(21, 5, '2026-06-05', '12:30', '13:00', 3),  -- Mateo Reyes      Confirmada

-- Zarama Velasco (id=7)  Vie 07:00-13:00  8 turnos de 45min
(22, 7, '2026-06-05', '07:00', '07:45', 3),  -- Valeria Mora     Confirmada
(23, 7, '2026-06-05', '07:45', '08:30', 3),  -- Tomás León       Confirmada
(9,  7, '2026-06-05', '08:30', '09:15', 2),  -- Juan Torres      Pendiente
(10, 7, '2026-06-05', '09:15', '10:00', 3),  -- María Ramírez    Confirmada
(11, 7, '2026-06-05', '10:00', '10:45', 3),  -- Pedro Herrera    Confirmada
(12, 7, '2026-06-05', '10:45', '11:30', 2),  -- Ana Jiménez      Pendiente
(13, 7, '2026-06-05', '11:30', '12:15', 3),  -- Luis Mendoza     Confirmada
(14, 7, '2026-06-05', '12:15', '13:00', 3),  -- Paola Ríos       Confirmada

-- Armando Peña (id=8)  Vie 15:00-19:00  8 turnos de 30min
(15, 8, '2026-06-05', '15:00', '15:30', 3),  -- Diego Silva      Confirmada
(16, 8, '2026-06-05', '15:30', '16:00', 2),  -- Natalia Rojas    Pendiente
(17, 8, '2026-06-05', '16:00', '16:30', 3),  -- Sebastián Ortiz  Confirmada
(18, 8, '2026-06-05', '16:30', '17:00', 3),  -- Daniela Núñez    Confirmada
(19, 8, '2026-06-05', '17:00', '17:30', 2),  -- Felipe Vega      Pendiente
(20, 8, '2026-06-05', '17:30', '18:00', 3),  -- Alejandra Soto   Confirmada
(21, 8, '2026-06-05', '18:00', '18:30', 3),  -- Mateo Reyes      Confirmada
(22, 8, '2026-06-05', '18:30', '19:00', 2);  -- Valeria Mora     Pendiente

-- ──────────────────────────────────────────────────────────
-- LUNES 8 JUNIO 2026  (próximo día hábil)
-- ──────────────────────────────────────────────────────────
-- Clara Inés Córdoba (id=3)  Lun 07:00-12:00  10 turnos de 30min
INSERT INTO cita (id_paciente, id_medico, fecha, hora_inicio, hora_fin, id_estado_cita) VALUES
(9,  3, '2026-06-08', '07:00', '07:30', 2),  -- Juan Torres      Pendiente
(10, 3, '2026-06-08', '07:30', '08:00', 2),  -- María Ramírez    Pendiente
(11, 3, '2026-06-08', '08:00', '08:30', 2),  -- Pedro Herrera    Pendiente
(12, 3, '2026-06-08', '08:30', '09:00', 2),  -- Ana Jiménez      Pendiente
(13, 3, '2026-06-08', '09:00', '09:30', 2),  -- Luis Mendoza     Pendiente
(14, 3, '2026-06-08', '09:30', '10:00', 2),  -- Paola Ríos       Pendiente
(15, 3, '2026-06-08', '10:00', '10:30', 2),  -- Diego Silva      Pendiente
(16, 3, '2026-06-08', '10:30', '11:00', 2),  -- Natalia Rojas    Pendiente
(17, 3, '2026-06-08', '11:00', '11:30', 2),  -- Sebastián Ortiz  Pendiente
(18, 3, '2026-06-08', '11:30', '12:00', 2),  -- Daniela Núñez    Pendiente

-- Jose Ignacio Garcia (id=4)  Lun 14:00-18:00  8 turnos de 30min
(19, 4, '2026-06-08', '14:00', '14:30', 2),  -- Felipe Vega      Pendiente
(20, 4, '2026-06-08', '14:30', '15:00', 2),  -- Alejandra Soto   Pendiente
(21, 4, '2026-06-08', '15:00', '15:30', 2),  -- Mateo Reyes      Pendiente
(22, 4, '2026-06-08', '15:30', '16:00', 2),  -- Valeria Mora     Pendiente
(23, 4, '2026-06-08', '16:00', '16:30', 2),  -- Tomás León       Pendiente
(9,  4, '2026-06-08', '16:30', '17:00', 2),  -- Juan Torres      Pendiente
(10, 4, '2026-06-08', '17:00', '17:30', 2),  -- María Ramírez    Pendiente
(11, 4, '2026-06-08', '17:30', '18:00', 2),  -- Pedro Herrera    Pendiente

-- Ibis Gonzalez (id=5)  Lun 08:00-13:00  10 turnos de 30min
(12, 5, '2026-06-08', '08:00', '08:30', 2),  -- Ana Jiménez      Pendiente
(13, 5, '2026-06-08', '08:30', '09:00', 2),  -- Luis Mendoza     Pendiente
(14, 5, '2026-06-08', '09:00', '09:30', 2),  -- Paola Ríos       Pendiente
(15, 5, '2026-06-08', '09:30', '10:00', 2),  -- Diego Silva      Pendiente
(16, 5, '2026-06-08', '10:00', '10:30', 2),  -- Natalia Rojas    Pendiente
(17, 5, '2026-06-08', '10:30', '11:00', 2),  -- Sebastián Ortiz  Pendiente
(18, 5, '2026-06-08', '11:00', '11:30', 2),  -- Daniela Núñez    Pendiente
(19, 5, '2026-06-08', '11:30', '12:00', 2),  -- Felipe Vega      Pendiente
(20, 5, '2026-06-08', '12:00', '12:30', 2),  -- Alejandra Soto   Pendiente
(21, 5, '2026-06-08', '12:30', '13:00', 2),  -- Mateo Reyes      Pendiente

-- Zarama Velasco (id=7)  Lun 07:00-13:00  8 turnos de 45min
(22, 7, '2026-06-08', '07:00', '07:45', 2),  -- Valeria Mora     Pendiente
(23, 7, '2026-06-08', '07:45', '08:30', 2),  -- Tomás León       Pendiente
(9,  7, '2026-06-08', '08:30', '09:15', 2),  -- Juan Torres      Pendiente
(10, 7, '2026-06-08', '09:15', '10:00', 2),  -- María Ramírez    Pendiente
(11, 7, '2026-06-08', '10:00', '10:45', 2),  -- Pedro Herrera    Pendiente
(12, 7, '2026-06-08', '10:45', '11:30', 2),  -- Ana Jiménez      Pendiente
(13, 7, '2026-06-08', '11:30', '12:15', 2),  -- Luis Mendoza     Pendiente
(14, 7, '2026-06-08', '12:15', '13:00', 2),  -- Paola Ríos       Pendiente

-- Armando Peña (id=8)  Lun 15:00-19:00  8 turnos de 30min
(15, 8, '2026-06-08', '15:00', '15:30', 2),  -- Diego Silva      Pendiente
(16, 8, '2026-06-08', '15:30', '16:00', 2),  -- Natalia Rojas    Pendiente
(17, 8, '2026-06-08', '16:00', '16:30', 2),  -- Sebastián Ortiz  Pendiente
(18, 8, '2026-06-08', '16:30', '17:00', 2),  -- Daniela Núñez    Pendiente
(19, 8, '2026-06-08', '17:00', '17:30', 2),  -- Felipe Vega      Pendiente
(20, 8, '2026-06-08', '17:30', '18:00', 2),  -- Alejandra Soto   Pendiente
(21, 8, '2026-06-08', '18:00', '18:30', 2),  -- Mateo Reyes      Pendiente
(22, 8, '2026-06-08', '18:30', '19:00', 2);  -- Valeria Mora     Pendiente

-- ──────────────────────────────────────────────────────────
-- CITAS HISTÓRICAS  (mayo 2026 – para auditoría/historial)
-- ──────────────────────────────────────────────────────────
INSERT INTO cita (id_paciente, id_medico, fecha, hora_inicio, hora_fin, id_estado_cita) VALUES
-- Clara – semana 26 mayo
(9,  3, '2026-05-26', '07:00', '07:30', 4),  -- Juan Torres      Completada
(10, 3, '2026-05-26', '07:30', '08:00', 4),  -- María Ramírez    Completada
(11, 3, '2026-05-26', '08:00', '08:30', 1),  -- Pedro Herrera    Cancelada
(12, 3, '2026-05-26', '08:30', '09:00', 4),  -- Ana Jiménez      Completada
(13, 3, '2026-05-26', '09:00', '09:30', 5),  -- Luis Mendoza     No Asistió
-- Jose – semana 26 mayo
(14, 4, '2026-05-26', '14:00', '14:30', 4),  -- Paola Ríos       Completada
(15, 4, '2026-05-26', '14:30', '15:00', 4),  -- Diego Silva      Completada
(16, 4, '2026-05-26', '15:00', '15:30', 1),  -- Natalia Rojas    Cancelada
-- Ibis – semana 25 mayo (Lunes)
(17, 5, '2026-05-25', '08:00', '08:30', 4),  -- Sebastián Ortiz  Completada
(18, 5, '2026-05-25', '08:30', '09:00', 4),  -- Daniela Núñez    Completada
(19, 5, '2026-05-25', '09:00', '09:30', 5),  -- Felipe Vega      No Asistió
-- Christian – semana 26 mayo (Martes)
(20, 6, '2026-05-26', '09:00', '09:30', 4),  -- Alejandra Soto   Completada
(21, 6, '2026-05-26', '09:30', '10:00', 4),  -- Mateo Reyes      Completada
(22, 6, '2026-05-26', '10:00', '10:30', 1),  -- Valeria Mora     Cancelada
-- Zarama – semana 25 mayo
(23, 7, '2026-05-25', '07:00', '07:45', 4),  -- Tomás León       Completada
(9,  7, '2026-05-25', '07:45', '08:30', 4),  -- Juan Torres      Completada
(10, 7, '2026-05-25', '08:30', '09:15', 5),  -- María Ramírez    No Asistió
-- Armando – semana 26 mayo
(11, 8, '2026-05-26', '15:00', '15:30', 4),  -- Pedro Herrera    Completada
(12, 8, '2026-05-26', '15:30', '16:00', 4),  -- Ana Jiménez      Completada
(13, 8, '2026-05-26', '16:00', '16:30', 1);  -- Luis Mendoza     Cancelada
