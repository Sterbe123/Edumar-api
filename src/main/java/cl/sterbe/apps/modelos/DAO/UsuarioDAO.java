package cl.sterbe.apps.modelos.DAO;

import cl.sterbe.apps.modelos.DTO.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioDAO extends CrudRepository<Usuario, Long> {
}
