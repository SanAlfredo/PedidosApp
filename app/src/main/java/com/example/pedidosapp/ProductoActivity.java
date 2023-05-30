package com.example.pedidosapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ProductoActivity extends AppCompatActivity {
    //instanciar los botones
    ImageButton btn1;
    Button btn2, btn3;
    //instanciar los input
    EditText edt1, edt2, edt3, edt4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.producto);
        //encontramos por el id
        btn1 = findViewById(R.id.btn_pro_buscar);
        btn2 = findViewById(R.id.btn_pro_add);
        btn3 = findViewById(R.id.btn_pro_mod);
        edt1 = findViewById(R.id.et_pro_cod);
        edt2 = findViewById(R.id.et_pro_nombre);
        edt3 = findViewById(R.id.et_pro_descripcion);
        edt4 = findViewById(R.id.et_pro_precio);
        //metodos en los botones
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se verifica que el campo no esté vacío, por que si esta vacio no se podra buscar el producto
                if (edt1.getText().toString().isEmpty()) {
                    //si el campo Codigo producto esta vacio se llama una alerta con un mensaje
                    ventanaMensaje(ProductoActivity.this,"Debe llenar el código de producto para poder buscarlo");

                } else {
                    //si el campo codigo tiene un elemento entonces lo guardamos en la variable cod
                    String[] cod = {edt1.getText().toString()};
                    //usamos la funcion creada para buscar por id
                    buscaID(cod);
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt2.getText().toString().isEmpty() || edt3.getText().toString().isEmpty() ||
                        edt4.getText().toString().isEmpty()) {
                    //usando la alerta mensaje
                    ventanaMensaje(ProductoActivity.this,"Debe llenar los campos:\n" +
                            "\t\t* Nombre del producto\n\t\t* Descripción\n\t\t* Precio");
                } else {
                    String nombre = edt2.getText().toString();
                    String descripcion = edt3.getText().toString();
                    Float precio = Float.parseFloat(edt4.getText().toString());
                    guardarProducto(nombre, descripcion, precio);
                }
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt1.getText().toString().isEmpty() || edt2.getText().toString().isEmpty()
                        || edt3.getText().toString().isEmpty() || edt4.getText().toString().isEmpty()) {
                    //usando la alerta mensaje
                    ventanaMensaje(ProductoActivity.this,"Debe llenar los campos:\n" +
                            "\t\t* Código del producto\n\t\t* Nombre del producto\n\t\t* Descripción\n\t\t* Precio");

                } else {
                    String[] cod = {edt1.getText().toString()};
                    String nombre = edt2.getText().toString();
                    String descripcion = edt3.getText().toString();
                    Float precio = Float.parseFloat(edt4.getText().toString());
                    modificarProducto(cod, nombre, descripcion, precio);
                }
            }
        });
    }

    // función que llama a una ventana de dialogo con el mensaje que se le pone
    public void ventanaMensaje(Context context,String mensaje) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(context);
        dialogo.setMessage(mensaje)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        dialogo.show();
    }

    //función que guarda los datos de productos en la base de datos
    public void guardarProducto(String nombre, String descripcion, Float precio) {
        //necesario instanciar la clase DBHelper que contiene la conexion a la base de datos
        DBHelper helper = new DBHelper(ProductoActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            //se colocan los valores en el contenedor
            ContentValues c = new ContentValues();
            c.put("nombre", nombre);
            c.put("descripcion", descripcion);
            c.put("precio", precio);
            //se insertan en la base de datos
            db.insert("productos", null, c);
            //se cierra la base de datos
            db.close();
            // un pequeño mensaje de registrado con exito
            Toast.makeText(getApplication(), "Registro exitoso", Toast.LENGTH_SHORT).show();
            //y volver todas las casillas a vacio
            limpiarProducto();
        } catch (Exception e) {
            Toast.makeText(getApplication(), "ERROR " + e, Toast.LENGTH_SHORT).show();
        }
    }

    //funcion que busca por codigo de producto
    public void buscaID(String[] cod) {
        //instanciamos data base helper
        DBHelper helper = new DBHelper(ProductoActivity.this);
        //instanciamos la libreria de sqlite
        SQLiteDatabase db = helper.getReadableDatabase();
        //generamos la consulta
        String consulta = "select * from productos where codigo =?";
        try {
            //ejecutamos la consulta dentro un try catch por si hubiera algun error
            Cursor c = db.rawQuery(consulta, cod);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                //llenar los campos con los valores encontrados
                edt2.setText(c.getString(1));
                edt3.setText(c.getString(2));
                edt4.setText(c.getString(3));
            } else {
                ventanaMensaje(ProductoActivity.this,"No existen datos para mostrar");
                limpiarProducto();
            }
        } catch (Exception e) {
            Toast.makeText(getApplication(), "ERROR " + e,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void modificarProducto(String[] cod, String nombre, String descripcion, Float precio) {
        //necesario instanciar la clase DBHelper que contiene la conexion a la base de datos
        DBHelper helper = new DBHelper(ProductoActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            //se colocan los valores en el contenedor
            ContentValues c = new ContentValues();
            c.put("nombre", nombre);
            c.put("descripcion", descripcion);
            c.put("precio", precio);
            //actualizamos la tabla
            db.update("productos", c, "codigo =?", cod);
            //se cierra la base de datos
            db.close();
            // un pequeño mensaje de modificado con exito
            Toast.makeText(getApplication(), "Modificado con exito", Toast.LENGTH_SHORT).show();
            //y volver todas las casillas a vacio
            limpiarProducto();
        } catch (Exception e) {
            Toast.makeText(getApplication(), "ERROR " + e, Toast.LENGTH_SHORT).show();
        }
    }

    //para limpiar los campos
    public void limpiarProducto() {
        edt1.setText("");
        edt2.setText("");
        edt3.setText("");
        edt4.setText("");
    }
}