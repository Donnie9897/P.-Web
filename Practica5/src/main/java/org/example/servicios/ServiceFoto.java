package org.example.servicios;

import org.example.DBService;
import org.example.clases.Foto;

public class ServiceFoto extends DBService<Foto> {

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

