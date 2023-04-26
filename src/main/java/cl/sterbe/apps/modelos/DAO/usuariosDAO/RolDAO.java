package cl.sterbe.apps.modelos.DAO.usuariosDAO;

import cl.sterbe.apps.modelos.DTO.usuarios.Rol;
import org.springframework.data.repository.CrudRepository;

public interface RolDAO extends CrudRepository<Rol, Long> {
}
