package cl.sterbe.apps.componentes;

import org.springframework.beans.factory.annotation.Autowired;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.stereotype.Component;

@Component
public class EncriptarYDesencriptar {

    @Autowired
    private StandardPBEStringEncryptor encryptor;

    public String encrypt(String data) {
        return this.encryptor.encrypt(data);
    }

    public String decrypt(String encryptedData) {
        return this.encryptor.decrypt(encryptedData);
    }
}
