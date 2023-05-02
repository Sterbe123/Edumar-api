package cl.sterbe.apps.security;

import cl.sterbe.apps.modelos.DTO.usuarios.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@AllArgsConstructor
@Getter
@Setter
public class UserDetailsImple implements UserDetails {

  private final Usuario USUARIO;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(USUARIO.getRol().getRol()));
    }

    @Override
    public String getPassword() {
        return USUARIO.getContrasena();
    }

    @Override
    public String getUsername() {
        return USUARIO.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId(){
        return USUARIO.getId();
    }

    public boolean isVerify(){
        return USUARIO.isVerificacion();
    }

    public boolean isEstado(){
        return USUARIO.isEstado();
    }
}
