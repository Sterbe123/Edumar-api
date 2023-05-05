package cl.sterbe.apps.servicios.usuariosServicio.implementacion;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorListaVacia;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoSeEncontroPojo;
import cl.sterbe.apps.modelos.DAO.usuariosDAO.UsuarioDAO;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import cl.sterbe.apps.servicios.usuariosServicio.UsuarioServicio;
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
    public List<Usuario> findAll() throws ErrorListaVacia {
        return Optional.of((List<Usuario>) this.usuarioDAO.findAll())
                .filter(u -> !u.isEmpty())
                .orElseThrow(() -> new ErrorListaVacia("usuarios"));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        return this.usuarioDAO.findById(id).orElseThrow(() -> new NoSeEncontroPojo("usuario"));
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
    @Transactional(readOnly = true)
    public Optional<Usuario> findOneByEmail(String email) {
        return this.usuarioDAO.findOneByEmail(email);
    }
}
