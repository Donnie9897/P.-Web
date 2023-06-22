package Practica8.Controlador;

import Practica8.Clases.Estudiante;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

public class Controlador {

    public static void listar(String url)
    {
        HttpResponse<JsonNode> response = Unirest.get(url)
                .header("accept", "application/json")
                .queryString("apiKey", "123")
                .asJson();

        System.out.println("Respuesta: "+response.getStatus());
        JSONArray array = response.getBody().getArray();
        int cantidad = array.length();
        System.out.println("Respuesta: "+cantidad);

        for (int i = 0; i < cantidad; i++) {
            JSONObject object = array.getJSONObject(i);
            System.out.println("Matrícula del estudiante: "+object.getInt("matricula"));
            System.out.println("Nombre del estudiante: "+object.getString("nombre"));
            System.out.println("Carrera del estudiante: "+object.getString("carrera"));
        }
    }

    public static void borrar(int matricula, String url)
    {
        HttpResponse estudiante = Unirest.delete(url+matricula).asEmpty();
        System.out.println("Respuesta: "+estudiante.getStatus());
    }

    public static void buscar(int matricula, String url)
    {
        HttpResponse response = Unirest.get(url+matricula)
                .queryString("apiKey", "123")
                .asJson();

        System.out.println("Respuesta: "+response.getStatus());

        JSONObject object = new JSONObject(response.getBody().toString());

        System.out.println("Escriba la matrícula: "+object.getInt("matricula"));
        System.out.println("Escriba la nombre: "+object.getString("nombre"));
        System.out.println("Escriba la carrera: "+object.getString("carrera"));

    }

    public static void crear(int matricula, String nombre, String carrera, String url)
    {
        HttpResponse<JsonNode> estudiante = Unirest.post(url)
                .header("Content-Type", "application/json")
                .queryString("apiKey", "123")
                .body(new Estudiante(matricula,nombre,carrera))
                .asJson();
        System.out.println("Respuesta: "+estudiante.getStatus());
        System.out.println("MSJ: "+estudiante.getBody().toString());
    }


}
