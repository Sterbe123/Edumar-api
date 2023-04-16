package cl.sterbe.apps.modelos.servicios;

import cl.sterbe.apps.modelos.DTO.Direccion;

import java.util.List;

public interface DireccionServicio {

    Direccion save(Direccion direccion);

    void delete(Long id);
}
