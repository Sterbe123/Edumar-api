package cl.sterbe.apps.controladores;

import cl.sterbe.apps.componentes.Correos;
import cl.sterbe.apps.componentes.ValidarCampos;
import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.UsuarioServicio;
import cl.sterbe.apps.security.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

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
    private ValidarCampos validarCampos;

    @Autowired
    private Correos correos;

    @GetMapping("/verificacion-cuenta/{token}")
    public ResponseEntity<?> verificarCuenta(@PathVariable String token){

        //Atributos
        Usuario usuario;
        Map<String, Object> mensajes = new HashMap<>();
        Map<String, Object> mensajesToken = TokenUtils.verifyAuthenticationToken(token);
        boolean verificacion = false;

        if(mensajesToken.get("verificacion").toString().equals("true")){
            verificacion = true;
        }

        if(!verificacion){
            mensajes.put("error", "Su cuenta no pudo ser verificada, por favor reenvie el correo.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(mensajes);
        }

        //Atributos para el usuario verificado}
        Long id = Long.parseLong(mensajesToken.get("id").toString());
        String email = mensajesToken.get("email").toString();
        String rol = mensajesToken.get("role").toString();

        //Buscamos el usuario
        usuario = this.usuarioServicio.findOneByEmail(email).orElse(null);

        //Validaremos el usuario
        if(usuario == null){
            mensajes.put("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }else if(!usuario.getId().equals(id)){
            mensajes.put("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }else if(!usuario.getRol().getRol().equals(rol)){
            mensajes.put("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        } else if (usuario.isVerificacion()) {
            mensajes.put("error", "El usuario ya se encuantra verificado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos el usuario
        usuario.setVerificacion(true);
        usuario.setCheckAt(new Date());

        try {
            usuario = this.usuarioServicio.save(usuario);
            usuario.setContrasena("");
        }catch (DataAccessException e){
            mensajes.put("error", e.getMessage() + " " + e.getLocalizedMessage());
        }

        //Mensajes de exito
        mensajes.put("exito", "Se verifico el usuario correctamente.");
        mensajes.put("usuario", usuario);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(mensajes);
    }

    @GetMapping("/re-enviar-verificacion/{email}")
    public ResponseEntity<?> reEnviarToken(@PathVariable String email){

        //Atributos
        Map<String, Object> mensajes = new HashMap<>();
        String token;
        Usuario usuario;

        if(email.equals("")){
            mensajes.put("error", "El email es requerido");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Buscamos el usuario
        usuario = this.usuarioServicio.findOneByEmail(email).orElse(null);

        //validar si exite el usuario
        if(usuario == null){
            mensajes.put("error", "No se encontro el usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validar si ya esta autenticado
        if(usuario.isVerificacion()){
            mensajes.put("error", "El usuario ya esta verificado.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Generar nuevo token
        token = TokenUtils.crearTokenValidacionUsuario(usuario.getId(), usuario.getEmail(),
                Arrays.asList(new SimpleGrantedAuthority(usuario.getRol().getRol())));

        //Enviar el correo
        this.correos.enviarCorreoVerificacion(usuario.getEmail(), token);
        mensajes.put("exito", "Se envio el link de verificación a su correo.");

        return ResponseEntity.status(HttpStatus.OK).body(mensajes);
    }
}
