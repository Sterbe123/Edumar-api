package cl.sterbe.apps.componentes;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidarCampos {

    /**
     * Valida los campos del modelo y retorna una lista de errores.
     * @param bindingResult
     * @return List<String>
     */
    public List<String> validarCampos(BindingResult bindingResult){
        return bindingResult.getFieldErrors()
                .stream()
                .map(e -> "El campo " + e.getField() + " " + e.getDefaultMessage())
                .collect(Collectors.toList());
    }
}
