-- Migración: activar todas las personas existentes que no tengan el campo activo seteado
-- Se ejecuta en cada arranque pero solo afecta filas donde activo IS NULL
UPDATE persona SET activo = true WHERE activo IS NULL;
