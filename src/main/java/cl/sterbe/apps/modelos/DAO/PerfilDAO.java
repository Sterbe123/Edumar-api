package cl.sterbe.apps.modelos.DAO;

import cl.sterbe.apps.modelos.DTO.Perfil;
import org.springframework.data.repository.CrudRepository;

public interface PerfilDAO extends CrudRepository<Perfil, Long> {
}
