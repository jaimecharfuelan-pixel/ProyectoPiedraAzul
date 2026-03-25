-- ============================================================
-- SCRIPT DE DATOS DE PRUEBA - PROYECTO PIEDRA AZUL
-- Esquema: esquemaprueba
-- Base de datos: BaseDeDatosPiedraAzul
-- ============================================================

SET search_path TO esquemaprueba, public;

-- Ejecutar antes para evitar problemas:
TRUNCATE TABLE Cita, Jornada_Laboral, Paciente, Agendador, Medico_Terapista, Persona, Rol, Usuario, Dominio_Estado, Dominio_Genero, Dominio_Especialidad, Dominio_EstadoCita RESTART IDENTITY CASCADE;

-- ============================================================
-- 1. TABLAS DE DOMINIO
-- ============================================================

INSERT INTO Dominio_Estado (nombre) VALUES
('Inactivo'),
('Activo'),
('Suspendido'),
('Pendiente'),
('Eliminado');

INSERT INTO Dominio_Genero (nombre) VALUES
('Masculino'),
('Femenino'),
('No Binario'),
('Prefiero no decir');

INSERT INTO Dominio_Especialidad (nombre) VALUES
('Medicina General'),
('Fisioterapia'),
('Psicología'),
('Nutrición'),
('Cardiología'),
('Neurología'),
('Pediatría'),
('Dermatología'),
('Ortopedia'),
('Terapia Ocupacional');

INSERT INTO Dominio_EstadoCita (nombre) VALUES
('Cancelada'),
('Pendiente'),
('Confirmada'),
('Completada'),
('No Asistió');


-- ============================================================
-- 2. USUARIOS (30 registros)
-- ============================================================

INSERT INTO Usuario (usuario, contrasena) VALUES
('admin',          'admin123'),
('agendador1',     'agenda123'),
('agendador2',     'agenda456'),
('medico.garcia',  'medico123'),
('medico.lopez',   'medico456'),
('medico.torres',  'medico789'),
('medico.ramirez', 'medico321'),
('medico.vargas',  'medico654'),
('medico.moreno',  'medico987'),
('medico.castro',  'medico111'),
('paciente.perez',    'pac123'),
('paciente.gomez',    'pac456'),
('paciente.herrera',  'pac789'),
('paciente.jimenez',  'pac321'),
('paciente.mendoza',  'pac654'),
('paciente.rios',     'pac987'),
('paciente.silva',    'pac111'),
('paciente.rojas',    'pac222'),
('paciente.ortiz',    'pac333'),
('paciente.nunez',    'pac444'),
('paciente.vega',     'pac555'),
('paciente.soto',     'pac666'),
('paciente.reyes',    'pac777'),
('paciente.mora',     'pac888'),
('paciente.leon',     'pac999'),
('paciente.ruiz',     'pac000'),
('paciente.diaz',     'pac101'),
('paciente.fuentes',  'pac202'),
('paciente.pinto',    'pac303'),
('paciente.salazar',  'pac404');


-- ============================================================
-- 3. ROLES (uno por usuario)
-- ============================================================

INSERT INTO Rol (nombre, id_usuario) VALUES
('Administrador', 1),
('Agendador',     2),
('Agendador',     3),
('Medico',        4),
('Medico',        5),
('Medico',        6),
('Medico',        7),
('Medico',        8),
('Medico',        9),
('Medico',        10),
('Paciente',      11),
('Paciente',      12),
('Paciente',      13),
('Paciente',      14),
('Paciente',      15),
('Paciente',      16),
('Paciente',      17),
('Paciente',      18),
('Paciente',      19),
('Paciente',      20),
('Paciente',      21),
('Paciente',      22),
('Paciente',      23),
('Paciente',      24),
('Paciente',      25),
('Paciente',      26),
('Paciente',      27),
('Paciente',      28),
('Paciente',      29),
('Paciente',      30);


-- ============================================================
-- 4. PERSONAS - AGENDADORES (id_usuario 2 y 3)
-- ============================================================

INSERT INTO Persona (nombre, cedula_ciudadania, apellido, celular, id_genero, fecha_nacimiento, correo, id_usuario, id_estado) VALUES
('Laura',   '10000000001', 'Mendez',   '3001000001', 2, '1990-03-15', 'laura.mendez@piedraazul.com',   2, 2),
('Carlos',  '10000000002', 'Pineda',   '3001000002', 1, '1988-07-22', 'carlos.pineda@piedraazul.com',  3, 2);

-- Registrar en tabla Agendador
INSERT INTO Agendador (id_persona) VALUES (1), (2);

-- ============================================================
-- 5. PERSONAS - MÉDICOS/TERAPISTAS (id_usuario 4 al 10)
-- ============================================================

INSERT INTO Persona (nombre, cedula_ciudadania, apellido, celular, id_genero, fecha_nacimiento, correo, id_usuario, id_estado) VALUES
('Andrés',    '20000000001', 'García',   '3102000001', 1, '1975-01-10', 'andres.garcia@piedraazul.com',   4, 2),
('Sofía',     '20000000002', 'López',    '3102000002', 2, '1980-05-20', 'sofia.lopez@piedraazul.com',     5, 2),
('Miguel',    '20000000003', 'Torres',   '3102000003', 1, '1978-09-14', 'miguel.torres@piedraazul.com',   6, 2),
('Valentina', '20000000004', 'Ramírez',  '3102000004', 2, '1983-11-30', 'valentina.ramirez@piedraazul.com', 7, 2),
('Julián',    '20000000005', 'Vargas',   '3102000005', 1, '1970-04-05', 'julian.vargas@piedraazul.com',   8, 2),
('Camila',    '20000000006', 'Moreno',   '3102000006', 2, '1985-08-18', 'camila.moreno@piedraazul.com',   9, 2),
('Ricardo',   '20000000007', 'Castro',   '3102000007', 1, '1972-12-25', 'ricardo.castro@piedraazul.com',  10, 2);

-- Registrar en tabla Medico_Terapista (id_persona 3 al 9, especialidades 1 al 7)
INSERT INTO Medico_Terapista (id_persona, id_especialidad) VALUES
(3, 1),
(4, 2),
(5, 3),
(6, 4),
(7, 5),
(8, 6),
(9, 7);


-- ============================================================
-- 6. PERSONAS - PACIENTES (id_usuario 11 al 30)
-- ============================================================

INSERT INTO Persona (nombre, cedula_ciudadania, apellido, celular, id_genero, fecha_nacimiento, correo, id_usuario, id_estado) VALUES
('Juan',       '30000000001', 'Pérez',    '3203000001', 1, '1995-02-14', 'juan.perez@mail.com',      11, 2),
('María',      '30000000002', 'Gómez',    '3203000002', 2, '1992-06-30', 'maria.gomez@mail.com',     12, 2),
('Pedro',      '30000000003', 'Herrera',  '3203000003', 1, '1988-10-05', 'pedro.herrera@mail.com',   13, 2),
('Ana',        '30000000004', 'Jiménez',  '3203000004', 2, '2000-01-20', 'ana.jimenez@mail.com',     14, 2),
('Luis',       '30000000005', 'Mendoza',  '3203000005', 1, '1997-07-11', 'luis.mendoza@mail.com',    15, 2),
('Paola',      '30000000006', 'Ríos',     '3203000006', 2, '1990-03-28', 'paola.rios@mail.com',      16, 2),
('Diego',      '30000000007', 'Silva',    '3203000007', 1, '1985-09-17', 'diego.silva@mail.com',     17, 2),
('Natalia',    '30000000008', 'Rojas',    '3203000008', 2, '1993-12-03', 'natalia.rojas@mail.com',   18, 2),
('Sebastián',  '30000000009', 'Ortiz',    '3203000009', 1, '1999-04-22', 'sebastian.ortiz@mail.com', 19, 2),
('Daniela',    '30000000010', 'Núñez',    '3203000010', 2, '1996-08-09', 'daniela.nunez@mail.com',   20, 2),
('Felipe',     '30000000011', 'Vega',     '3203000011', 1, '1987-05-16', 'felipe.vega@mail.com',     21, 2),
('Alejandra',  '30000000012', 'Soto',     '3203000012', 2, '2001-11-27', 'alejandra.soto@mail.com',  22, 2),
('Mateo',      '30000000013', 'Reyes',    '3203000013', 1, '1994-01-08', 'mateo.reyes@mail.com',     23, 2),
('Valeria',    '30000000014', 'Mora',     '3203000014', 2, '1991-07-19', 'valeria.mora@mail.com',    24, 2),
('Tomás',      '30000000015', 'León',     '3203000015', 1, '1998-03-04', 'tomas.leon@mail.com',      25, 2),
('Isabella',   '30000000016', 'Ruiz',     '3203000016', 2, '2002-09-13', 'isabella.ruiz@mail.com',   26, 2),
('Nicolás',    '30000000017', 'Díaz',     '3203000017', 1, '1986-06-25', 'nicolas.diaz@mail.com',    27, 2),
('Gabriela',   '30000000018', 'Fuentes',  '3203000018', 2, '1989-02-07', 'gabriela.fuentes@mail.com',28, 2),
('Esteban',    '30000000019', 'Pinto',    '3203000019', 1, '2003-10-31', 'esteban.pinto@mail.com',   29, 2),
('Mariana',    '30000000020', 'Salazar',  '3203000020', 2, '1984-04-15', 'mariana.salazar@mail.com', 30, 2);

-- Registrar en tabla Paciente (id_persona 10 al 29)
INSERT INTO Paciente (id_persona) VALUES
(10),(11),(12),(13),(14),(15),(16),(17),(18),(19),
(20),(21),(22),(23),(24),(25),(26),(27),(28),(29);


-- ============================================================
-- 7. JORNADAS LABORALES (30 registros, médicos id_persona 3-9)
-- ============================================================

INSERT INTO Jornada_Laboral (id_medico, hora_inicio, hora_fin, dias_semana, duracion_estimada_atencion) VALUES
-- Médico 3 (García - Medicina General)
(3, '07:00', '12:00', 'Lunes',     30),
(3, '07:00', '12:00', 'Miércoles', 30),
(3, '07:00', '12:00', 'Viernes',   30),
(3, '14:00', '18:00', 'Martes',    30),
-- Médico 4 (López - Fisioterapia)
(4, '08:00', '13:00', 'Lunes',     45),
(4, '08:00', '13:00', 'Martes',    45),
(4, '08:00', '13:00', 'Jueves',    45),
(4, '14:00', '17:00', 'Viernes',   45),
-- Médico 5 (Torres - Psicología)
(5, '09:00', '14:00', 'Lunes',     60),
(5, '09:00', '14:00', 'Miércoles', 60),
(5, '15:00', '19:00', 'Jueves',    60),
-- Médico 6 (Ramírez - Nutrición)
(6, '07:30', '12:30', 'Martes',    30),
(6, '07:30', '12:30', 'Jueves',    30),
(6, '14:00', '18:00', 'Sábado',    30),
-- Médico 7 (Vargas - Cardiología)
(7, '06:00', '11:00', 'Lunes',     30),
(7, '06:00', '11:00', 'Miércoles', 30),
(7, '06:00', '11:00', 'Viernes',   30),
-- Médico 8 (Moreno - Neurología)
(8, '10:00', '15:00', 'Martes',    45),
(8, '10:00', '15:00', 'Jueves',    45),
(8, '10:00', '14:00', 'Sábado',    45),
-- Médico 9 (Castro - Pediatría)
(9, '07:00', '12:00', 'Lunes',     30),
(9, '07:00', '12:00', 'Martes',    30),
(9, '07:00', '12:00', 'Miércoles', 30),
(9, '07:00', '12:00', 'Jueves',    30),
(9, '07:00', '12:00', 'Viernes',   30),
-- Jornadas adicionales para completar 30
(3, '14:00', '18:00', 'Jueves',    30),
(4, '14:00', '17:00', 'Miércoles', 45),
(5, '09:00', '14:00', 'Viernes',   60),
(6, '07:30', '12:30', 'Lunes',     30),
(7, '14:00', '18:00', 'Martes',    30);


-- ============================================================
-- 8. CITAS (30 registros)
-- Pacientes: id_persona 10-29 | Médicos: id_persona 3-9
-- Estados: 1=Cancelada, 2=Pendiente, 3=Confirmada, 4=Completada, 5=No Asistió
-- ============================================================

INSERT INTO Cita (id_paciente, id_medico, fecha, hora_inicio, hora_fin, id_estado_cita) VALUES
(10, 3, '2026-04-07', '07:00', '07:30', 3),
(11, 4, '2026-04-07', '08:00', '08:45', 2),
(12, 5, '2026-04-08', '09:00', '10:00', 3),
(13, 6, '2026-04-08', '07:30', '08:00', 2),
(14, 7, '2026-04-06', '06:00', '06:30', 4),
(15, 8, '2026-04-08', '10:00', '10:45', 3),
(16, 9, '2026-04-07', '07:00', '07:30', 2),
(17, 3, '2026-04-09', '07:00', '07:30', 2),
(18, 4, '2026-04-09', '08:00', '08:45', 3),
(19, 5, '2026-04-09', '09:00', '10:00', 2),
(20, 6, '2026-04-10', '07:30', '08:00', 3),
(21, 7, '2026-04-06', '06:30', '07:00', 4),
(22, 8, '2026-04-10', '10:00', '10:45', 2),
(23, 9, '2026-04-07', '07:30', '08:00', 3),
(24, 3, '2026-04-11', '07:00', '07:30', 2),
(25, 4, '2026-04-07', '08:45', '09:30', 4),
(26, 5, '2026-04-08', '10:00', '11:00', 1),
(27, 6, '2026-04-08', '08:00', '08:30', 3),
(28, 7, '2026-04-09', '06:00', '06:30', 2),
(29, 8, '2026-04-10', '10:45', '11:30', 3),
(10, 9, '2026-04-09', '08:00', '08:30', 4),
(11, 3, '2026-04-11', '07:30', '08:00', 2),
(12, 4, '2026-04-11', '09:30', '10:15', 3),
(13, 5, '2026-04-10', '09:00', '10:00', 5),
(14, 6, '2026-04-11', '07:30', '08:00', 2),
(15, 7, '2026-04-09', '06:30', '07:00', 3),
(16, 8, '2026-04-11', '11:30', '12:15', 2),
(17, 9, '2026-04-10', '07:00', '07:30', 4),
(18, 3, '2026-04-14', '14:00', '14:30', 2),
(19, 4, '2026-04-14', '14:00', '14:45', 3);


-- ============================================================
-- 9. SESIONES TOKEN (30 registros de ejemplo)
-- Estados: 1=Inactivo, 2=Activo
-- ============================================================

INSERT INTO Sesion_Token (token_hash, fecha_creacion, fecha_expiracion, id_estado, id_usuario) VALUES
('tok_admin_001',      '2026-03-24 08:00:00', '2026-03-24 09:00:00', 1, 1),
('tok_agend_001',      '2026-03-24 08:05:00', '2026-03-24 09:05:00', 1, 2),
('tok_agend_002',      '2026-03-24 08:10:00', '2026-03-24 09:10:00', 1, 3),
('tok_med_garcia',     '2026-03-24 08:15:00', '2026-03-24 09:15:00', 1, 4),
('tok_med_lopez',      '2026-03-24 08:20:00', '2026-03-24 09:20:00', 1, 5),
('tok_med_torres',     '2026-03-24 08:25:00', '2026-03-24 09:25:00', 1, 6),
('tok_med_ramirez',    '2026-03-24 08:30:00', '2026-03-24 09:30:00', 1, 7),
('tok_med_vargas',     '2026-03-24 08:35:00', '2026-03-24 09:35:00', 1, 8),
('tok_med_moreno',     '2026-03-24 08:40:00', '2026-03-24 09:40:00', 1, 9),
('tok_med_castro',     '2026-03-24 08:45:00', '2026-03-24 09:45:00', 1, 10),
('tok_pac_perez',      '2026-03-24 09:00:00', '2026-03-24 10:00:00', 1, 11),
('tok_pac_gomez',      '2026-03-24 09:05:00', '2026-03-24 10:05:00', 1, 12),
('tok_pac_herrera',    '2026-03-24 09:10:00', '2026-03-24 10:10:00', 1, 13),
('tok_pac_jimenez',    '2026-03-24 09:15:00', '2026-03-24 10:15:00', 1, 14),
('tok_pac_mendoza',    '2026-03-24 09:20:00', '2026-03-24 10:20:00', 1, 15),
('tok_pac_rios',       '2026-03-24 09:25:00', '2026-03-24 10:25:00', 1, 16),
('tok_pac_silva',      '2026-03-24 09:30:00', '2026-03-24 10:30:00', 1, 17),
('tok_pac_rojas',      '2026-03-24 09:35:00', '2026-03-24 10:35:00', 1, 18),
('tok_pac_ortiz',      '2026-03-24 09:40:00', '2026-03-24 10:40:00', 1, 19),
('tok_pac_nunez',      '2026-03-24 09:45:00', '2026-03-24 10:45:00', 1, 20),
-- Tokens activos (sesiones vigentes)
('tok_active_admin',   '2026-03-24 10:00:00', '2026-03-25 10:00:00', 2, 1),
('tok_active_agend1',  '2026-03-24 10:05:00', '2026-03-25 10:05:00', 2, 2),
('tok_active_med1',    '2026-03-24 10:10:00', '2026-03-25 10:10:00', 2, 4),
('tok_active_med2',    '2026-03-24 10:15:00', '2026-03-25 10:15:00', 2, 5),
('tok_active_med3',    '2026-03-24 10:20:00', '2026-03-25 10:20:00', 2, 6),
('tok_active_pac1',    '2026-03-24 10:25:00', '2026-03-25 10:25:00', 2, 11),
('tok_active_pac2',    '2026-03-24 10:30:00', '2026-03-25 10:30:00', 2, 12),
('tok_active_pac3',    '2026-03-24 10:35:00', '2026-03-25 10:35:00', 2, 13),
('tok_active_pac4',    '2026-03-24 10:40:00', '2026-03-25 10:40:00', 2, 14),
('tok_active_pac5',    '2026-03-24 10:45:00', '2026-03-25 10:45:00', 2, 15);

-- ============================================================
-- FIN DEL SCRIPT DE DATOS DE PRUEBA
-- ============================================================
