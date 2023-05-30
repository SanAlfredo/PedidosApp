package com.example.pedidosapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NOMBRE = "registrador.db";
    private static final int DB_VERSION = 1;
    public static final String TABLA_PRODUCTOS = "productos";
    public static final String TABLA_PEDIDOS = "pedidos";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NOMBRE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLA_PRODUCTOS+"(" +
                "codigo integer primary key autoincrement," +
                "nombre text,descripcion text,precio real)");
        db.execSQL("create table if not exists "+TABLA_PEDIDOS+"(" +
                "codigo integer primary key autoincrement," +
                "cod_producto integer,nombre_cliente text," +
                "celular_cliente text, cantidad_producto integer," +
                "fecha_pedido text,precio_total real, " +
                "foreign key (cod_producto) references "+TABLA_PRODUCTOS+"(codigo))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table "+TABLA_PRODUCTOS);
        db.execSQL("drop table "+TABLA_PEDIDOS);
        onCreate(db);
    }

}
