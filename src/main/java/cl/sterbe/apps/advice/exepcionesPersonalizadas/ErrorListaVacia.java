package cl.sterbe.apps.advice.exepcionesPersonalizadas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorListaVacia extends Exception{

    private String nombre;

    public ErrorListaVacia(String nombre){
        this.nombre = nombre;
    }
}
