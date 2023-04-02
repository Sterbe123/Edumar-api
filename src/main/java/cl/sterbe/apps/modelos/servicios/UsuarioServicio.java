package cl.sterbe.apps.modelos.servicios;

import cl.sterbe.apps.modelos.DTO.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioServicio {

    List<Usuario> findAll();

    Usuario findById(Long id);

    Usuario save(Usuario usuario);

    void delete(Long id);

    Optional<Usuario> findOneByEmail(String email);
}
