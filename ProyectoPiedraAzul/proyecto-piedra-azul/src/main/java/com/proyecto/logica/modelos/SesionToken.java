package com.proyecto.logica.modelos;

import java.time.LocalDateTime;

public class SesionToken {
    private int attIdToken;
    private String attTokenHash;
    private LocalDateTime attFechaCreacion;
    private LocalDateTime attFechaExpiracion;
    private int attIdEstado;
    private int attIdUsuario;

    public SesionToken() {}

    public SesionToken(int prmIdToken, String prmTokenHash, LocalDateTime prmFechaCreacion, 
                       LocalDateTime prmFechaExpiracion, int prmIdEstado, int prmIdUsuario) {
        this.attIdToken = prmIdToken;
        this.attTokenHash = prmTokenHash;
        this.attFechaCreacion = prmFechaCreacion;
        this.attFechaExpiracion = prmFechaExpiracion;
        this.attIdEstado = prmIdEstado;
        this.attIdUsuario = prmIdUsuario;
    }

    public int getIdToken() { return attIdToken; }
    public void setIdToken(int prmIdToken) { this.attIdToken = prmIdToken; }

    public String getTokenHash() { return attTokenHash; }
    public void setTokenHash(String prmTokenHash) { this.attTokenHash = prmTokenHash; }

    public LocalDateTime getFechaCreacion() { return attFechaCreacion; }
    public void setFechaCreacion(LocalDateTime prmFechaCreacion) { this.attFechaCreacion = prmFechaCreacion; }

    public LocalDateTime getFechaExpiracion() { return attFechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime prmFechaExpiracion) { this.attFechaExpiracion = prmFechaExpiracion; }

    public int getIdEstado() { return attIdEstado; }
    public void setIdEstado(int prmIdEstado) { this.attIdEstado = prmIdEstado; }

    public int getIdUsuario() { return attIdUsuario; }
    public void setIdUsuario(int prmIdUsuario) { this.attIdUsuario = prmIdUsuario; }
}