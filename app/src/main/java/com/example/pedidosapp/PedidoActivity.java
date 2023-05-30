package com.example.pedidosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PedidoActivity extends AppCompatActivity {
    int x = 0;
    int y = 0;
    //instanciando la clase Producto
    ProductoActivity prod = new ProductoActivity();
    //creando variables locales
    ImageButton btn1;
    Button btn2, btn3, btn4, btn5;
    //creando variables locales
    EditText edt1, edt2, edt3, edt4, edt5, edt6;
    TextView tv1;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedido);
        calendar= Calendar.getInstance();
        //sincronizar con el layout
        btn1 = findViewById(R.id.btn_ped_buscar);
        btn2 = findViewById(R.id.btn_ped_add);
        btn3 = findViewById(R.id.btn_ped_mod);
        btn4 = findViewById(R.id.btn_ped_del);
        btn5 = findViewById(R.id.btn_calcular);
        edt1 = findViewById(R.id.et_ped_cod);
        edt2 = findViewById(R.id.et_ped_pro);
        edt3 = findViewById(R.id.et_ped_nombre);
        edt4 = findViewById(R.id.et_ped_cel);
        edt5 = findViewById(R.id.et_ped_cantidad);
        edt6 = findViewById(R.id.et_ped_fecha);
        tv1 = findViewById(R.id.tv_ped_tot);

        //este metodo permite crear la fecha y actualizar el campo fecha con un calendario
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
                actualizaFecha();
            }
        };
        // aqui al dar click abrimos el calendario
        edt6.setOnClickListener( view -> {
            new DatePickerDialog(PedidoActivity.this,date,calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se verifica que el campo no esté vacío, por que si esta vacio no se podra buscar el producto
                if (edt1.getText().toString().isEmpty()) {
                    //si el campo Codigo producto esta vacio se llama una alerta con un mensaje
                    prod.ventanaMensaje(PedidoActivity.this, "Debe llenar el código de pedido para poder buscarlo");

                } else {
                    //si el campo codigo tiene un elemento entonces lo guardamos en la variable cod
                    String[] cod = {edt1.getText().toString()};
                    //usamos la funcion creada para buscar por id
                    buscaIDPedido(cod);
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt2.getText().toString().isEmpty() || edt3.getText().toString().isEmpty() ||
                        edt4.getText().toString().isEmpty() || edt5.getText().toString().isEmpty() ||
                        edt6.getText().toString().isEmpty()) {
                    //usando la alerta mensaje
                    prod.ventanaMensaje(PedidoActivity.this, "Debe llenar los campos:\n" +
                            "\t\t* Código del producto\n\t\t* Nombre del cliente\n\t\t* Celular del cliente" +
                            "\n\t\t* Cantidad de pedido\n\t\t* fecha del pedido");
                } else {
                    if (tv1.getText() == "") {
                        prod.ventanaMensaje(PedidoActivity.this, "Debe calcular el precio total\n" +
                                "dando click al botón calcular");
                    } else {
                        String[] cod ={edt2.getText().toString()};
                        if (verificarPro(cod)){
                            int cod_pro = Integer.parseInt(edt2.getText().toString());
                            String nombre = edt3.getText().toString();
                            String celular = edt4.getText().toString();
                            int cantidad = Integer.parseInt(edt5.getText().toString());
                            String fecha = edt6.getText().toString();
                            Float precio = Float.parseFloat(tv1.getText().toString());
                            guardarPedido(cod_pro, nombre, celular, cantidad, fecha, precio);
                        }else{
                            prod.ventanaMensaje(PedidoActivity.this, "Ingrese un código de producto válido," +
                                    "\nEl producto ingresado no existe");
                        }
                    }
                }
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt2.getText().toString().isEmpty() || edt5.getText().toString().isEmpty()) {
                    //usando la alerta mensaje
                    prod.ventanaMensaje(PedidoActivity.this, "Debe llenar los campos:\n" +
                            "\t\t* Código del producto\n\t\t* Cantidad de pedido");
                } else {
                    String[] cod = {edt2.getText().toString()};
                    Float cantidad = Float.parseFloat(edt5.getText().toString());
                    float a = buscaID(cod);
                    if (a != 0.0F) {
                        Float r = cantidad * a;
                        tv1.setText(r.toString());
                        x = Integer.parseInt(edt2.getText().toString());
                        y = Integer.parseInt(edt5.getText().toString());
                    }
                }
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt1.getText().toString().isEmpty() || edt2.getText().toString().isEmpty() ||
                        edt3.getText().toString().isEmpty() || edt4.getText().toString().isEmpty() ||
                        edt5.getText().toString().isEmpty() || edt6.getText().toString().isEmpty()) {
                    //usando la alerta mensaje
                    prod.ventanaMensaje(PedidoActivity.this, "Debe llenar los campos:\n" +
                            "\t\t* Código del pedido\n\t\t* Código del producto\n\t\t* Nombre del cliente" +
                            "\n\t\t* Celular del cliente" +
                            "\n\t\t* Cantidad de pedido\n\t\t* fecha del pedido");
                } else {
                    String[] cod = {edt1.getText().toString()};
                    int cod_pro = Integer.parseInt(edt2.getText().toString());
                    String nombre = edt3.getText().toString();
                    String celular = edt4.getText().toString();
                    int cantidad = Integer.parseInt(edt5.getText().toString());
                    String fecha = edt6.getText().toString();
                    if (tv1.getText() == "" || cod_pro != x || cantidad != y) {
                        prod.ventanaMensaje(PedidoActivity.this, "Debe calcular el precio total\n" +
                                "dando click al botón calcular");
                    } else {
                        Float precio = Float.parseFloat(tv1.getText().toString());
                        modificarPedido(cod, cod_pro, nombre, celular, cantidad, fecha, precio);
                    }
                }
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt1.getText().toString().isEmpty()) {
                    prod.ventanaMensaje(PedidoActivity.this, "Es necesario el código del pedido a borrar");
                } else {
                    String[] cod = {edt1.getText().toString()};
                    eliminarPedido(cod);
                }

            }
        });
    }
    //actualiza la fecha de acuerdo al calendario
    public void actualizaFecha(){
        //organiza la fecha con el formato requerido
        String miFormato = "dd/MM/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(miFormato, Locale.US);
        edt6.setText(dateFormat.format(calendar.getTime()));
    }

    //busca un pedido por el id
    public void buscaIDPedido(String[] cod) {
        //instanciamos data base helper
        DBHelper helper = new DBHelper(PedidoActivity.this);
        //instanciamos la libreria de sqlite
        SQLiteDatabase db = helper.getReadableDatabase();
        //generamos la consulta
        String consulta = "select * from pedidos where codigo =?";
        try {
            //ejecutamos la consulta dentro un try catch por si hubiera algun error
            Cursor c = db.rawQuery(consulta, cod);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                //llenar los campos con los valores encontrados
                edt2.setText(c.getString(1));
                edt3.setText(c.getString(2));
                edt4.setText(c.getString(3));
                edt5.setText(c.getString(4));
                edt6.setText(c.getString(5));
                tv1.setText(c.getString(6));
                x = Integer.parseInt(c.getString(1));
                y = Integer.parseInt(c.getString(4));
            } else {
                prod.ventanaMensaje(PedidoActivity.this, "No existen datos para mostrar");
                limpiarPedido();
            }
        } catch (Exception e) {
            Toast.makeText(getApplication(), "ERROR " + e,
                    Toast.LENGTH_SHORT).show();
        }
    }

    //función que guarda los datos de pedidos en la base de datos
    public void guardarPedido(int producto, String nombre, String celular, int cantidad,
                              String fecha, Float precio) {
        //necesario instanciar la clase DBHelper que contiene la conexion a la base de datos
        DBHelper helper = new DBHelper(PedidoActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            //se colocan los valores en el contenedor
            ContentValues c = new ContentValues();
            c.put("cod_producto", producto);
            c.put("nombre_cliente", nombre);
            c.put("celular_cliente", celular);
            c.put("cantidad_producto", cantidad);
            c.put("fecha_pedido", fecha);
            c.put("precio_total", precio);
            //se insertan en la base de datos
            db.insert("pedidos", null, c);
            //se cierra la base de datos
            db.close();
            // un pequeño mensaje de registrado con exito
            Toast.makeText(getApplication(), "Registro exitoso", Toast.LENGTH_SHORT).show();
            //y volver todas las casillas a vacio
            limpiarPedido();
        } catch (Exception e) {
            Toast.makeText(getApplication(), "ERROR " + e, Toast.LENGTH_SHORT).show();
        }
    }

    //función que modifica los datos de pedidos en la base de datos
    public void modificarPedido(String[] cod, int producto, String nombre, String celular, int cantidad,
                                String fecha, Float precio) {
        //necesario instanciar la clase DBHelper que contiene la conexion a la base de datos
        DBHelper helper = new DBHelper(PedidoActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            //se colocan los valores en el contenedor
            ContentValues c = new ContentValues();
            c.put("cod_producto", producto);
            c.put("nombre_cliente", nombre);
            c.put("celular_cliente", celular);
            c.put("cantidad_producto", cantidad);
            c.put("fecha_pedido", fecha);
            c.put("precio_total", precio);
            //actualizamos la tabla
            db.update("pedidos", c, "codigo =?", cod);
            //se cierra la base de datos
            db.close();
            // un pequeño mensaje de modificado con exito
            Toast.makeText(getApplication(), "Modificado exitoso", Toast.LENGTH_SHORT).show();
            //y volver todas las casillas a vacio
            limpiarPedido();
        } catch (Exception e) {
            Toast.makeText(getApplication(), "ERROR " + e, Toast.LENGTH_SHORT).show();
        }
    }

    //funcion para borrar un pedido de la base de datos
    public void eliminarPedido(String[] cod) {
        //necesario instanciar la clase DBHelper que contiene la conexion a la base de datos
        DBHelper helper = new DBHelper(PedidoActivity.this);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        SQLiteDatabase db = helper.getWritableDatabase();
        String consulta = "select * from pedidos where codigo =?";
        try {
            Cursor c = db1.rawQuery(consulta, cod);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                db.delete("pedidos", "codigo =?", cod);
                Toast.makeText(getApplication(), "Eliminado exitoso", Toast.LENGTH_SHORT).show();
                db1.close();
                db.close();
                limpiarPedido();
            } else {
                prod.ventanaMensaje(PedidoActivity.this, "No existe ese registro");
                edt1.setText("");
            }
        } catch (Exception e) {
            Toast.makeText(getApplication(), "ERROR " + e, Toast.LENGTH_SHORT).show();
        }
    }

    //funcion que busca por codigo de producto
    public float buscaID(String[] cod) {
        float a = 0.0F;
        //instanciamos data base helper
        DBHelper helper = new DBHelper(PedidoActivity.this);
        //instanciamos la libreria de sqlite
        SQLiteDatabase db = helper.getReadableDatabase();
        //generamos la consulta
        String consulta = "select precio from productos where codigo =?";
        try {
            //ejecutamos la consulta dentro un try catch por si hubiera algun error
            Cursor c = db.rawQuery(consulta, cod);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                a = Float.parseFloat(c.getString(0));
                return a;
            } else {
                prod.ventanaMensaje(PedidoActivity.this, "No existen datos para mostrar\n" +
                        "para ese producto");
                edt2.setText("");
                tv1.setText("");
                return a;
            }
        } catch (Exception e) {
            Toast.makeText(getApplication(), "ERROR " + e,
                    Toast.LENGTH_SHORT).show();
        }
        return a;
    }

    //para verificar si el producto existe
    public boolean verificarPro(String[] cod) {
        //instanciamos data base helper
        DBHelper helper = new DBHelper(PedidoActivity.this);
        //instanciamos la libreria de sqlite
        SQLiteDatabase db = helper.getReadableDatabase();
        //generamos la consulta
        String consulta = "select * from productos where codigo =?";
        try {
            //ejecutamos la consulta dentro un try catch por si hubiera algun error
            Cursor c = db.rawQuery(consulta, cod);
            if (c != null && c.getCount() > 0) {
                return true;
            } else {
                edt2.setText("");
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(getApplication(), "ERROR " + e,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //limpiar
    public void limpiarPedido() {
        edt1.setText("");
        edt2.setText("");
        edt3.setText("");
        edt4.setText("");
        edt5.setText("");
        edt6.setText("");
        tv1.setText("0.0");
    }

}