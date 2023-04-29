package cl.sterbe.apps.controladores;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoEstaHabilitado;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoEstaVerificado;
import cl.sterbe.apps.componentes.Hora;
import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.componentes.UsuarioAutenticado;
import cl.sterbe.apps.modelos.DTO.productos.Producto;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
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
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductoControlador {

    @Autowired
    private ProductoServicio productoServicio;

    @Autowired
    private Mensaje mensajes;

    @Autowired
    private UsuarioAutenticado usuarioAutenticado;

    @Autowired
    private Hora hora;

    @GetMapping("/productos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> productos() throws NoEstaVerificado, NoEstaHabilitado {

        //Atributos
        List<Producto> productos = this.productoServicio.findAll();
        Usuario usuarioAutenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar estado del usuario y verificaicon
        this.usuarioAutenticado.autenticarUsuario();

        //Limpiar mensajes
        this.mensajes.limpiar();

        //Validar si la lista se encuentra vacia
        if(productos.isEmpty()){
            this.mensajes.agregar("error", "No se encontraron productos.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Mensajes de exito
        this.mensajes.agregar("exito", "Se encontraron los productos.");
        this.mensajes.agregar("productos", productos);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("/productos/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> producto(@PathVariable Long id)
            throws NoEstaVerificado, NoEstaHabilitado {

        //Atributos
        Producto productoBD;
        Usuario usuarioAutenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar estado del usuario y verificaion
        this.usuarioAutenticado.autenticarUsuario();

        //buscamos el producto en la base de datos
        productoBD = this.productoServicio.findById(id);

        //Mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se encontro el producto correctamente.");
        this.mensajes.agregar("producto", productoBD);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("/productos/codigo-interno/{codigoInterno}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') OR hasRole('ROLE_TRABAJADOR')")
    public ResponseEntity<?> buscarPorCodigointerno(@PathVariable String codigoInterno)
            throws NoEstaVerificado, NoEstaHabilitado {

        //Atributos
        Producto productoBD;
        Usuario usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar estado y verificion
        this.usuarioAutenticado.autenticarUsuario();

        //Buscamos el producto en la base de datos
        productoBD = this.productoServicio.findOneByCodigoInterno(codigoInterno);

        //Mensajes
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se encontro el producto correctamente.");
        this.mensajes.agregar("producto", productoBD);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @GetMapping("/productos/codigo-barra/{codigoBarra}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') OR hasRole('ROLE_TRABAJADOR')")
    public ResponseEntity<?> buscarPorCodigoBarra(@PathVariable String codigoBarra)
            throws NoEstaVerificado, NoEstaHabilitado {

        //Atributos
        Producto productoBD;
        Usuario usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar estado y verificion
        this.usuarioAutenticado.autenticarUsuario();

        //Buscar producto en la base de datos
        productoBD = this.productoServicio.findOneByCodigoBarra(codigoBarra);

        //Mensajes
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se encontro el producto correctamente.");
        this.mensajes.agregar("producto", productoBD);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }

    @PostMapping("/productos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') OR hasRole('ROLE_TRABAJADOR')")
    public ResponseEntity<?> guardar(@Valid @RequestBody Producto producto , BindingResult bindingResult)
            throws NoEstaVerificado, NoEstaHabilitado, BindException {

        //Atributos
        Usuario usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar estado y verificacion
        this.usuarioAutenticado.autenticarUsuario();

        if(bindingResult.hasErrors()){
            throw  new BindException(bindingResult);
        }

        //Establecer código interno
        producto.setCodigoInterno(this.hora.codigoInterno(producto.getCategoria().getNombre().substring(0, 3)));

        //persistencia
        producto = this.productoServicio.save(producto);

        //mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se agrego el producto correctamente.");
        this.mensajes.agregar("producto", producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @PutMapping("/productos/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') OR hasRole('ROLE_TRABAJADOR')")
    public ResponseEntity<?> editar(@Valid @RequestBody Producto producto , BindingResult bindingResult,
                                    @PathVariable Long id)
            throws NoEstaVerificado, NoEstaHabilitado, BindException {

        //Atributos
        Producto productoBD;
        Usuario usuarioAuthenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Validar estado y verificacion
        this.usuarioAutenticado.autenticarUsuario();

        if(bindingResult.hasErrors()){
            throw  new BindException(bindingResult);
        }

        //Establecer código interno
        producto.setCodigoInterno(this.hora.codigoInterno(producto.getCategoria().getNombre().substring(0, 3)));

        //Buscar producto en la base de datos
        productoBD = this.productoServicio.findById(id);

        //Actualizamos el producto
        productoBD.setNombre(producto.getNombre());
        productoBD.setDescripcion(producto.getDescripcion());
        productoBD.setPrecio(producto.getPrecio());
        productoBD.setStock(producto.getStock());
        productoBD.setCategoria(producto.getCategoria());
        productoBD.setCodigoBarra(producto.getCodigoBarra());
        productoBD.setCodigoInterno(producto.getCodigoInterno());
        productoBD.setUpdateAt(new Date());

        //persistencia
        productoBD = this.productoServicio.save(productoBD);

        //mensajes de exito
        this.mensajes.limpiar();
        this.mensajes.agregar("exito", "Se actualizo el producto correctamente.");
        this.mensajes.agregar("producto", productoBD);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.mensajes.mostrarMensajes());
    }

    @DeleteMapping("/productos/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> eliminar(@PathVariable Long id){

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
