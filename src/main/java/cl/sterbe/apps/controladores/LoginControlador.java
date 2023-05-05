package cl.sterbe.apps.controladores;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorContrasena;
import cl.sterbe.apps.componentes.Correos;
import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.componentes.ValidarContrasena;
import cl.sterbe.apps.modelos.DTO.usuarios.Rol;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import cl.sterbe.apps.servicios.usuariosServicio.RolServicio;
import cl.sterbe.apps.servicios.usuariosServicio.UsuarioServicio;
import cl.sterbe.apps.security.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.SendFailedException;
import java.util.Arrays;
import java.util.Date;
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
    private ValidarContrasena validarContrasena;

    @Autowired
    private Correos correos;

    @Autowired
    private Mensaje mensajes;

    @PostMapping("/registro/{rol}")
    public ResponseEntity<Map<String, Object>> registro(@Valid @RequestBody Usuario usuario, BindingResult bindingResult,
                                        @PathVariable(value = "rol") Long id)
            throws BindException, ErrorContrasena {

        //Atributos
        Rol rol;

        //Validamos los campos vacios o mal escritos en el e-mail
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        //Validamos si la contraseña cumple con los requisitos
        this.validarContrasena.validarContrasena(usuario.getContrasena());

        //Asignamos rol al usuario *** Atención esto es solo momentaneo (de prueba)
        rol = this.rolServicio.findById(id);

        //Encriptar Informacion del usuario para la insersion en la base de datos
        usuario.setContrasena(this.passwordEncoder.encode(usuario.getContrasena()));

        //Establecer el estado del usuario, fecha de registro y rol
        usuario.setEstado(true);
        usuario.setRol(rol);
        usuario.setVerificacion(false);
        usuario.setCreateAt(new Date());

        //Realizamos la insercion a la base de datos
        usuario = this.usuarioServicio.save(usuario);

        //Limpiar mensajes
        this.mensajes.limpiar();

        //Enviamos correo de verificacion
        String token = TokenUtils.crearTokenValidacionUsuario(usuario.getId(), usuario.getEmail(),
                Arrays.asList(new SimpleGrantedAuthority(usuario.getRol().getRol())));

        //****** se comenta ya que supere el limite mensual en mailtrap xD
   /*     try {
            this.correos.enviarCorreoVerificacion(usuario.getEmail(), token);
        }catch (SendFailedException e){
            this.usuarioServicio.delete(usuario.getId());
            this.mensajes.agregar("excepciones", e.getMessage() + " " + e.getLocalizedMessage());
            this.mensajes.agregar("error", "Correo no válido.");
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(this.mensajes.mostrarMensajes());
        }   */

        //Realizamos el mensaje correspondientes
        this.mensajes.agregar("mensaje", "Se ha creado con exito el usuario.");
        this.mensajes.agregar("token", token);
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
