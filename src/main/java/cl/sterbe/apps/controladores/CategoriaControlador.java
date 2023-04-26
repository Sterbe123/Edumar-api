package cl.sterbe.apps.controladores;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/categorias")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listarCategorias(){

        //Atributos
        List<Categoria> categorias = this.categoriaServicio.findAll();
        Map<String, Object> mensajes = new HashMap<>();
        Usuario usuarioAutenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validamos
        if(!usuarioAutenticado.isEstado()){
            mensajes.put("error", "Su cuenta se encuentra temporalmente suspendida, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        if(!usuarioAutenticado.isVerificacion()){
            mensajes.put("error", "Su cuenta aun no se a verificado.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //filtramos las categorias disponibles
        categorias = categorias.stream().filter(c -> c.isEstado() == true).collect(Collectors.toList());

        //Validar si la lsita esta vacia
        if(categorias.isEmpty()){
            mensajes.put("error", "No se encontraron las categorias");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes);
        }

        //mensajes de exito
        mensajes.put("exito","Se encontraron las categorias");
        mensajes.put("categorias", categorias);
        return ResponseEntity.status(HttpStatus.OK).body(mensajes);
    }

    @GetMapping("/categorias/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> mostrarCategoria(@PathVariable Long id){

        //Atributos
        Categoria categoria;
        Map<String, Object> mensajes = new HashMap<>();
        Usuario usuarioAutenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validamos
        if(!usuarioAutenticado.isEstado()){
            mensajes.put("error", "Su cuenta se encuentra temporalmente suspendida, contacte con el administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        if(!usuarioAutenticado.isVerificacion()){
            mensajes.put("error", "Su cuenta aun no se a verificado.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes);
        }

        //Validar parametro
        if(id <= 0){
            mensajes.put("error", "El parametro no puede ser igual o inferior a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Buscamos la categoria
        categoria = this.categoriaServicio.findById(id);

        //Validamos la categoria
        if(categoria == null){
            mensajes.put("error", "No se encontro la categoria");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes);
        }

        //Validamos si esta disponible
        if(!categoria.isEstado()){
            mensajes.put("error", "Categoria no disponible");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes);
        }

        //mensaje de exito
        mensajes.put("exito", "Se encontro la categoria");
        mensajes.put("categoria", categoria);
        return ResponseEntity.status(HttpStatus.OK).body(mensajes);
    }

    @PostMapping("/categorias")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> guardarCategoria(@Valid @RequestBody Categoria categoria, BindingResult result){

        //Atributos
        Map<String, Object> mensajes = new HashMap<>();

        //Validar campos de la categoria
        if(result.hasErrors()){
            mensajes.put("error", this.validarCampos.validarCampos(result));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Realizar la persistencia
        try{
            categoria.setEstado(true);
            categoria.setCreateAt(new Date());
            categoria = this.categoriaServicio.save(categoria);
        }catch (DataIntegrityViolationException e){
            mensajes.put("error", "El nombre de ya existe.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Mensajes de exito
        mensajes.put("exito","Se guardo correctamente la categoria.");
        mensajes.put("categoria", categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }

    @PutMapping("/categorias/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> editarCategoria(@Valid @RequestBody Categoria categoria, BindingResult result,
                                             @PathVariable Long id){

        //Atributos
        Map<String, Object> mensajes = new HashMap<>();
        Categoria categoriaBD;

        //validar parametros
        if(id <= 0){
            mensajes.put("error", "El parametro no debe ser menor o igual a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Validar campos
        if (result.hasErrors()){
            mensajes.put("error", this.validarCampos.validarCampos(result));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Buscamos la categoria en la base de datos
        categoriaBD = this.categoriaServicio.findById(id);

        //Validamos el categoria
        if(categoriaBD == null){
            mensajes.put("error", "No se encontro la categoria.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes);
        }

        //Actualizamos los datos
        categoriaBD.setNombre(categoria.getNombre());
        categoriaBD.setUpdateAt(new Date());

        //Hacemos la persistencia
        try{
            categoriaBD = this.categoriaServicio.save(categoriaBD);
        }catch (DataIntegrityViolationException e){
            mensajes.put("error", "El nombre de ya existe.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Mensajes de exito
        mensajes.put("exito","Se guardo correctamente la categoria.");
        mensajes.put("categoria", categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }
}
