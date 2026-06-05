-- ============================================================
-- SCRIPT DB: db_usuarios  –  Datos reales Piedrazul
-- Fecha carga: 2026-06-05
-- ============================================================

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
    id_estado         INT REFERENCES dominio_estado(id_estado),
    activo            BOOLEAN NOT NULL DEFAULT TRUE
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

-- ─────────────────────────────────────────
-- DOMINIOS
-- ─────────────────────────────────────────

INSERT INTO dominio_estado (nombre) VALUES
('Inactivo'),    -- 1
('Activo'),      -- 2
('Suspendido'),  -- 3
('Pendiente'),   -- 4
('Eliminado');   -- 5

INSERT INTO dominio_genero (nombre) VALUES
('Masculino'),        -- 1
('Femenino'),         -- 2
('No Binario'),       -- 3
('Prefiero no decir');-- 4

-- Especialidades reales de Piedrazul
INSERT INTO dominio_especialidad (nombre) VALUES
('Terapia Neural'),  -- 1
('Fisioterapia'),    -- 2
('Quiropraxia'),     -- 3
('Medicina General'),-- 4
('Psicología'),      -- 5
('Nutrición'),       -- 6
('Cardiología'),     -- 7
('Neurología'),      -- 8
('Pediatría'),       -- 9
('Ortopedia');       -- 10

-- ─────────────────────────────────────────
-- USUARIOS
-- Mapa de IDs:
--   1  = admin
--   2  = agendador1
--   3  = agendador2
--   4  = clara.cordoba      (Terapia Neural)
--   5  = jose.garcia        (Terapia Neural)
--   6  = ibis.gonzalez      (Terapia Neural)
--   7  = christian.gonzalez (Terapia Neural)
--   8  = zarama.velasco     (Fisioterapia)
--   9  = armando.pena       (Quiropraxia)
--   10..19 = pacientes 1-10
--   20..24 = pacientes 11-15
-- ─────────────────────────────────────────

INSERT INTO usuario (usuario, contrasena) VALUES
-- Administrador
('admin',               'admin123'),        -- 1

-- Agendadores
('agendador1',          'agenda123'),       -- 2
('agendador2',          'agenda456'),       -- 3

-- Médicos / Terapistas Piedrazul
('clara.cordoba',       'medico123'),       -- 4
('jose.garcia',         'medico456'),       -- 5
('ibis.gonzalez',       'medico789'),       -- 6
('christian.gonzalez',  'medico321'),       -- 7
('zarama.velasco',      'medico654'),       -- 8
('armando.pena',        'medico987'),       -- 9

-- Pacientes
('paciente.torres',     'pac123'),          -- 10
('paciente.ramirez',    'pac456'),          -- 11
('paciente.herrera',    'pac789'),          -- 12
('paciente.jimenez',    'pac321'),          -- 13
('paciente.mendoza',    'pac654'),          -- 14
('paciente.rios',       'pac987'),          -- 15
('paciente.silva',      'pac111'),          -- 16
('paciente.rojas',      'pac222'),          -- 17
('paciente.ortiz',      'pac333'),          -- 18
('paciente.nunez',      'pac444'),          -- 19
('paciente.vega',       'pac555'),          -- 20
('paciente.soto',       'pac666'),          -- 21
('paciente.reyes',      'pac777'),          -- 22
('paciente.mora',       'pac888'),          -- 23
('paciente.leon',       'pac999');          -- 24

-- ─────────────────────────────────────────
-- ROLES
-- ─────────────────────────────────────────

INSERT INTO rol (nombre, id_usuario) VALUES
('Administrador', 1),
('Agendador',     2),
('Agendador',     3),
('Medico',        4),
('Medico',        5),
('Medico',        6),
('Medico',        7),
('Medico',        8),
('Medico',        9),
('Paciente',     10),
('Paciente',     11),
('Paciente',     12),
('Paciente',     13),
('Paciente',     14),
('Paciente',     15),
('Paciente',     16),
('Paciente',     17),
('Paciente',     18),
('Paciente',     19),
('Paciente',     20),
('Paciente',     21),
('Paciente',     22),
('Paciente',     23),
('Paciente',     24);

-- ─────────────────────────────────────────
-- PERSONAS – Agendadores  (id_persona 1-2)
-- ─────────────────────────────────────────

INSERT INTO persona (nombre, cedula_ciudadania, apellido, celular, id_genero, fecha_nacimiento, correo, id_usuario, id_estado, activo) VALUES
('Laura',   '10000000001', 'Mendez', '3001000001', 2, '1990-03-15', 'laura.mendez@piedraazul.com',  2, 2, TRUE),
('Carlos',  '10000000002', 'Pineda', '3001000002', 1, '1988-07-22', 'carlos.pineda@piedraazul.com', 3, 2, TRUE);

INSERT INTO agendador (id_persona) VALUES (1), (2);

-- ─────────────────────────────────────────
-- PERSONAS – Médicos / Terapistas Piedrazul  (id_persona 3-8)
-- ─────────────────────────────────────────

INSERT INTO persona (nombre, cedula_ciudadania, apellido, celular, id_genero, fecha_nacimiento, correo, id_usuario, id_estado, activo) VALUES
('Clara Inés',  '40000000001', 'Córdoba',          '3154000001', 2, '1978-04-12', 'c.cordoba@piedraazul.com',    4, 2, TRUE),   -- id_persona 3
('Jose Ignacio','40000000002', 'Garcia',            '3154000002', 1, '1975-09-20', 'j.garcia@piedraazul.com',     5, 2, TRUE),   -- id_persona 4
('Ibis',        '40000000003', 'Gonzalez',          '3154000003', 2, '1982-02-28', 'i.gonzalez@piedraazul.com',   6, 2, TRUE),   -- id_persona 5
('Christian',   '40000000004', 'Gonzalez',          '3154000004', 1, '1985-11-05', 'ch.gonzalez@piedraazul.com',  7, 2, TRUE),   -- id_persona 6
('Zarama',      '40000000005', 'Velasco',           '3154000005', 2, '1980-06-17', 'z.velasco@piedraazul.com',    8, 2, TRUE),   -- id_persona 7
('Armando',     '40000000006', 'Peña',              '3154000006', 1, '1973-08-30', 'a.pena@piedraazul.com',       9, 2, TRUE);   -- id_persona 8

INSERT INTO medico_terapista (id_persona, id_especialidad) VALUES
(3, 1),   -- Clara Inés Córdoba     → Terapia Neural
(4, 1),   -- Jose Ignacio Garcia    → Terapia Neural
(5, 1),   -- Ibis Gonzalez          → Terapia Neural
(6, 1),   -- Christian Gonzalez     → Terapia Neural
(7, 2),   -- Zarama Velasco         → Fisioterapia
(8, 3);   -- Armando Peña           → Quiropraxia

-- ─────────────────────────────────────────
-- PERSONAS – Pacientes  (id_persona 9-23)
-- ─────────────────────────────────────────

INSERT INTO persona (nombre, cedula_ciudadania, apellido, celular, id_genero, fecha_nacimiento, correo, id_usuario, id_estado, activo) VALUES
('Juan',       '30000000001', 'Torres',    '3203000001', 1, '1990-03-14', 'juan.torres@mail.com',       10, 2, TRUE),  -- 9
('María',      '30000000002', 'Ramírez',   '3203000002', 2, '1985-07-22', 'maria.ramirez@mail.com',     11, 2, TRUE),  -- 10
('Pedro',      '30000000003', 'Herrera',   '3203000003', 1, '1978-11-05', 'pedro.herrera@mail.com',     12, 2, TRUE),  -- 11
('Ana',        '30000000004', 'Jiménez',   '3203000004', 2, '1995-01-20', 'ana.jimenez@mail.com',       13, 2, TRUE),  -- 12
('Luis',       '30000000005', 'Mendoza',   '3203000005', 1, '1992-07-11', 'luis.mendoza@mail.com',      14, 2, TRUE),  -- 13
('Paola',      '30000000006', 'Ríos',      '3203000006', 2, '1988-03-28', 'paola.rios@mail.com',        15, 2, TRUE),  -- 14
('Diego',      '30000000007', 'Silva',     '3203000007', 1, '1982-09-17', 'diego.silva@mail.com',       16, 2, TRUE),  -- 15
('Natalia',    '30000000008', 'Rojas',     '3203000008', 2, '1997-12-03', 'natalia.rojas@mail.com',     17, 2, TRUE),  -- 16
('Sebastián',  '30000000009', 'Ortiz',     '3203000009', 1, '1993-04-22', 'sebastian.ortiz@mail.com',   18, 2, TRUE),  -- 17
('Daniela',    '30000000010', 'Núñez',     '3203000010', 2, '1991-08-09', 'daniela.nunez@mail.com',     19, 2, TRUE),  -- 18
('Felipe',     '30000000011', 'Vega',      '3203000011', 1, '1986-05-16', 'felipe.vega@mail.com',       20, 2, TRUE),  -- 19
('Alejandra',  '30000000012', 'Soto',      '3203000012', 2, '2000-11-27', 'alejandra.soto@mail.com',    21, 2, TRUE),  -- 20
('Mateo',      '30000000013', 'Reyes',     '3203000013', 1, '1994-01-08', 'mateo.reyes@mail.com',       22, 2, TRUE),  -- 21
('Valeria',    '30000000014', 'Mora',      '3203000014', 2, '1989-07-19', 'valeria.mora@mail.com',      23, 2, TRUE),  -- 22
('Tomás',      '30000000015', 'León',      '3203000015', 1, '1996-03-04', 'tomas.leon@mail.com',        24, 2, TRUE);  -- 23

INSERT INTO paciente (id_persona) VALUES
(9),(10),(11),(12),(13),(14),(15),(16),(17),(18),(19),(20),(21),(22),(23);

-- ─────────────────────────────────────────
-- TOKENS DE SESIÓN (inactivos, referencia)
-- ─────────────────────────────────────────

INSERT INTO sesion_token (token_hash, fecha_creacion, fecha_expiracion, id_estado, id_usuario) VALUES
('tok_admin_demo',   '2026-06-05 08:00:00', '2026-06-05 09:40:00', 2, 1),
('tok_agend1_demo',  '2026-06-05 08:05:00', '2026-06-05 09:45:00', 2, 2),
('tok_clara_demo',   '2026-06-05 08:10:00', '2026-06-05 09:50:00', 2, 4),
('tok_jose_demo',    '2026-06-05 08:15:00', '2026-06-05 09:55:00', 2, 5),
('tok_pac1_demo',    '2026-06-05 09:00:00', '2026-06-05 10:40:00', 2, 10),
('tok_pac2_demo',    '2026-06-05 09:05:00', '2026-06-05 10:45:00', 2, 11);
