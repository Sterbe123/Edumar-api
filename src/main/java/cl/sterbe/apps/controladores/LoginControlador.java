package cl.sterbe.apps.controladores;

import cl.sterbe.apps.componentes.Correos;
import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.componentes.ValidarCampos;
import cl.sterbe.apps.componentes.ValidarContrasena;
import cl.sterbe.apps.modelos.DTO.usuarios.Rol;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import cl.sterbe.apps.modelos.servicios.usuariosServicio.RolServicio;
import cl.sterbe.apps.modelos.servicios.usuariosServicio.UsuarioServicio;
import cl.sterbe.apps.security.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.SendFailedException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private RolServicio rolServicio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ValidarCampos validarCampos;

    @Autowired
    private ValidarContrasena validarContrasena;

    @Autowired
    private Correos correos;

    @Autowired
    private Mensaje mensajes;

    @PostMapping("/registro/{rol}")
    public ResponseEntity<?> registro(@Valid @RequestBody Usuario usuario, BindingResult bindingResult, @PathVariable(value = "rol") Long id){

        //Atributos
        Rol rol;

        //Limpiar mensajes
        this.mensajes.limpiar();

        //Validamos los campos vacios o mal escritos en el e-mail
        if(bindingResult.hasErrors()){
            this.mensajes.agregar("errores", this.validarCampos.validarCampos(bindingResult));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes);
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

        //Asignamos rol al usuario *** Atención esto es solo momentaneo (de prueba)
        rol = this.rolServicio.findById(id);
        if(rol != null){
            usuario.setRol(rol);
        }else{
            this.mensajes.agregar("error", "No se pudo Encontrar el rol.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //Encriptar Informacion del usuario para la insersion en la base de datos
        usuario.setContrasena(this.passwordEncoder.encode(usuario.getContrasena()));

        //Establecer el estado del usuario y fecha de registro
        usuario.setEstado(true);
        usuario.setVerificacion(false);
        usuario.setCreateAt(new Date());

        //Realizamos la insercion a la base de datos
        try{
            usuario = this.usuarioServicio.save(usuario);
            usuario.setContrasena("");
        }catch (DataAccessException e){ //devolver el mensaje de error
            this.mensajes.agregar("mensaje", "El correo ya esta en uso.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //Enviamos correo de verificacion
        String token = TokenUtils.crearTokenValidacionUsuario(usuario.getId(), usuario.getEmail(),
                Arrays.asList(new SimpleGrantedAuthority(usuario.getRol().getRol())));
        try {
            this.correos.enviarCorreoVerificacion(usuario.getEmail(), token);
        }catch (SendFailedException e){
            this.usuarioServicio.delete(usuario.getId());
            this.mensajes.agregar("excepciones", e.getMessage() + " " + e.getLocalizedMessage());
            this.mensajes.agregar("error", "Correo no válido.");
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(this.mensajes.mostrarMensajes());
        }

        //Realizamos el mensaje correspondientes
        this.mensajes.agregar("mensaje", "Se ha creado con exito el usuario.");
        this.mensajes.agregar("usuario", usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication); // invalidar la sesión actual del usuario
        }
        return ResponseEntity.ok().build();
    }
}
