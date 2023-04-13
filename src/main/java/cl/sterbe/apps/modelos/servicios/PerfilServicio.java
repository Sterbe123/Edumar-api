package cl.sterbe.apps.modelos.servicios;

import cl.sterbe.apps.modelos.DTO.Perfil;
import cl.sterbe.apps.modelos.DTO.Usuario;

import java.util.List;
import java.util.Optional;

public interface PerfilServicio {

    List<Perfil> findAll();

    Perfil findById(Long id);

    Perfil save(Perfil perfil);

    void delete(Long id);

    Perfil findOneByUsuario(Usuario usuario);
}
