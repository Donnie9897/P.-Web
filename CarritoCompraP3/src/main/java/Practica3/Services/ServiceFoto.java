package Practica3.Services;

import Practica3.Entidades.Foto;

public class ServiceFoto extends ServicioDBS<Foto>{
    private static ServiceFoto instance;

    private ServiceFoto(){
        super(Foto.class);
    }

    public static ServiceFoto getInstancia(){
        if(instance==null){
            instance = new ServiceFoto();
        }
        return instance;
    }
}
