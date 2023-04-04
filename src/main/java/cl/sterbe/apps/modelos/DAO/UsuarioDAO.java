package cl.sterbe.apps.modelos.DAO;

import cl.sterbe.apps.modelos.DTO.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioDAO extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findOneByEmail(String email);
}
