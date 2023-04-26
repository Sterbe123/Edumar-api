package cl.sterbe.apps.modelos.servicios.productosSevicio;

import cl.sterbe.apps.modelos.DTO.productos.Categoria;

import java.util.List;

public interface CategoriaServicio {

    List<Categoria> findAll();

    Categoria findById(Long id);

    Categoria save(Categoria categoria);

    void delete(Long id);
}
