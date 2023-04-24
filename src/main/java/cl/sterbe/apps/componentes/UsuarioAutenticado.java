package cl.sterbe.apps.componentes;

import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAutenticado {

    private Usuario usuario = null;

    private Authentication auth = null;

    @Autowired
    private UsuarioServicio usuarioServicio;

    /**
     * verifica la sesion y de vuelve el usuario autenticado
     * @return Usuario
     */
    public Usuario getUsuarioAutenticado(){
        auth = SecurityContextHolder.getContext().getAuthentication();
        usuario = this.usuarioServicio.findOneByEmail(auth.getName()).orElse(new Usuario());
        return usuario;
    }
}
