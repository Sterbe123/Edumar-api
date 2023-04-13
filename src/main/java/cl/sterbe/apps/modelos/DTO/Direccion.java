package cl.sterbe.apps.modelos.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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

    @Column(name = "quien_recibe")
    private String quienRecibe;

    @Column(name = "region_id")
    private int region;

    @Column(name = "comuna_id")
    private int comuna;

    private String poblacion;

    private String calle;

    private String numero;

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

    @Temporal(TemporalType.DATE)
    @Column(name = "delete_at")
    private Date deleteAt;

    @PrePersist
    public void prePersit(){
        this.createAt = new Date();
    }
}
