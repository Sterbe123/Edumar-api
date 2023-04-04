package cl.sterbe.apps.modelos.DAO;

import cl.sterbe.apps.modelos.DTO.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolDAO extends JpaRepository<Rol, Long> {
}
