package org.example;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinVelocity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args){

        //Inicia el server
        Javalin app = Javalin.create().start(8000);
        //Renderer
        JavalinRenderer.register(new JavalinVelocity(), ".vm");
        //Primera instancia
        Sesion sesion = Sesion.getInstance();

        //Si no existe, se crea una
        app.before(ctx -> {
            Cart aux_cart = ctx.sessionAttribute("carrito");
            if(aux_cart == null){
                aux_cart = new Cart(sesion.getCarrito());
            }
            ctx.sessionAttribute("carrito",aux_cart);

        });


        //carrito con productos agregados
        app.get("/carrito", ctx -> {
            Cart aux_cart = ctx.sessionAttribute("carrito");
            if(aux_cart == null){
                aux_cart = new Cart(sesion.getCarrito());
            }

            ctx.sessionAttribute("carrito",aux_cart);

            Map<String, Object> aux_model = new HashMap<>();
            aux_model.put("productos",aux_cart.getProductos());
            aux_model.put("cantidad",aux_cart.getProductos().size());

            ctx.render("/visuales/carrito.vm",aux_model);
        });

        //Mostrar los productos disponibles
        app.get("/", ctx -> {
            Cart aux_cart = ctx.sessionAttribute("carrito");

            List<Producto> list_pro = sesion.getProductos();
            Map<String, Object> aux_model = new HashMap<>();
            aux_model.put("productos", list_pro);

            aux_model.put("cantidad",aux_cart.getProductos().size());
            ctx.render("/visuales/listpro.vm", aux_model);

        });

        //Metodo post para agregar al carrito segun la cantidad
        app.post("/comprar", ctx -> {
            Cart cart = ctx.sessionAttribute("carrito");

            Producto aux = cart.getProID(Integer.parseInt(ctx.formParam("id")));
            if(aux == null){
                aux = sesion.obtenerProID(Integer.parseInt(ctx.formParam("id")));
                aux.setCantidad(Integer.parseInt(ctx.formParam("cantidad")));
                cart.agregar(aux);
                ctx.sessionAttribute("carrito",cart);
                ctx.redirect("/comprar");

            }else{

                int i = cart.position(Integer.parseInt(ctx.formParam("id")));
                aux.setCantidad(Integer.parseInt(ctx.formParam("cantidad")) + aux.getCantidad());
                cart.editPro(aux,i);
            }

            ctx.sessionAttribute("carrito", cart);
            ctx.redirect("/comprar");
        });

        //Procesar la compra de uno o varios productos
        app.post("/procesar",ctx -> {
            Cart aux_cart = ctx.sessionAttribute("carrito");
            if(aux_cart.getProductos().size() < 1){
                ctx.redirect("/carrito");
            }

            String aux_nom = ctx.formParam("nombre");
            Sales aux_sale = new Sales(sesion.getSales().size() + (1), aux_nom,aux_cart.aux_pro);
            sesion.agregarSale(aux_sale);
            aux_cart.eliminarPro();
            ctx.sessionAttribute("carrito",aux_cart);

            ctx.redirect("/comprar");
        });

        //Redirecciona hacia el Login para autentificar el usuario
        app.get("/autenti/<path>", ctx -> {

            String autenti = ctx.pathParam("path");
            Map<String, Object> aux_model = new HashMap<>();
            aux_model.put("direc", autenti);

            ctx.render("/visuales/autentificacion.vm", aux_model);
        });

        app.get("/comprar", ctx -> {
            ctx.redirect("/");
        });


        //Obtener todas las ventas completadas
        app.get("/ventas", ctx -> {

            if( ctx.cookie("usuario") == null
                           || ctx.cookie("password") == null) {

                ctx.redirect("/autenti/ventas");
                return;
            }
            Cart aux_cart = ctx.sessionAttribute("carrito");
            List<Sales> aux_sales = sesion.getSales();
            Map<String, Object> aux_model = new HashMap<>();
            aux_model.put("ventas", aux_sales);

            aux_model.put("cantidad",aux_cart.getProductos().size());

            ctx.render("/visuales/ventas.vm",aux_model);
        });


        //Autentificar usuario
        app.post("/autenti/{direc}",ctx -> {
            String aux_user = ctx.formParam("usuario");
            String aux_pwd = ctx.formParam("password");
            String aux = ctx.pathParam("direc");

            if(aux_user == null || aux_pwd == null){
                ctx.redirect("/autenti/" + aux);
            }
            ctx.cookie("usuario", aux_user);
            ctx.cookie("password", aux_pwd);

            ctx.redirect("/" + aux);

        });

        //CRUD de productos
        app.get("/productos", ctx -> {
            if(  ctx.cookie("usuario") == null
                        || ctx.cookie("password") == null){
                ctx.redirect("/autenti/productos");
                return;
            }
            List<Producto> list_pro = sesion.getProductos();
            Map<String, Object> aux_model = new HashMap<>();
            aux_model.put("productos",list_pro);
            Cart aux_cart = ctx.sessionAttribute("carrito");
            aux_model.put("cantidad", aux_cart.getProductos().size());
            ctx.render("/visuales/productos.vm",aux_model);

        });

        //Creacion de un producto nuevo
        app.get("/registrar", ctx -> {

            Map<String, Object> aux_model = new HashMap<>(); //tabla hash para ID
            aux_model.put("accion","/registrar");
            Cart aux_cart = ctx.sessionAttribute("carrito");
            aux_model.put("cantidad", aux_cart.getProductos().size());

            ctx.render("/visuales/crud.vm", aux_model);
        });

        //Agregar el producto a la lista de productos disponibles
        app.post("/registrar", ctx -> {
            String aux_nom = ctx.formParam("nombre");
            int aux_prc = Integer.parseInt(ctx.formParam("precio"));

            Producto aux = new Producto(aux_nom, aux_prc);
            sesion.registrarProducto(aux);

            ctx.redirect("/productos");
        });

        //Para remover productos del carrito
        app.get("/remover/{id}", ctx -> {
            sesion.eliminarProducto(Integer.parseInt(ctx.pathParam("id")));
            ctx.redirect("/productos");
        });

        //Editar producto segun el modelo empleado
        app.get("/editar/{id}", ctx -> {
            Producto aux = sesion.obtenerProID(Integer.parseInt(ctx.pathParam("id")));
            Map<String, Object> aux_model = new HashMap<>();
            aux_model.put("producto",aux);
            aux_model.put("accion", "/editar/" + ctx.pathParam("id"));

            Cart aux_cart = ctx.sessionAttribute("carrito");
            aux_model.put("cantidad", aux_cart.getProductos().size());

            ctx.render("/visuales/crud.vm",aux_model);
        });

        //Metodo post para la modificacion de un producto
        app.post("/editar/{id}", ctx -> {
            Producto aux = new Producto(ctx.formParam("nombre"), Integer.parseInt(ctx.formParam("precio")));
            aux.setId(Integer.parseInt(ctx.pathParam("id")));
            sesion.actualizarProducto(aux);

            ctx.redirect("/productos");
        });


        //Eliminar del carrito
        app.get("/eliminar/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));

            Cart aux_cart = ctx.sessionAttribute("carrito");
            aux_cart.eliminarProId(id); //delete

            ctx.sessionAttribute("carrito", aux_cart);

            ctx.redirect("/carrito"); //cart
        });



        //Procesa una commpra
        app.post("/procesar", ctx -> {
            Cart aux_cart = ctx.sessionAttribute("carrito");
            if(aux_cart.getProductos().size() < 1){
                ctx.redirect("/carrito");             }
            String aux_nom = ctx.formParam("nombre");

            Sales aux_sale = new Sales(sesion.getSales().size() + (1), aux_nom, aux_cart.aux_pro);

            sesion.agregarSale(aux_sale);
            aux_cart.eliminarPro();
            ctx.sessionAttribute("carrito", aux_cart);

            ctx.redirect("/comprar");
        });



        //Para limpiar el carrito entero
        app.get("/limpiar", ctx -> {
            Cart aux_cart = ctx.sessionAttribute("carrito");
            aux_cart.eliminarPro();

            ctx.redirect("/comprar");
        });

    }

}

