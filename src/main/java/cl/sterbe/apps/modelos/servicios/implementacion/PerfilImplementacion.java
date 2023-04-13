package cl.sterbe.apps.modelos.servicios.implementacion;

import cl.sterbe.apps.modelos.DAO.PerfilDAO;
import cl.sterbe.apps.modelos.DTO.Perfil;
import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.PerfilServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PerfilImplementacion implements PerfilServicio {

    @Autowired
    private PerfilDAO perfilDAO;

    @Override
    @Transactional(readOnly = true)
    public List<Perfil> findAll() {
        return (List<Perfil>) this.perfilDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Perfil findById(Long id) {
        return this.perfilDAO.findById(id).orElse(null);
    }

    @Override
    public Perfil save(Perfil perfil) {
        return this.perfilDAO.save(perfil);
    }

    @Override
    public void delete(Long id) {
        this.perfilDAO.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Perfil findOneByUsuario(Usuario usuario) {
        return this.perfilDAO.findOneByUsuario(usuario).orElse(null);
    }
}
