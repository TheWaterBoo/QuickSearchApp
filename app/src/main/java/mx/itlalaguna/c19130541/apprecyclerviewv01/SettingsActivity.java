/*------------------------------------------------------------------------------------------
:*                         TECNOLOGICO NACIONAL DE MEXICO
:*                                CAMPUS LA LAGUNA
:*                     INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                             DESARROLLO EN ANDROID "A"
:*
:*                   SEMESTRE: ENE-JUN/2023    HORA: 08-09 HRS
:*
:*                  Activity que despliega la pantalla de ajustes
:*
:*  Archivo     : SettingsActivity.java
:*  Autor       : Pedro Lopez Ramirez   19130541
:*  Fecha       : 26/04/2023
:*  Compilador  : Android Studio Electric Eel 2022.1
:*  Descripcion : Este activity despliega una pantalla con los ajustes disponibles para la app
:*                por ejemplo poder cambiar de tema, tamaño de letra, etc...
:*  Ultima modif:
:*  Fecha       Modifico             Motivo
:*==========================================================================================
:*
:*------------------------------------------------------------------------------------------*/

package mx.itlalaguna.c19130541.apprecyclerviewv01;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import java.io.File;

import mx.itlalaguna.c19130541.apprecyclerviewv01.database.DBDefiniciones;

public class SettingsActivity extends AppCompatActivity {

    Switch botonCambioTema;
    Button botonEliminarTodo;

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String ruta = getApplicationContext().getExternalFilesDir(null) + File.separator;
        //Primero se comprueba el estado del tema, antes de crear los componetes
        setDayNight();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Se crea y define sharedPreferences, donde se almacenan las preferencias de la app
        SharedPreferences sp = getSharedPreferences("SP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        botonCambioTema = findViewById(R.id.switchTema);
        botonEliminarTodo = findViewById(R.id.buttonClearAll);

        int tema = sp.getInt("Tema", 1);
        if(tema == 1){
            botonCambioTema.setChecked(false);
        } else {
            botonCambioTema.setChecked(true);
        }

        DBDefiniciones dbDefiniciones = new DBDefiniciones(SettingsActivity.this);

        //Listener para el boton switch de cambio de tema, dependiendo del tema se queda marcado
        botonCambioTema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (botonCambioTema.isChecked()){
                    editor.putInt("Tema",0);
                } else {
                    editor.putInt("Tema", 1);
                }
                editor.commit();
                setDayNight();
            }
        });

        File fileDirectory = new File(ruta);
        File[] archivos = fileDirectory.listFiles();

        botonEliminarTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDeleteAll = new AlertDialog.Builder(SettingsActivity.this);
                alertDeleteAll.setIcon(R.drawable.warn_icon)
                        .setTitle("Atencion!")
                        .setMessage("¿Esta seguro de querer eliminar todas las definiciones y el contenido que este relacionado a estas?")
                        .setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (archivos != null) {
                                    for (File archivo : archivos) {
                                        archivo.delete();
                                    }
                                }
                                if (dbDefiniciones.suprimirTodo()) {
                                    volverInicio();
                                }
                            }
                        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Nothing here
                            }
                        }).setCancelable(false).show();
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //Metodo para establecer el tema de la app
    public void setDayNight(){
        SharedPreferences sp = getSharedPreferences("SP", MODE_PRIVATE);
        int tema = sp.getInt("Tema", 1);
        if(tema == 0){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    //----------------------------------------------------------------------------------------------
    //Metodo para la accion del boton atras del Sistema operativo, se refresca el activity padre
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //----------------------------------------------------------------------------------------------

    private void volverInicio(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //----------------------------------------------------------------------------------------------
}