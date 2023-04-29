package cl.sterbe.apps.servicios.usuariosServicio;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorEditarRecurso;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorPerfilRegistrado;
import cl.sterbe.apps.modelos.DTO.usuarios.Perfil;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;

import java.util.List;

public interface PerfilServicio {

    List<Perfil> findAll();

    Perfil findById(Long id);

    Perfil save(Perfil perfil);

    void delete(Long id);

    void findOneByUsuario(Usuario usuario) throws ErrorPerfilRegistrado, ErrorEditarRecurso;
}
