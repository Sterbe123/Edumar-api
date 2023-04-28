package cl.sterbe.apps.componentes;

import cl.sterbe.apps.advice.exepcionesPersonalizadas.ErrorContrasena;
import org.springframework.stereotype.Component;

@Component
public class ValidarContrasena {

    /**
     *
     * @param contrasena
     * @throws ErrorContrasena
     */
    public void validarContrasena(String contrasena) throws ErrorContrasena {

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
                        throw new ErrorContrasena("No se permiten comillas simples");
                    }
                }
            }

            if(!contieneMayusculas){
                throw new ErrorContrasena("No contienen mayúsculas");
            }

            if(!contieneMinusculas){
                throw new ErrorContrasena("No contienen minúsculas");
            }

            if(!contieneNumeros){
                throw new ErrorContrasena("No contiene un número");
            }

        }else{
            throw new ErrorContrasena("Debe contener más de 8 catacter.");
        }
    }
}
