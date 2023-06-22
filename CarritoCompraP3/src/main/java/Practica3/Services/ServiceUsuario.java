package Practica3.Services;


import Practica3.Entidades.Usuario;

public class ServiceUsuario extends ServicioDBS<Usuario>{
    private static ServiceUsuario instancia;

    private ServiceUsuario(){
        super(Usuario.class);
    }

    public static ServiceUsuario getInstancia(){
        if(instancia ==null){
            instancia = new ServiceUsuario();
        }
        return instancia;
    }

    public static String autentificarUsuario(Usuario aux) {
        return "ADM";
    }

}
