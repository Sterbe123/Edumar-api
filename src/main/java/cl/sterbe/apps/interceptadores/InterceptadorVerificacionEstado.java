package cl.sterbe.apps.interceptadores;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoEstaHabilitado;
import cl.sterbe.apps.advice.exepcionesPersonalizadas.NoEstaVerificado;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

public class InterceptadorVerificacionEstado implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, String> credenciales = (Map<String, String>) auth.getCredentials();

        if(credenciales.get("estado").equals("false")){
            throw new NoEstaHabilitado();
        }

        if (credenciales.get("verificacion").equals("false")){
            throw new NoEstaVerificado();
        }

        return true;
    }
}
