package cl.sterbe.apps.controladores;

import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.componentes.UsuarioAutenticado;
import cl.sterbe.apps.componentes.ValidarCampos;
import cl.sterbe.apps.componentes.ValidarContrasena;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import cl.sterbe.apps.modelos.servicios.usuariosServicio.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
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
    private ValidarCampos validarCampos;

    @Autowired
    private ValidarContrasena validarContrasena;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

    @Autowired
    private Mensaje mensajes;

    @GetMapping("usuarios")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> buscarUsuarios(){

        //Atributos
        List<Usuario> usuarios = this.usuarioServicio.findAll();

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validamos si la lista esta vacia
        if(usuarios.isEmpty()){
            this.mensajes.agregar("error", "La lista de usuarios esta vacia");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(this.mensajes.mostrarMensajes());
        }

        //setear las contraseña a vacío
        usuarios.forEach(u -> u.setContrasena(""));

        //Mandar los mensajes de exito
        this.mensajes.agregar("exito", "Se encontraron los usuarios con exito");
        this.mensajes.agregar("usuarios", usuarios);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("usuarios/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> buscarUsuario(@PathVariable Long id){

        //Atributos
        Usuario usuario;

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validar parametros
        if(id <= 0){
            this.mensajes.agregar("error", "el parametro debe ser mayor a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos el usuario en la base de datos
        usuario = this.usuarioServicio.findById(id);

        //Validamos el usuario
        if(usuario == null){
            this.mensajes.agregar("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Establecer la contraseña
        usuario.setContrasena("");

        //Mandar mensajes de exito
        this.mensajes.agregar("exito", "Se encontro el usuario con exito");
        this.mensajes.agregar("usuario", usuario);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @PutMapping("usuarios/editar-contrasena")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editarContrasena(@Valid @RequestBody Usuario usuario, BindingResult bindingResult){

        //Atributos
        Usuario usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validar si estas habilitado
        if(!usuarioAuthenticado.isEstado()){
            this.mensajes.agregar("error", "Tu cuenta se encuentra suspendida temporalmente, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //Validar si el usuario esta verificado
        if(!usuarioAuthenticado.isVerificacion()){
            this.mensajes.agregar("error", "Tu cuenta aun no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //Validar campos
        if(bindingResult.hasErrors()){
            this.mensajes.agregar("error", this.validarCampos.validarCampos(bindingResult));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si la contraseña cumple con los requerimientos
        if(!this.validarContrasena.validarContrasena(usuario.getContrasena())){
            this.mensajes.agregar("error", "Error en la contraseña");
            this.mensajes.agregar("condición 1", "La contraseña debe ser superior a 8 caracter");
            this.mensajes.agregar("condición 2", "La contraseña debe contener por lo menos una mayúscula");
            this.mensajes.agregar("condición 3", "La contraseña debe contener minúsculas");
            this.mensajes.agregar("condición 4", "La contraseña debe contener por lo menos un número");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos que la contraseña no sean la misma a la que va actualizar
        if(this.passwordEncoder.matches(usuario.getContrasena(), usuarioAuthenticado.getContrasena())){
            this.mensajes.agregar("error", "La contraseña no debe ser igual a la anterio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //cambiamos la contraseña y la encriptamos
        usuarioAuthenticado.setContrasena(this.passwordEncoder.encode(usuario.getContrasena()));
        usuarioAuthenticado.setUpdateAt(new Date());

        //hacemos la persistencia
        try {
            usuarioAuthenticado = this.usuarioServicio.save(usuarioAuthenticado);
            usuarioAuthenticado.setContrasena("");
        }catch (DataAccessException e){
            this.mensajes.agregar("error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //mensaje de exito
        this.mensajes.agregar("exito", "Se actualizo la contraseña correctamente");
        this.mensajes.agregar("usuario", usuarioAuthenticado);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PatchMapping("usuarios/deshabilitar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> deshabilitarUsuario(@PathVariable Long id){

        //Atributo
        Usuario usuarioBD;

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validar el parametro
        if(id <= 0){
            this.mensajes.agregar("error", "El parametro no debe ser 0 o inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos el usuario en la base de dato
        usuarioBD = this.usuarioServicio.findById(id);

        //Validamos el usuario
        if(usuarioBD == null){
            this.mensajes.agregar("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

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
        try {
            usuarioBD = this.usuarioServicio.save(usuarioBD);
            usuarioBD.setContrasena("");
        }catch (DataAccessException e){
            this.mensajes.agregar("error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //mensajes de exito
        this.mensajes.agregar("exito", "El usuario se deshabilito correctamente");
        this.mensajes.agregar("usuario", usuarioBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PatchMapping("usuarios/habilitar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> habilitarUsuario(@PathVariable Long id){

        //Atributo
        Usuario usuarioBD;

        //Limpiar los mensajes
        this.mensajes.limpiar();

        //Validar el parametro
        if(id <= 0){
            this.mensajes.agregar("error", "El parametro no debe ser 0 o inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos el usuario en la base de datoa
        usuarioBD = this.usuarioServicio.findById(id);

        //Validamos el usuario
        if(usuarioBD == null){
            this.mensajes.agregar("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

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
        try {
            usuarioBD = this.usuarioServicio.save(usuarioBD);
            usuarioBD.setContrasena("");
        }catch (DataAccessException e){
            this.mensajes.agregar("error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //mensajes de exito
        this.mensajes.agregar("exito", "El usuario se habilito correctamente");
        this.mensajes.agregar("usuario", usuarioBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }
}
