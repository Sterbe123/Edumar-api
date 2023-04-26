package cl.sterbe.apps.modelos.DAO.usuariosDAO;

import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioDAO extends CrudRepository<Usuario, Long> {

    Optional<Usuario> findOneByEmail(String email);
}
