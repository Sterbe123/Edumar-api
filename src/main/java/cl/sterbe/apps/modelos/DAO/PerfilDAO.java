package cl.sterbe.apps.modelos.DAO;

import cl.sterbe.apps.modelos.DTO.Perfil;
import cl.sterbe.apps.modelos.DTO.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PerfilDAO extends CrudRepository<Perfil, Long> {

    Optional<Perfil> findOneByUsuario(Usuario usuario);
}
