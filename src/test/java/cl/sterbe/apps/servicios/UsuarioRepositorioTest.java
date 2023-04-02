package cl.sterbe.apps.servicios;

import cl.sterbe.apps.modelos.DAO.UsuarioDAO;
import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.UsuarioServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;

public class UsuarioRepositorioTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @InjectMocks
    private UsuarioServicio usuarioServicio;

    private Usuario usuario;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
        usuario = new Usuario();
        usuario.setEmail("rodrigo@gmail.com");
        usuario.setContrasena("123");
        usuario.setEstado(true);
        usuario.setCreateAt(new Date());
    }

    @Test
    void usuarioServicioFindAll(){
        Mockito.when(this.usuarioServicio.findAll()).thenReturn(Arrays.asList(usuario));

    }

    @Test
    void save(){
        Mockito.when(this.usuarioServicio.save(Mockito.any(Usuario.class))).thenReturn(usuario);
    }
}
