package cl.sterbe.apps.servicios.productosSevicio;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorListaVacia;
import cl.sterbe.apps.modelos.DTO.productos.Producto;

import java.util.List;

public interface ProductoServicio {

    List<Producto> findAll() throws ErrorListaVacia;

    Producto findById(Long id);

    Producto save(Producto producto);

    List<Producto> saveAll(List<Producto> productos);

    void delete(Long id);

    Producto findOneByCodigoInterno(String codigoInterno);

    Producto findOneByCodigoBarra(String codigoBarra);
}
