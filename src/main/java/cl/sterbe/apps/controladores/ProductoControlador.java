package cl.sterbe.apps.controladores;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorListaVacia;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoEstaHabilitado;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoEstaVerificado;
import cl.sterbe.apps.componentes.Hora;
import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.componentes.UsuarioAutenticado;
import cl.sterbe.apps.modelos.DTO.productos.Categoria;
import cl.sterbe.apps.modelos.DTO.productos.Producto;
import cl.sterbe.apps.servicios.productosSevicio.CategoriaServicio;
import cl.sterbe.apps.servicios.productosSevicio.ProductoServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProductoControlador {

    @Autowired
    private ProductoServicio productoServicio;

    @Autowired
    private CategoriaServicio categoriaServicio;

    @Autowired
    private Mensaje mensajes;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

    @Autowired
    private Hora hora;

    @GetMapping("/productos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> productos() throws NoEstaVerificado, NoEstaHabilitado, ErrorListaVacia {

        //Validar estado del usuario y verificaicon
        this.usuarioAutenticado.autenticarUsuario();
        this.usuarioAutenticado.verificarUsuario();

        //Mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se encontraron los productos.");
        this.mensajes.agregar("productos", this.productoServicio.findAll());
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("/productos/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> producto(@PathVariable Long id)
            throws NoEstaVerificado, NoEstaHabilitado {

        //Validar estado del usuario y verificaion
        this.usuarioAutenticado.autenticarUsuario();
        this.usuarioAutenticado.verificarUsuario();

        //Mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se encontro el producto correctamente.");
        this.mensajes.agregar("producto", this.productoServicio.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("/productos/codigo-interno/{codigoInterno}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') OR hasRole('ROLE_TRABAJADOR')")
    public ResponseEntity<Map<String, Object>> buscarPorCodigointerno(@PathVariable String codigoInterno)
            throws NoEstaVerificado, NoEstaHabilitado {

        //Validar estado y verificion
        this.usuarioAutenticado.autenticarUsuario();
        this.usuarioAutenticado.verificarUsuario();

        //Mensajes
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se encontro el producto correctamente.");
        this.mensajes.agregar("producto", this.productoServicio.findOneByCodigoInterno(codigoInterno));
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("/productos/codigo-barra/{codigoBarra}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') OR hasRole('ROLE_TRABAJADOR')")
    public ResponseEntity<Map<String, Object>> buscarPorCodigoBarra(@PathVariable String codigoBarra)
            throws NoEstaVerificado, NoEstaHabilitado {

        //Validar estado y verificion
        this.usuarioAutenticado.autenticarUsuario();
        this.usuarioAutenticado.verificarUsuario();

        //Mensajes
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se encontro el producto correctamente.");
        this.mensajes.agregar("producto", this.productoServicio.findOneByCodigoBarra(codigoBarra));
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @PostMapping("/productos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') OR hasRole('ROLE_TRABAJADOR')")
    public ResponseEntity<Map<String, Object>> guardar(@Valid @RequestBody Producto producto , BindingResult bindingResult)
            throws NoEstaVerificado, NoEstaHabilitado, BindException {

        //Validar estado y verificacion
        this.usuarioAutenticado.autenticarUsuario();
        this.usuarioAutenticado.verificarUsuario();

        if(bindingResult.hasErrors()){
            throw  new BindException(bindingResult);
        }

        //Establecer código interno
        producto.setCodigoInterno(this.hora.codigoInterno(producto.getCategoria().getNombre().substring(0, 3)));

        //mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se agrego el producto correctamente.");
        this.mensajes.agregar("producto", this.productoServicio.save(producto));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PutMapping("/productos/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') OR hasRole('ROLE_TRABAJADOR')")
    public ResponseEntity<Map<String, Object>> editar(@Valid @RequestBody Producto producto , BindingResult bindingResult,
                                    @PathVariable Long id)
            throws NoEstaVerificado, NoEstaHabilitado, BindException {

        //Validar estado y verificacion
        this.usuarioAutenticado.autenticarUsuario();
        this.usuarioAutenticado.verificarUsuario();

        //Atributos
        Producto productoBD;

        if(bindingResult.hasErrors()){
            throw  new BindException(bindingResult);
        }

        //Buscar producto en la base de datos
        productoBD = this.productoServicio.findById(id);

        //Actualizamos el producto
        productoBD.setNombre(producto.getNombre());
        productoBD.setDescripcion(producto.getDescripcion());
        productoBD.setPrecio(producto.getPrecio());
        productoBD.setStock(producto.getStock());
        productoBD.setCodigoBarra(producto.getCodigoBarra());
        productoBD.setCodigoInterno(producto.getCodigoInterno());
        productoBD.setUpdateAt(new Date());

        //mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se actualizo el producto correctamente.");
        this.mensajes.agregar("producto", this.productoServicio.save(productoBD));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PutMapping("/productos/cambiar-categoria/{id-producto}/{id-categoria}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> editarProductoCategoria(@PathVariable(value = "id-producto") Long idProducto,
                                                     @PathVariable(value = "id-categoria") Long idCategoria){

        //Atributos
        Producto productoBD;
        Categoria categoriaBD;

        //Buscamos el producto y categoria en la base de datos
        productoBD = this.productoServicio.findById(idProducto);
        categoriaBD = this.categoriaServicio.findById(idCategoria);

        //Cambiamos la categoria
        productoBD.setCategoria(categoriaBD);

        //mensajes
        this.mensajes.agregar("exito", "se actualizo la categoria del producto");
        this.mensajes.agregar("producto", this.productoServicio.save(productoBD));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @DeleteMapping("/productos/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id){

        //Atributos
        Producto productoBD;

        //Buscamos el producto en la base de datos
        productoBD = this.productoServicio.findById(id);

        //eliminamos el producto
        this.productoServicio.delete(productoBD.getId());

        //mensajes
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se elimino el producto correctamente.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(this.mensajes.mostrarMensajes());
    }
}
