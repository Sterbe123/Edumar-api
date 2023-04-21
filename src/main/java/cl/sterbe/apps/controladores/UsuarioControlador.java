package cl.sterbe.apps.controladores;

import cl.sterbe.apps.componentes.ValidarCampos;
import cl.sterbe.apps.componentes.ValidarContrasena;
import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("usuarios")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> buscarUsuarios(){

        //Atributos
        List<Usuario> usuarios = this.usuarioServicio.findAll();
        Map<String, Object> mensajes = new HashMap<>();

        //Validamos si la lista esta vacia
        if(usuarios.isEmpty()){
            mensajes.put("error", "La lista de usuarios esta vacia");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mensajes);
        }

        //setear las contraseña a vacío
        usuarios.forEach(u -> u.setContrasena(""));

        //Mandar los mensajes de exito
        mensajes.put("exito", "Se encontraron los usuarios con exito");
        mensajes.put("usuarios", usuarios);
        return ResponseEntity.status(HttpStatus.OK).body(mensajes);
    }

    @GetMapping("usuarios/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> buscarUsuario(@PathVariable Long id){

        //Atributos
        Usuario usuario = null;
        Map<String, Object> mensajes = new HashMap<>();

        //Validar parametros
        if(id <= 0){
            mensajes.put("error", "el parametro debe ser mayor a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Buscamos el usuario en la base de datos
        usuario = this.usuarioServicio.findById(id);

        //Validamos el usuario
        if(usuario == null){
            mensajes.put("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes);
        }

        //Establecer la contraseña
        usuario.setContrasena("");

        //Mandar mensajes de exito
        mensajes.put("exito", "Se encontro el usuario con exito");
        mensajes.put("usuario", usuario);
        return ResponseEntity.status(HttpStatus.OK).body(mensajes);
    }

    @PutMapping("usuarios/editar-contrasena")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editarContrasena(@Valid @RequestBody Usuario usuario, BindingResult bindingResult){

        //Atributos
        Usuario usuarioSave = null;
        Usuario usuarioAuthenticado = null;
        Map<String, Object> mensajes = new HashMap<>();
        Authentication auth = null;

        //Validr campos
        if(bindingResult.hasErrors()){
            mensajes.put("error", this.validarCampos.validarCampos(bindingResult));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos si la contraseña cumple con los requerimientos
        if(!this.validarContrasena.validarContrasena(usuario.getContrasena())){
            mensajes.put("error", "Error en la contraseña");
            mensajes.put("condición 1", "La contraseña debe ser superior a 8 caracter");
            mensajes.put("condición 2", "La contraseña debe contener por lo menos una mayúscula");
            mensajes.put("condición 3", "La contraseña debe contener minúsculas");
            mensajes.put("condición 4", "La contraseña debe contener por lo menos un número");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Autenticacion del usuario
        auth = SecurityContextHolder.getContext().getAuthentication();
        usuarioAuthenticado = this.usuarioServicio.findOneByEmail(auth.getName()).orElse(null);

        //Validamos si exite el usuario
        if(usuarioAuthenticado == null){
            mensajes.put("error", "No registrado");
            return ResponseEntity.status(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED).body(mensajes);
        }

        //Validamos que la contraseña no sean la misma a la que va actualizar
        if(this.passwordEncoder.matches(usuario.getContrasena(), usuarioAuthenticado.getContrasena())){
            mensajes.put("error", "La contraseña no debe ser igual a la anterio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //cambiamos la contrasena y la encriptamos
        usuarioAuthenticado.setContrasena(this.passwordEncoder.encode(usuario.getContrasena()));
        usuarioAuthenticado.setUpdateAt(new Date());

        //hacemos la persistencia
        try {
            usuarioSave = this.usuarioServicio.save(usuarioAuthenticado);
        }catch (DataAccessException e){
            mensajes.put("error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Establecemos la contraseña a vacio
        usuarioSave.setContrasena("");

        //mensaje de exito
        mensajes.put("exito", "Se actualizo la contraseña correctamente");
        mensajes.put("usuario", usuarioSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }

    @PatchMapping("usuarios/deshabilitar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> deshabilitarUsuario(@PathVariable Long id){

        //Atributo
        Usuario usuarioBD = null;
        Usuario usuarioSave = null;
        Map<String, Object> mensajes = new HashMap<>();

        //Validar el parametro
        if(id <= 0){
            mensajes.put("error", "El parametro no debe ser 0 o inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Buscamos el usuario en la base de dato
        usuarioBD = this.usuarioServicio.findById(id);

        //Validamos el usuario
        if(usuarioBD == null){
            mensajes.put("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Validamos que el usuario a deshabilitar no sea un administrador
        if(usuarioBD.getRol().getRol().equals("ROLE_ADMINISTRADOR")){
            mensajes.put("error", "No puedes deshabilitar un administrador");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos si el usuario se encuentre habilitado
        if(!usuarioBD.isEstado()){
            mensajes.put("error", "El usuario ya se encuentra deshabilitado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }else{
            usuarioBD.setEstado(false);
        }

        //Hacemos la actualizacion
        try {
            usuarioSave = this.usuarioServicio.save(usuarioBD);
        }catch (DataAccessException e){
            mensajes.put("error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Establecemos la contraseña a vacio
        usuarioSave.setContrasena("");

        //mensajes de exito
        mensajes.put("exito", "El usuario se deshabilito correctamente");
        mensajes.put("usuario", usuarioSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }

    @PatchMapping("usuarios/habilitar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> habilitarUsuario(@PathVariable Long id){

        //Atributo
        Usuario usuarioBD = null;
        Usuario usuarioSave = null;
        Map<String, Object> mensajes = new HashMap<>();

        //Validar el parametro
        if(id <= 0){
            mensajes.put("error", "El parametro no debe ser 0 o inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Buscamos el usuario en la base de datoa
        usuarioBD = this.usuarioServicio.findById(id);

        //Validamos el usuario
        if(usuarioBD == null){
            mensajes.put("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Validamos que el usuario a deshabilitar no sea un administrador
        if(usuarioBD.getRol().getRol().equals("ROLE_ADMINISTRADOR")){
            mensajes.put("error", "No puedes deshabilitar un administrador");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos si el usuario se encuentre habilitado
        if(usuarioBD.isEstado()){
            mensajes.put("error", "El usuario ya se encuentra habilitado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }else{
            usuarioBD.setEstado(true);
        }

        //Hacemos la actualizacion
        try {
            usuarioSave = this.usuarioServicio.save(usuarioBD);
        }catch (DataAccessException e){
            mensajes.put("error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Establecemos la contraseña a vacio
        usuarioSave.setContrasena("");

        //mensajes de exito
        mensajes.put("exito", "El usuario se habilito correctamente");
        mensajes.put("usuario", usuarioSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }
}