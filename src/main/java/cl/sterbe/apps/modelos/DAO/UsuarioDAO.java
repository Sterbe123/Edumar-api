package cl.sterbe.apps.modelos.DAO;

import cl.sterbe.apps.modelos.DTO.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioDAO extends CrudRepository<Usuario, Long> {

    Optional<Usuario> findOneByEmail(String email);
}
