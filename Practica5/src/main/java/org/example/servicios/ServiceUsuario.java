package org.example.servicios;

import org.example.DBService;
import org.example.clases.User;

public class ServiceUsuario extends DBService<User>  {

    private static ServiceUsuario instance;

    private ServiceUsuario(){
        super(User.class);
    }

    public static ServiceUsuario getInstancia(){
        if(instance==null){
            instance = new ServiceUsuario();
        }
        return instance;
    }

    public static String autentificarUsuario(User aux) {
        return "ADM";
    }
}
