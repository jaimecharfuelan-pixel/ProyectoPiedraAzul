package com.proyecto.presentacion.dto;

import java.util.List;

/**
 * DTO para transmitir errores de validación de agendamiento al frontend.
 */
public class ErrorValidacionDTO {
    private boolean exitosa;
    private String mensajePrincipal;
    private List<String> errores;
    private int codigoError; // 0: éxito, 1: solapamiento, 2: paciente con cita pendiente, 3: otro

    public ErrorValidacionDTO() {}

    public ErrorValidacionDTO(boolean exitosa, String mensajePrincipal, List<String> errores, int codigoError) {
        this.exitosa = exitosa;
        this.mensajePrincipal = mensajePrincipal;
        this.errores = errores;
        this.codigoError = codigoError;
    }

    public boolean isExitosa() { return exitosa; }
    public void setExitosa(boolean exitosa) { this.exitosa = exitosa; }

    public String getMensajePrincipal() { return mensajePrincipal; }
    public void setMensajePrincipal(String mensajePrincipal) { this.mensajePrincipal = mensajePrincipal; }

    public List<String> getErrores() { return errores; }
    public void setErrores(List<String> errores) { this.errores = errores; }

    public int getCodigoError() { return codigoError; }
    public void setCodigoError(int codigoError) { this.codigoError = codigoError; }

    @Override
    public String toString() {
        return String.join("\n", errores);
    }
}
