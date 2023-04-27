package cl.sterbe.apps.modelos.DTO.productos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @NotNull
    private String nombre;

    @NotEmpty
    @NotNull
    private String descripcion;

    @NotEmpty
    @NotNull
    private String precio;

    @NotNull
    private int stock;

    @OneToOne
    @JoinColumn(name = "categoria_id", referencedColumnName = "id")
    private Categoria categoria;

    @NotNull
    @NotEmpty
    @Column(name = "codigo_barra", unique = true)
    private String codigoBarra;

    @Column(name = "codigo_interno", unique = true)
    private String codigoInterno;

    @Temporal(TemporalType.DATE)
    @Column(name = "create_at")
    private Date createAt;

    @Temporal(TemporalType.DATE)
    @Column(name = "update_at")
    private Date updateAt;
}
