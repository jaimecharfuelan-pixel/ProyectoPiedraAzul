package com.proyecto.microservicio_agendamiento.infrastructure.adapters.in.web.dto;

import java.util.List;

public class ErrorValidacionDTO {
    private boolean exitosa;
    private String mensajePrincipal;
    private List<String> errores;
    private int codigoError;

    public ErrorValidacionDTO() {}

    public ErrorValidacionDTO(boolean exitosa, String mensajePrincipal, List<String> errores, int codigoError) {
        this.exitosa = exitosa;
        this.mensajePrincipal = mensajePrincipal;
        this.errores = errores;
        this.codigoError = codigoError;
    }

    public boolean isExitosa() {
        return exitosa;
    }

    public void setExitosa(boolean exitosa) {
        this.exitosa = exitosa;
    }

    public String getMensajePrincipal() {
        return mensajePrincipal;
    }

    public void setMensajePrincipal(String mensajePrincipal) {
        this.mensajePrincipal = mensajePrincipal;
    }

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores;
    }

    public int getCodigoError() {
        return codigoError;
    }

    public void setCodigoError(int codigoError) {
        this.codigoError = codigoError;
    }
}
