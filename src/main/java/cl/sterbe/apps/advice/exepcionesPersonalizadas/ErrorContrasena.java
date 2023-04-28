package cl.sterbe.apps.advice.exepcionesPersonalizadas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorContrasena extends Exception{

    private String mensaje;

    public ErrorContrasena(String mensaje){
        this.mensaje = mensaje;
    }
}
