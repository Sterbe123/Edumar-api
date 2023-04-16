package cl.sterbe.apps.modelos.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "direcciones")
public class Direccion {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Column(name = "quien_recibe")
    private String quienRecibe;

    @NotNull
    @Column(name = "region_id")
    private int region;

    @NotNull
    @Column(name = "comuna_id")
    private int comuna;

    @NotNull
    @NotEmpty
    private String poblacion;

    @NotNull
    @NotEmpty
    private String calle;

    @NotNull
    @NotEmpty
    private String numero;

    private boolean principal;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "perfiles_id", referencedColumnName = "id")
    private Perfil perfil;

    @Temporal(TemporalType.DATE)
    @Column(name = "create_at")
    private Date createAt;

    @Temporal(TemporalType.DATE)
    @Column(name = "update_at")
    private Date updateAt;
}
