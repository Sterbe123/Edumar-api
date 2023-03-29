package cl.sterbe.apps.modelos.DTO;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String contrasena;

    private boolean estado;

    @OneToOne
    @JoinColumn(name = "rol_id", referencedColumnName = "id")
    private Rol rol;

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
