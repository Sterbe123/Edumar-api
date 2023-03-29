package cl.sterbe.apps.security;

import cl.sterbe.apps.modelos.DAO.UsuarioDAO;
import cl.sterbe.apps.modelos.DTO.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImple implements UserDetailsService {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = this.usuarioDAO.findOneByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario con el email " + email + " no existe."));

        return new UserDetailsImple(usuario);
    }
}
