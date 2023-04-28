package cl.sterbe.apps.controladores;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoSeEncontroPojo;
import cl.sterbe.apps.componentes.Correos;
import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import cl.sterbe.apps.servicios.usuariosServicio.UsuarioServicio;
import cl.sterbe.apps.security.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.mail.SendFailedException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VerificacionCuentaControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private Correos correos;

    @Autowired
    private Mensaje mensajes;

    @GetMapping("/verificacion-cuenta/{token}")
    public ResponseEntity<?> verificarCuenta(@PathVariable String token){

        //Atributos
        Usuario usuario;
        Map<String, Object> mensajesToken = TokenUtils.verifyAuthenticationToken(token);
        boolean verificacion = false;

        //Validar verificacion
        if(mensajesToken.get("verificacion").toString().equals("true")){
            verificacion = true;
        }

        //Limpiar mensajes

        if(!verificacion){
            this.mensajes.agregar("error", "Su cuenta no pudo ser verificada, por favor reenvie el correo.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(this.mensajes.mostrarMensajes());
        }

        //Atributos para el usuario verificado}
        Long id = Long.parseLong(mensajesToken.get("id").toString());
        String email = mensajesToken.get("email").toString();
        String rol = mensajesToken.get("role").toString();

        //Buscamos el usuario
        usuario = this.usuarioServicio.findOneByEmail(email).orElse(null);

        //Validaremos el usuario
        if(usuario == null){
            this.mensajes.agregar("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }else if(!usuario.getId().equals(id)){
            this.mensajes.agregar("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }else if(!usuario.getRol().getRol().equals(rol)){
            this.mensajes.agregar("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        } else if (usuario.isVerificacion()) {
            this.mensajes.agregar("error", "El usuario ya se encuantra verificado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos el usuario
        usuario.setVerificacion(true);
        usuario.setCheckAt(new Date());


        //persistencia
        usuario = this.usuarioServicio.save(usuario);
        usuario.setContrasena("");

        //Mensajes de exito
        this.mensajes.agregar("exito", "Se verifico el usuario correctamente.");
        this.mensajes.agregar("usuario", usuario);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("/re-enviar-verificacion/{email}")
    public ResponseEntity<?> reEnviarToken(@PathVariable String email){

        //Atributos
        String token;
        Usuario usuario;

        if(email.equals("")){
            this.mensajes.agregar("error", "El email es requerido");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos el usuario
        usuario = this.usuarioServicio.findOneByEmail(email).orElseThrow(() -> new NoSeEncontroPojo("usuario"));

        //validar si exite el usuario
        if(usuario == null){
            this.mensajes.agregar("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validar si ya esta autenticado
        if(usuario.isVerificacion()){
            this.mensajes.agregar("error", "El usuario ya esta verificado.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Generar nuevo token
        token = TokenUtils.crearTokenValidacionUsuario(usuario.getId(), usuario.getEmail(),
                Arrays.asList(new SimpleGrantedAuthority(usuario.getRol().getRol())));

        //Enviar el correo
        try {
            this.correos.enviarCorreoVerificacion(usuario.getEmail(), token);
            this.mensajes.agregar("exito", "Se envio el link de verificación a su correo.");
        }catch (SendFailedException e){
            this.mensajes.agregar("excepciones", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(this.mensajes.mostrarMensajes());
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }
}
