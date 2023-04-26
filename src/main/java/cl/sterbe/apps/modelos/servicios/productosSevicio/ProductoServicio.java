package cl.sterbe.apps.modelos.servicios.productosSevicio;

import cl.sterbe.apps.modelos.DTO.productos.Producto;

import java.util.List;

public interface ProductoServicio {

    List<Producto> findAll();

    Producto findById(Long id);

    Producto save(Producto producto);

    List<Producto> saveAll(List<Producto> productos);

    void delete(Long id);
}
