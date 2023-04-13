package cl.sterbe.apps.controladores;

import cl.sterbe.apps.componentes.ValidarCampos;
import cl.sterbe.apps.componentes.ValidarRun;
import cl.sterbe.apps.modelos.DTO.Perfil;
import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.PerfilServicio;
import cl.sterbe.apps.modelos.servicios.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/perfil/")
public class PerfilControlador {

    @Autowired
    private PerfilServicio perfilServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private ValidarRun validarRun;

    @Autowired
    private ValidarCampos validarCampos;

    @GetMapping("perfiles")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> buscarPerfiles(){

        //Atributos
        List<Perfil> perfiles = this.perfilServicio.findAll();
        Map<String, Object> mensajes = new HashMap<>();

        //Validar si la lista viene vacia de la base de datos
        if(perfiles.isEmpty()){
            mensajes.put("error", "No existen perfiles.");
            mensajes.put("perfiles", perfiles);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mensajes);
        }

        //Enviar mensaje de exito y los perfiles
        mensajes.put("exito", "Se han encontrado los perfiles.");
        mensajes.put("perfiles", perfiles);
        return ResponseEntity.status(HttpStatus.OK).body(mensajes);
    }

    @GetMapping("perfiles/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> buscarPerfil(@PathVariable Long id){

        //Atributos
        Perfil perfil = null;
        Map<String, Object> mensajes = new HashMap<>();
        Usuario usuarioAuthenticado = null;
        Authentication auth = null;

        //Validamos que el parametro supere o sea igual a 1
        if(id <= 0){
            mensajes.put("error", "no puedes enviar un parametro 0 o inferior");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Autenticacion del usuario
        auth = SecurityContextHolder.getContext().getAuthentication();
        usuarioAuthenticado = this.usuarioServicio.findOneByEmail(auth.getName());

        //Validamos al usuario
        if(!usuarioAuthenticado.getRol().getRol().equals("ROLE_ADMINISTRADOR")){
            if(id != usuarioAuthenticado.getId()){
                mensajes.put("denegado", "No tienes acceso al recurso solicitado");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
            }
        }

        //Buscamos el perfil en la base de datos
        perfil = this.perfilServicio.findById(id);

        //Validamos si exite el perfil en la base de datos
        if(perfil == null){
            mensajes.put("error", "No se encontro el recurso solicitado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes);
        }

        //enviar los mensajes de exito y el perfil
        mensajes.put("exito", "Se ha encontro con exito el perfil.");
        mensajes.put("perfil", perfil);
        return ResponseEntity.status(HttpStatus.OK).body(mensajes);
    }

    @PostMapping("perfiles")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> guardarPerfil(@Valid @RequestBody Perfil perfil, BindingResult bindingResult){

        //Atributos
        Map<String, Object> mensajes = new HashMap<>();
        Perfil perfilSave = null;
        Authentication auth = null;
        Usuario usuarioAuthenticado = null;

        //Validamos los campos vacios o nulos
        if(bindingResult.hasErrors()){
            mensajes.put("errores", this.validarCampos.validarCampos(bindingResult));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos si el run es correcto
        if(!this.validarRun.validarRun(perfil.getRun())){
            mensajes.put("Error", "Run no válido.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Autenticacion del usuario
        auth = SecurityContextHolder.getContext().getAuthentication();
        usuarioAuthenticado = this.usuarioServicio.findOneByEmail(auth.getName());

        if(this.perfilServicio.findOneByUsuario(usuarioAuthenticado) != null){
            mensajes.put("error", "El usuario ya tiene un perfil registrado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Agregamos el usuario correspondiente al perfil
        perfil.setUsuario(usuarioAuthenticado);

        //Agregamos el perfil a la base de datos
        try {
            perfilSave = this.perfilServicio.save(perfil);
        } catch (DataIntegrityViolationException e) {
            mensajes.put("error", "el run ya esta en uso");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Mandamos el mensaje de exito
        mensajes.put("Exito", "Se creo el perfil con exito.");
        mensajes.put("perfil", perfilSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }

    @PutMapping("perfiles/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> editarPerfil(@Valid @RequestBody Perfil perfil, BindingResult bindingResult, @PathVariable Long id){

        //Atributos
        Map<String, Object> mensajes = new HashMap<>();
        Perfil perfilBD = null;
        Perfil perfilSave = null;
        Authentication auth = null;
        Usuario usuarioAuthenticado = null;

        if(id <= 0) {
            mensajes.put("Error", "El parametro no debe ser 0 ni inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos los campos vacios o nulos
        if(bindingResult.hasErrors()){
            mensajes.put("errores", this.validarCampos.validarCampos(bindingResult));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos si el run existe
        if(!this.validarRun.validarRun(perfil.getRun())){
            mensajes.put("Error", "Run no válido.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Buscamos el perfil en la base de datos
        perfilBD = this.perfilServicio.findById(id);

        //Validamos si exite el perfil
        if(perfilBD == null){
            mensajes.put("Error", "No se encontro el perfil.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Autenticacion del usuario
        auth = SecurityContextHolder.getContext().getAuthentication();
        usuarioAuthenticado = this.usuarioServicio.findOneByEmail(auth.getName());

        //Validamos si el perfil corresponde con el usuario autenticado
        if(perfilBD.getUsuario().getId() != usuarioAuthenticado.getId()){
            mensajes.put("Denegado", "No estas autorizado a editar este recurso");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Actualizamos los datos
        perfilBD.setRun(perfil.getRun());
        perfilBD.setNombre(perfil.getNombre());
        perfilBD.setApellidoPaterno(perfil.getApellidoPaterno());
        perfilBD.setApellidoMaterno(perfil.getApellidoMaterno());
        perfilBD.setContacto(perfil.getContacto());
        perfilBD.setUpdateAt(new Date());

        //Como igual se puede actualizar el run de debe validar si exite o no
        try {
            perfilSave = this.perfilServicio.save(perfilBD);
        }catch (DataAccessException e){
            mensajes.put("Error", "El run ya esta en uso");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Actualizamos base de datos
        mensajes.put("Exito", "Se actualizo con exito perfil.");
        mensajes.put("perfil", perfilSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }
}
