package com.example.pedidosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class QueryActivity extends AppCompatActivity {

    //Variable local list view
    ListView list1;
    ArrayList<String> listado;
    //instanciar para usar la ventana de mensajes
    ProductoActivity prod = new ProductoActivity();

    //instanciando el cuadro de texto
    EditText edt1;
    //instanciando el botón
    ImageButton btn1;
    //el QR
    ImageView imv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query);
        list1 = findViewById(R.id.list_total);
        edt1 = findViewById(R.id.et_ped_query);
        btn1 = findViewById(R.id.btn_ped_buscar_query);
        imv1 = findViewById(R.id.iv_qr_generado);
        //boton para buscar al pedido por id
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Comprobar que no esté vacio el cuadro de texto
                if (edt1.getText().toString().isEmpty()) {
                    prod.ventanaMensaje(QueryActivity.this, "Introduzca un código de producto válido");
                } else {
                    String[] cod = {edt1.getText().toString()};
                    //comprobar que el pedido exista en la base de datos
                    if (buscaIDPedido(cod)) {
                        //si existe cargarlo
                        cargarLista(cod);
                    } else {
                        prod.ventanaMensaje(QueryActivity.this, "El código no se encuentra en la base de datos");
                    }
                }
            }
        });

    }

    //se necesita una lista para poder almacenar los datos que provienden de la base de datos
    private ArrayList<String> ListaPedidos(String[] cod) {
        ArrayList<String> datos = new ArrayList<String>();
        DBHelper helper = new DBHelper(QueryActivity.this);
        SQLiteDatabase db = helper.getReadableDatabase();
        //la consulta debe hacerse con un inner join para aprovechar la llave foranea
        String consulta = "select b.nombre, b.descripcion, pedidos.nombre_cliente," +
                "pedidos.celular_cliente, pedidos.fecha_pedido, pedidos.cantidad_producto, pedidos.precio_total " +
                "from pedidos inner join productos b on b.codigo = pedidos.cod_producto where pedidos.codigo =?";
        try {
            Cursor c = db.rawQuery(consulta, cod);
            if (c.moveToFirst()) {
                //si es que hay datos en la base de datos volveran los resultados
                do {
                    String linea = "Nombre del producto: " + c.getString(0) + "\nDescripcion: " + c.getString(1) +
                            "\nNombre del cliente: " + c.getString(2) +
                            "\nCelular del cliente: " + c.getString(3) +
                            "\nFecha del pedido: " + c.getString(4) +
                            "\nCantidad de producto: " + c.getInt(5) +
                            "\nPrecio total: " + c.getFloat(6);
                    datos.add(linea);
                } while (c.moveToNext());
            } else {
                //si no hay datos un mensaje de error
                prod.ventanaMensaje(QueryActivity.this, "No se encontraron datos para mostrar");
            }
            c.close();
            db.close();
        } catch (Exception e) {
            Toast.makeText(getApplication(), "ERROR " + e, Toast.LENGTH_LONG).show();
        }
        return datos;
    }

    //permite cargar la lista en un adaptador para usar el arraylist
    private void cargarLista(String[] cod) {
        listado = ListaPedidos(cod);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(QueryActivity.this,
                android.R.layout.simple_list_item_1, listado);
        list1.setAdapter(adapter);
    }

    //busca un pedido por el id
    public boolean buscaIDPedido(String[] cod) {
        //instanciamos data base helper
        DBHelper helper = new DBHelper(QueryActivity.this);
        //instanciamos la libreria de sqlite
        SQLiteDatabase db = helper.getReadableDatabase();
        //generamos la consulta
        String consulta = "select * from pedidos where codigo =?";
        try {
            //ejecutamos la consulta dentro un try catch por si hubiera algun error
            Cursor c = db.rawQuery(consulta, cod);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                //si existe retornamos true
                return true;
            } else {
                edt1.setText("");
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(getApplication(), "ERROR " + e,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}