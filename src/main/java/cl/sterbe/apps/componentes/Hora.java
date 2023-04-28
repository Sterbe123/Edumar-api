package cl.sterbe.apps.componentes;

import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Hora {

    private ZonedDateTime zonedDateTime;


    private DateTimeFormatter dateTimeFormatter;


    private Hora(){
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    }

    public String codigoInterno(String categoria){
        this.zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Santiago"));
        String codigo = zonedDateTime.format(this.dateTimeFormatter);
        categoria = categoria.concat(codigo);
        return categoria;
    }
}
