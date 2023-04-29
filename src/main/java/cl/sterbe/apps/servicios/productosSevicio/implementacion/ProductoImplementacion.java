package cl.sterbe.apps.servicios.productosSevicio.implementacion;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorListaVacia;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoSeEncontroPojo;
import cl.sterbe.apps.modelos.DAO.productosDAO.ProductoDAO;
import cl.sterbe.apps.modelos.DTO.productos.Producto;
import cl.sterbe.apps.servicios.productosSevicio.ProductoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoImplementacion implements ProductoServicio {

    @Autowired
    private ProductoDAO productoDAO;

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() throws ErrorListaVacia {
        return Optional.of((List<Producto>) this.productoDAO.findAll())
                .filter(p -> !p.isEmpty())
                .orElseThrow(() -> new ErrorListaVacia("productos"));
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findById(Long id) {
        return this.productoDAO.findById(id)
                .orElseThrow(() -> new NoSeEncontroPojo("producto"));
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

    @Override
    @Transactional(readOnly = true)
    public Producto findOneByCodigoInterno(String codigoInterno) {
        return this.productoDAO.findOneByCodigoInterno(codigoInterno)
                .orElseThrow(() -> new NoSeEncontroPojo("producto"));
    }

    @Override
    public Producto findOneByCodigoBarra(String codigoBarra) {
        return this.productoDAO.findOneByCodigoBarra(codigoBarra)
                .orElseThrow(() -> new NoSeEncontroPojo("producto"));
    }
}
