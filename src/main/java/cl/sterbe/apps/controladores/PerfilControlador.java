package cl.sterbe.apps.controladores;

import cl.sterbe.apps.componentes.ValidarRun;
import cl.sterbe.apps.modelos.DTO.Perfil;
import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.PerfilServicio;
import cl.sterbe.apps.modelos.servicios.UsuarioServicio;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/perfil/")
public class PerfilControlador {

    @Autowired
    private PerfilServicio perfilServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private ValidarRun validarRun;

    private Logger logger = LoggerFactory.getLogger(PerfilControlador.class);

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

        //Validamos que el parametro supere o sea igual a 1
        if(id <= 0){
            mensajes.put("error", "no puedes enviar un parametro 0 o inferior");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Autenticacion del usuario
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuarioAuthenticado = this.usuarioServicio.findOneByEmail(email);

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

        //Validamos los campos vacios o nulos
        if(bindingResult.hasErrors()){
            List<String> error = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> "El campo " + e.getField() + " " + e.getDefaultMessage())
                    .collect(Collectors.toList());
            mensajes.put("errores", error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos si el run existe
        if(!this.validarRun.validarRun(perfil.getRun())){
            mensajes.put("Error", "Run no válido.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Autenticacion del usuario
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuarioAuthenticado = this.usuarioServicio.findOneByEmail(email);

        if(usuarioAuthenticado == null){
            mensajes.put("denegado", "Debe autenticarse con el sistema.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mensajes);
        }

        perfil.setUsuario(usuarioAuthenticado);

        try {
            perfilSave = this.perfilServicio.save(perfil);
        } catch (DataIntegrityViolationException e) {
            mensajes.put("error", "el run ya esta en uso");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        mensajes.put("Exito", "Se creo el perfil con exito.");
        mensajes.put("perfil", this.perfilServicio.save(perfilSave));
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }

    @PutMapping("perfiles/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or @userRepository.findById(#id)?.username == authentication.principal.username")
    public ResponseEntity<?> editarPerfil(@Valid @RequestBody Perfil perfil, BindingResult bindingResult, @PathVariable Long id){

        //Atributos
        Map<String, Object> mensajes = new HashMap<>();
        Perfil perfilBD = null;
        String email = "";
        Usuario usuario = null;

        if(id <= 0) {
            mensajes.put("Error", "El parametro no debe ser 0 ni inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        perfilBD = this.perfilServicio.findById(id);

        if(perfilBD == null){
            mensajes.put("Error", "No se encontro el perfil.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos los campos vacios o nulos
        if(bindingResult.hasErrors()){
            List<String> error = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> "El campo " + e.getField() + " " + e.getDefaultMessage())
                    .collect(Collectors.toList());
            mensajes.put("errores", error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos si el run existe
        if(!this.validarRun.validarRun(perfil.getRun())){
            mensajes.put("Error", "Run no válido.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Obtenemos el id del usuario autenticado por el sistema
    /*    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        email = ( (UserDetailsImple) authentication.getPrincipal()).getUsername();
        usuario = this.usuarioServicio.findOneByEmail(email).orElse(null);      */

        if(usuario == null){
            mensajes.put("denegado", "Debe autenticarse con el sistema.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mensajes);
        }

        //Actualizamos los datos
        perfilBD.setRun(perfil.getRun());
        perfilBD.setNombre(perfil.getNombre());
        perfilBD.setApellidoPaterno(perfil.getApellidoPaterno());
        perfilBD.setApellidoMaterno(perfil.getApellidoMaterno());
        perfilBD.setContacto(perfil.getContacto());


        //Actualizamos base de datos
        mensajes.put("Exito", "Se actualizo con exito perfil.");
        mensajes.put("perfil", this.perfilServicio.save(perfilBD));
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }

    @DeleteMapping("perfiles/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> eliminarPerfil(@PathVariable Long id){
        Map<String, Object> mensajes = new HashMap<>();
        Perfil perfilBD = null;

        if(id <= 0) {
            mensajes.put("Error", "El parametro no debe ser 0 ni inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        perfilBD = this.perfilServicio.findById(id);

        if(perfilBD == null){
            mensajes.put("Error", "No se encontro el perfil.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Eliminamos el perfil
        this.perfilServicio.delete(perfilBD.getId());

        //Mensajes
        mensajes.put("Exito", "Se elimino el perfil correctamente.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mensajes);
    }
}
