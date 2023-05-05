package cl.sterbe.apps.servicios.usuariosServicio.implementacion;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorEditarRecurso;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorListaVacia;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoSeEncontroPojo;
import cl.sterbe.apps.modelos.DAO.usuariosDAO.PerfilDAO;
import cl.sterbe.apps.modelos.DTO.usuarios.Perfil;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import cl.sterbe.apps.servicios.usuariosServicio.PerfilServicio;
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
    public List<Perfil> findAll() throws ErrorListaVacia {
        return Optional.of((List<Perfil>) this.perfilDAO.findAll())
                .filter(p -> !p.isEmpty())
                .orElseThrow(() -> new ErrorListaVacia("perfiles"));
    }

    @Override
    @Transactional(readOnly = true)
    public Perfil findById(Long id) {
        return this.perfilDAO.findById(id).orElseThrow(() -> new NoSeEncontroPojo("perfil"));
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
    public void findOneByUsuario(Usuario usuario) throws ErrorEditarRecurso {
        Perfil perfil = this.perfilDAO.findOneByUsuario(usuario).orElse(null);
        if(perfil != null){
            throw new ErrorEditarRecurso();
        }
    }
}
