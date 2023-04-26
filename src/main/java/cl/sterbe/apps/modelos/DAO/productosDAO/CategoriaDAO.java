package cl.sterbe.apps.modelos.DAO.productosDAO;

import cl.sterbe.apps.modelos.DTO.productos.Categoria;
import org.springframework.data.repository.CrudRepository;

public interface CategoriaDAO extends CrudRepository<Categoria, Long> {
}
