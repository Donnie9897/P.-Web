package org.example.servicios;

import org.example.DBService;
import org.example.clases.User;

public class ServicioUsuario extends DBService<User>  {

    private static ServicioUsuario instance;

    private ServicioUsuario(){
        super(User.class);
    }

    public static ServicioUsuario getInstancia(){
        if(instance==null){
            instance = new ServicioUsuario();
        }
        return instance;
    }

    public static String autentificarUsuario(User aux) {
        return "ADM";
    }
}
