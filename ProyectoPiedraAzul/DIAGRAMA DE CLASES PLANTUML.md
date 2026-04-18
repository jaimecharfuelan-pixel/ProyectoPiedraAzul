@startuml
skinparam style strictuml
skinparam packageStyle folder
skinparam stereotypeCBackgroundColor #White
skinparam stereotypeIBackgroundColor #LawnGreen
skinparam classAttributeIconSize 0

title Estructura Arquitectónica Final - Sistema de Citas Médicas
package "Proyecto"{
' --- CAPA DE PRESENTACIÓN ---
package "presentacion" {
    

    package "controladores" {
        class ControladorLogin {
            - attServicioUsuarios: IServicioUsuarios
            + ControladorLogin(prmServicio: IServicioUsuarios)
            + iniciarSesion(prmUsuario: String, prmClave: String): void
        }
        class ControladorPaciente {
            - attServicioCitas: IServicioCitas
            + ControladorPaciente(prmServicio: IServicioCitas)
            + agendarCita(prmFecha: LocalDate, prmHora: LocalTime, prmMedico: Medico): void
        }
        class ControladorAdmin {
            - attServicioConfiguracion: IServicioConfiguracion
            - attServicioUsuarios: IServicioUsuarios
            + ControladorAdmin(prmServicioConfig: IServicioConfiguracion, prmServicioUsuarios: IServicioUsuarios)
            + gestionarUsuario(prmDocumento: String, prmNombre: String, prmApellido: String, prmCelular: String, prmGenero: String, prmNacimiento: LocalDate, prmEmail: String, prmUsuario: String, prmClave: String, prmRol: String): void
            + configurarParametros(prmMedico: Medico, prmSemanas: int, prmDiasAtencion: String, prmHoraInicio: LocalTime, prmHoraFin: LocalTime, prmIntervalo: int): void
        }
        class ControladorAgendador {
            - attServicioCitas: IServicioCitas
            - attServicioUsuarios: IServicioUsuarios
            + ControladorAgendador(prmServicioCitas: IServicioCitas, prmServicioUsuarios: IServicioUsuarios)
            + listarCitas(prmMedico: Medico, prmFecha: LocalDate): void
            + agendarCita(prmDocumento: String, prmNombre: String, prmApellido: String, prmCelular: String, prmGenero: String, prmNacimiento: LocalDate, prmMedico: Medico, prmHora: LocalTime): void
        }
    }

    class SesionUsuario << (S,#FF7700) Singleton >> {
        - {static} attInstancia: SesionUsuario
        - attUsuarioActual: Persona
        + {static} getInstancia(): SesionUsuario
        + setUsuario(prmUsuario: Persona): void
        + getUsuario(): Persona
    }
}

' --- CAPA DE LÓGICA ---
package "logica" {
    package "modelos" {
        abstract class Persona <<abstract>> {
            - attDocumento: String
            - attNombre: String
            - attApellido: String
            - attCelular: String
            - attGenero: String
            - attFechaNacimiento: LocalDate
            - attEmail: String
        }
        class Paciente extends Persona
        class Medico extends Persona {
            - attEspecialidad: String
        }
        class Agendador extends Persona
        class Administrador extends Persona
        class Cita {
            - attFecha: LocalDate
            - attHora: LocalTime
            - attMedico: Medico
            - attPaciente: Paciente
        }
        class ConfiguracionAgenda {
            - attIdMedico: String
            - attVentanaSemanas: int
            - attDiasAtencion: String
            - attHoraInicio: LocalTime
            - attHoraFin: LocalTime
            - attIntervaloMinutos: int
        }
    }

    package "interfaces" as int_log {
        interface IServicioUsuarios <<interface>> {
            + registrarPaciente(prmPaciente: Paciente): boolean
            + buscarPorDocumento(prmDocumento: String): Persona
            + autenticar(prmUsuario: String, prmClave: String): Persona
        }
        interface IServicioCitas <<interface>> {
            + agendarCita(prmCita: Cita): boolean
            + listarCitasMedico(prmIdMedico: String, prmFecha: LocalDate): List<Cita>
            + obtenerHorariosDisponibles(prmIdMedico: String, prmFecha: LocalDate): List<LocalTime>
        }
        interface IServicioConfiguracion <<interface>> {
            + guardarConfiguracion(prmConfiguracion: ConfiguracionAgenda): boolean
            + obtenerConfiguracion(prmIdMedico: String): ConfiguracionAgenda
        }
    }

    package "servicios" {
        class ServicioUsuariosImpl implements IServicioUsuarios {
            - attRepositorioPersona: IRepositorioPersona
            + ServicioUsuariosImpl(prmRepositorio: IRepositorioPersona)
        }
        class ServicioCitasImpl implements IServicioCitas {
            - attRepoCitas: IRepositorioCitas
            - attRepoConfiguracion: IRepositorioConfiguracion
            + ServicioCitasImpl(prmRepoCitas: IRepositorioCitas, prmRepoConfig: IRepositorioConfiguracion)
        }
        class ServicioConfiguracionImpl implements IServicioConfiguracion {
            - attRepositorioConfig: IRepositorioConfiguracion
            + ServicioConfiguracionImpl(prmRepositorio: IRepositorioConfiguracion)
        }
    }
}

' --- CAPA DE PERSISTENCIA ---
package "persistencia" {
    package "interfaces" as int_per {
        interface IRepositorioPersona <<interface>> {
            + guardar(prmPersona: Persona): boolean
            + buscarPorDoc(prmDocumento: String): Persona
        }
        interface IRepositorioCitas <<interface>> {
            + guardar(prmCita: Cita): boolean
            + buscarPorMedicoFecha(prmIdMedico: String, prmFecha: LocalDate): List<Cita>
        }
        interface IRepositorioConfiguracion <<interface>> {
            + guardar(prmConfiguracion: ConfiguracionAgenda): boolean
            + buscarPorMedico(prmIdMedico: String): ConfiguracionAgenda
        }
    }

    package "repositorios" {
        class RepositorioPersonaPostgres implements IRepositorioPersona {
            - attConexion: Connection
            + RepositorioPersonaPostgres(prmConexion: Connection)
            + guardar(prmPersona: Persona): boolean
            + buscarPorDoc(prmDocumento: String): Persona
        }
        class RepositorioCitasPostgres implements IRepositorioCitas {
            - attConexion: Connection
            + RepositorioCitasPostgres(prmConexion: Connection)
            + guardar(prmCita: Cita): boolean
            + buscarPorMedicoFecha(prmIdMedico: String, prmFecha: LocalDate): List<Cita>
        }
        class RepositorioConfiguracionPostgres implements IRepositorioConfiguracion {
            - attConexion: Connection
            + RepositorioConfiguracionPostgres(prmConexion: Connection)
            + guardar(prmConfiguracion: ConfiguracionAgenda): boolean
            + buscarPorMedico(prmIdMedico: String): ConfiguracionAgenda
        }
    }
    
    class ConexionBD << (S,#FF7700) Singleton >> {
        - {static} attInstancia: ConexionBD
        + {static} obtenerInstancia(): Connection
    }
}
}
package "Recursos"{
  
  package "Estilos"{
  }
  
  package "vistas" {
        class VistaLogin {
            + iniciarSesion(prmUsuario: String, prmClave: String): void
        }
        class VistaPaciente {
            + agendarCita(prmFecha: LocalDate, prmHora: LocalTime, prmMedico: Medico): void
        }
        class VistaAdmin {
            + gestionarUsuario(prmDocumento: String, prmNombre: String, prmApellido: String, prmCelular: String, prmGenero: String, prmNacimiento: LocalDate, prmEmail: String, prmUsuario: String, prmClave: String, prmRol: String): void
            + configurarParametros(prmMedico: Medico, prmSemanas: int, prmDiasAtencion: String, prmHoraInicio: LocalTime, prmHoraFin: LocalTime, prmIntervalo: int): void
        }
        class VistaAgendador {
            + listarCitas(prmMedico: Medico, prmFecha: LocalDate): void
            + agendarCita(prmDocumento: String, prmNombre: String, prmApellido: String, prmCelular: String, prmGenero: String, prmNacimiento: LocalDate, prmMedico: Medico, prmHora: LocalTime): void
        }
    }
}  
' --- RELACIONES ---
vistas --> controladores
controladores ..> int_log
servicios --> int_per
servicios ..> modelos
repositorios --> ConexionBD

@enduml