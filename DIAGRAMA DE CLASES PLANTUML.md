@startuml ProyectoPiedraAzul

skinparam classAttributeIconSize 0
skinparam classFontSize 10
skinparam packageFontSize 11
hide empty members
left to right direction

' ══════════════════════════════════════════════
' MODELOS
' ══════════════════════════════════════════════
package "logica.modelos" {

    abstract class Persona {
        - attIdPersona : int
        - attNombre : String
        - attCedulaCiudadania : String
        - attApellido : String
        - attCelular : String
        - attIdGenero : Integer
        - attFechaNacimiento : LocalDate
        - attCorreo : String
        - attIdUsuario : Integer
        - attIdEstado : Integer
    }

    class Paciente {
    }

    class MedicoTerapista {
        - attIdEspecialidad : int
    }

    class Agendador {
    }

    class Usuario {
        - attIdUsuario : int
        - attUsuario : String
        - attContrasena : String
    }

    class Cita {
        - attIdCita : int
        - attIdPaciente : int
        - attIdMedico : int
        - attFecha : LocalDate
        - attHoraInicio : LocalTime
        - attHoraFin : LocalTime
        - attIdEstadoCita : Integer
    }

    class JornadaLaboral {
        - attIdJornada : int
        - attDiaSemana : String
        - attHoraInicio : LocalTime
        - attHoraFin : LocalTime
        - attIdEstado : int
        - attIdUsuario : int
    }

    class DominioEspecialidad {
        - attIdEspecialidad : int
        - attNombre : String
    }

    class Rol {
        - attIdRol : int
        - attNombre : String
        - attIdUsuario : int
    }

    class SesionToken {
        - attIdToken : int
        - attTokenHash : String
        - attFechaCreacion : LocalDateTime
        - attFechaExpiracion : LocalDateTime
        - attIdEstado : int
        - attIdUsuario : int
    }
}

' ══════════════════════════════════════════════
' INTERFACES DE SERVICIO
' ══════════════════════════════════════════════
package "logica.interfaces" {

    interface IServicioAgendamiento {
        + crearCitaManual(Cita) : boolean
        + consultarDisponibilidad(int, LocalDate) : List
        + agendarCitaWeb(int, int, LocalDate, LocalTime) : boolean
        + listarCitas(Integer, LocalDate) : List
        + listarTodasLasCitas() : List
        + editarCita(Cita) : boolean
        + cancelarCita(int) : boolean
    }

    interface IServicioConfiguracion {
        + configurarDisponibilidadMedico(JornadaLaboral) : boolean
        + obtenerTodasLasJornadas() : List
    }

    interface IServicioPaciente {
        + registrarPaciente(Paciente, Usuario) : boolean
        + obtenerHistorialCitas(int) : List
        + obtenerCitasFuturas(int) : List
    }

    interface IServicioPersona {
        + crearPersona(Persona) : int
        + editarPersona(Persona) : boolean
        + inactivarPersona(int) : boolean
        + listarPersonas() : List
        + buscarPorDocumento(String) : Persona
        + listarMedicosActivos() : List
        + asignarEspecialidad(MedicoTerapista, int) : boolean
        + listarEspecialidades() : List
    }

    interface IServicioUsuarios {
        + autenticar(String, String) : Usuario
        + registrarUsuario(Usuario) : boolean
        + editarUsuario(Usuario) : boolean
        + eliminarUsuario(int) : boolean
        + listarUsuarios() : List
    }
}

' ══════════════════════════════════════════════
' SERVICIOS
' ══════════════════════════════════════════════
package "logica.servicios" {

    class ServicioAgendamiento {
        - repoCitas : IRepositorioCitas
        - repoJornada : IRepositorioJornadaLaboral
        - repoMedico : IRepositorioMedicoTerapista
        + consultarDisponibilidad(int, LocalDate) : List
        + agendarCitaWeb(int, int, LocalDate, LocalTime) : boolean
        + crearCitaManual(Cita) : boolean
        + listarCitas(Integer, LocalDate) : List
        + editarCita(Cita) : boolean
        + cancelarCita(int) : boolean
    }

    class ServicioConfiguracion {
        - repoJornada : IRepositorioJornadaLaboral
        + configurarDisponibilidadMedico(JornadaLaboral) : boolean
        + obtenerTodasLasJornadas() : List
        + editarTurno(JornadaLaboral) : boolean
        + eliminarTurno(int) : boolean
    }

    class ServicioPaciente {
        - repoUsuario : IRepositorioUsuario
        - repoCitas : IRepositorioCitas
        + registrarPaciente(Paciente, Usuario) : boolean
        + obtenerHistorialCitas(int) : List
        + obtenerCitasFuturas(int) : List
    }

    class ServicioPersona {
        - repoPersona : IRepositorioPersona
        - repoMedico : IRepositorioMedicoTerapista
        - repoEspecialidad : IRepositorioDominioEspecialidad
        + crearPersona(Persona) : int
        + editarPersona(Persona) : boolean
        + inactivarPersona(int) : boolean
        + listarPersonas() : List
        + buscarPorDocumento(String) : Persona
        + listarMedicosActivos() : List
        + asignarEspecialidad(MedicoTerapista, int) : boolean
        + listarEspecialidades() : List
    }

    class ServicioUsuarios {
        - repoUsuario : IRepositorioUsuario
        + autenticar(String, String) : Usuario
        + registrarUsuario(Usuario) : boolean
        + editarUsuario(Usuario) : boolean
        + eliminarUsuario(int) : boolean
        + listarUsuarios() : List
    }

    class ServicioAuth {
        - servicioUsuarios : ServicioUsuarios
        - repoToken : RepositorioSesionToken
        + login(String, String) : String
    }
}

' ══════════════════════════════════════════════
' INTERFACES DE REPOSITORIO
' ══════════════════════════════════════════════
package "persistencia.interfaces" {

    interface IRepositorioAgendador {
        + guardar(Agendador) : int
        + buscarPorId(int) : Agendador
        + listar() : List
        + actualizar(Agendador) : boolean
        + inactivar(int) : boolean
    }

    interface IRepositorioCitas {
        + guardar(Cita) : int
        + buscarPorId(int) : Cita
        + listar() : List
        + actualizar(Cita) : boolean
        + inactivar(int) : boolean
    }

    interface IRepositorioDominioEspecialidad {
        + guardar(DominioEspecialidad) : boolean
        + buscar(int) : DominioEspecialidad
        + listar() : List
        + actualizar(DominioEspecialidad) : boolean
        + inactivar(int) : boolean
    }

    interface IRepositorioJornadaLaboral {
        + guardar(JornadaLaboral) : boolean
        + buscar(int) : JornadaLaboral
        + listar() : List
        + actualizar(JornadaLaboral) : boolean
        + inactivar(int) : boolean
    }

    interface IRepositorioMedicoTerapista {
        + guardar(MedicoTerapista) : boolean
        + buscar(int) : MedicoTerapista
        + listar() : List
        + listarActivos() : List
        + actualizar(MedicoTerapista) : boolean
        + inactivar(int) : boolean
    }

    interface IRepositorioPaciente {
        + guardar(Paciente) : int
        + buscarPorId(int) : Paciente
        + listar() : List
        + actualizar(Paciente) : boolean
        + inactivar(int) : boolean
    }

    interface IRepositorioPersona {
        + guardar(Persona) : int
        + buscarPorId(int) : Persona
        + buscarPorDocumento(String) : Persona
        + listar() : List
        + listarPorEstado(int) : List
        + actualizar(Persona) : boolean
        + inactivar(int) : boolean
    }

    interface IRepositorioRol {
        + guardar(Rol) : int
        + buscarPorId(int) : Rol
        + listarPorUsuario(int) : List
        + listarTodo() : List
        + actualizar(Rol) : boolean
        + inactivar(int) : boolean
        + eliminarPorUsuario(int) : boolean
    }

    interface IRepositorioSesionToken {
        + guardar(SesionToken) : int
        + buscarPorHash(String) : SesionToken
        + buscarActivoPorUsuario(int) : SesionToken
        + listarPorUsuario(int) : List
        + actualizarEstado(int, int) : boolean
        + inactivarExpirados() : boolean
        + esTokenValido(String) : boolean
    }

    interface IRepositorioUsuario {
        + guardar(Usuario) : int
        + buscarPorId(int) : Usuario
        + buscarPorNombreUsuario(String) : Usuario
        + listar() : List
        + actualizar(Usuario) : boolean
        + inactivar(int) : boolean
        + validarCredenciales(String, String) : boolean
    }
}

' ══════════════════════════════════════════════
' REPOSITORIOS
' ══════════════════════════════════════════════
package "persistencia.repositorios" {
    class RepositorioAgendador
    class RepositorioCitas
    class RepositorioDominioEspecialidad
    class RepositorioJornadaLaboral
    class RepositorioMedicoTerapista
    class RepositorioPaciente
    class RepositorioPersona
    class RepositorioRol
    class RepositorioSesionToken
    class RepositorioUsuario
}

package "persistencia" {
    class ConexionBD <<Singleton>> {
        - instancia : Connection
        + getInstance() : Connection
    }
}

' ══════════════════════════════════════════════
' PRESENTACION
' ══════════════════════════════════════════════
package "seguridad" {
    class JwtUtil {
        + generarToken(int, String) : String
        + validarToken(String) : Claims
    }
}

package "presentacion" {
    class SesionUsuario <<Singleton>> {
        - token : String
        - idUsuario : int
        - rol : String
        - idPacienteActual : int
        + getInstancia() : SesionUsuario
        + limpiarSesion() : void
    }
}

package "presentacion.controladores" {

    class ControladorLogin {
        - servicioAuth : ServicioAuth
        + initialize() : void
        + onBtnIniciarSesionClicked() : void
    }

    class ControladorAdmin {
        - repoMedico : RepositorioMedicoTerapista
        - repoPersona : RepositorioPersona
        - repoUsuario : RepositorioUsuario
        - repoCitas : RepositorioCitas
        - repoEspecialidad : RepositorioDominioEspecialidad
        - servicioUsuarios : ServicioUsuarios
        + initialize(URL, ResourceBundle) : void
        + onAsignarRol(ActionEvent) : void
        + onAsignarEspecialidad(ActionEvent) : void
        + onCerrarSesion(ActionEvent) : void
    }

    class ControladorAgendador {
        - servicioAgendamiento : IServicioAgendamiento
        - repoMedico : IRepositorioMedicoTerapista
        - repoPaciente : IRepositorioPaciente
        + initialize(URL, ResourceBundle) : void
        + onFiltrar(ActionEvent) : void
        + onNuevaCita(ActionEvent) : void
        + onCerrarSesion(ActionEvent) : void
    }

    class ControladorAgendarCita {
        - servicio : ServicioAgendamiento
        - repoPaciente : RepositorioPaciente
        - repoPersona : RepositorioPersona
        - repoMedico : RepositorioMedicoTerapista
        + initialize() : void
        + setModoPaciente(int) : void
        - onGuardarCita() : void
        - onBuscarPaciente() : void
    }

    class ControladorPaciente {
        - servicioPaciente : ServicioPaciente
        - repoPersona : RepositorioPersona
        - idPaciente : int
        + initialize(URL, ResourceBundle) : void
        + onAgendarCita(ActionEvent) : void
        + onCerrarSesion(ActionEvent) : void
    }
}

' ══════════════════════════════════════════════
' RELACIONES - HERENCIA
' ══════════════════════════════════════════════
Persona <|-- Paciente
Persona <|-- MedicoTerapista
Persona <|-- Agendador

' ══════════════════════════════════════════════
' RELACIONES - MODELOS
' ══════════════════════════════════════════════
MedicoTerapista --> DominioEspecialidad : idEspecialidad
Cita --> Paciente : idPaciente
Cita --> MedicoTerapista : idMedico
JornadaLaboral --> MedicoTerapista : idUsuario
Rol --> Usuario : idUsuario
SesionToken --> Usuario : idUsuario
Persona --> Usuario : idUsuario

' ══════════════════════════════════════════════
' RELACIONES - SERVICIOS IMPLEMENTAN INTERFACES
' ══════════════════════════════════════════════
ServicioAgendamiento ..|> IServicioAgendamiento
ServicioConfiguracion ..|> IServicioConfiguracion
ServicioPaciente ..|> IServicioPaciente
ServicioPersona ..|> IServicioPersona
ServicioUsuarios ..|> IServicioUsuarios
ServicioAuth --> ServicioUsuarios

' ══════════════════════════════════════════════
' RELACIONES - REPOSITORIOS IMPLEMENTAN INTERFACES
' ══════════════════════════════════════════════
RepositorioAgendador ..|> IRepositorioAgendador
RepositorioCitas ..|> IRepositorioCitas
RepositorioDominioEspecialidad ..|> IRepositorioDominioEspecialidad
RepositorioJornadaLaboral ..|> IRepositorioJornadaLaboral
RepositorioMedicoTerapista ..|> IRepositorioMedicoTerapista
RepositorioPaciente ..|> IRepositorioPaciente
RepositorioPersona ..|> IRepositorioPersona
RepositorioRol ..|> IRepositorioRol
RepositorioSesionToken ..|> IRepositorioSesionToken
RepositorioUsuario ..|> IRepositorioUsuario

RepositorioAgendador --> RepositorioPersona : delega
RepositorioPaciente --> RepositorioPersona : delega
RepositorioMedicoTerapista --> RepositorioPersona : delega

RepositorioAgendador --> ConexionBD
RepositorioCitas --> ConexionBD
RepositorioDominioEspecialidad --> ConexionBD
RepositorioJornadaLaboral --> ConexionBD
RepositorioMedicoTerapista --> ConexionBD
RepositorioPaciente --> ConexionBD
RepositorioPersona --> ConexionBD
RepositorioRol --> ConexionBD
RepositorioSesionToken --> ConexionBD
RepositorioUsuario --> ConexionBD

' ══════════════════════════════════════════════
' RELACIONES - CONTROLADORES
' ══════════════════════════════════════════════
ControladorLogin --> SesionUsuario
ControladorAdmin --> SesionUsuario
ControladorAgendador --> SesionUsuario
ControladorAgendarCita --> SesionUsuario
ControladorPaciente --> SesionUsuario

ControladorLogin ..> ServicioAuth
ControladorAdmin ..> ServicioUsuarios
ControladorAgendador ..> IServicioAgendamiento
ControladorAgendarCita ..> ServicioAgendamiento
ControladorPaciente ..> ServicioPaciente
ServicioAuth ..> JwtUtil

@enduml
