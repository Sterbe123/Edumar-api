package cl.sterbe.apps.servicios.productosSevicio;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorListaVacia;
import cl.sterbe.apps.modelos.DTO.productos.Categoria;

import java.util.List;

public interface CategoriaServicio {

    List<Categoria> findAll() throws ErrorListaVacia;

    Categoria findById(Long id);

    Categoria save(Categoria categoria);

    void delete(Long id);
}
