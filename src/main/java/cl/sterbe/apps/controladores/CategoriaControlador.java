package cl.sterbe.apps.controladores;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoEstaHabilitado;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoEstaVerificado;
import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.componentes.UsuarioAutenticado;
import cl.sterbe.apps.modelos.DTO.productos.Categoria;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import cl.sterbe.apps.servicios.productosSevicio.CategoriaServicio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
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
    private Mensaje mensajes;

    @GetMapping("/categorias")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listarCategorias() throws NoEstaVerificado, NoEstaHabilitado {

        //Atributos
        List<Categoria> categorias = this.categoriaServicio.findAll();
        Usuario usuarioAutenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validamos usuario estado y verificacion
        this.usuarioAutenticado.autenticarUsuario();

        //filtramos las categorias disponibles
        categorias = categorias.stream().filter(c -> c.isEstado()).collect(Collectors.toList());

        //Limpiar mensajes
        this.mensajes.limpiar();

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
    public ResponseEntity<?> mostrarCategoria(@PathVariable Long id) throws NoEstaVerificado, NoEstaHabilitado {

        //Atributos
        Categoria categoria;
        Usuario usuarioAutenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validamos usuario estado y verificacion
        this.usuarioAutenticado.autenticarUsuario();

        //Buscamos la categoria
        categoria = this.categoriaServicio.findById(id);

        //Limpiar mensajes
        this.mensajes.limpiar();

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
    public ResponseEntity<?> guardarCategoria(@Valid @RequestBody Categoria categoria, BindingResult result) throws BindException {

        //Validar campos de la categoria
        if(result.hasErrors()){
            throw new BindException(result);
        }

        //Realizar la persistencia
        categoria = this.categoriaServicio.save(categoria);

        //Mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito","Se guardo correctamente la categoria.");
        this.mensajes.agregar("categoria", categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PutMapping("/categorias/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> editarCategoria(@Valid @RequestBody Categoria categoria, BindingResult result,
                                             @PathVariable Long id)
            throws BindException {

        //Atributos
        Categoria categoriaBD;

        //Validar campos
        if (result.hasErrors()){
            throw  new BindException(result);
        }

        //Buscamos la categoria en la base de datos
        categoriaBD = this.categoriaServicio.findById(id);

        //Actualizamos los datos
        categoriaBD.setNombre(categoria.getNombre());
        categoriaBD.setUpdateAt(new Date());

        //Hacemos la persistencia
        categoriaBD = this.categoriaServicio.save(categoriaBD);

        //Mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito","Se guardo correctamente la categoria.");
        this.mensajes.agregar("categoria", categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PatchMapping("/categorias/deshabilitar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> deshabilitarCategoria(@PathVariable Long id){

        //Atributos
        Categoria categoriaBD;

        //Buscamos la categoria en la base de datos
        categoriaBD = this.categoriaServicio.findById(id);

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validamos que la categoria no este deshabilitada
        if(!categoriaBD.isEstado()){
            this.mensajes.agregar("error", "La categoria ya se encuentra deshabilitada");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //actualizamos la categoria
        categoriaBD.setEstado(false);
        categoriaBD = this.categoriaServicio.save(categoriaBD);

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

        //Buscamos la categoria en la base de datos
        categoriaBD = this.categoriaServicio.findById(id);

        //Limpiamos los mensajes
        this.mensajes.limpiar();

        //Validamos que la categoria no este deshabilitada
        if(categoriaBD.isEstado()){
            this.mensajes.agregar("error", "La categoria ya se encuentra habilitada");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //actualizamos la categoria
        categoriaBD.setEstado(true);
        categoriaBD = this.categoriaServicio.save(categoriaBD);

        //Mensajes de exito
        this.mensajes.agregar("exito", "Se deshabilito con extio la categoria.");
        this.mensajes.agregar("categoria", categoriaBD);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }
}
