/*------------------------------------------------------------------------------------------
:*                         TECNOLOGICO NACIONAL DE MEXICO
:*                                CAMPUS LA LAGUNA
:*                     INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                             DESARROLLO EN ANDROID "A"
:*
:*                   SEMESTRE: ENE-JUN/2023    HORA: 08-09 HRS
:*
:*       Clase para editar, borrar, mostrar y agregar definiciones a la base de datos
:*
:*  Archivo     : DBDefiniciones.java
:*  Autor       : Pedro Lopez Ramirez   19130541
:*  Fecha       : 26/04/2023
:*  Compilador  : Android Studio Electric Eel 2022.1
:*  Descripcion : Esta clase incluye varios metodos, los principales para agregar, editar y borrar,
:*                y tambien para poder mostrar las definiciones ya sea solo una en un solo activity
:*                o todas las definiciones mediante items del RecyclerView
:*  Ultima modif:
:*  Fecha       Modifico             Motivo
:*==========================================================================================
:*
:*------------------------------------------------------------------------------------------*/

package mx.itlalaguna.c19130541.apprecyclerviewv01.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import mx.itlalaguna.c19130541.apprecyclerviewv01.entidades.Definiciones;

public class DBDefiniciones extends DataBaseHelper {

    Context context;

    //----------------------------------------------------------------------------------------------
    //Constructor de DBDefiniciones
    public DBDefiniciones(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    //----------------------------------------------------------------------------------------------
    //Metodo que agrega definiciones nuevas en la base de datos
    public long insertarDefinicion(String tituloDefinicion, String definicion) {
        long id = 0;
        try {
            DataBaseHelper dbHelper = new DataBaseHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //Se inicializa "valores" que tendra lo que se va a insertar en la base de datos
            ContentValues valores = new ContentValues();
            valores.put("nombreConcepto", tituloDefinicion);
            valores.put("textoDefinicion", definicion);
            //Una vez tengamos los valores los pasamos al insert de la base de datos
            id = db.insert(TABLA_DEFINICIONES, null, valores);
        } catch (Exception ex) {
            ex.toString();
        }
        return id;
    }

    //----------------------------------------------------------------------------------------------
    //Metodo que edita definiciones ya agregadas en la base de datos
    public boolean editarDefinicion(int id, String tituloDefinicion, String definicion) {
        boolean bandera = false;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL(" UPDATE " + TABLA_DEFINICIONES + " SET nombreConcepto = '" + tituloDefinicion +
                    "', textoDefinicion = '" + definicion + "' WHERE id = '" + id + "'");
            bandera = true;
        } catch (Exception ex) {
            ex.toString();
        } finally {
            //no importa si se actualiza o no, solo se cierra la base de datos
            db.close();
        }
        return bandera;
    }

    //----------------------------------------------------------------------------------------------
    //Metodo con el cual se muestran todas las definiciones en el RecyclerView
    public ArrayList<Definiciones> mostrarDefiniciones() {
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<Definiciones> listaDefiniciones = new ArrayList<>();
        Definiciones definicion = null;
        Cursor cursorDefinicion = null;

        //Se hace un select y se muestran las definiciones por orden de ID
        cursorDefinicion = db.rawQuery("SELECT * FROM " + TABLA_DEFINICIONES, null);
        if (cursorDefinicion.moveToFirst()) {
            do {
                definicion = new Definiciones();
                definicion.setId(cursorDefinicion.getInt(0));
                definicion.setTitulo(cursorDefinicion.getString(1));
                definicion.setDefinicion(cursorDefinicion.getString(2));
                listaDefiniciones.add(definicion);
            } while (cursorDefinicion.moveToNext());
        }
        cursorDefinicion.close();
        return listaDefiniciones;
    }

    //----------------------------------------------------------------------------------------------
    //Metodo para mostrar una definicion en espesifico
    public Definiciones verDefinicion(int id) {
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Definiciones definicion = null;
        Cursor cursorDefinicion = null;

        //Se consulta el dato seleccionado y se muestra en otro activity
        cursorDefinicion = db.rawQuery(" SELECT * FROM " + TABLA_DEFINICIONES + " WHERE id = " + id + " LIMIT 1 ", null);
        if (cursorDefinicion.moveToFirst()) {
            definicion = new Definiciones();
            definicion.setId(cursorDefinicion.getInt(0));
            definicion.setTitulo(cursorDefinicion.getString(1));
            definicion.setDefinicion(cursorDefinicion.getString(2));
        }
        cursorDefinicion.close();
        return definicion;
    }

    //----------------------------------------------------------------------------------------------
    //Metodo para eliminar definiciones
    public boolean suprimirDefinicion(int id) {
        boolean bandera = false;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Se necesita del id para eliminar, por lo que se tiene que estar previamente en la definicion
        try {
            db.execSQL(" DELETE FROM " + TABLA_DEFINICIONES + " WHERE id = '" + id + "'");
            bandera = true;
        } catch (Exception ex) {
            ex.toString();
        } finally {
            db.close();
        }
        return bandera;
    }

    //----------------------------------------------------------------------------------------------
    public boolean suprimirTodo() {
        boolean bandera = false;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL(" DELETE FROM " + TABLA_DEFINICIONES);
            bandera = true;
        } catch (Exception ex) {
            ex.toString();
        } finally {
            db.close();
        }
        return bandera;
    }

    //----------------------------------------------------------------------------------------------
    public void importDataFromTextFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");

                if (data.length == 3) {
                    String tituloDefinicion = data[0].trim();
                    String sutbtitle = data[1].trim();  //no se necesita para esta app
                    String definicion = data[2].trim();

                    // Insertar los datos en la base de datos
                    insertarDefinicion(tituloDefinicion, definicion);
                } else if (data.length == 2){
                    String tituloDefinicion = data[0].trim();
                    String definicion = data[1].trim();

                    // Insertar los datos en la base de datos
                    insertarDefinicion(tituloDefinicion, definicion);
                }
            }
            reader.close();

            // Mostrar un mensaje de éxito
            Toast.makeText(context, "Datos importados correctamente, Recargando app...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Mostrar un mensaje de error en caso de fallo en la importación
            Toast.makeText(context, "Ocurrio un error al importar los datos, intenta de nuevo", Toast.LENGTH_SHORT).show();
        }
    }

    //----------------------------------------------------------------------------------------------
    public void exportDataToTextFile(File file) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT nombreConcepto, textoDefinicion FROM " + TABLA_DEFINICIONES, null);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            while (cursor.moveToNext()) {
                String tituloDefinicion = cursor.getString(0);
                String definicion = cursor.getString(1);

                // Escribir los datos en el archivo de texto
                writer.write(tituloDefinicion + "||" + definicion + "||");
                writer.newLine();
            }
            writer.close();
            cursor.close();

            // Mostrar un mensaje de éxito
            Toast.makeText(context, "Los datos han sido exportados correctamente", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Mostrar un mensaje de error en caso de fallo en la exportación
            Toast.makeText(context, "Ocurrio un error al exportar los datos, intenta de nuevo", Toast.LENGTH_SHORT).show();
        }
    }
}
