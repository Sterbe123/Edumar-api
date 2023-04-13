package cl.sterbe.apps.modelos.servicios;

import cl.sterbe.apps.modelos.DTO.Perfil;

import java.util.List;

public interface PerfilServicio {

    List<Perfil> findAll();

    Perfil findById(Long id);

    Perfil save(Perfil perfil);

    void delete(Long id);
}
