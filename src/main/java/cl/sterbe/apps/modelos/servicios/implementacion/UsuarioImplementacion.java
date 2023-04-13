package cl.sterbe.apps.modelos.servicios.implementacion;

import cl.sterbe.apps.modelos.DAO.UsuarioDAO;
import cl.sterbe.apps.modelos.DTO.Rol;
import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.UsuarioServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioImplementacion implements UsuarioServicio {

    @Autowired
    private UsuarioDAO usuarioDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioImplementacion.class);

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
    public Usuario findOneByEmail(String email) {
        Usuario usuario = this.usuarioDAO.findOneByEmail(email).orElse(null);

        if(usuario == null){
            return null;
        }

        return usuario;
    }
}
