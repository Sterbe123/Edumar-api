package cl.sterbe.apps.servicios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.UsuarioServicio;
import cl.sterbe.apps.modelos.servicios.implementacion.UsuarioImplementacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;

public class UsuarioRepositorioTest {

    @Mock
    private UsuarioServicio usuarioDAO;

    @InjectMocks
    private UsuarioImplementacion usuarioServicio;

    private Usuario usuario;

    @BeforeEach
    void setUp(){
        usuario = new Usuario();
        usuario.setEmail("rodrigo@gmail.com");
        usuario.setContrasena("123");
        usuario.setEstado(true);
    }

    @Test
    void usuarioServicioFindAll(){
        when(this.usuarioServicio.findAll()).thenReturn(Arrays.asList(usuario));
    }

    @Test
    void save(){
        Usuario usuarioNuevo = this.usuarioServicio.save(usuario);
        assertThat(usuarioNuevo).isNotNull();
        assertThat(usuarioNuevo.getId()).isGreaterThan(0);
    }
}
