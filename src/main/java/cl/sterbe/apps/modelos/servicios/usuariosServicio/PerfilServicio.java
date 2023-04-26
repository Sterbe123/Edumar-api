package cl.sterbe.apps.modelos.servicios.usuariosServicio;

import cl.sterbe.apps.modelos.DTO.usuarios.Perfil;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;

import java.util.List;

public interface PerfilServicio {

    List<Perfil> findAll();

    Perfil findById(Long id);

    Perfil save(Perfil perfil);

    void delete(Long id);

    Perfil findOneByUsuario(Usuario usuario);
}
