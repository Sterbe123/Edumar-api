package cl.sterbe.apps.controladores;

import cl.sterbe.apps.componentes.Correos;
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

    @PostMapping("/registro/{rol}")
    public ResponseEntity<?> registro(@Valid @RequestBody Usuario usuario, BindingResult bindingResult, @PathVariable(value = "rol") Long id){

        Map<String, Object> mensajes = new HashMap<>();
        Rol rol;

        //Validamos los campos vacios o mal escritos en el e-mail
        if(bindingResult.hasErrors()){
            mensajes.put("errores", this.validarCampos.validarCampos(bindingResult));
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

        //Asignamos rol al usuario *** Atención esto es solo momentaneo (de prueba)
        rol = this.rolServicio.findById(id);
        if(rol != null){
            usuario.setRol(rol);
        }else{
            mensajes.put("error", "No se pudo Encontrar el rol.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
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
        }catch (DataAccessException e){ //devolver el mensaje de error
            mensajes.put("mensaje", "El correo ya esta en uso.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Enviamos correo de verificacion
        String token = TokenUtils.crearTokenValidacionUsuario(usuario.getId(), usuario.getEmail(),
                Arrays.asList(new SimpleGrantedAuthority(usuario.getRol().getRol())));
        try {
            this.correos.enviarCorreoVerificacion(usuario.getEmail(), token);
        }catch (SendFailedException e){
            this.usuarioServicio.delete(usuario.getId());
            mensajes.put("excepciones", e.getMessage() + " " + e.getLocalizedMessage());
            mensajes.put("error", "Correo no válido.");
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(mensajes);
        }

        //Realizamos el mensaje correspondientes
        usuario.setContrasena("");
        mensajes.put("mensaje", "Se ha creado con exito el usuario.");
        mensajes.put("usuario", usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication); // invalidar la sesión actual del usuario
        }
        return ResponseEntity.ok().build();
    }
}
