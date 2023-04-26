package cl.sterbe.apps.modelos.servicios.usuariosServicio.implementacion;

import cl.sterbe.apps.modelos.DAO.usuariosDAO.DireccionDAO;
import cl.sterbe.apps.modelos.DTO.usuarios.Direccion;
import cl.sterbe.apps.modelos.servicios.usuariosServicio.DireccionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DireccionImplementacion implements DireccionServicio {

    @Autowired
    private DireccionDAO direccionDAO;

    @Override
    public Direccion save(Direccion direccion) {
        return this.direccionDAO.save(direccion);
    }

    @Override
    public void delete(Long id) {
        this.direccionDAO.deleteById(id);
    }
}
