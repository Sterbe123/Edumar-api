package cl.sterbe.apps.controladores;

import cl.sterbe.apps.componentes.UsuarioAutenticado;
import cl.sterbe.apps.componentes.ValidarCampos;
import cl.sterbe.apps.componentes.ValidarRun;
import cl.sterbe.apps.modelos.DTO.Direccion;
import cl.sterbe.apps.modelos.DTO.Perfil;
import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.DireccionServicio;
import cl.sterbe.apps.modelos.servicios.PerfilServicio;
import cl.sterbe.apps.modelos.servicios.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/")
public class PerfilControlador {

    @Autowired
    private PerfilServicio perfilServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private DireccionServicio direccionServicio;

    @Autowired
    private ValidarRun validarRun;

    @Autowired
    private ValidarCampos validarCampos;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

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
        perfiles.forEach(p -> p.getUsuario().setContrasena(""));
        mensajes.put("exito", "Se han encontrado los perfiles.");
        mensajes.put("perfiles", perfiles);
        return ResponseEntity.status(HttpStatus.OK).body(mensajes);
    }

    @GetMapping("perfiles/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarPerfil(@PathVariable Long id){

        //Atributos
        Perfil perfil;
        Map<String, Object> mensajes = new HashMap<>();
        Usuario usuarioAuthenticado;

        //Autenticacion del usuario
        usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar si estas habilitado
        if(!usuarioAuthenticado.isEstado()){
            mensajes.put("error", "Tu cuenta se encuentra deshabilitada temporalmente, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validar si el usuario esta verificado
        if(!usuarioAuthenticado.isVerificacion()){
            mensajes.put("error", "Tu cuenta aun no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validamos que el parametro supere o sea igual a 1
        if(id <= 0){
            mensajes.put("error", "no puedes enviar un parametro 0 o inferior");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Buscamos el perfil en la base de datos
        perfil = this.perfilServicio.findById(id);

        //Validamos si exite el perfil en la base de datos
        if(perfil == null){
            mensajes.put("error", "No se encontro el recurso solicitado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes);
        }

        //Validamos al usuario
        if(!usuarioAuthenticado.getRol().getRol().equals("ROLE_ADMINISTRADOR")){
            if(!perfil.getUsuario().getId().equals(usuarioAuthenticado.getId())){
                mensajes.put("denegado", "No tienes acceso al recurso solicitado");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
            }
        }

        //enviar los mensajes de exito y el perfil
        perfil.getUsuario().setContrasena("");
        mensajes.put("exito", "Se ha encontro con exito el perfil.");
        mensajes.put("perfil", perfil);
        return ResponseEntity.status(HttpStatus.OK).body(mensajes);
    }

    @PostMapping("perfiles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> guardarPerfil(@Valid @RequestBody Perfil perfil, BindingResult bindingResult){

        //Atributos
        Map<String, Object> mensajes = new HashMap<>();
        Usuario usuarioAuthenticado;

        //Autenticacion del usuario
        usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar si estas habilitado
        if(!usuarioAuthenticado.isEstado()){
            mensajes.put("error", "Tu cuenta se encuentra deshabilitada temporalmente, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validar si el usuario esta verificado
        if(!usuarioAuthenticado.isVerificacion()){
            mensajes.put("error", "Tu cuenta aun no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

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

        //Validamos si el usuario ya tienen un perfil
        if(this.perfilServicio.findOneByUsuario(usuarioAuthenticado) != null){
            mensajes.put("error", "El usuario ya tiene un perfil registrado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Agregamos el usuario correspondiente al perfil y fecha de registro
        perfil.setUsuario(usuarioAuthenticado);
        perfil.setCreateAt(new Date());
        perfil.setDirecciones(Arrays.asList());

        //Agregamos el perfil a la base de datos
        try {
            perfil = this.perfilServicio.save(perfil);
            perfil.getUsuario().setContrasena("");
        } catch (DataIntegrityViolationException e) {
            mensajes.put("error", "el run ya esta en uso");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Mandamos el mensaje de exito
        mensajes.put("Exito", "Se creo el perfil con exito.");
        mensajes.put("perfil", perfil);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }

    @PutMapping("perfiles/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editarPerfil(@Valid @RequestBody Perfil perfil, BindingResult bindingResult,
                                          @PathVariable Long id){

        //Atributos
        Map<String, Object> mensajes = new HashMap<>();
        Perfil perfilBD;
        Usuario usuarioAuthenticado;

        //Autenticacion del usuario
        usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar si estas habilitado
        if(!usuarioAuthenticado.isEstado()){
            mensajes.put("error", "Tu cuenta se encuentra deshabilitada temporalmente, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validar si el usuario esta verificado
        if(!usuarioAuthenticado.isVerificacion()){
            mensajes.put("error", "Tu cuenta aun no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

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

        //Validamos si el perfil corresponde con el usuario autenticado
        if(!perfilBD.getUsuario().getId().equals(usuarioAuthenticado.getId())){
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
            perfilBD = this.perfilServicio.save(perfilBD);
            perfilBD.getUsuario().setContrasena("");
        }catch (DataAccessException e){
            mensajes.put("Error", "El run ya esta en uso");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Actualizamos base de datos
        mensajes.put("Exito", "Se actualizo con exito perfil.");
        mensajes.put("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }

    @PostMapping("perfiles/direcciones/{perfil_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> agregarDireccion(@Valid @RequestBody Direccion direccion, BindingResult bindingResult,
                                                @PathVariable(value = "perfil_id") Long id){

        //Atributos
        Map<String, Object> mensajes = new HashMap<>();
        Perfil perfilBD;
        Usuario usuarioAuthenticado;

        //Autenticacion del usuario
        usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar si estas habilitado
        if(!usuarioAuthenticado.isEstado()){
            mensajes.put("error", "Tu cuenta se encuentra deshabilitada temporalmente, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validar si el usuario esta verificado
        if(!usuarioAuthenticado.isVerificacion()){
            mensajes.put("error", "Tu cuenta aun no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validamos los parametros
        if(id <= 0) {
            mensajes.put("Error", "El parametro no debe ser 0 ni inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos los campos vacios o nulos
        if(bindingResult.hasErrors()){
            mensajes.put("errores", this.validarCampos.validarCampos(bindingResult));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Buscamos el perfil en la base de datos
        perfilBD = this.perfilServicio.findById(id);

        //Validamos si exite el perfil
        if(perfilBD == null){
            mensajes.put("Error", "No se encontro el perfil.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        if(!perfilBD.getUsuario().getId().equals(usuarioAuthenticado.getId())){
            mensajes.put("Denegado", "No estas autorizado a editar este recurso");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //seteamos las fecha de creacion
        direccion.setCreateAt(new Date());
        direccion.setPerfil(perfilBD);

        if(perfilBD.getDirecciones().size() == 5){
            mensajes.put("error", "Superaste el límite de direcciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }else if(perfilBD.getDirecciones().isEmpty()){
            direccion.setPrincipal(true);
        }else{
            direccion.setPrincipal(false);
        }

        //Hacemos la insercion a la base de datos
        try {
            direccion = this.direccionServicio.save(direccion);
            perfilBD.getDirecciones().add(direccion);
            perfilBD = this.perfilServicio.save(perfilBD);
            perfilBD.getUsuario().setContrasena("");
        }catch (DataAccessException e){
            mensajes.put("error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Mandamos el mensaje de exito
        mensajes.put("exito", "Se agrego la direción con exito");
        mensajes.put("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }

    @PutMapping("perfiles/direcciones/{perfil_id}/{direccion_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editarDireccion(@Valid @RequestBody Direccion direccion, BindingResult bindingResult,
                                             @PathVariable(value = "perfil_id") Long perfilId,
                                             @PathVariable(value = "direccion_id") Long direccionId){

        //Atributos
        Map<String, Object> mensajes = new HashMap<>();
        Perfil perfilBD;
        Usuario usuarioAuthenticado;
        boolean direccionEncontrada = false;

        //Autenticacion del usuario
        usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar si estas habilitado
        if(!usuarioAuthenticado.isEstado()){
            mensajes.put("error", "Tu cuenta se encuentra deshabilitada temporalmente, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validar si el usuario esta verificado
        if(!usuarioAuthenticado.isVerificacion()){
            mensajes.put("error", "Tu cuenta aun no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validamos que los parametros recibidos sean mayores que 0
        if(perfilId <= 0 && direccionId <= 0) {
            mensajes.put("Error", "El parametro no debe ser 0 ni inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos los campos de la direccion
        if(bindingResult.hasErrors()){
            mensajes.put("errores", this.validarCampos.validarCampos(bindingResult));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Buscamos el perfil en la base de datos
        perfilBD = this.perfilServicio.findById(perfilId);

        //Validamos si exite el perfil
        if(perfilBD == null){
            mensajes.put("Error", "No se encontro el perfil.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        if(!perfilBD.getUsuario().getId().equals(usuarioAuthenticado.getId())){
            mensajes.put("Denegado", "No estas autorizado a editar este recurso");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Buscamos las direcciones en el perfil
        for(Direccion d: perfilBD.getDirecciones()){
            if(d.getId().equals(direccionId)){
                //Hacemos los cambios en la direccion
                d.setQuienRecibe(direccion.getQuienRecibe());
                d.setRegion(direccion.getRegion());
                d.setComuna(direccion.getComuna());
                d.setPoblacion(direccion.getPoblacion());
                d.setCalle(direccion.getCalle());
                d.setNumero(direccion.getNumero());
                d.setUpdateAt(new Date());
                direccionEncontrada = true;
                break;
            }
        }

        //Validamos di encontro la direccion en el perfil
        if(!direccionEncontrada){
            mensajes.put("Error", "Dirección no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes);
        }

        try {
            perfilBD = this.perfilServicio.save(perfilBD);
            perfilBD.getUsuario().setContrasena("");
        }catch (DataAccessException e){
            mensajes.put("Error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Creamos el mensaje de exito
        mensajes.put("Exito", "Se actualizo la dirección con exito");
        mensajes.put("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }

    @DeleteMapping("perfiles/direcciones/{perfil_id}/{direccion_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> eliminarDireccion(@PathVariable(value = "perfil_id") Long perfilId,
                                               @PathVariable(value = "direccion_id") Long direccionId){

        //Atributos
        Map<String, Object> mensajes = new HashMap<>();
        Perfil perfilBD;
        Usuario usuarioAuthenticado;
        boolean direccionEncontrada = false;

        //Autenticacion del usuario
        usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar si estas habilitado
        if(!usuarioAuthenticado.isEstado()){
            mensajes.put("error", "Tu cuenta se encuentra deshabilitada temporalmente, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validar si el usuario esta verificado
        if(!usuarioAuthenticado.isVerificacion()){
            mensajes.put("error", "Tu cuenta aun no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validamos que los parametros recibidos sean mayores que 0
        if(perfilId <= 0 && direccionId <= 0) {
            mensajes.put("Error", "El parametro no debe ser 0 ni inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Buscamos el perfil en la base de datos
        perfilBD = this.perfilServicio.findById(perfilId);

        //Validamos si exite el perfil
        if(perfilBD == null){
            mensajes.put("Error", "No se encontro el perfil.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        if(!perfilBD.getUsuario().getId().equals(usuarioAuthenticado.getId())){
            mensajes.put("Denegado", "No estas autorizado a editar este recurso");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Buscamos el id de la direccion para eliminar
        for(Direccion d: perfilBD.getDirecciones()){
            if(d.getId().equals(direccionId)){
                perfilBD.getDirecciones().remove(d);
                direccionEncontrada = true;
                break;
            }
        }

        //Validamos si no pudo encontrar la direccion
        if(!direccionEncontrada){
            mensajes.put("error", "Dirección no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes);
        }

        try {
            perfilBD = this.perfilServicio.save(perfilBD);
            this.direccionServicio.delete(direccionId);
            perfilBD.getUsuario().setContrasena("");
        }catch (DataAccessException e){
            mensajes.put("Error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Creamos el mensaje de exito
        mensajes.put("Exito", "Se elimino la dirección con exito");
        mensajes.put("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mensajes);
    }

    @PatchMapping("perfiles/direcciones/{perfil_id}/{direccion_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editarDireccionPrincipal(@PathVariable(value = "perfil_id") Long perfilId,
                                                      @PathVariable(value = "direccion_id") Long direccionId){

        //Atributos
        Map<String, Object> mensajes = new HashMap<>();
        Perfil perfilBD;
        Usuario usuarioAuthenticado;
        boolean direccionEncontrada = false;

        //Autenticacion del usuario
        usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar si estas habilitado
        if(!usuarioAuthenticado.isEstado()){
            mensajes.put("error", "Tu cuenta se encuentra deshabilitada temporalmente, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validar si el usuario esta verificado
        if(!usuarioAuthenticado.isVerificacion()){
            mensajes.put("error", "Tu cuenta aun no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validamos los parametros
        if(perfilId <= 0 && direccionId <= 0){
            mensajes.put("error", "El parametro no debe ser inferior o igual a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Buscamos el perfil en la base de datos
        perfilBD = this.perfilServicio.findById(perfilId);

        //Validamos si exite el perfil
        if(perfilBD == null){
            mensajes.put("Error", "No se encontro el perfil.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        if(!perfilBD.getUsuario().getId().equals(usuarioAuthenticado.getId())){
            mensajes.put("Denegado", "No estas autorizado a editar este recurso");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Actualizamos la direccion principal
        for(Direccion d: perfilBD.getDirecciones()){
            if(d.getId().equals(direccionId)){
                d.setPrincipal(true);
                d.setUpdateAt(new Date());
                direccionEncontrada = true;
            }else{
                d.setPrincipal(false);
            }
        }

        if(!direccionEncontrada){
            mensajes.put("error", "Dirección no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes);
        }

        try {
            perfilBD = this.perfilServicio.save(perfilBD);
            perfilBD.getUsuario().setContrasena("");
        }catch (DataAccessException e){
            mensajes.put("Error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Mensajes de exito
        mensajes.put("exito", "Se actualizo la dirección con exito.");
        mensajes.put("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }
}
