package org.example;

import org.example.clases.*;
import org.example.servicios.*;
import io.javalin.Javalin;
import io.javalin.http.sse.SseClient;

import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinFreemarker;
import io.javalin.plugin.rendering.template.JavalinVelocity;
import org.jasypt.util.text.AES256TextEncryptor;
import org.eclipse.jetty.websocket.api.Session;


import java.io.IOException;
import java.util.*;


public class Main {
    private static String modoConexion = "";
    public static List<Session> usuariosConectados = new ArrayList<>();
    public static List<SseClient> UserList = new ArrayList<>();
    public static void main(String[] args){


        //Server
        BootStrapServices.startDB();
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.addStaticFiles("/publico");
        }).start(7000);


        //Para las plantillas
        JavalinRenderer.register(JavalinVelocity.INSTANCE, ".vm");
        JavalinRenderer.register(JavalinFreemarker.INSTANCE, ".ftl");
        crearUsuarios();
        //Crear el carrito si no existe en la sesion
        app.before(ctx -> {
            Cart cart = ctx.sessionAttribute("carrito");
            if(cart == null){
                cart = new Cart();
            }

            ctx.sessionAttribute("carrito",cart);
        });

        app.after(ctx -> {
           System.out.println(ctx.attributeMap().toString());
        });
                /*Registra un producto en el sistema a partir de los valores del formulario*/
        app.post("/registrar", ctx -> {

            String nom = ctx.formParam("nombre");
            int prec = ctx.formParam("precio",Integer.class).get();
            String desc = ctx.formParam("desc");
            List<Foto> fotos = new ArrayList<Foto>();
            ctx.uploadedFiles("img").forEach(uploadedFile -> {
                try {

                    byte[] bytes = uploadedFile.getContent().readAllBytes();
                    String encodedString = Base64.getEncoder().encodeToString(bytes);
                    Foto foto = new Foto(uploadedFile.getFilename(), uploadedFile.getContentType(), encodedString);
                    ServicioPic.getInstancia().create(foto);
                    fotos.add(foto);
                } catch (IOException e) {
                    e.printStackTrace();

                }
            });


            Producto aux = new Producto(nom,prec,desc);
            aux.setFotos(fotos);

            ServicioProdu.getInstance().create(aux);


            ctx.redirect("/productos");
        });

        app.get("/ver/:id",ctx -> {
            int id = ctx.pathParam("id", Integer.class).get();
            Producto temp = ServicioProdu.getInstance().find(id);
            List<Coment> comments = ServicioComent.getInstancia().findComments(id);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("temp",temp);
            modelo.put("comments",comments);
            String user = ctx.cookie("usuario");
            modelo.put("user",user);
            ctx.render("/publico/ver.vm",modelo);
        });

        app.get("/logout", ctx -> {
            if(ctx.cookie("usuario")!= null && ctx.cookie("mist")!= null){
                ctx.removeCookie("usuario");
                ctx.removeCookie("mist");
            }
            ctx.redirect("/");
        });

        app.post("/addComment/:id", ctx->{
           String comment = ctx.formParam("coment");
           int id = ctx.pathParam("id", Integer.class).get();
           Coment temp = new Coment(comment,id);
           ServicioComent.getInstancia().create(temp);
           ctx.redirect("/ver/"+id);
        });

        app.get("/delComent/:id/:coment", ctx ->{
            int id = ctx.pathParam("id", Integer.class).get();
            int comment = ctx.pathParam("coment",Integer.class).get();
            System.out.println("El id del comentario es: "+comment);
            ServicioComent.getInstancia().deleteComent(comment);
            ctx.redirect("/ver/"+id);
        });

        /*Esta es el root que meustra los productos disponibles para agregar*/
        app.get("/", ctx -> {
            Cart carrito = ctx.sessionAttribute("carrito");
            List<Producto> productos = ServicioProdu.getInstance().findProd(0, 10);

            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            modelo.put("cantidad",carrito.getProductos().size());
            List<String> paginas = getPaginas();
            modelo.put("paginas",paginas);
            String user = ctx.cookie("usuario");
            modelo.put("user",user);
            ctx.render("/publico/listadoProductos.vm", modelo);
        });

        app.get("/comprar/:id", ctx -> {
            int pos = ctx.pathParam("id", Integer.class).get() * 10;
            Cart carrito = ctx.sessionAttribute("carrito");
            List<Producto> productos = ServicioProdu.getInstance().findProd(pos, pos+10);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            modelo.put("cantidad",carrito.getProductos().size());
            List<String> paginas = getPaginas();
            modelo.put("paginas",paginas);
            String user = ctx.cookie("usuario");
            modelo.put("user",user);
            ctx.render("/publico/listadoProductos.vm", modelo);
        });


        /*Peticion que agrega un producto al carrito del usuario
        * Si el producto ya está en el carrito entonces se aumenta la cantidad que se quiere*/
        app.post("/comprar", ctx -> {
            Cart carrito = ctx.sessionAttribute("carrito");

            Producto temp = carrito.getProductosPorID(ctx.formParam("id",Integer.class).get());
            if(temp == null){
                temp = ServicioProdu.getInstance().find(ctx.formParam("id", Integer.class).get());
                temp.setCantidad(ctx.formParam("cantidad",Integer.class).get() );
                carrito.addProducto(temp);
                ctx.sessionAttribute("carrito",carrito);
                ctx.redirect("/comprar");
            }else{
                int pos = carrito.getPos(ctx.formParam("id",Integer.class).get());
                temp.setCantidad(ctx.formParam("cantidad",Integer.class).get() + temp.getCantidad());
                carrito.cambiarProducto(temp,pos);
            }

            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/comprar");
        });

        app.get("/comprar", ctx -> {
            ctx.redirect("/");
        });

        /*Ventana con las ventas ya hechas*/
        app.get("/ventas", ctx -> {
            if( ctx.cookie("usuario") == null || ctx.cookie("mist")== null){
                ctx.redirect("/autenti/ventas");
                return;
            } else{
                AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
                textEncryptor.setPassword("myEncryptionPassword");
                String mist = textEncryptor.decrypt(ctx.cookie("mist"));
                User aux = new User(ctx.cookie("usuario"),mist);
                if(!ServicioUsuario.autentificarUsuario(aux).equalsIgnoreCase("ADM")){
                    ctx.redirect("/autenti/ventas");
                    return;
                }
            }
            Cart carrito = ctx.sessionAttribute("carrito");
            List<VProducto> ventas = ServicioVentas.getInstance().getVentas();
            for (VProducto venta: ventas) {
                System.out.println(venta.getId());
            }
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("ventas",ventas);
            modelo.put("cantidad",carrito.getProductos().size());
            String user = ctx.cookie("usuario");
            modelo.put("user",user);
            ctx.render("/publico/ventas.vm",modelo);
        });



        /*Ventana para editar o registrar productos*/
        app.get("/productos", ctx -> {
            if( ctx.cookie("usuario") == null || ctx.cookie("mist")== null){
                ctx.redirect("/autenti/productos");
                return;
            } else{
                AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
                textEncryptor.setPassword("myEncryptionPassword");
                String mist = textEncryptor.decrypt(ctx.cookie("mist"));
                User aux = new User(ctx.cookie("usuario"),mist);
                if(!ServicioUsuario.autentificarUsuario(aux).equalsIgnoreCase("ADM")){
                    ctx.redirect("/autenti/ventas");
                    return;
                }
            }
            List<Producto> productos = ServicioProdu.getInstance().findProd(0,0);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            Cart carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getProductos().size());
            String user = ctx.cookie("usuario");
            modelo.put("user",user);
            ctx.render("/publico/productos.vm",modelo);
        });

        /* Registrar un nuevo producto*/
        app.get("/registrar", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("accion","/registrar");
            Cart carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getProductos().size());
            String user = ctx.cookie("usuario");
            modelo.put("user",user);
            ctx.render("/publico/productoCE.vm",modelo);
        });

        

        /*Quitar los articulos dado un ID*/
        app.get("/remover/:id", ctx -> {
            ServicioProdu.getInstance().deleteProducto(ctx.pathParam("id",Integer.class).get());
            ctx.redirect("/productos");
        });

        /*Editar un producto ya creado*/
        app.get("/editar/:id", ctx -> {
            Producto temp = ServicioProdu.getInstance().find(ctx.pathParam("id", Integer.class).get());
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("producto",temp);
            modelo.put("accion","/editar/"+ctx.pathParam("id", Integer.class).get());

            Cart carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getProductos().size());
            String user = ctx.cookie("usuario");
            modelo.put("user",user);
            ctx.render("/publico/productoCE.vm",modelo);
        });

        /*Modificar un producto ya registrado*/
        app.post("/editar/:id", ctx -> {
            String nombre = ctx.formParam("nombre");
            int precio = ctx.formParam("precio",Integer.class).get();
            String desc = ctx.formParam("desc");
            Producto temp = new Producto(nombre,precio,desc);
            temp.setId(ctx.pathParam("id",Integer.class).get());
            ServicioProdu.getInstance().edit(temp);

            ctx.redirect("/productos");
        });

        /*Cargar el inicio de sesions*/
        app.get("/autenti/:direc", ctx -> {
            String direc = ctx.pathParam("direc");
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("direc",direc);
            String user = ctx.cookie("usuario");
            modelo.put("user",user);
            ctx.render("/publico/autentificacion.vm",modelo);
        });

        /*Para auntentificar el usuario*/
        app.post("/autenti/:direc",ctx -> {
            String usuario = ctx.formParam("usuario");
            String pass = ctx.formParam("password");
            String temp = ctx.pathParam("direc");
            String recordar = ctx.formParam("recordar");

            if(usuario == null || pass == null){
                ctx.redirect("/autenti/"+temp);
            }
            User user = new User(usuario,pass);

            AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
            textEncryptor.setPassword("myEncryptionPassword");
            pass = textEncryptor.encrypt(pass);

            if(recordar != null){
                ctx.cookie("usuario", usuario,(3600*24*7));//Una semana en segundos
                ctx.cookie("mist", pass,(3600*24*7));
            }

            //Ocultar bajo encriptacion
            ctx.cookie("usuario", usuario);
            ctx.cookie("mist", pass);

            ctx.redirect("/"+temp);

        });

        /*Lista de productos en el carrito*/
        app.get("/carrito", ctx -> {
            Cart carrito = ctx.sessionAttribute("carrito");
            if(carrito == null){
                carrito = new Cart();
            }
            ctx.sessionAttribute("carrito",carrito);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",carrito.getProductos());
            modelo.put("cantidad",carrito.getProductos().size());
            String user = ctx.cookie("usuario");
            modelo.put("user",user);
            ctx.render("/publico/carrito.vm",modelo);
        });



        /*Quitar productos dado un ID*/
        app.get("/eliminar/:id", ctx -> {
            int id = ctx.pathParam("id", Integer.class).get();
            Cart carrito = ctx.sessionAttribute("carrito");
            carrito.eliminarProductoPorId(id);

            ctx.sessionAttribute("carrito",carrito);
            ctx.redirect("/carrito");
        });

        /*Procesar compras y editar*/
        app.post("/procesar",ctx -> {
           Cart carrito = ctx.sessionAttribute("carrito");
           if(carrito.getProductos().size() < 1){
               ctx.redirect("/carrito");
           }
           String nombre = ctx.formParam("nombre");
           VProducto venta = new VProducto(nombre);
           List<Comprado> list = ServicioComprado.getInstance().convertProd(carrito.productos,venta.getId());
           venta.setListaProductos(list);

           ServicioVentas.getInstance().create(venta);
           carrito.borrarProductos();
           ctx.sessionAttribute("carrito",carrito);
            String estadisticas = calcularEstadisticas().toString();
            System.out.println(estadisticas);
            int ventas = ServicioVentas.getInstance().getVentas().size();

            for (SseClient cliente: UserList) {cliente.sendEvent("estadistica",estadisticas);}
            for (SseClient cliente: UserList) {cliente.sendEvent("ventas", Integer.toString(ventas));}
            ctx.redirect("/comprar");
        });

        /*Limpiar el carrito*/
        app.get("/limpiar", ctx -> {
            Cart carrito = ctx.sessionAttribute("carrito");
            carrito.borrarProductos();

            ctx.redirect("/comprar");
        });

        app.get("/admin", ctx-> {
            if( ctx.cookie("usuario") == null || ctx.cookie("mist")== null){
                ctx.redirect("/autenti/admin");
                return;
            } else{
                AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
                textEncryptor.setPassword("myEncryptionPassword");
                String mist = textEncryptor.decrypt(ctx.cookie("mist"));
                User aux = new User(ctx.cookie("usuario"),mist);
                if(!ServicioUsuario.autentificarUsuario(aux).equalsIgnoreCase("ADM")){
                    ctx.redirect("/autenti/admin");
                    return;
                }
            }
            int Ventas = ServicioVentas.getInstance().getVentas().size();
            int UsersCant = usuariosConectados.size();
            int cantProd = ServicioProdu.getInstance().findAll().size();
            String estadisticas = calcularEstadisticas().toString();
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("cantUser",UsersCant);
            modelo.put("cantVentas",Ventas);
            modelo.put("cantProd",cantProd);
            modelo.put("estats",estadisticas);
            String user = ctx.cookie("usuario");
            modelo.put("user",user);
            ctx.render("/publico/admin.vm",modelo);
        });

        app.get("/comentarios/:id",ctx -> {
            int id = ctx.pathParam("id", Integer.class).get();
            List<Coment> comments = ServicioComent.getInstancia().findComments(id);
            String comentarios = "";
            for (Coment comentario: comments) {
                String url = "/delComent/" + id + "/"+ comentario.getId();
                comentarios +=  "<div class=\"card m-auto mt-2\" style=\"max-width: 80%; margin-top: 20px;\">\n" +
                        "                <div class=\"card-header\">\n" +
                        "                    <h5>Anonimo</h>\n" +
                        "                </div>\n" +
                        "                <div class=\"card-body\">\n" +
                        "                    <div class=\"row g-2 align-items-center\">\n" +
                        "                        <div class=\"col-auto\">\n" +
                        "                            <h6>"+comentario.getComentario()+"</h6>\n" +
                        "                        </div>\n" +
                        "                        <div  id = \"btn\" class=\"col-auto \" style=\"margin-left: 80%;\">\n" +
                        "                            <a id = \"btn\" href="+url+" class=\"btn btn-danger\">Eliminar</a>\n" +
                        "                        </div>\n" +
                        "                    </div>\n" +
                        "                </div>\n" +
                        "            </div>\n" +
                        "        </div>";
            }
            ctx.result(comentarios);
        });

        app.sse("estats", sseClient -> {
            System.out.println("Conectado");
            UserList.add(sseClient);
            sseClient.onClose(() -> {UserList.remove(sseClient);
            });
        });

/*Despues de registrar una venta realizada*/
        app.ws("/admini", ws->{

            ws.onConnect(ctx -> {
                System.out.println("Conexión Iniciada - "+ctx.getSessionId());
                if(!usuariosConectados.contains(ctx.session)) {
                    usuariosConectados.add(ctx.session);
                }

            });

            ws.onMessage(ctx -> {
                enviarEstadisticas();
            });

            ws.onClose(ctx -> {
                System.out.println("Conexión Cerrada - "+ctx.getSessionId());
                usuariosConectados.remove(ctx.session);
                enviarEstadisticas();

            });



        });
    }

    private static void enviarEstadisticas() {
        int cantidad = usuariosConectados.size();
        for (Session sesion: usuariosConectados){
            try {
                sesion.getRemote().sendString(Integer.toString(cantidad));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Map<String, Integer> calcularEstadisticas() {
        System.out.println("////////////////////////////////");
        Map<String, Integer> mapa = new HashMap<>();
        List<Comprado> productos = ServicioComprado.getInstance().getProd();
        for (Comprado aux : productos) {
            if(mapa.containsKey(aux.getNombre())){
                int valAux = mapa.get(aux.getNombre());
                mapa.put(aux.getNombre(), aux.getCantidad() + valAux);
            }else {
                mapa.put(aux.getNombre(), aux.getCantidad());
            }
            System.out.println(aux.getNombre()+mapa.get(aux.getNombre()));
        }
        System.out.println("//////////////////////////////////");
        return mapa;
    }

    private static void crearUsuarios(){
        String nombre;
        int precio;
        String desc;
        List<Foto> fotos = new ArrayList<Foto>();
        for(int i = 0 ; i < 19; i++){
            nombre = "producto "+ i;
            precio = 10 * i;
            desc = "Este es el producto "+i;
            Producto temp = new Producto(nombre,precio,desc);
            temp.setFotos(fotos);
            ServicioProdu.getInstance().create(temp);
        }
            
    }
    private static List<String> getPaginas() {
        int pag = ServicioProdu.getInstance().pag();
        List<String> list = new ArrayList<String>();
        for(int i = 0; i <= pag; i++){
            String aux = "<a class=\"page-link\" href=\"/comprar/"+i+"\">"+(i+1)+"</a>";
            list.add(aux);
        }
        return list;
    }

    public static String getConnection(){
        return modoConexion;
    }

}
