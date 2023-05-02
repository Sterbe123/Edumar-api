package cl.sterbe.apps.configuraciones;

import cl.sterbe.apps.interceptadores.InterceptadorVerificacionEstado;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptadoresConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InterceptadorVerificacionEstado())
                .excludePathPatterns("/login")
                .excludePathPatterns("/api/registro/{id}")
                .excludePathPatterns("/api/verificacion-cuenta/{token}")
                .excludePathPatterns("/api/re-enviar-verificacion/{email}");
    }
}
