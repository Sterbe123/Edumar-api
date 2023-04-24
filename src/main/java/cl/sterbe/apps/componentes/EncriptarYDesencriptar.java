package cl.sterbe.apps.componentes;

import org.springframework.beans.factory.annotation.Autowired;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.stereotype.Component;

@Component
public class EncriptarYDesencriptar {

    @Autowired
    private StandardPBEStringEncryptor encryptor;

    /**
     * Sirve para encriptar una cadena de texto
     * @param texto
     * @return
     */
    public String encriptar(String texto) {
        return this.encryptor.encrypt(texto);
    }

    /**
     * Sirve para desencriptar un cadena de texto encriptada
     * @param textoEncriptador
     * @return
     */
    public String desencriptar(String textoEncriptador) {
        return this.encryptor.decrypt(textoEncriptador);
    }
}
