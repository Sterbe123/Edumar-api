package cl.sterbe.apps.modelos.servicios.productosSevicio.implementacion;

import cl.sterbe.apps.modelos.DAO.productosDAO.ProductoDAO;
import cl.sterbe.apps.modelos.DTO.productos.Producto;
import cl.sterbe.apps.modelos.servicios.productosSevicio.ProductoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoImplementacion implements ProductoServicio {

    @Autowired
    private ProductoDAO productoDAO;

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return (List<Producto>) this.productoDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findById(Long id) {
        return this.productoDAO.findById(id).orElse(null);
    }

    @Override
    public Producto save(Producto producto) {
        return this.productoDAO.save(producto);
    }

    @Override
    public List<Producto> saveAll(List<Producto> productos) {
        return (List<Producto>) this.productoDAO.saveAll(productos);
    }

    @Override
    public void delete(Long id) {
        this.productoDAO.deleteById(id);
    }
}
