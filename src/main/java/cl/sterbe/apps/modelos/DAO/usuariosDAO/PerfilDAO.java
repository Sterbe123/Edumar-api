package cl.sterbe.apps.modelos.DAO.usuariosDAO;

import cl.sterbe.apps.modelos.DTO.usuarios.Perfil;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PerfilDAO extends CrudRepository<Perfil, Long> {

    Optional<Perfil> findOneByUsuario(Usuario usuario);
}
