/*------------------------------------------------------------------------------------------
:*                         TECNOLOGICO NACIONAL DE MEXICO
:*                                CAMPUS LA LAGUNA
:*                     INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                             DESARROLLO EN ANDROID "A"
:*
:*                   SEMESTRE: ENE-JUN/2023    HORA: 08-09 HRS
:*
:*              Activity que despliega el la pantalla de introduccion (Splash)
:*
:*  Archivo     : SplashActivity.java
:*  Autor       : Pedro Lopez Ramirez   19130541
:*  Fecha       : 26/04/2023
:*  Compilador  : Android Studio Electric Eel 2022.1
:*  Descripcion : Este activity despliega la pantalla de carga de la aplicacion (Splash)
:*                esta pantalla dura 2 segundos y medio
:*  Ultima modif:
:*  Fecha       Modifico             Motivo
:*==========================================================================================
:*
:*------------------------------------------------------------------------------------------*/

package mx.itlalaguna.c19130541.apprecyclerviewv01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

public class splashActivity extends AppCompatActivity {

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Se establece el tema de la app, antes de crear los componentes
        setDayNight();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Se oculta el ActionBar por defecto de este activity, con el fin de ver solo el splash limpio
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        } else {
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getSupportActionBar().hide();
        }

        //Se referencia al progress bar que esta en el splash,
        //luego se aplica un retraso para pasar al siguiente activity
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);
    }

    //----------------------------------------------------------------------------------------------
    //Metodo que establece el tema de la app
    public void setDayNight(){
        SharedPreferences sp = getSharedPreferences("SP", MODE_PRIVATE);
        int tema = sp.getInt("Tema", 1);
        if(tema == 0){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}