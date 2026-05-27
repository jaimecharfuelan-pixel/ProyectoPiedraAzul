package com.proyecto.presentacion.dto;

/**
 * DTO para representar un rol asignado a un usuario.
 * Los roles están asociados a la entidad Usuario, no a Persona.
 */
public class RolDTO {
    private int    idRol;
    private int    idUsuario;
    private String nombre;

    // ── Constructores ─────────────────────────────────────────

    public RolDTO() {}

    public RolDTO(int idRol, int idUsuario, String nombre) {
        this.idRol = idRol;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
    }

    // ── Getters y Setters ─────────────────────────────────────

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // ── Otros ─────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Rol{" +
                "idRol=" + idRol +
                ", idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolDTO rolDTO)) return false;
        return idRol == rolDTO.idRol && nombre.equals(rolDTO.nombre);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(idRol, nombre);
    }
}
