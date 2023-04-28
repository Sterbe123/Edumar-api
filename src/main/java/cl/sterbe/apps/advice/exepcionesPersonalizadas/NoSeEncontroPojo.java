package cl.sterbe.apps.advice.exepcionesPersonalizadas;

import lombok.Getter;
import lombok.Setter;

import java.util.NoSuchElementException;

@Getter
@Setter
public class NoSeEncontroPojo extends NoSuchElementException {

    private String nombreClase;

    public NoSeEncontroPojo(String nombreClase) {
        this.nombreClase = nombreClase;
    }
}
