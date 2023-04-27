package cl.sterbe.apps.controladores;

import cl.sterbe.apps.componentes.Mensaje;
import cl.sterbe.apps.componentes.UsuarioAutenticado;
import cl.sterbe.apps.modelos.DTO.productos.Producto;
import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import cl.sterbe.apps.modelos.servicios.productosSevicio.ProductoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/productos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> productos(){

        //Atributos
        List<Producto> productos = this.productoServicio.findAll();
        Usuario usuarioAutenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Limpiar mensajes

        //Validar estado del usuario
        if(!usuarioAutenticado.isEstado()){
            this.mensajes.agregar("error", "Su cuenta se encuentra suspendida, contacte con el adminsitrador");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //Validar si el usuario esta verificado
        if(!usuarioAutenticado.isVerificacion()){
            this.mensajes.agregar("error", "Su cuenta todavia no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

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
    public ResponseEntity<?> producto(@PathVariable Long id){

        //Atributos
        Producto productoBD;
        Usuario usuarioAutenticado = this.usuarioAutenticado.getUsuarioAutenticado();

        //Limpiar mensajes
        this.mensajes.limpiar();

        //Validar estado del usuario
        if(!usuarioAutenticado.isEstado()){
            this.mensajes.agregar("error", "Su cuenta se encuentra suspendida, contacte con el adminsitrador");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //Validar si el usuario esta verificado
        if(!usuarioAutenticado.isVerificacion()){
            this.mensajes.agregar("error", "Su cuenta todavia no esta verificada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
        }

        //Validamos los parametros
        if(id <= 0){
            this.mensajes.agregar("error", "El parametro no debe ser inferior o igual a 0.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
        }

        //buscamos el producto en la base de datos
        productoBD = this.productoServicio.findById(id);

        //Validamos el producto
        if(productoBD == null){
            this.mensajes.agregar("error", "No se encontro el producto.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
        }

        //Mensajes de exito
        this.mensajes.agregar("exito", "Se encontro el producto correctamente.");
        this.mensajes.agregar("producto", productoBD);
        return ResponseEntity.status(HttpStatus.OK).body(this.mensajes.mostrarMensajes());
    }
}
