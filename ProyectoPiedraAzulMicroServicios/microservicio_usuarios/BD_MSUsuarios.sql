-- ============================================================
-- SCRIPT DB: db_usuarios
-- Microservicio: ms-usuarios (puerto 8081)
-- Contiene: usuarios, roles, personas, médicos, pacientes,
--           agendadores, tokens de sesión y dominios de apoyo
-- ============================================================

-- ─────────────────────────────────────────
-- TABLAS DE DOMINIO (solo las que usa este MS)
-- ─────────────────────────────────────────

CREATE TABLE IF NOT EXISTS dominio_estado (
    id_estado SERIAL PRIMARY KEY,
    nombre    VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS dominio_genero (
    id_genero SERIAL PRIMARY KEY,
    nombre    VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS dominio_especialidad (
    id_especialidad SERIAL PRIMARY KEY,
    nombre          VARCHAR(50) NOT NULL
);

-- ─────────────────────────────────────────
-- SEGURIDAD
-- ─────────────────────────────────────────

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario SERIAL PRIMARY KEY,
    usuario    VARCHAR(50) UNIQUE NOT NULL,
    contrasena TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS rol (
    id_rol     SERIAL PRIMARY KEY,
    nombre     VARCHAR(30) NOT NULL,
    id_usuario INT NOT NULL,
    CONSTRAINT fk_rol_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sesion_token (
    id_token         SERIAL PRIMARY KEY,
    token_hash       TEXT NOT NULL,
    fecha_creacion   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP NOT NULL,
    id_estado        INT REFERENCES dominio_estado(id_estado),
    id_usuario       INT NOT NULL,
    CONSTRAINT fk_token_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- ─────────────────────────────────────────
-- PERSONAS Y SUBTIPOS
-- ─────────────────────────────────────────

CREATE TABLE IF NOT EXISTS persona (
    id_persona        SERIAL PRIMARY KEY,
    nombre            VARCHAR(100) NOT NULL,
    cedula_ciudadania VARCHAR(20)  UNIQUE NOT NULL,
    apellido          VARCHAR(100) NOT NULL,
    celular           VARCHAR(15),
    id_genero         INT REFERENCES dominio_genero(id_genero),
    fecha_nacimiento  DATE,
    correo            VARCHAR(100),
    id_usuario        INT UNIQUE REFERENCES usuario(id_usuario),
    id_estado         INT REFERENCES dominio_estado(id_estado)
);

CREATE TABLE IF NOT EXISTS medico_terapista (
    id_persona      INT PRIMARY KEY REFERENCES persona(id_persona) ON DELETE CASCADE,
    id_especialidad INT REFERENCES dominio_especialidad(id_especialidad)
);

CREATE TABLE IF NOT EXISTS agendador (
    id_persona INT PRIMARY KEY REFERENCES persona(id_persona) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS paciente (
    id_persona INT PRIMARY KEY REFERENCES persona(id_persona) ON DELETE CASCADE
);

-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

-- Dominios
INSERT INTO dominio_estado (nombre) VALUES
('Inactivo'), ('Activo'), ('Suspendido'), ('Pendiente'), ('Eliminado');

INSERT INTO dominio_genero (nombre) VALUES
('Masculino'), ('Femenino'), ('No Binario'), ('Prefiero no decir');

INSERT INTO dominio_especialidad (nombre) VALUES
('Medicina General'), ('Fisioterapia'), ('Psicología'), ('Nutrición'),
('Cardiología'), ('Neurología'), ('Pediatría'), ('Dermatología'),
('Ortopedia'), ('Terapia Ocupacional');

-- Usuarios (30)
INSERT INTO usuario (usuario, contrasena) VALUES
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

-- Roles
INSERT INTO rol (nombre, id_usuario) VALUES
('Administrador', 1), ('Agendador', 2), ('Agendador', 3),
('Medico', 4), ('Medico', 5), ('Medico', 6), ('Medico', 7),
('Medico', 8), ('Medico', 9), ('Medico', 10),
('Paciente', 11), ('Paciente', 12), ('Paciente', 13), ('Paciente', 14),
('Paciente', 15), ('Paciente', 16), ('Paciente', 17), ('Paciente', 18),
('Paciente', 19), ('Paciente', 20), ('Paciente', 21), ('Paciente', 22),
('Paciente', 23), ('Paciente', 24), ('Paciente', 25), ('Paciente', 26),
('Paciente', 27), ('Paciente', 28), ('Paciente', 29), ('Paciente', 30);

-- Personas - Agendadores (id_usuario 2 y 3)
INSERT INTO persona (nombre, cedula_ciudadania, apellido, celular, id_genero, fecha_nacimiento, correo, id_usuario, id_estado) VALUES
('Laura',  '10000000001', 'Mendez', '3001000001', 2, '1990-03-15', 'laura.mendez@piedraazul.com',  2, 2),
('Carlos', '10000000002', 'Pineda', '3001000002', 1, '1988-07-22', 'carlos.pineda@piedraazul.com', 3, 2);

INSERT INTO agendador (id_persona) VALUES (1), (2);

-- Personas - Médicos (id_usuario 4 al 10)
INSERT INTO persona (nombre, cedula_ciudadania, apellido, celular, id_genero, fecha_nacimiento, correo, id_usuario, id_estado) VALUES
('Andrés',    '20000000001', 'García',   '3102000001', 1, '1975-01-10', 'andres.garcia@piedraazul.com',      4, 2),
('Sofía',     '20000000002', 'López',    '3102000002', 2, '1980-05-20', 'sofia.lopez@piedraazul.com',        5, 2),
('Miguel',    '20000000003', 'Torres',   '3102000003', 1, '1978-09-14', 'miguel.torres@piedraazul.com',      6, 2),
('Valentina', '20000000004', 'Ramírez',  '3102000004', 2, '1983-11-30', 'valentina.ramirez@piedraazul.com',  7, 2),
('Julián',    '20000000005', 'Vargas',   '3102000005', 1, '1970-04-05', 'julian.vargas@piedraazul.com',      8, 2),
('Camila',    '20000000006', 'Moreno',   '3102000006', 2, '1985-08-18', 'camila.moreno@piedraazul.com',      9, 2),
('Ricardo',   '20000000007', 'Castro',   '3102000007', 1, '1972-12-25', 'ricardo.castro@piedraazul.com',    10, 2);

-- id_persona de médicos: 3 al 9
INSERT INTO medico_terapista (id_persona, id_especialidad) VALUES
(3, 1), (4, 2), (5, 3), (6, 4), (7, 5), (8, 6), (9, 7);

-- Personas - Pacientes (id_usuario 11 al 30)
INSERT INTO persona (nombre, cedula_ciudadania, apellido, celular, id_genero, fecha_nacimiento, correo, id_usuario, id_estado) VALUES
('Juan',      '30000000001', 'Pérez',    '3203000001', 1, '1995-02-14', 'juan.perez@mail.com',       11, 2),
('María',     '30000000002', 'Gómez',    '3203000002', 2, '1992-06-30', 'maria.gomez@mail.com',      12, 2),
('Pedro',     '30000000003', 'Herrera',  '3203000003', 1, '1988-10-05', 'pedro.herrera@mail.com',    13, 2),
('Ana',       '30000000004', 'Jiménez',  '3203000004', 2, '2000-01-20', 'ana.jimenez@mail.com',      14, 2),
('Luis',      '30000000005', 'Mendoza',  '3203000005', 1, '1997-07-11', 'luis.mendoza@mail.com',     15, 2),
('Paola',     '30000000006', 'Ríos',     '3203000006', 2, '1990-03-28', 'paola.rios@mail.com',       16, 2),
('Diego',     '30000000007', 'Silva',    '3203000007', 1, '1985-09-17', 'diego.silva@mail.com',      17, 2),
('Natalia',   '30000000008', 'Rojas',    '3203000008', 2, '1993-12-03', 'natalia.rojas@mail.com',    18, 2),
('Sebastián', '30000000009', 'Ortiz',    '3203000009', 1, '1999-04-22', 'sebastian.ortiz@mail.com',  19, 2),
('Daniela',   '30000000010', 'Núñez',    '3203000010', 2, '1996-08-09', 'daniela.nunez@mail.com',    20, 2),
('Felipe',    '30000000011', 'Vega',     '3203000011', 1, '1987-05-16', 'felipe.vega@mail.com',      21, 2),
('Alejandra', '30000000012', 'Soto',     '3203000012', 2, '2001-11-27', 'alejandra.soto@mail.com',   22, 2),
('Mateo',     '30000000013', 'Reyes',    '3203000013', 1, '1994-01-08', 'mateo.reyes@mail.com',      23, 2),
('Valeria',   '30000000014', 'Mora',     '3203000014', 2, '1991-07-19', 'valeria.mora@mail.com',     24, 2),
('Tomás',     '30000000015', 'León',     '3203000015', 1, '1998-03-04', 'tomas.leon@mail.com',       25, 2),
('Isabella',  '30000000016', 'Ruiz',     '3203000016', 2, '2002-09-13', 'isabella.ruiz@mail.com',    26, 2),
('Nicolás',   '30000000017', 'Díaz',     '3203000017', 1, '1986-06-25', 'nicolas.diaz@mail.com',     27, 2),
('Gabriela',  '30000000018', 'Fuentes',  '3203000018', 2, '1989-02-07', 'gabriela.fuentes@mail.com', 28, 2),
('Esteban',   '30000000019', 'Pinto',    '3203000019', 1, '2003-10-31', 'esteban.pinto@mail.com',    29, 2),
('Mariana',   '30000000020', 'Salazar',  '3203000020', 2, '1984-04-15', 'mariana.salazar@mail.com',  30, 2);

-- id_persona de pacientes: 10 al 29
INSERT INTO paciente (id_persona) VALUES
(10),(11),(12),(13),(14),(15),(16),(17),(18),(19),
(20),(21),(22),(23),(24),(25),(26),(27),(28),(29);

-- Tokens de sesión
INSERT INTO sesion_token (token_hash, fecha_creacion, fecha_expiracion, id_estado, id_usuario) VALUES
('tok_admin_001',     '2026-03-24 08:00:00', '2026-03-24 09:00:00', 1, 1),
('tok_agend_001',     '2026-03-24 08:05:00', '2026-03-24 09:05:00', 1, 2),
('tok_med_garcia',    '2026-03-24 08:15:00', '2026-03-24 09:15:00', 1, 4),
('tok_med_lopez',     '2026-03-24 08:20:00', '2026-03-24 09:20:00', 1, 5),
('tok_pac_perez',     '2026-03-24 09:00:00', '2026-03-24 10:00:00', 1, 11),
('tok_active_admin',  '2026-03-24 10:00:00', '2026-03-25 10:00:00', 2, 1),
('tok_active_agend1', '2026-03-24 10:05:00', '2026-03-25 10:05:00', 2, 2),
('tok_active_med1',   '2026-03-24 10:10:00', '2026-03-25 10:10:00', 2, 4),
('tok_active_pac1',   '2026-03-24 10:25:00', '2026-03-25 10:25:00', 2, 11),
('tok_active_pac2',   '2026-03-24 10:30:00', '2026-03-25 10:30:00', 2, 12);
