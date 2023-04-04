package cl.sterbe.apps.DAO;

import static org.assertj.core.api.Assertions.assertThat;
import cl.sterbe.apps.modelos.DAO.UsuarioDAO;
import cl.sterbe.apps.modelos.DTO.Rol;
import cl.sterbe.apps.modelos.DTO.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UsuarioDaoTest {

    @Autowired
    private UsuarioDAO usuarioDAO;

    //BDD patron: Given-When-Then

    @DisplayName("Test para registrar el usuario en la base de datos")
    @Test
    void testSaveUsuario(){

        //Crear Rol
        Rol rol = new Rol();
        rol.setRol("ROL_ADMINISTRADOR");

        //Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail("rodrigo@gmail.com");
        usuario.setContrasena("123");
        usuario.setEstado(true);
        usuario.setRol(rol);

        Usuario guardarUsuario = this.usuarioDAO.save(usuario);

        assertThat(guardarUsuario).isNotNull();
        assertThat(guardarUsuario.getId()).isGreaterThan(0);
    }
}
