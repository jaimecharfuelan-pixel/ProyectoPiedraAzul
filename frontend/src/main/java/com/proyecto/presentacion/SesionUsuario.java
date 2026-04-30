package com.proyecto.presentacion;

/**
 * Singleton que guarda los datos del usuario logueado en memoria.
 * En microservicios ya no guarda un objeto Persona completo —
 * solo el token, idUsuario, rol e idPaciente que vienen del login via HTTP.
 */
public class SesionUsuario {

    private static SesionUsuario instancia;

    private String token;
    private int    idUsuario;
    private String rol;
    private int    idPacienteActual;

    private SesionUsuario() {}

    public static SesionUsuario getInstancia() {
        if (instancia == null) instancia = new SesionUsuario();
        return instancia;
    }

    public void limpiarSesion() {
        token            = null;
        idUsuario        = 0;
        rol              = null;
        idPacienteActual = 0;
    }

    public String getToken()                        { return token; }
    public void   setToken(String token)            { this.token = token; }

    public int    getIdUsuario()                    { return idUsuario; }
    public void   setIdUsuario(int idUsuario)       { this.idUsuario = idUsuario; }

    public String getRol()                          { return rol; }
    public void   setRol(String rol)                { this.rol = rol; }

    public int    getIdPacienteActual()             { return idPacienteActual; }
    public void   setIdPacienteActual(int id)       { this.idPacienteActual = id; }
}
