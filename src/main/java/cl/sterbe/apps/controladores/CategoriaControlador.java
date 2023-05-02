package cl.sterbe.apps.controladores;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorListaVacia;
import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.componentes.UsuarioAutenticado;
import cl.sterbe.apps.modelos.DTO.productos.Categoria;
import cl.sterbe.apps.servicios.productosSevicio.CategoriaServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    private Mensaje mensajes;

    @GetMapping("/categorias")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> listarCategorias()
            throws ErrorListaVacia {

        //Atributos
        List<Categoria> categorias = this.categoriaServicio.findAll();
        categorias = categorias.stream().filter(Categoria::isEstado).collect(Collectors.toList());

        //mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito","Se encontraron las categorias");
        this.mensajes.agregar("categorias", categorias);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("/categorias/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> mostrarCategoria(@PathVariable Long id){

        //mensaje de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se encontro la categoria");
        this.mensajes.agregar("categoria", this.categoriaServicio.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @PostMapping("/categorias")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> guardarCategoria(@Valid @RequestBody Categoria categoria, BindingResult result)
            throws BindException {

        //Validar campos de la categoria
        if(result.hasErrors()){
            throw new BindException(result);
        }

        //Mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito","Se guardo correctamente la categoria.");
        this.mensajes.agregar("categoria", this.categoriaServicio.save(categoria));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PutMapping("/categorias/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> editarCategoria(@Valid @RequestBody Categoria categoria, BindingResult result,
                                             @PathVariable Long id)
            throws BindException {

        //Buscamos la categoria en la base de datos
        Categoria categoriaBD = this.categoriaServicio.findById(id);

        //Validar campos
        if (result.hasErrors()){
            throw  new BindException(result);
        }

        //Actualizamos los datos
        categoriaBD.setNombre(categoria.getNombre());
        categoriaBD.setUpdateAt(new Date());

        //Mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito","Se guardo correctamente la categoria.");
        this.mensajes.agregar("categoria", this.categoriaServicio.save(categoriaBD));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PatchMapping("/categorias/deshabilitar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> deshabilitarCategoria(@PathVariable Long id){

        //Buscamos la categoria en la base de datos
        Categoria categoriaBD = this.categoriaServicio.findById(id);

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validamos que la categoria no este deshabilitada
        if(!categoriaBD.isEstado()){
            this.mensajes.agregar("error", "La categoria ya se encuentra deshabilitada");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //actualizamos la categoria
        categoriaBD.setEstado(false);

        //Mensajes de exito
        this.mensajes.agregar("exito", "Se deshabilito con extio la categoria.");
        this.mensajes.agregar("categoria", this.categoriaServicio.save(categoriaBD));
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @PatchMapping("/categorias/habilitar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> habilitarCategoria(@PathVariable Long id){

        //Buscamos la categoria en la base de datos
        Categoria categoriaBD = this.categoriaServicio.findById(id);

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validamos que la categoria no este deshabilitada
        if(categoriaBD.isEstado()){
            this.mensajes.agregar("error", "La categoria ya se encuentra habilitada");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //actualizamos la categoria
        categoriaBD.setEstado(true);

        //Mensajes de exito
        this.mensajes.agregar("exito", "Se deshabilito con extio la categoria.");
        this.mensajes.agregar("categoria", this.categoriaServicio.save(categoriaBD));
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }
}
