package cl.sterbe.apps.controladores;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.*;
import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.componentes.UsuarioAutenticado;
import cl.sterbe.apps.componentes.ValidarRun;
import cl.sterbe.apps.modelos.DTO.usuarios.Direccion;
import cl.sterbe.apps.modelos.DTO.usuarios.Perfil;
import cl.sterbe.apps.servicios.usuariosServicio.DireccionServicio;
import cl.sterbe.apps.servicios.usuariosServicio.PerfilServicio;
import cl.sterbe.apps.servicios.usuariosServicio.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
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
    private UsuarioAutenticado usuarioAutenticado;

    @Autowired
    private Mensaje mensajes;

    @GetMapping("perfiles")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> buscarPerfiles() throws ErrorListaVacia {

        List<Perfil> perfiles = this.perfilServicio.findAll();
        perfiles.forEach(perfil -> perfil.getUsuario().setContrasena(""));

        //Enviar mensaje de exito y los perfiles
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se han encontrado los perfiles.");
        this.mensajes.agregar("perfiles", perfiles);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("perfiles/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> buscarPerfil(@PathVariable Long id){

        //Buscamos el perfil en la base de datos
        Perfil perfil = this.perfilServicio.findById(id);

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validamos al usuario
        if(!this.usuarioAutenticado.getUsuarioAutenticado().getRol().getRol().equals("ROLE_ADMINISTRADOR")){
            if(!perfil.getUsuario().getId().equals(this.usuarioAutenticado.getUsuarioAutenticado().getId())){
                this.mensajes.agregar("denegado", "No tienes acceso al recurso solicitado");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
            }
        }

        //editar contraseña
        perfil.getUsuario().setContrasena("");

        //enviar los mensajes de exito y el perfil
        this.mensajes.agregar("exito", "Se ha encontro con exito el perfil.");
        this.mensajes.agregar("perfil", perfil);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @PostMapping("perfiles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> guardarPerfil(@Valid @RequestBody Perfil perfil, BindingResult bindingResult)
            throws BindException, ErrorRun, ErrorEditarRecurso {

        //Validamos los campos vacios o nulos
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        //Validamos si el run es correcto
        this.validarRun.validarRun(perfil.getRun());

        //Validamos si el usuario ya tienen un perfil
        this.perfilServicio.findOneByUsuario(this.usuarioAutenticado.getUsuarioAutenticado());

        //Agregamos el usuario correspondiente al perfil
        perfil.setUsuario(this.usuarioAutenticado.getUsuarioAutenticado());
        perfil.setDirecciones(Arrays.asList());

        //Agregamos el perfil a la base de datos
        perfil = this.perfilServicio.save(perfil);
        perfil.getUsuario().setContrasena("");

        //Mandamos el mensaje de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("Exito", "Se creo el perfil con exito.");
        this.mensajes.agregar("perfil", perfil);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PutMapping("perfiles/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> editarPerfil(@Valid @RequestBody Perfil perfil, BindingResult bindingResult,
                                          @PathVariable Long id)
            throws BindException, ErrorRun, ErrorEditarRecurso {

        //Buscamos el perfil en la base de datos
        Perfil perfilBD = this.perfilServicio.findById(id);

        //Validamos los campos vacios o nulos
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        //Validamos si el run existe
        this.validarRun.validarRun(perfil.getRun());

        //Validamos si el perfil corresponde con el usuario autenticado
        this.usuarioAutenticado.autenticarEditarRecurso(perfilBD.getUsuario().getId());

        //Actualizamos los datos
        perfilBD.setRun(perfil.getRun());
        perfilBD.setNombre(perfil.getNombre());
        perfilBD.setApellidoPaterno(perfil.getApellidoPaterno());
        perfilBD.setApellidoMaterno(perfil.getApellidoMaterno());
        perfilBD.setContacto(perfil.getContacto());
        perfilBD.setUpdateAt(new Date());

        //Como igual se puede actualizar el run de debe validar si exite o no
        perfilBD = this.perfilServicio.save(perfilBD);
        perfilBD.getUsuario().setContrasena("");

        //Actualizamos base de datos
        this.mensajes.limpiar();
        this.mensajes.agregar("Exito", "Se actualizo con exito perfil.");
        this.mensajes.agregar("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PostMapping("perfiles/direcciones/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> agregarDireccion(@Valid @RequestBody Direccion direccion, BindingResult bindingResult,
                                                @PathVariable Long id)
            throws BindException, ErrorEditarRecurso {

        //Buscamos el perfil en la base de datos
        Perfil perfilBD = this.perfilServicio.findById(id);

        //Validamos los campos vacios o nulos
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        this.usuarioAutenticado.autenticarEditarRecurso(perfilBD.getUsuario().getId());

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
        direccion = this.direccionServicio.save(direccion);
        perfilBD.getDirecciones().add(direccion);
        perfilBD = this.perfilServicio.save(perfilBD);
        perfilBD.getUsuario().setContrasena("");

        //Mandamos el mensaje de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se agrego la direción con exito");
        this.mensajes.agregar("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PutMapping("perfiles/direcciones/{perfil_id}/{direccion_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> editarDireccion(@Valid @RequestBody Direccion direccion, BindingResult bindingResult,
                                             @PathVariable(value = "perfil_id") Long perfilId,
                                             @PathVariable(value = "direccion_id") Long direccionId)
            throws BindException, ErrorEditarRecurso {

        //Atributos
        boolean direccionEncontrada = false;

        //Buscamos el perfil en la base de datos
        Perfil perfilBD = this.perfilServicio.findById(perfilId);

        //Limpiar mensajes
        this.mensajes.limpiar();

        //Validamos que los parametros recibidos sean mayores que 0
        if(direccionId <= 0) {
            this.mensajes.agregar("Error", "El parametro no debe ser 0 ni inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos los campos de la direccion
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        this.usuarioAutenticado.autenticarEditarRecurso(perfilBD.getUsuario().getId());

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

        //Persistencia en el perfil
        perfilBD = this.perfilServicio.save(perfilBD);
        perfilBD.getUsuario().setContrasena("");

        //Creamos el mensaje de exito
        this.mensajes.agregar("Exito", "Se actualizo la dirección con exito");
        this.mensajes.agregar("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @DeleteMapping("perfiles/direcciones/{perfil_id}/{direccion_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> eliminarDireccion(@PathVariable(value = "perfil_id") Long perfilId,
                                               @PathVariable(value = "direccion_id") Long direccionId)
            throws ErrorEditarRecurso {

        //Atributos
        boolean direccionEncontrada = false;

        //Buscamos el perfil en la base de datos
        Perfil perfilBD = this.perfilServicio.findById(perfilId);

        //Limpiar mensajes
        this.mensajes.limpiar();

        //Validamos que los parametros recibidos sean mayores que 0
        if(direccionId <= 0) {
            this.mensajes.agregar("Error", "El parametro no debe ser 0 ni inferior");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        this.usuarioAutenticado.autenticarEditarRecurso(perfilBD.getUsuario().getId());

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

        //Persistencia en el perfil
        perfilBD = this.perfilServicio.save(perfilBD);
        perfilBD.getUsuario().setContrasena("");
        this.direccionServicio.delete(direccionId);

        //Creamos el mensaje de exito
        this.mensajes.agregar("Exito", "Se elimino la dirección con exito");
        this.mensajes.agregar("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(this.mensajes.mostrarMensajes());
    }

    @PatchMapping("perfiles/direcciones/{perfil_id}/{direccion_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> editarDireccionPrincipal(@PathVariable(value = "perfil_id") Long perfilId,
                                                      @PathVariable(value = "direccion_id") Long direccionId)
            throws ErrorEditarRecurso {

        //Atributos
        boolean direccionEncontrada = false;

        //Buscamos el perfil en la base de datos
        Perfil perfilBD = this.perfilServicio.findById(perfilId);

        //Limpiar mensajes
        this.mensajes.limpiar();

        //Validamos los parametros
        if(direccionId <= 0){
            this.mensajes.agregar("error", "El parametro no debe ser inferior o igual a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si el perfil corresponde con el usuario autenticado
        this.usuarioAutenticado.autenticarEditarRecurso(perfilBD.getUsuario().getId());

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

        //Persistencia en el perfil
        perfilBD = this.perfilServicio.save(perfilBD);
        perfilBD.getUsuario().setContrasena("");

        //Mensajes de exito
        this.mensajes.agregar("exito", "Se actualizo la dirección con exito.");
        this.mensajes.agregar("perfil", perfilBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }
}
