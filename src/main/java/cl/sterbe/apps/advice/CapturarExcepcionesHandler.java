package cl.sterbe.apps.advice;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.*;
import cl.sterbe.apps.componentes.Mensaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CapturarExcepcionesHandler {

    @Autowired
    private Mensaje mensajes;

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(DataAccessException ex) {

        //Limpiar mensajes
        this.mensajes.limpiar();

        //mensajes
        this.mensajes.agregar("error", "Ocurrió un error al acceder a la base de datos: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> capturarBindResult(BindException ex) {

        //Limpiar mensajes
        this.mensajes.limpiar();

        List<String> errores = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> "El campo '" + error.getField() + "' " + error.getDefaultMessage())
                    .collect(Collectors.toList());

        //Agregar Mensajes
        this.mensajes.agregar("errores", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarExcepcionDeIntegridad(DataIntegrityViolationException e) {

        //Limpiar mensajes
        this.mensajes.limpiar();

        //mensajes
        this.mensajes.agregar("error", "Un campo se esta repitendo, debe de ser único.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(NoEstaHabilitado.class)
    public ResponseEntity<Map<String, Object>> noEstaHabilitado(NoEstaHabilitado e){

        //Limpiar mensaes
        this.mensajes.limpiar();

        //mensajes
        this.mensajes.agregar("Error", "Su cuenta se encuentra suspendida, Contactar con el administrador");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(NoEstaVerificado.class)
    public ResponseEntity<Map<String, Object>> noEstaVerificado(NoEstaVerificado e){

        //Limpiar mensajes
        this.mensajes.limpiar();

        //mensajes
        this.mensajes.agregar("error", "Su cuenta no se encuentra verificada.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(NoSeEncontroPojo.class)
    public ResponseEntity<Map<String, Object>> pojoNull(NoSeEncontroPojo e){

        //Limpiar mensajes
        this.mensajes.limpiar();

        //mensajes
        this.mensajes.agregar("error", "No se encontro " + e.getNombreClase());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> parametroInvalido(MethodArgumentTypeMismatchException e){
        //Limpiar mensajes
        this.mensajes.limpiar();

        //mensajes
        this.mensajes.agregar("error", "Parametro inválido");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(ErrorContrasena.class)
    public ResponseEntity<Map<String, Object>> errorContrasena(ErrorContrasena e){
        //Limpiar mensajes
        this.mensajes.limpiar();

        this.mensajes.agregar("error", "Error en la contraseña");
        this.mensajes.agregar("condición", e.getMensaje());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(ErrorRun.class)
    public ResponseEntity<Map<String, Object>> errorRun(ErrorRun e){
        //Limpiar mensajes
        this.mensajes.limpiar();

        this.mensajes.agregar("error", "Run incorrecto.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(ErrorPerfilRegistrado.class)
    public ResponseEntity<Map<String, Object>> errorPerfilRegistrado(ErrorPerfilRegistrado e){
        //Limpiar mensajes
        this.mensajes.limpiar();

        this.mensajes.agregar("error", "El usuario ya tiene un perfil registrado.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(ErrorEditarRecurso.class)
    public ResponseEntity<Map<String, Object>> errorEditarRecursos(ErrorEditarRecurso e){
        //Limpiar mensajes
        this.mensajes.limpiar();

        this.mensajes.agregar("error", "No estas autorizado para editar este recurso.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<Map<String, Object>> errorAutenticar(InternalAuthenticationServiceException e){
        //Limpiar mensajes
        this.mensajes.limpiar();

        this.mensajes.agregar("error", "Usuario no registrado o autenticado");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(ErrorListaVacia.class)
    public ResponseEntity<Map<String, Object>> errorListaVacia(ErrorListaVacia e){
        //Limpiar mensajes
        this.mensajes.limpiar();

        this.mensajes.agregar("error", "No se encontraron " + e.getNombre());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> nullPointException(NullPointerException e){
        this.mensajes.limpiar();
        this.mensajes.agregar("error", "null");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.mensajes.mostrarMensajes());
    }
}
