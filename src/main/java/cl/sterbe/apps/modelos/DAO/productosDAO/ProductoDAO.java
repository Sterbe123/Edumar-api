package cl.sterbe.apps.modelos.DAO.productosDAO;

import cl.sterbe.apps.modelos.DTO.productos.Producto;
import org.springframework.data.repository.CrudRepository;

public interface ProductoDAO extends CrudRepository<Producto, Long> {
}
