package cl.sterbe.apps.modelos.DTO;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @Column(unique = true)
    @NotEmpty
    @NotNull
    @Email
    private String email;

    @NotNull
    @NotEmpty
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
}
