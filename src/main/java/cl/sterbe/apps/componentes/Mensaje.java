package cl.sterbe.apps.componentes;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Mensaje {

    private Map<String, Object> mensajes;

    public Mensaje(){
        this.mensajes = new HashMap<>();
    }

    public Map<String, Object> mostrarMensajes(){
        return this.mensajes;
    }

    public void limpiar(){
        this.mensajes.clear();
    }

    public void agregar(String llave, Object valor){
        this.mensajes.put(llave, valor);
    }
}
