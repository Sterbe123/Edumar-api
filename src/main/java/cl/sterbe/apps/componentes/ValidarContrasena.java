package cl.sterbe.apps.componentes;

import org.springframework.stereotype.Component;

@Component
public class ValidarContrasena {

    /**
     * Validamos la contraseña
     * @param contrasena
     * @return
     */
    public boolean validarContrasena(String contrasena){

        boolean contieneMayusculas = false;
        boolean contieneMinusculas = false;
        boolean contieneNumeros = false;

        if(contrasena.length() >= 8){
            for(int i=0;i<contrasena.length();i++){

                //Validar si tiene un numero o mas
                try{
                    Integer.parseInt(contrasena.substring(i, (i+1)));
                    contieneNumeros = true;
                }catch (NumberFormatException e){
                    //Validar si contiene una mayúscula o más
                    if(contrasena.substring(i, (i+1)).equals(contrasena.substring(i, (i+1)).toUpperCase())){
                        contieneMayusculas = true;
                    }

                    //Validar si contiene una mayuscula o mas
                    if(contrasena.substring(i, (i+1)).equals(contrasena.substring(i, (i+1)).toLowerCase())){
                        contieneMinusculas = true;
                    }

                    //Validar si contriene una comilla
                    if(contrasena.substring(i, (i+1)).equals("'")){
                        return false;
                    }
                }
            }

            if(!contieneMayusculas){
                return false;
            }

            if(!contieneMinusculas){
                return false;
            }

            if(!contieneNumeros){
                return false;
            }

        }else{
            return false;
        }

        return true;
    }
}
