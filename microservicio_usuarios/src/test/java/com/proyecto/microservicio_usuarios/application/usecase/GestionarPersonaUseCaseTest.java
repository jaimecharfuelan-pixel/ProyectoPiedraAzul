package com.proyecto.microservicio_usuarios.application.usecase;

import com.proyecto.microservicio_usuarios.application.dto.CrearPersonaCommand;
import com.proyecto.microservicio_usuarios.domain.model.Paciente;
import com.proyecto.microservicio_usuarios.domain.model.Persona;
import com.proyecto.microservicio_usuarios.domain.model.Rol;
import com.proyecto.microservicio_usuarios.domain.model.Usuario;
import com.proyecto.microservicio_usuarios.domain.ports.out.PacienteRepositoryPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.PersonaRepositoryPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.RolRepositoryPort;
import com.proyecto.microservicio_usuarios.domain.ports.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GestionarPersonaUseCaseTest {

    @Mock
    private PersonaRepositoryPort personaRepo;

    @Mock
    private PacienteRepositoryPort pacienteRepo;

    @Mock
    private UsuarioRepositoryPort usuarioRepo;

    @Mock
    private RolRepositoryPort rolRepo;

    @InjectMocks
    private GestionarPersonaUseCase useCase;

    @Test
    void crearAdmin_rechazaCedulaDuplicada() {
        CrearPersonaCommand command = new CrearPersonaCommand();
        command.setNombre("Ana");
        command.setApellido("Prueba");
        command.setCedulaCiudadania("10000000001");
        command.setUsuarioLogin("ana.prueba");
        command.setContrasena("123456");
        command.setFechaNacimiento(LocalDate.of(1995, 5, 20));

        when(personaRepo.findByCedula("10000000001")).thenReturn(Optional.of(new Paciente()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.crearAdmin(command));

        assertEquals("La cédula '10000000001' ya existe.", ex.getMessage());
        verify(usuarioRepo, never()).save(any());
        verify(pacienteRepo, never()).save(any());
        verify(rolRepo, never()).save(any());
    }

    @Test
    void crearAdmin_rechazaUsuarioDuplicado() {
        CrearPersonaCommand command = new CrearPersonaCommand();
        command.setNombre("Luis");
        command.setApellido("Nuevo");
        command.setCedulaCiudadania("10000000002");
        command.setUsuarioLogin("luis.nuevo");
        command.setContrasena("123456");
        command.setFechaNacimiento(LocalDate.of(1990, 6, 6));

        when(personaRepo.findByCedula("10000000002")).thenReturn(Optional.empty());
        when(usuarioRepo.existsByNombreUsuario("luis.nuevo")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.crearAdmin(command));

        assertEquals("El nombre de usuario 'luis.nuevo' ya existe.", ex.getMessage());
        verify(usuarioRepo, never()).save(any());
        verify(pacienteRepo, never()).save(any());
        verify(rolRepo, never()).save(any());
    }

    @Test
    void crearAdmin_guardaPersonaCuandoDatosSonValidos() {
        CrearPersonaCommand command = new CrearPersonaCommand();
        command.setNombre("Maria");
        command.setApellido("Lopez");
        command.setCedulaCiudadania("10000000003");
        command.setUsuarioLogin("maria.lopez");
        command.setContrasena("123456");
        command.setRol("Paciente");
        command.setFechaNacimiento(LocalDate.of(1992, 3, 3));

        when(personaRepo.findByCedula("10000000003")).thenReturn(Optional.empty());
        when(usuarioRepo.existsByNombreUsuario("maria.lopez")).thenReturn(false);

        Usuario usuarioGuardado = new Usuario(1, "maria.lopez", "123456");
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Paciente paciente = new Paciente();
        paciente.setIdPersona(10);
        paciente.setNombre("Maria");
        paciente.setApellido("Lopez");
        paciente.setCedulaCiudadania("10000000003");
        paciente.setIdUsuario(1);
        when(pacienteRepo.save(any(Paciente.class))).thenReturn(paciente);

        Persona resultado = useCase.crearAdmin(command);

        assertEquals("10000000003", resultado.getCedulaCiudadania());
        verify(usuarioRepo).save(any(Usuario.class));
        verify(pacienteRepo).save(any(Paciente.class));
        verify(rolRepo).save(any(Rol.class));
    }
}
