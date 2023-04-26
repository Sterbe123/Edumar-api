package cl.sterbe.apps.modelos.servicios.productosSevicio.implementacion;

import cl.sterbe.apps.modelos.DAO.productosDAO.CategoriaDAO;
import cl.sterbe.apps.modelos.DTO.productos.Categoria;
import cl.sterbe.apps.modelos.servicios.productosSevicio.CategoriaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaImplementacion implements CategoriaServicio {

    @Autowired
    private CategoriaDAO categoriaDAO;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findAll() {
        return (List<Categoria>) this.categoriaDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria findById(Long id) {
        return this.categoriaDAO.findById(id).orElse(null);
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
