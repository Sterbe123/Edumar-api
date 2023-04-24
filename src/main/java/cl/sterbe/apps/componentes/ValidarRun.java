package cl.sterbe.apps.componentes;

import org.springframework.stereotype.Component;

@Component
public class ValidarRun {

    /**
     * Si el run es correcto es true.
     * @param run
     * @return boolean
     */
    public boolean validarRun(String run){
        int digitoVerificado;
        int suma = 0;
        int multiplicar = 2;
        int resultado;

        try{
            if(run.length() == 10 || run.length() == 9){
                if(run.toLowerCase().substring((run.length() - 1)).equals("k")){
                    digitoVerificado = 10;
                }else if(run.substring((run.length() - 1)).equals("0")){
                    digitoVerificado = 11;
                }else{
                    digitoVerificado = Integer.parseInt(run.substring((run.length() - 1)));
                }

                for(int i=(run.length() - 3);i>=0;i--){
                    suma += (Integer.parseInt(run.substring(i, (i+1))) * multiplicar);
                    multiplicar++;

                    if(multiplicar == 8){
                        multiplicar = 2;
                    }
                }

                resultado = (int) Math.ceil(suma%11);

                resultado = (11 - resultado);

                if(resultado == digitoVerificado){
                    return true;
                }
            }
        }catch (NumberFormatException e){
            return false;
        }

        return false;
    }
}
