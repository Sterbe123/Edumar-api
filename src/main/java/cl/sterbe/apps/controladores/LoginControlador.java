package cl.sterbe.apps.controladores;

import cl.sterbe.apps.modelos.DTO.Usuario;
import cl.sterbe.apps.modelos.servicios.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoginControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody Usuario usuario, BindingResult bindingResult){

        Map<String, Object> mensajes = new HashMap<>();
        Usuario usuarioNuevo = null;

        //Validamos los campos vacios o mal escritos en el e-mail
        if(bindingResult.hasErrors()){
            List<String> error = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> "El campo " + e.getField() + " " + e.getDefaultMessage())
                    .collect(Collectors.toList());
            mensajes.put("errores", error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensajes);
        }

        //Encriptar Informacion del usuario para la insersion en la base de datos---------------- se va hacer más adelante...

        //Realizamos la insercion a la base de datos
        try{
            usuarioNuevo = this.usuarioServicio.save(usuario);
        }catch (DataAccessException e){ //devolver el mensaje de error
            mensajes.put("mensaje", "Error al ingresar el usuario a la base de datos");
            mensajes.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajes);
        }

        //Desencriptar informacion para mostrar al usuario autenticado-------------------- se va hacer más adelante...

        //Realizamos el mensaje correspondientes
        mensajes.put("mensaje", "Se ha creado con exito el usuario");
        mensajes.put("usuario", usuarioNuevo);

        return ResponseEntity.status(HttpStatus.CREATED).body(mensajes);
    }
}
