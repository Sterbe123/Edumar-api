package cl.sterbe.apps.modelos.servicios.logica;

import cl.sterbe.apps.modelos.DAO.RolDAO;
import cl.sterbe.apps.modelos.DTO.Rol;
import cl.sterbe.apps.modelos.servicios.RolServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolRepositorio implements RolServicio {

    @Autowired
    private RolDAO rolDAO;

    @Override
    @Transactional(readOnly = true)
    public Rol findById(Long id) {
        return this.rolDAO.findById(id).orElse(null);
    }
}
