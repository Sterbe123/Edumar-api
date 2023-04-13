package cl.sterbe.apps.modelos.servicios;

import cl.sterbe.apps.modelos.DTO.Usuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UsuarioServicio {

    List<Usuario> findAll();

    Usuario findById(Long id);

    Usuario save(Usuario usuario);

    void delete(Long id);

    Usuario findOneByEmail(String email);
}
