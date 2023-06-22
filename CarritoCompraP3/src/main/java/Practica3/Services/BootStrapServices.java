package Practica3.Services;

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class BootStrapServices {
    private static Server server;
    private static BootStrapServices instancia;

    public static BootStrapServices getInstancia(){
        if(instancia == null){
            instancia=new BootStrapServices();
        }
        return instancia;
    }

    public void startDB() {
        try {
            //Modo servidor H2.
            Server.createTcpServer("-tcpPort",
                    "9092",
                    "-tcpAllowOthers",
                    "-tcpDaemon",
                    "-ifNotExists").start();
            //Abriendo el cliente web. El valor 0 representa puerto aleatorio.
            String status = Server.createWebServer("-trace", "-webPort", "0").start().getStatus();
            //
            System.out.println("Status Web: "+status);
        }catch (SQLException ex){
            System.out.println("Problema con la base de datos: "+ex.getMessage());
        }
    }

    public static void crearTablas() throws SQLException{
        EliminarTablas();
        crearTablaVentas();
        crearTablaProductos();
        crearTablaVentasProductos();
    }

    public static void EliminarTablas() throws SQLException {

        String query = "DROP TABLE IF EXISTS VENTA,PRODUCTO,VENTAPRODUCTOS;";
        System.out.println("se eliminaron");
        ejecutarQuery(query);
    }
    public static void crearTablaProductos() throws SQLException {

        String query = "CREATE TABLE IF NOT EXISTS PRODUCTO\n" +
                "(\n" +
                "  ID INTEGER PRIMARY KEY  NOT NULL GENERATED ALWAYS AS IDENTITY,\n" +
                "  NOMBRE VARCHAR(100) NOT NULL,\n" +
                "  ESTADO VARCHAR(100) NOT NULL DEFAULT 'TRUE',\n" +
                "  PRECIO INTEGER NOT NULL, \n" +
                "  DESCRIPCION VARCHAR(500) NOT NULL \n" +
                ");";

        ejecutarQuery(query);
    }

    public static void crearTablaVentas() throws SQLException {

        String query = "CREATE TABLE IF NOT EXISTS VENTA\n"+
                "(\n" +
                " ID INTEGER PRIMARY KEY  NOT NULL GENERATED ALWAYS AS IDENTITY  ,\n"+
                " FECHA DATE NOT NULL,\n"+
                " NOMBRE VARCHAR(25) NOT NULL \n" +
                ");";

        ejecutarQuery(query);

    }

    public static void crearTablaVentasProductos() throws SQLException {

        String query = "CREATE TABLE IF NOT EXISTS VENTAPRODUCTOS\n"+
                "(\n" +
                " VENTAID INTEGER NOT NULL,\n"+
                " PRODUCTOID INTEGER NOT NULL,\n"+
                " CANTIDAD INTEGER NOT NULL, \n" +
                "FOREIGN KEY (VENTAID) REFERENCES VENTA(ID), \n" +
                "FOREIGN KEY (PRODUCTOID) REFERENCES PRODUCTO(ID), \n"+
                "CONSTRAINT PK_ID PRIMARY KEY (VENTAID,PRODUCTOID) \n" +
                ");";

        ejecutarQuery(query);

    }

    public static void ejecutarQuery(String query) throws SQLException{
        Connection connection = ServicioDBS.getInstance().getConnection();
        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
        connection.close();
    }

    public static void stopDB() throws SQLException {
        if(server != null){
            server.stop();
        }
    }

    public void init(){
        startDB();
    }

}
