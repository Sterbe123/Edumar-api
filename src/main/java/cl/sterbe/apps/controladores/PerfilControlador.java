package cl.sterbe.apps.controladores;

import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.componentes.UsuarioAutenticado;
import cl.sterbe.apps.componentes.ValidarCampos;
import cl.sterbe.apps.componentes.ValidarRun;
import cl.sterbe.apps.modelos.DTO.usuarios.Direccion;
import cl.sterbe.apps.modelos.DTO.usuarios.Perfil;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import cl.sterbe.apps.modelos.servicios.usuariosServicio.DireccionServicio;
import cl.sterbe.apps.modelos.servicios.usuariosServicio.PerfilServicio;
import cl.sterbe.apps.modelos.servicios.usuariosServicio.UsuarioServicio;
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

    @Autowired
    private Mensaje mensajes;

    @GetMapping("perfiles")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> buscarPerfiles(){

        //Atributos
        List<Perfil> perfiles = this.perfilServicio.findAll();

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validar si la lista viene vacia de la base de datos
        if(perfiles.isEmpty()){
            this.mensajes.agregar("error", "No existen perfiles.");
            this.mensajes.agregar("perfiles", perfiles);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(this.mensajes.mostrarMensajes());
        }

        //Enviar mensaje de exito y los perfiles
        perfiles.forEach(p -> p.getUsuario().setContrasena(""));
        this.mensajes.agregar("exito", "Se han encontrado los perfiles.");
        this.mensajes.agregar("perfiles", perfiles);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("perfiles/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarPerfil(@PathVariable Long id){

        //Atributos
        Perfil perfil;
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

        //Validamos que el parametro supere o sea igual a 1
        if(id <= 0){
            this.mensajes.agregar("error", "no puedes enviar un parametro 0 o inferior");
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos el perfil en la base de datos
        perfil = this.perfilServicio.findById(id);

        //Validamos si exite el perfil en la base de datos
        if(perfil == null){
            this.mensajes.agregar("error", "No se encontro el recurso solicitado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Validamos al usuario
        if(!usuarioAuthenticado.getRol().getRol().equals("ROLE_ADMINISTRADOR")){
            if(!perfil.getUsuario().getId().equals(usuarioAuthenticado.getId())){
                this.mensajes.agregar("denegado", "No tienes acceso al recurso solicitado");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
            }
        }

        //enviar los mensajes de exito y el perfil
        perfil.getUsuario().setContrasena("");
        this.mensajes.agregar("exito", "Se ha encontro con exito el perfil.");
        this.mensajes.agregar("perfil", perfil);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @PostMapping("perfiles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> guardarPerfil(@Valid @RequestBody Perfil perfil, BindingResult bindingResult){

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

        //Validamos los campos vacios o nulos
        if(bindingResult.hasErrors()){
            this.mensajes.agregar("errores", this.validarCampos.validarCampos(bindingResult));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si el run es correcto
        if(!this.validarRun.validarRun(perfil.getRun())){
            this.mensajes.agregar("Error", "Run no válido.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si el usuario ya tienen un perfil
        if(this.perfilServicio.findOneByUsuario(usuarioAuthenticado) != null){
            this.mensajes.agregar("error", "El usuario ya tiene un perfil registrado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
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
            this.mensajes.agregar("error", "el run ya esta en uso");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //Mandamos el mensaje de exito
        this.mensajes.agregar("Exito", "Se creo el perfil con exito.");
        this.mensajes.agregar("perfil", perfil);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PutMapping("perfiles/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editarPerfil(@Valid @RequestBody Perfil perfil, BindingResult bindingResult,
                                          @PathVariable Long id){

        //Atributos
        Perfil perfilBD;
        Usuario usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Lipiaremos los mensajes
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

        if(id <= 0) {
            this.mensajes.agregar("Error", "El parametro no debe ser 0 ni inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos los campos vacios o nulos
        if(bindingResult.hasErrors()){
            this.mensajes.agregar("errores", this.validarCampos.validarCampos(bindingResult));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si el run existe
        if(!this.validarRun.validarRun(perfil.getRun())){
            this.mensajes.agregar("Error", "Run no válido.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos el perfil en la base de datos
        perfilBD = this.perfilServicio.findById(id);

        //Validamos si exite el perfil
        if(perfilBD == null){
            this.mensajes.agregar("Error", "No se encontro el perfil.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        if(!perfilBD.getUsuario().getId().equals(usuarioAuthenticado.getId())){
            this.mensajes.agregar("Denegado", "No estas autorizado a editar este recurso");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
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
            this.mensajes.agregar("Error", "El run ya esta en uso");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //Actualizamos base de datos
        this.mensajes.agregar("Exito", "Se actualizo con exito perfil.");
        this.mensajes.agregar("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PostMapping("perfiles/direcciones/{perfil_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> agregarDireccion(@Valid @RequestBody Direccion direccion, BindingResult bindingResult,
                                                @PathVariable(value = "perfil_id") Long id){

        //Atributos
        Perfil perfilBD;
        Usuario usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Limpiar mensajes
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

        //Validamos los parametros
        if(id <= 0) {
            this.mensajes.agregar("Error", "El parametro no debe ser 0 ni inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos los campos vacios o nulos
        if(bindingResult.hasErrors()){
            this.mensajes.agregar("errores", this.validarCampos.validarCampos(bindingResult));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos el perfil en la base de datos
        perfilBD = this.perfilServicio.findById(id);

        //Validamos si exite el perfil
        if(perfilBD == null){
            this.mensajes.agregar("Error", "No se encontro el perfil.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        if(!perfilBD.getUsuario().getId().equals(usuarioAuthenticado.getId())){
            this.mensajes.agregar("Denegado", "No estas autorizado a editar este recurso");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //seteamos las fecha de creacion
        direccion.setCreateAt(new Date());
        direccion.setPerfil(perfilBD);

        if(perfilBD.getDirecciones().size() == 5){
            this.mensajes.agregar("error", "Superaste el límite de direcciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
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
            this.mensajes.agregar("error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //Mandamos el mensaje de exito
        this.mensajes.agregar("exito", "Se agrego la direción con exito");
        this.mensajes.agregar("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PutMapping("perfiles/direcciones/{perfil_id}/{direccion_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editarDireccion(@Valid @RequestBody Direccion direccion, BindingResult bindingResult,
                                             @PathVariable(value = "perfil_id") Long perfilId,
                                             @PathVariable(value = "direccion_id") Long direccionId){

        //Atributos
        Perfil perfilBD;
        boolean direccionEncontrada = false;
        Usuario usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Limpiar mensajes
        this.mensajes.limpiar();

        //Validar si estas habilitado
        if(!usuarioAuthenticado.isEstado()){
            this.mensajes.agregar("error", "Tu cuenta se encuentra suspendido temporalmente, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //Validar si el usuario esta verificado
        if(!usuarioAuthenticado.isVerificacion()){
            this.mensajes.agregar("error", "Tu cuenta aun no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //Validamos que los parametros recibidos sean mayores que 0
        if(perfilId <= 0 && direccionId <= 0) {
            this.mensajes.agregar("Error", "El parametro no debe ser 0 ni inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos los campos de la direccion
        if(bindingResult.hasErrors()){
            this.mensajes.agregar("errores", this.validarCampos.validarCampos(bindingResult));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos el perfil en la base de datos
        perfilBD = this.perfilServicio.findById(perfilId);

        //Validamos si exite el perfil
        if(perfilBD == null){
            this.mensajes.agregar("Error", "No se encontro el perfil.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        if(!perfilBD.getUsuario().getId().equals(usuarioAuthenticado.getId())){
            this.mensajes.agregar("Denegado", "No estas autorizado a editar este recurso");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
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
            this.mensajes.agregar("Error", "Dirección no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        try {
            perfilBD = this.perfilServicio.save(perfilBD);
            perfilBD.getUsuario().setContrasena("");
        }catch (DataAccessException e){
            this.mensajes.agregar("Error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //Creamos el mensaje de exito
        this.mensajes.agregar("Exito", "Se actualizo la dirección con exito");
        this.mensajes.agregar("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @DeleteMapping("perfiles/direcciones/{perfil_id}/{direccion_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> eliminarDireccion(@PathVariable(value = "perfil_id") Long perfilId,
                                               @PathVariable(value = "direccion_id") Long direccionId){

        //Atributos
        Perfil perfilBD;
        boolean direccionEncontrada = false;
        Usuario usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Limpiar mensajes
        this.mensajes.limpiar();

        //Validar si estas habilitado
        if(!usuarioAuthenticado.isEstado()){
            this.mensajes.agregar("error", "Tu cuenta se encuentra suspendido temporalmente, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //Validar si el usuario esta verificado
        if(!usuarioAuthenticado.isVerificacion()){
            this.mensajes.agregar("error", "Tu cuenta aun no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //Validamos que los parametros recibidos sean mayores que 0
        if(perfilId <= 0 && direccionId <= 0) {
            this.mensajes.agregar("Error", "El parametro no debe ser 0 ni inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos el perfil en la base de datos
        perfilBD = this.perfilServicio.findById(perfilId);

        //Validamos si exite el perfil
        if(perfilBD == null){
            this.mensajes.agregar("Error", "No se encontro el perfil.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        if(!perfilBD.getUsuario().getId().equals(usuarioAuthenticado.getId())){
            this.mensajes.agregar("Denegado", "No estas autorizado a editar este recurso");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
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
            this.mensajes.agregar("error", "Dirección no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        try {
            perfilBD = this.perfilServicio.save(perfilBD);
            this.direccionServicio.delete(direccionId);
            perfilBD.getUsuario().setContrasena("");
        }catch (DataAccessException e){
            this.mensajes.agregar("Error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //Creamos el mensaje de exito
        this.mensajes.agregar("Exito", "Se elimino la dirección con exito");
        this.mensajes.agregar("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(this.mensajes.mostrarMensajes());
    }

    @PatchMapping("perfiles/direcciones/{perfil_id}/{direccion_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editarDireccionPrincipal(@PathVariable(value = "perfil_id") Long perfilId,
                                                      @PathVariable(value = "direccion_id") Long direccionId){

        //Atributos
        Perfil perfilBD;
        boolean direccionEncontrada = false;
        Usuario usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Limpiar mensajes
        this.mensajes.limpiar();

        //Validar si estas habilitado
        if(!usuarioAuthenticado.isEstado()){
            this.mensajes.agregar("error", "Tu cuenta se encuentra deshabilitada temporalmente, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //Validar si el usuario esta verificado
        if(!usuarioAuthenticado.isVerificacion()){
            this.mensajes.agregar("error", "Tu cuenta aun no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //Validamos los parametros
        if(perfilId <= 0 && direccionId <= 0){
            this.mensajes.agregar("error", "El parametro no debe ser inferior o igual a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos el perfil en la base de datos
        perfilBD = this.perfilServicio.findById(perfilId);

        //Validamos si exite el perfil
        if(perfilBD == null){
            this.mensajes.agregar("Error", "No se encontro el perfil.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        if(!perfilBD.getUsuario().getId().equals(usuarioAuthenticado.getId())){
            this.mensajes.agregar("Denegado", "No estas autorizado a editar este recurso");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
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
            this.mensajes.agregar("error", "Dirección no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        try {
            perfilBD = this.perfilServicio.save(perfilBD);
            perfilBD.getUsuario().setContrasena("");
        }catch (DataAccessException e){
            this.mensajes.agregar("Error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //Mensajes de exito
        this.mensajes.agregar("exito", "Se actualizo la dirección con exito.");
        this.mensajes.agregar("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }
}
