/*------------------------------------------------------------------------------------------
:*                         TECNOLOGICO NACIONAL DE MEXICO
:*                                CAMPUS LA LAGUNA
:*                     INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                             DESARROLLO EN ANDROID "A"
:*
:*                   SEMESTRE: ENE-JUN/2023    HORA: 08-09 HRS
:*
:*           Clase que realiza la creacion y actualizacion de la base de datos
:*
:*  Archivo     : DataBaseHelper.java
:*  Autor       : Pedro Lopez Ramirez   19130541
:*  Fecha       : 26/04/2023
:*  Compilador  : Android Studio Electric Eel 2022.1
:*  Descripcion : En esta clase se crea la base de datos, ademas de que se define la
:*                version de esta, en caso de cambiar la version de la BD, esta no se volvera
:*                a crear, si no que solo se actualizara
:*  Ultima modif:
:*  Fecha       Modifico             Motivo
:*==========================================================================================
:*
:*------------------------------------------------------------------------------------------*/

package mx.itlalaguna.c19130541.apprecyclerviewv01.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "definiciones.db";
    public static final String TABLA_DEFINICIONES = "t_definiciones";

    //----------------------------------------------------------------------------------------------
    //Constructor base de la clase DataBaseHelper
    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Se crea la base de datos en caso de que no este creada antes
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLA_DEFINICIONES + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombreConcepto TEXT NOT NULL," +
                "textoDefinicion TEXT NOT NULL)");
    }

    //----------------------------------------------------------------------------------------------
    //Al actualizar la BD, esta se elimina y ademas se le agrega lo que esta en tabla_definiciones
    //Aplica cuando se cambia de version de base de datos
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE " + TABLA_DEFINICIONES);
        onCreate(sqLiteDatabase);
    }
}
