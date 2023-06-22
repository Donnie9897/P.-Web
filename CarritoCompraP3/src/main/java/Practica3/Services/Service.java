package Practica3.Services;

import Practica3.Entidades.Comprado;
import Practica3.Entidades.Producto;
import Practica3.Entidades.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Service {
    private static Service instancia;
    private List<Venta> ventas;
    private int cont;
    private long carrito;

    public Service() {
        //BootStrapServices.startDB();

        ServicioDBS.getInstance().testConnection();
        try {
            BootStrapServices.crearTablas();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static Service getInstance(){
        if(instancia == null){
            instancia = new Service();
        }
        return instancia;
    }

    public Producto registrarProducto(Producto producto){
        boolean ok =false;

        Connection connection = null;
        try {
            String query = "INSERT INTO PRODUCTO(NOMBRE,PRECIO,DESCRIPCION) values(?,?,?)";
            connection = ServicioDBS.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, producto.getNombre());
            preparedStatement.setInt(2,producto.getPrecio());
            preparedStatement.setString(3,producto.getDescripcion());
            int fila = preparedStatement.executeUpdate();
            System.out.printf("PrOducto registrado");
        } catch (SQLException ex){
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            try{
                connection.close();
            } catch (SQLException ex){
                Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return producto;
    }

    public boolean addVentas(Venta venta) {
        boolean ok =false;

        Connection con = null;
        try {
            String query = "insert into venta(fecha, nombre) values(CURRENT_DATE,?)";
            con = ServicioDBS.getInstance().getConnection();
            //
            PreparedStatement prepareStatement = con.prepareStatement(query);
            //Antes de ejecutar seteo los parametros.
            prepareStatement.setString(1, venta.getNombreCliente());

            int fila = prepareStatement.executeUpdate();
            addProductosVenta(fila, venta.getListaProductos());
            ok = fila > 0 ;

            System.out.println("Venta hecha");

        } catch (SQLException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ok;
    }

    public boolean addProductosVenta(int fila, List<Comprado> listaProductos){
        boolean ok =false;

        Connection con = null;
        for (Comprado producto : listaProductos) {
            try {
                String query = "insert into ventaproductos(ventaid, productoid,cantidad) values(?,?,?)";
                con = ServicioDBS.getInstance().getConnection();
                PreparedStatement prepareStatement = con.prepareStatement(query);
                prepareStatement.setInt(1, fila);
                prepareStatement.setInt(2,producto.getProductId());
                prepareStatement.setInt(3,producto.getCantidad());


                int fil = prepareStatement.executeUpdate();
                ok = fil > 0 ;

            } catch (SQLException ex) {
                Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
            } finally{
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return ok;

    }


}
