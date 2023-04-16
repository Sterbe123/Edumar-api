package cl.sterbe.apps.modelos.DAO;

import cl.sterbe.apps.modelos.DTO.Direccion;
import org.springframework.data.repository.CrudRepository;

public interface DireccionDAO extends CrudRepository<Direccion, Long> {
}
