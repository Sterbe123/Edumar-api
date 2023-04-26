package cl.sterbe.apps.modelos.servicios.usuariosServicio.implementacion;

import cl.sterbe.apps.modelos.DAO.usuariosDAO.RolDAO;
import cl.sterbe.apps.modelos.DTO.usuarios.Rol;
import cl.sterbe.apps.modelos.servicios.usuariosServicio.RolServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolImplementacion implements RolServicio {

    @Autowired
    private RolDAO rolDAO;

    @Override
    @Transactional(readOnly = true)
    public Rol findById(Long id) {
        return this.rolDAO.findById(id).orElse(null);
    }
}
