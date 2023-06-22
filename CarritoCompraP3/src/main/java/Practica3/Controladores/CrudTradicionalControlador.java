package Practica3.Controladores;

import Practica3.Entidades.*;
import Practica3.Services.*;
import io.javalin.Javalin;
import org.jasypt.util.text.AES256TextEncryptor;


import java.io.IOException;
import java.util.*;


/**
 * Representa las rutas para manejar las operaciones de petición - respuesta.
 */
public class CrudTradicionalControlador extends Base {

    private static String modoConexion = "";
    static Service service = Service.getInstance();


    public CrudTradicionalControlador(Javalin app) {
        super(app);
    }

    /**
     * Las clases que implementan el sistema de plantilla están agregadas en PlantillasControlador.
     * http://localhost:7000/crud-simple/listar
     */
    @Override
    public void aplicarRutas(){
        crearProductos();

        /*Si el carrito no existe dentro de la sesion entonces se crea y se agrega como un atributo*/
        app.before(ctx -> {
            Carrito carro = ctx.sessionAttribute("carrito");
            if(carro == null){
                carro = new Carrito();
            }
            ctx.sessionAttribute("carrito",carro);

        });
        /*Ruta raiz
         * Muesta los productos disponibles para agragar al carrito*/
        app.get("/", ctx -> {
            Carrito carro = ctx.sessionAttribute("carrito");
            List<Producto> productos = ServiceProducto.getInstancia().findProd(0, 10);
            Map<String, Object> modelo = new HashMap<>();
            List<String> paginas = getPaginas();
            if( ctx.cookie("usuario") == null || ctx.cookie("mist") == null) {
                if(ctx.sessionAttribute("usuario") == null){
                    ctx.sessionAttribute("usuario","visitante");
                }
                if(ctx.sessionAttribute("usuario").equals("ADM")){
                    ctx.sessionAttribute("usuario","ADM");
                }else{
                    ctx.sessionAttribute("usuario","visitante");
                }
                //List<Producto> productos = service.getListaProductos();
                modelo.put("productos",productos);
                modelo.put("usuario",ctx.sessionAttribute("usuario"));
                modelo.put("cantidad",carro.getProductos().size());
                modelo.put("paginas",paginas);
                ctx.render("/publico/listadoProductos.html", modelo);
            }else {
                //List<Producto> productos = service.getListaProductos();
                modelo.put("productos", productos);
                modelo.put("usuario", ctx.cookie("usuario"));
                modelo.put("cantidad", carro.getProductos().size());
                modelo.put("paginas",paginas);
                ctx.render("/publico/listadoProductos.html", modelo);
            }
        });

        /*Peticion que agrega un producto al carrito del usuario
         * Si el producto ya está en el carrito entonces se aumenta la cantidad que se quiere*/
        app.post("/comprar", ctx -> {
            Carrito carro = ctx.sessionAttribute("carrito");
            Producto temp = carro.getProductosID(ctx.formParamAsClass("id",Integer.class).get());
            if(temp == null){
                temp = ServiceProducto.getInstancia().find(ctx.formParamAsClass("id", Integer.class).get());
                temp.setCantidad(ctx.formParamAsClass("cantidad",Integer.class).get() );
                carro.agregarProducto(temp);
                ctx.sessionAttribute("carrito",carro);
                ctx.redirect("/comprar");
            }else{
                int pos = carro.posicion(ctx.formParamAsClass("id",Integer.class).get());
                temp.setCantidad(ctx.formParamAsClass("cantidad",Integer.class).get() + temp.getCantidad());
                carro.cambiarProducto(temp,pos);
            }
            //System.out.println(temp.getId());
            ctx.sessionAttribute("carrito",carro);
            ctx.redirect("/comprar");
        });

        app.get("/comprar", ctx -> {
            ctx.redirect("/");
        });

        app.get("/comprar/{id}", ctx -> {
            String a = ctx.pathParamAsClass("id", String.class).get().replaceAll(";jsessionid=[^?]*", "");
            int pos = Integer.parseInt(a) * 10;
            Carrito carro = ctx.sessionAttribute("carrito");
            List<Producto> productos = ServiceProducto.getInstancia().findProd(pos, pos+10);
            Map<String, Object> modelo = new HashMap<>();
            List<String> paginas = getPaginas();
            if( ctx.cookie("usuario") == null || ctx.cookie("mist") == null) {
                if(ctx.sessionAttribute("usuario") == null){
                    ctx.sessionAttribute("usuario","visitante");
                }
                if(ctx.sessionAttribute("usuario").equals("ADM")){
                    ctx.sessionAttribute("usuario","ADM");
                }else{
                    ctx.sessionAttribute("usuario","visitante");
                }
                //List<Producto> productos = service.getListaProductos();
                modelo.put("productos",productos);
                modelo.put("usuario",ctx.sessionAttribute("usuario"));
                modelo.put("cantidad",carro.getProductos().size());
                modelo.put("paginas",paginas);
                ctx.render("/publico/listadoProductos.html", modelo);
            }else {
                //List<Producto> productos = service.getListaProductos();
                modelo.put("productos", productos);
                modelo.put("usuario", ctx.cookie("usuario"));
                modelo.put("cantidad", carro.getProductos().size());
                modelo.put("paginas",paginas);
                ctx.render("/publico/listadoProductos.html", modelo);
            }
        });

        /*Carga la pestaña con todas las ventas realizadas
         * Si el usuario no se ha logeado entonces se redirige al log-in*/
        app.get("/ventas", ctx -> {

            if( ctx.cookie("usuario") == null || ctx.cookie("mist") == null) {
                ctx.redirect("/autenti/ventas");
                return;
            } else {
                AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
                textEncryptor.setPassword("myEncryptionPassword");
                String mist = textEncryptor.decrypt(ctx.cookie("mist"));
                Usuario aux = new Usuario(ctx.cookie("usuario"),mist);
                if(!ServiceUsuario.autentificarUsuario(aux).equalsIgnoreCase("ADM")){
                    ctx.redirect("/autenti/ventas");
                    return;
                }
            }
            Carrito carro = ctx.sessionAttribute("carrito");
            List<Venta> ventas = ServiceVenta.getInstancia().getVentas();
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("ventas",ventas);
            modelo.put("cantidad",carro.getProductos().size());

            ctx.render("/publico/ventas.html",modelo);
        });

        /*Carga la ventana para hacer crud de los productos*/
        app.get("/productos", ctx -> {
            List<String> paginas = getPaginas();
            if( ctx.cookie("usuario") == null || ctx.cookie("mist") == null) {
                ctx.redirect("/autenti/productos");
                return;
            }
            else{
                AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
                textEncryptor.setPassword("myEncryptionPassword");
                String mist = textEncryptor.decrypt(ctx.cookie("mist"));
                Usuario aux = new Usuario(ctx.cookie("usuario"),mist);
                if(!ServiceUsuario.autentificarUsuario(aux).equalsIgnoreCase("ADM")){
                    ctx.redirect("/autenti/ventas");
                    return;
                }
            }
            List<Producto> productos = ServiceProducto.getInstancia().findProd(0,10);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            modelo.put("paginas",paginas);
            Carrito carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getProductos().size());
            ctx.render("/publico/administrarProductosV.html",modelo);
        });

        app.get("/productos/{id}", ctx -> {
            String a = ctx.pathParamAsClass("id", String.class).get().replaceAll(";jsessionid=[^?]*", "");
            int pos = Integer.parseInt(a) * 10;
            List<String> paginas = getPaginas();
            if( ctx.cookie("usuario") == null || ctx.cookie("mist") == null) {
                ctx.redirect("/autenti/productos");
                return;
            }else{
                AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
                textEncryptor.setPassword("myEncryptionPassword");
                String mist = textEncryptor.decrypt(ctx.cookie("mist"));
                Usuario aux = new Usuario(ctx.cookie("usuario"),mist);
                if(!ServiceUsuario.autentificarUsuario(aux).equalsIgnoreCase("ADM")){
                    ctx.redirect("/autenti/ventas");
                    return;
                }
            }
            List<Producto> productos = ServiceProducto.getInstancia().findProd(pos,pos+10);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("productos",productos);
            modelo.put("paginas",paginas);
            Carrito carrito = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carrito.getProductos().size());
            ctx.render("/publico/administrarProductosV.html",modelo);
        });

        /*Carga la ventana para registrar un nuevo producto en el sistema
         * Envia un string accion para poder especificar lo que se va a realizar al momento de hacer post en el formulario
         * ya que se utiliza la misma vista que para editar un producto*/
        app.get("/registrar", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("accion","/registrar");
            Carrito carro = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carro.getProductos().size());
            ctx.render("/publico/productoCE.html",modelo);
        });

        /*Registra un producto en el sistema a partir de los valores del formulario*/
        app.post("/registrar", ctx -> {
            String nombre = ctx.formParam("nombre");
            int precio = ctx.formParamAsClass("precio",Integer.class).get();
            String desc = ctx.formParam("desc");
            List<Foto> fotos = new ArrayList<>();
            ctx.uploadedFiles("img").forEach(uploadedFile -> {
                try {
                    byte[] bytes = uploadedFile.getContent().readAllBytes();
                    String encodedString = Base64.getEncoder().encodeToString(bytes);
                    Foto foto = new Foto(uploadedFile.getFilename(), uploadedFile.getContentType(), encodedString);
                    ServiceFoto.getInstancia().create(foto);
                    fotos.add(foto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Producto temp = new Producto(nombre,precio,desc);
            temp.setFotos(fotos);
            service.registrarProducto(temp);
            ServiceProducto.getInstancia().create(temp);
            ctx.redirect("/productos");
        });

        /*Remueve un articulo de los disponibles a partir de su id*/
        app.get("/remover/{id}", ctx -> {
            ServiceProducto.getInstancia().deleteProducto(ctx.pathParamAsClass("id",Integer.class).get());
            ctx.redirect("/productos");
        });

        /*Permite editar un producto ya agregado
         * Se envia un string para determinar que se realizará una modificación luego del post*/
        app.get("/editar/{id}", ctx -> {
            Producto temp = ServiceProducto.getInstancia().find(ctx.pathParamAsClass("id", Integer.class).get());
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("producto",temp);
            modelo.put("accion","/editar/"+ctx.pathParamAsClass("id", Integer.class).get());

            Carrito carro = ctx.sessionAttribute("carrito");
            modelo.put("cantidad",carro.getProductos().size());
            ctx.render("/publico/productoCE.html",modelo);
        });

        /*Post luego del formulario de modificar producto
         * Actualiza los valores a partir de lo enviado en el formulario*/
        app.post("/editar/{id}", ctx -> {
            String nombre = ctx.formParam("nombre");
            int precio = ctx.formParamAsClass("precio",Integer.class).get();
            String desc = ctx.formParam("desc");

            Producto temp = new Producto(nombre,precio, desc);
            temp.setId(ctx.pathParamAsClass("id",Integer.class).get());
            ServiceProducto.getInstancia().edit(temp);

            ctx.redirect("/productos");
        });

        /*Hace render al log-in
         * direc determina a que vista será rediccionado luego de autentificarse correctamente*/
        app.get("/autenti/{direc}", ctx -> {
            String direc = ctx.pathParam("direc");
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("direc",direc);
            ctx.render("/publico/login.html",modelo);
        });

        /*Post de autentificacion
         * redirige a la ventana especificada en el get*/
        app.post("/autenti/{direc}",ctx -> {
            String usuario = ctx.formParam("usuario");
            String pass = ctx.formParam("password");
            String temp = ctx.pathParam("direc");
            String recordar = ctx.formParam("recordar");

            if(usuario == null || pass == null){
                ctx.redirect("/autenti/"+temp);
            }
            Usuario user = new Usuario(usuario,pass);
            AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
            textEncryptor.setPassword("myEncryptionPassword");
            pass = textEncryptor.encrypt(pass);
            if(recordar != null){
                ctx.cookie("usuario", usuario,(3600*24*7));//Una semana en segundos
                ctx.cookie("mist", pass,(3600*24*7));
            }
            //Encriptar cookie
            ctx.cookie("usuario", usuario);
            ctx.cookie("mist", pass);
            ServiceUsuario.getInstancia().create(user);
            if(!usuario.equals("ADM") && temp.equals("productos")){
                ctx.redirect("/");
            }else {
                ctx.redirect("/" +temp);
            }
        });

        /*Carga el carrito pasando la lista de productos que se tiene dentro del carro*/
        app.get("/carrito", ctx -> {
            List<String> paginas = getPaginas();
            Carrito carro = ctx.sessionAttribute("carrito");
            if(carro == null){
                carro = new Carrito();
            }
            ctx.sessionAttribute("carrito",carro);
            Map<String, Object> modelo = new HashMap<>();
            if( ctx.cookie("usuario") == null || ctx.cookie("mist") == null) {
                if (ctx.sessionAttribute("usuario") == null) {
                    ctx.sessionAttribute("usuario", "visitante");
                }
                if (ctx.sessionAttribute("usuario").equals("ADM")) {
                    ctx.sessionAttribute("usuario", "ADM");
                } else {
                    ctx.sessionAttribute("usuario", "visitante");
                }
                modelo.put("productos",carro.getProductos());
                modelo.put("cantidad",carro.getProductos().size());
                modelo.put("usuario",ctx.sessionAttribute("usuario"));
                ctx.render("/publico/carrito.html",modelo);
            }else {
                modelo.put("productos", carro.getProductos());
                modelo.put("cantidad", carro.getProductos().size());
                modelo.put("usuario", ctx.cookie("usuario"));
                ctx.render("/publico/carrito.html",modelo);
            }
        });


        /*Elimina un producto del carrito a partir de su id*/
        app.get("/eliminar/{id}", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            Carrito carro = ctx.sessionAttribute("carrito");
            carro.eliminarProductoId(id);

            ctx.sessionAttribute("carrito",carro);
            ctx.redirect("/carrito");
        });

        /*Procesa la compra
         * crea un objeto venta
         * Limpia el carrito del usuario*/
        app.post("/procesar",ctx -> {
            Carrito carro = ctx.sessionAttribute("carrito");
            if(carro.getProductos().size() < 1){
                ctx.redirect("/carrito");
            }
            String nombre = ctx.formParam("nombre");
            Venta venta = new Venta(nombre);
            List<Comprado> lista = ServiceComprado.getInstancia().convertProd(carro.productos,venta.getId());
            venta.setListaProductos(lista);
            service.addVentas(venta);
            ServiceVenta.getInstancia().create(venta);
            carro.eliminarProductos();
            ctx.sessionAttribute("carrito",carro);
            ctx.redirect("/comprar");
        });

        /*Limpia el carrito del usuario*/
        app.get("/limpiar", ctx -> {
            Carrito carro = ctx.sessionAttribute("carrito");
            carro.eliminarProductos();

            ctx.redirect("/comprar");
        });

        /*Sale de sesion*/
        app.get("/invalidarSesion", ctx -> {
            //invalidando la sesion.
            if(ctx.cookie("usuario")!= null && ctx.cookie("mist")!= null){
                ctx.removeCookie("usuario");
                ctx.removeCookie("mist");
            }
            ctx.redirect("/");
        });

        app.post("/addComment/{id}", ctx->{
            String comment = ctx.formParam("coment");
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            Comentarios temp = new Comentarios(comment,id);
            ServiceComentario.getInstancia().create(temp);;
            ctx.redirect("/ver/"+id);
        });

        app.get("/delComent/{id}/{coment}", ctx ->{
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            int comment = ctx.pathParamAsClass("coment",Integer.class).get();
            ServiceComentario.getInstancia().deleteComent(comment);
            ctx.redirect("/ver/"+id);
        });

        app.get("/ver/{id}",ctx -> {
            String id = ctx.pathParamAsClass("id", String.class).get().replaceAll(";jsessionid=[^?]*", "");
            Producto temp = ServiceProducto.getInstancia().find(Integer.parseInt(id));
            List<Comentarios> comments = ServiceComentario.getInstancia().findComments(Integer.parseInt(id));
            Map<String, Object> modelo = new HashMap<>();
            if( ctx.cookie("usuario") == null || ctx.cookie("mist") == null) {
                if (ctx.sessionAttribute("usuario") == null) {
                    ctx.sessionAttribute("usuario", "visitante");
                }
                if (ctx.sessionAttribute("usuario").equals("ADM")) {
                    ctx.sessionAttribute("usuario", "ADM");
                } else {
                    ctx.sessionAttribute("usuario", "visitante");
                }
                modelo.put("temp",temp);
                modelo.put("comments",comments);
                modelo.put("user",ctx.sessionAttribute("usuario"));
                Carrito carro = ctx.sessionAttribute("carrito");
                modelo.put("cantidad",carro.getProductos().size());
                ctx.render("/publico/verProducto.html",modelo);
            }else {
                modelo.put("temp", temp);
                modelo.put("comments", comments);
                modelo.put("user", ctx.cookie("usuario"));
                Carrito carro = ctx.sessionAttribute("carrito");
                modelo.put("cantidad", carro.getProductos().size());
                ctx.render("/publico/verProducto.html", modelo);
            }
        });

    }

    private static List<String> getPaginas() {
        int pag = ServiceProducto.getInstancia().pag();
        List<String> list = new ArrayList<>();
        for(int i = 0; i <= pag; i++){
            String aux = String.valueOf(i);
            list.add(aux);
        }

        return list;
    }

    private static void crearProductos(){
        String nombre;
        int precio;
        String desc;
        List<Foto> fotos = new ArrayList<Foto>();
        for(int i = 1 ; i < 41; i++){
            nombre = "producto "+ i;
            precio = 10 * i;
            desc = "Este es el producto "+i;
            Producto temp = new Producto(nombre,precio,desc);
            service.registrarProducto(temp);
            temp.setFotos(fotos);
            ServiceProducto.getInstancia().create(temp);
        }
    }

}
