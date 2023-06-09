package cl.sterbe.apps.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        AuthCredentials authCredentials = new AuthCredentials();

        try{
            authCredentials = new ObjectMapper().readValue(request.getReader(), AuthCredentials.class);
        }catch (IOException e){
            System.out.println(e.getMessage() + " " + e.getLocalizedMessage());
        }

        UsernamePasswordAuthenticationToken usernamePat = new UsernamePasswordAuthenticationToken(
                authCredentials.getEmail(),
                authCredentials.getContrasena(),
                authCredentials.getAuthorities()
        );

        return getAuthenticationManager().authenticate(usernamePat);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {

        UserDetailsImple userDetailsImple = (UserDetailsImple) authResult.getPrincipal();
        String token = TokenUtils.crearToken(userDetailsImple.getId(), userDetailsImple.getUsername(),
                userDetailsImple.getAuthorities(), userDetailsImple.isEstado(), userDetailsImple.isVerify());
        response.addHeader("Authorization", "Bearer " + token);
        response.getWriter().flush();

        super.successfulAuthentication(request, response, chain, authResult);
    }
}
