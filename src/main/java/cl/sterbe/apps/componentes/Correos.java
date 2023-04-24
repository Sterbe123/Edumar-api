package cl.sterbe.apps.componentes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class Correos {

    @Autowired
    private JavaMailSender javaMailSender;

    public void enviarCorreoVerificacion(String email, String token) {

        SimpleMailMessage mensaje = new SimpleMailMessage();
        String texto = "Bienvenido, porfavor hacer click al siguiente enlace para la validación de la cuenta \n" +
                "Link: http://localhost:8080/api/verificacion-cuenta/" + token + "\n\n" +
                "Si no pediste un reenvio de verificación, por favor ignorar este correo.";

        mensaje.setFrom("noreply@baeldung.com");
        mensaje.setTo(email);
        mensaje.setSubject("Mensaje de verificación de usuario");
        mensaje.setText(texto);
        javaMailSender.send(mensaje);
    }
}
