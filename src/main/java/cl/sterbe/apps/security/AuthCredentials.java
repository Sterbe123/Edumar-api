package cl.sterbe.apps.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class AuthCredentials {

    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

}
