package cl.sterbe.apps.servicios.productosSevicio.implementacion;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorListaVacia;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoSeEncontroPojo;
import cl.sterbe.apps.modelos.DAO.productosDAO.CategoriaDAO;
import cl.sterbe.apps.modelos.DTO.productos.Categoria;
import cl.sterbe.apps.servicios.productosSevicio.CategoriaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaImplementacion implements CategoriaServicio {

    @Autowired
    private CategoriaDAO categoriaDAO;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findAll() throws ErrorListaVacia {
        return Optional.of((List<Categoria>) this.categoriaDAO.findAll())
                .filter(c -> !c.isEmpty())
                .orElseThrow(() -> new ErrorListaVacia("categorias"));
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria findById(Long id) {
        return this.categoriaDAO.findById(id).orElseThrow(() -> new NoSeEncontroPojo("categoria"));
    }

    @Override
    public Categoria save(Categoria categoria) {
        return this.categoriaDAO.save(categoria);
    }

    @Override
    public void delete(Long id) {
        this.categoriaDAO.deleteById(id);
    }
}
