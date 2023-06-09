package cl.sterbe.apps.controladores;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorContrasena;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorListaVacia;
import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.componentes.UsuarioAutenticado;
import cl.sterbe.apps.componentes.ValidarContrasena;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import cl.sterbe.apps.servicios.usuariosServicio.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ValidarContrasena validarContrasena;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

    @Autowired
    private Mensaje mensajes;

    @GetMapping("usuarios")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> buscarUsuarios() throws ErrorListaVacia {

        List<Usuario> usuarios = this.usuarioServicio.findAll();
        usuarios.forEach(usuario -> usuario.setContrasena(""));

        //Mandar los mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se encontraron los usuarios con exito");
        this.mensajes.agregar("usuarios", usuarios);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("usuarios/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> buscarUsuario(@PathVariable Long id){

        Usuario usuario = this.usuarioServicio.findById(id);
        usuario.setContrasena("");

        //Mandar mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se encontro el usuario con exito");
        this.mensajes.agregar("usuario", usuario);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @PutMapping("usuarios/editar-contrasena")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> editarContrasena(@Valid @RequestBody Usuario usuario, BindingResult bindingResult)
            throws BindException, ErrorContrasena {

        //Validar campos
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        //Validamos si la contraseña cumple con los requerimientos
        this.validarContrasena.validarContrasena(usuario.getContrasena());

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validamos que la contraseña no sean la misma a la que va actualizar
        if(this.passwordEncoder.matches(usuario.getContrasena(), this.usuarioAutenticado.getUsuarioAutenticado().getContrasena())){
            this.mensajes.agregar("error", "La contraseña no debe ser igual a la anterio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //cambiamos la contraseña y la encriptamos
        this.usuarioAutenticado.getUsuarioAutenticado().setContrasena(this.passwordEncoder.encode(usuario.getContrasena()));
        this.usuarioAutenticado.getUsuarioAutenticado().setUpdateAt(new Date());

        //hacemos la persistencia
        usuario = this.usuarioServicio.save(this.usuarioAutenticado.getUsuarioAutenticado());
        usuario.setContrasena("");

        //mensaje de exito
        this.mensajes.agregar("exito", "Se actualizo la contraseña correctamente");
        this.mensajes.agregar("usuario", usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PatchMapping("usuarios/deshabilitar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> deshabilitarUsuario(@PathVariable Long id){

        //Buscamos el usuario en la base de dato
        Usuario usuarioBD = this.usuarioServicio.findById(id);

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validamos que el usuario a deshabilitar no sea un administrador
        if(usuarioBD.getRol().getRol().equals("ROLE_ADMINISTRADOR")){
            this.mensajes.agregar("error", "No puedes deshabilitar a un administrador");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si el usuario se encuentre habilitado
        if(!usuarioBD.isEstado()){
            this.mensajes.agregar("error", "El usuario ya se encuentra deshabilitado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }else{
            usuarioBD.setEstado(false);
        }

        //Hacemos la actualizacion
        usuarioBD = this.usuarioServicio.save(usuarioBD);
        usuarioBD.setContrasena("");

        //mensajes de exito
        this.mensajes.agregar("exito", "El usuario se deshabilito correctamente");
        this.mensajes.agregar("usuario", usuarioBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PatchMapping("usuarios/habilitar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> habilitarUsuario(@PathVariable Long id){

        //Atributo
        Usuario usuarioBD = this.usuarioServicio.findById(id);

        //Limpiar los mensajes
        this.mensajes.limpiar();

        //Validamos que el usuario a deshabilitar no sea un administrador
        if(usuarioBD.getRol().getRol().equals("ROLE_ADMINISTRADOR")){
            this.mensajes.agregar("error", "No puedes deshabilitar un administrador");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si el usuario se encuentre habilitado
        if(usuarioBD.isEstado()){
            this.mensajes.agregar("error", "El usuario ya se encuentra habilitado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }else{
            usuarioBD.setEstado(true);
        }

        //Hacemos la actualizacion
        usuarioBD = this.usuarioServicio.save(usuarioBD);
        usuarioBD.setContrasena("");

        //mensajes de exito
        this.mensajes.agregar("exito", "El usuario se habilito correctamente");
        this.mensajes.agregar("usuario", usuarioBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }
}
