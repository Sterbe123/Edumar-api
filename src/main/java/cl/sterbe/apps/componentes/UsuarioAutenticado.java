package cl.sterbe.apps.componentes;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorEditarRecurso;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoEstaHabilitado;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoEstaVerificado;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoSeEncontroPojo;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import cl.sterbe.apps.servicios.usuariosServicio.UsuarioServicio;
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
        usuario = this.usuarioServicio.findOneByEmail(auth.getName())
                .orElseThrow(() -> new NoSeEncontroPojo("usuario"));
        return usuario;
    }

    public void autenticarUsuario() throws NoEstaHabilitado, NoEstaVerificado {
        if(!this.usuario.isEstado()){
            throw new NoEstaHabilitado();
        }

        if(!this.usuario.isVerificacion()){
           throw  new NoEstaVerificado();
        }
    }

    public void autenticarEditarRecurso(Long id) throws ErrorEditarRecurso {
        if(!this.usuario.getId().equals(id)){
            throw new ErrorEditarRecurso();
        }
    }
}
