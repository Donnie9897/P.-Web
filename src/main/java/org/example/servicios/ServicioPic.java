package org.example.servicios;

import org.example.DBService;
import org.example.clases.Foto;

public class ServicioPic extends DBService<Foto> {

    private static ServicioPic instance;

    private ServicioPic(){
        super(Foto.class);
    }

    public static ServicioPic getInstancia(){
        if(instance==null){
            instance = new ServicioPic();
        }
        return instance;
    }
}

