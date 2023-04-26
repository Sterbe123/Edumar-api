package cl.sterbe.apps.modelos.DAO.usuariosDAO;

import cl.sterbe.apps.modelos.DTO.usuarios.Direccion;
import org.springframework.data.repository.CrudRepository;

public interface DireccionDAO extends CrudRepository<Direccion, Long> {
}
