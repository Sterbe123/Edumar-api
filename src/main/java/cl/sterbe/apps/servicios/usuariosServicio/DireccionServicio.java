package cl.sterbe.apps.servicios.usuariosServicio;

import cl.sterbe.apps.modelos.DTO.usuarios.Direccion;

public interface DireccionServicio {

    Direccion save(Direccion direccion);

    void delete(Long id);
}
