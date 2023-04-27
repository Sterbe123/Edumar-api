package cl.sterbe.apps.controladores;

import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.componentes.UsuarioAutenticado;
import cl.sterbe.apps.componentes.ValidarCampos;
import cl.sterbe.apps.modelos.DTO.productos.Categoria;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import cl.sterbe.apps.modelos.servicios.productosSevicio.CategoriaServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CategoriaControlador {

    @Autowired
    private CategoriaServicio categoriaServicio;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

    @Autowired
    private ValidarCampos validarCampos;

    @Autowired
    private Mensaje mensajes;

    @GetMapping("/categorias")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listarCategorias(){

        //Atributos
        List<Categoria> categorias = this.categoriaServicio.findAll();
        Usuario usuarioAutenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Limpiar mensajes
        this.mensajes.limpiar();

        //Validamos
        if(!usuarioAutenticado.isEstado()){
            this.mensajes.agregar("error", "Su cuenta se encuentra temporalmente suspendida, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        if(!usuarioAutenticado.isVerificacion()){
            this.mensajes.agregar("error", "Su cuenta aun no se a verificado.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //filtramos las categorias disponibles
        categorias = categorias.stream().filter(c -> c.isEstado()).collect(Collectors.toList());

        //Validar si la lsita esta vacia
        if(categorias.isEmpty()){
            this.mensajes.agregar("error", "No se encontraron las categorias");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //mensajes de exito
        this.mensajes.agregar("exito","Se encontraron las categorias");
        this.mensajes.agregar("categorias", categorias);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("/categorias/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> mostrarCategoria(@PathVariable Long id){

        //Atributos
        Categoria categoria;
        Usuario usuarioAutenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Limpiar mensajes
        this.mensajes.limpiar();

        //Validamos
        if(!usuarioAutenticado.isEstado()){
            this.mensajes.agregar("error", "Su cuenta se encuentra temporalmente suspendida, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        if(!usuarioAutenticado.isVerificacion()){
            this.mensajes.agregar("error", "Su cuenta aun no se a verificado.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //Validar parametro
        if(id <= 0){
            this.mensajes.agregar("error", "El parametro no puede ser igual o inferior a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos la categoria
        categoria = this.categoriaServicio.findById(id);

        //Validamos la categoria
        if(categoria == null){
            this.mensajes.agregar("error", "No se encontro la categoria");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Validamos si esta disponible
        if(!categoria.isEstado()){
            this.mensajes.agregar("error", "Categoria no disponible");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //mensaje de exito
        this.mensajes.agregar("exito", "Se encontro la categoria");
        this.mensajes.agregar("categoria", categoria);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @PostMapping("/categorias")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> guardarCategoria(@Valid @RequestBody Categoria categoria, BindingResult result){

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validar campos de la categoria
        if(result.hasErrors()){
            this.mensajes.agregar("error", this.validarCampos.validarCampos(result));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Realizar la persistencia
        try{
            categoria.setEstado(true);
            categoria.setCreateAt(new Date());
            categoria = this.categoriaServicio.save(categoria);
        }catch (DataIntegrityViolationException e){
            this.mensajes.agregar("error", "El nombre de ya existe.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //Mensajes de exito
        this.mensajes.agregar("exito","Se guardo correctamente la categoria.");
        this.mensajes.agregar("categoria", categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PutMapping("/categorias/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> editarCategoria(@Valid @RequestBody Categoria categoria, BindingResult result,
                                             @PathVariable Long id){

        //Atributos
        Categoria categoriaBD;

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //validar parametros
        if(id <= 0){
            this.mensajes.agregar("error", "El parametro no debe ser menor o igual a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Validar campos
        if (result.hasErrors()){
            this.mensajes.agregar("error", this.validarCampos.validarCampos(result));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos la categoria en la base de datos
        categoriaBD = this.categoriaServicio.findById(id);

        //Validamos el categoria
        if(categoriaBD == null){
            this.mensajes.agregar("error", "No se encontro la categoria.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Actualizamos los datos
        categoriaBD.setNombre(categoria.getNombre());
        categoriaBD.setUpdateAt(new Date());

        //Hacemos la persistencia
        try{
            categoriaBD = this.categoriaServicio.save(categoriaBD);
        }catch (DataIntegrityViolationException e){
            this.mensajes.agregar("error", "El nombre de ya existe.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //Mensajes de exito
        this.mensajes.agregar("exito","Se guardo correctamente la categoria.");
        this.mensajes.agregar("categoria", categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PatchMapping("/categorias/deshabilitar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> deshabilitarCategoria(@PathVariable Long id){

        //Atributos
        Categoria categoriaBD;

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validar parametro
        if(id <= 0){
            this.mensajes.agregar("error", "El parametro no de ser inferior a 0 o igual");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos la categoria en la base de datos
        categoriaBD = this.categoriaServicio.findById(id);

        //Validamos la categoria
        if(categoriaBD == null){
            this.mensajes.agregar("error", "No se encontro la categoria.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Validamos que la categoria no este deshabilitada
        if(!categoriaBD.isEstado()){
            this.mensajes.agregar("error", "La categoria ya se encuentra deshabilitada");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //actualizamos la categoria
        try {
            categoriaBD.setEstado(false);
            categoriaBD = this.categoriaServicio.save(categoriaBD);
        }catch (DataIntegrityViolationException e){
            this.mensajes.agregar("error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //Mensajes de exito
        this.mensajes.agregar("exito", "Se deshabilito con extio la categoria.");
        this.mensajes.agregar("categoria", categoriaBD);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @PatchMapping("/categorias/habilitar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> habilitarCategoria(@PathVariable Long id){

        //Atributos
        Categoria categoriaBD;

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validar parametro
        if(id <= 0){
            this.mensajes.agregar("error", "El parametro no de ser inferior a 0 o igual");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //Buscamos la categoria en la base de datos
        categoriaBD = this.categoriaServicio.findById(id);

        //Validamos la categoria
        if(categoriaBD == null){
            this.mensajes.agregar("error", "No se encontro la categoria.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Validamos que la categoria no este deshabilitada
        if(categoriaBD.isEstado()){
            this.mensajes.agregar("error", "La categoria ya se encuentra habilitada");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //actualizamos la categoria
        try {
            categoriaBD.setEstado(true);
            categoriaBD = this.categoriaServicio.save(categoriaBD);
        }catch (DataIntegrityViolationException e){
            this.mensajes.agregar("error", e.getMessage() + " " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
        }

        //Mensajes de exito
        this.mensajes.agregar("exito", "Se deshabilito con extio la categoria.");
        this.mensajes.agregar("categoria", categoriaBD);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }
}
