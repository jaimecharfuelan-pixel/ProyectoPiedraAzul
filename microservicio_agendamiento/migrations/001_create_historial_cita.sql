-- ============================================================
-- MIGRACIÓN: Agregar tabla historial_cita
-- Microservicio: ms-agendamiento (puerto 8082)
-- Propósito: Registro de auditoría de cambios en citas
-- Cambios registrados: Reagendamientos, cancelaciones, cambios de estado
-- ============================================================

CREATE TABLE IF NOT EXISTS historial_cita (
    id_historial SERIAL PRIMARY KEY,
    id_cita INT NOT NULL REFERENCES cita(id) ON DELETE CASCADE,
    tipo_cambio VARCHAR(50) NOT NULL,      -- REAGENDAMIENTO, CANCELACION, CAMBIO_ESTADO, CREACION
    valor_anterior TEXT,                   -- Valor anterior (estado, fecha/hora, etc.)
    valor_nuevo TEXT,                      -- Valor nuevo (estado, fecha/hora, etc.)
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NOT NULL,               -- id_persona del usuario que realizó el cambio
    descripcion TEXT                       -- Descripción adicional del cambio
);

-- Índice para búsquedas rápidas por cita
CREATE INDEX IF NOT EXISTS idx_historial_id_cita ON historial_cita(id_cita);

-- Índice para búsquedas por fecha
CREATE INDEX IF NOT EXISTS idx_historial_fecha_hora ON historial_cita(fecha_hora);

-- Verificar tabla creada
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' AND table_name = 'historial_cita';
