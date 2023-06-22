package org.example;
import org.jsoup.Connection.Response;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;
public class Main {

    public static void main(String[] args) throws IOException {


        System.out.println("Por favor, ingresa un URL: ");
        Scanner scanner = new Scanner(System.in);
        String input_url =  scanner.nextLine();

        try {
            
            //Tomar url como input
            
            Document aux_url = Jsoup.connect(input_url).timeout(15000)
                    .followRedirects(true)
                    .get();

            //Verificar el # de lineas del recurso
            
            long lin_cont = aux_url.html().lines().count();
            System.out.print("\nEl # de lineas en el documento es de: " + lin_cont + " lineas");

            //Verificar el # de parrafos
            Elements para_cont = aux_url.select("p");
            System.out.println("\nEl # de parrafos es de: " + para_cont.size());

            //Verificar el # de imagenes dentro de parrafos
            if (para_cont.size() != 0) {
                Elements img_cont = aux_url.select("p img");
                System.out.println("El # de imagenes dentro de los parrafos es de: " + img_cont.size());
            }

            //Tipo de formularios POST
            Elements aux_post = aux_url.select("form[method$=post]");
            System.out.println("El # de formularios con metodo POST es de: " + aux_post.size());

            //Tipo de formularios GET
            Elements aux_get = aux_url.select("form[method$=get]");
            System.out.println("El # de formularios con metodo GET es de: " + aux_get.size() + "\n");

            //Verificar el # forms
            getInputs(aux_post);
            getInputs(aux_get);

            //Verifica que solo se ejecute la funcion cuando haya campos POST
            if (aux_post.size() != 0) {
                getPosts(aux_post, input_url);
            }

        }catch (UnknownHostException error){
            System.out.println("La url que ha proveido no es valida, intente de nuevo!");
        }

    }

    public static void getInputs(Elements forms) {
        int aux_cont = 1;
        for (Element aux_form : forms) {
            if (aux_form.children().select("input").size() != 0) {
                System.out.println("Inputs del form " + aux_form.attr("method").toLowerCase()+""+aux_cont+"son:");
                for (Element i : aux_form.select("input")) {
                    System.out.println("\t" + i);
                }
            } else {
                System.out.println("Form " + aux_form.attr("method").toLowerCase()+"#"+aux_cont+"no tiene campos de Input");
            }

            aux_cont++;
        }
    }


    public static void getPosts(Elements forms,String url){
        //Ya iniciado
        int aux_cont = 1;

        for (Element form: forms) {
            try {
                String postURL = form.attr("action");
                Response response;

                //Verifica si es interna la direccion de post
                if(!postURL.contains("https")) {
                    postURL = url.concat(postURL);
                }
                response = Jsoup.connect(postURL)
                        .method(Method.POST)
                        .data("asignatura","practica1")
                        .header("matricula","20180224")
                        .execute();

                System.out.println("\nPOST #"+aux_cont+": \n Status code="+response.statusCode() + " ,URL=" + response.url());
                System.out.println(" Cabeceras: "+response.headers());

            } catch (IOException error) {
                System.out.println("\n " + error);
            }
        }
    }
}
