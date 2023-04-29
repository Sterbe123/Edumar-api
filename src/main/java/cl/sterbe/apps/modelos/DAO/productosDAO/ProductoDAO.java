package cl.sterbe.apps.modelos.DAO.productosDAO;

import cl.sterbe.apps.modelos.DTO.productos.Producto;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductoDAO extends CrudRepository<Producto, Long> {

    Optional<Producto> findOneByCodigoInterno(String codigoInterno);

    Optional<Producto> findOneByCodigoBarra(String codigoBarra);
}
