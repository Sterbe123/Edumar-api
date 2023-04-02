package cl.sterbe.apps.modelos.servicios.implementacion;

import cl.sterbe.apps.modelos.DAO.UsuarioDAO;
import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioImplementacion implements UsuarioServicio {

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

    @Override
    public Optional<Usuario> findOneByEmail(String email) {
        return this.usuarioDAO.findOneByEmail(email);
    }
}
