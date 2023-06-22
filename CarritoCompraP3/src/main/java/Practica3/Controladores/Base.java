package Practica3.Controladores;

import io.javalin.Javalin;

public abstract class Base {
    protected Javalin app;

    public Base(Javalin app){
        this.app = app;
    }

    abstract public void aplicarRutas();
}
