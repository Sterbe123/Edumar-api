package cl.sterbe.apps.modelos.servicios.logica;

import cl.sterbe.apps.modelos.DAO.UsuarioDAO;
import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioRepositorio implements UsuarioServicio {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return (List<Usuario>) this.usuarioDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        return this.usuarioDAO.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        return this.usuarioDAO.save(usuario);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        this.usuarioDAO.deleteById(id);
    }
}
