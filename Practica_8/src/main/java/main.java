
import Practica8.Controlador.Controlador;

import java.util.Scanner;

public class main {

    public static void main(String[] args) {


        System.out.println("Cliente");
        //String url = "http://localhost:7000/api/estudiante/";
        String url = "http://localhost:7000/api/estudiante/";

        System.out.println("REST API \n");

        int resp = 0;
        int aux_mat = 0;
        String aux_nom = null;
        String aux_car = null;
        Scanner temp = new Scanner(System.in);

        do{
            System.out.println("Digite el numero para realizar la accion indicada debajo \n");
            System.out.println("1 - Listar estudiante");
            System.out.println("2 - Consultar estudiante");
            System.out.println("3 - Crear estudiante");
            System.out.println("4 - Borrar estudiante");
            System.out.print("->");
            resp = temp.nextInt();

            if(resp == 1){
                Controlador.listar(url);
            } else if (resp == 2) {
                System.out.println("Matricula del estudiante:");
                System.out.print("->");
                aux_mat = temp.nextInt();
                Controlador.buscar(aux_mat,url);

            } else if (resp == 3) {
                System.out.println("Matricula del estudiante:");
                aux_mat = temp.nextInt();
                System.out.println("Nombre del estudiante:");
                temp.nextLine();
                aux_nom = temp.nextLine();
                System.out.println("Carrera que cursa: ");
                aux_car = temp.nextLine();
                Controlador.crear(aux_mat, aux_nom, aux_car, url);

                System.out.println("Estudiante registrado! ");

            } else if (resp == 4) {
                System.out.println("Matricula? :");
                System.out.print("->");
                aux_mat = temp.nextInt();
                Controlador.borrar(aux_mat, url);

                System.out.println("Estudiante ha sido eliminado");

            } else {
                System.out.println("Error, digite un numero valido");
            }

        }while(resp != 0);

    }




}
