/*------------------------------------------------------------------------------------------
:*                         TECNOLOGICO NACIONAL DE MEXICO
:*                                CAMPUS LA LAGUNA
:*                     INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                             DESARROLLO EN ANDROID "A"
:*
:*                   SEMESTRE: ENE-JUN/2023    HORA: 08-09 HRS
:*
:*         Activity que despliega la pantalla para agregar una nueva definicion
:*
:*  Archivo     : InsertarActivity.java
:*  Autor       : Pedro Lopez Ramirez   19130541
:*  Fecha       : 26/04/2023
:*  Compilador  : Android Studio Electric Eel 2022.1
:*  Descripcion : Este activity muestra la pantalla en donde se agregan las definiciones nuevas
:*                cuenta con dos EditText, los cuales tienen un limite de caracteres,
:*                y tambien con 2 botones, para cancelar y para guardar la definicion
:*  Ultima modif:
:*  Fecha       Modifico             Motivo
:*==========================================================================================
:*
:*------------------------------------------------------------------------------------------*/

package mx.itlalaguna.c19130541.apprecyclerviewv01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import mx.itlalaguna.c19130541.apprecyclerviewv01.database.DBDefiniciones;

public class InsertarActivity extends AppCompatActivity {

    EditText textoTitulo, textoDefinicion;
    Button botonGuardar, botonCancelar;
    //Para los contadores que estan debajo de los editText
    String formatoContador = "%d / %d", formatoFinal = "";
    TextView contadorTitulo, contadorDefinicion;
    int longitudMaximaTitulo = 25, longitudMaximaDefinicion = 500;
    int longitudRestanteTitulo = longitudMaximaTitulo;
    int longitudRestanteDefinicion = longitudMaximaDefinicion;

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Establece el tema antes de la creacion de los componentes
        setDayNight();
        super.onCreate(savedInstanceState);

        //Se referencian a los componentes
        setContentView(R.layout.activity_insertar);
        textoTitulo = findViewById(R.id.editTxtTitutlo);
        textoDefinicion = findViewById(R.id.editTxtDefinicion);
        botonGuardar = findViewById(R.id.botonGuardarDefinicion);
        botonCancelar = findViewById(R.id.botonCancelar);
        contadorTitulo = findViewById(R.id.contadorCaracteresTitulo);
        contadorDefinicion = findViewById(R.id.contadorCaracteresDefinicion);

        //Se define el formato y se asigna este al label que esta debajo de los TextField
        formatoFinal = String.format(formatoContador, longitudRestanteTitulo, longitudMaximaTitulo);
        contadorTitulo.setText(formatoFinal);
        formatoFinal = String.format(formatoContador, longitudRestanteDefinicion, longitudMaximaDefinicion);
        contadorDefinicion.setText(formatoFinal);

        //Listener para el boton que guarda las definiciones
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Se valida si los campos de texto no estan vacios, de ser asi entonces se guarda la definicion
                if (!textoTitulo.getText().toString().equals("") && !textoDefinicion.getText().toString().equals("")) {
                    DBDefiniciones dbDefiniciones = new DBDefiniciones(InsertarActivity.this);
                    long id = dbDefiniciones.insertarDefinicion(textoTitulo.getText().toString(), textoDefinicion.getText().toString());
                    //Mientras el id del dato sea mayor a 0, se inserta el dato
                    if (id > 0) {
                        Toast.makeText(InsertarActivity.this,
                                "Nueva definicion agregada!", Toast.LENGTH_SHORT).show();
                        limpiarYRegresar();
                    } else {
                        Toast.makeText(InsertarActivity.this,
                                "Ocurrio un error al añadir la definicion\nIntentelo de nuevo",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InsertarActivity.this,
                            "Los campos no pueden permanecer vacios", Toast.LENGTH_LONG).show();
                }
            }
        });

        //----------------------------------------------------------------------------------------------
        //Listener para el boton cancelar, este solo realiza la accion del boton hacia atras
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //----------------------------------------------------------------------------------------------
        //Listener para el EditText del titulo
        textoTitulo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //No se usa...
            }

            //Se escuchan los cambios en el texto, si el texto cambia dentro del campo entonces
            //modifica la cantidad de letras restantes en el campo
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int color;
                //Obtenemos la longitud del texto maxima, luego la restamos con la actual y se la
                //asignamos a una variable formateada para usarse en un TextVIew
                longitudRestanteTitulo = longitudMaximaTitulo - charSequence.length();
                formatoFinal = String.format(formatoContador, longitudRestanteTitulo, longitudMaximaTitulo);
                contadorTitulo.setText(formatoFinal);
                if (longitudRestanteTitulo == 0){
                    color = ContextCompat.getColor(InsertarActivity.this, R.color.color_rojo);
                    contadorTitulo.setTextColor(color);
                } else {
                    color = ContextCompat.getColor(InsertarActivity.this, R.color.black);
                    contadorTitulo.setTextColor(color);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //No se usa...
            }
        });

        //----------------------------------------------------------------------------------------------
        //Listener para el EditText de la definicion
        textoDefinicion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //Se escuchan los cambios en el texto, justo igual que en el listener de arriba
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int color;
                longitudRestanteDefinicion = longitudMaximaDefinicion - charSequence.length();
                formatoFinal = String.format(formatoContador, longitudRestanteDefinicion, longitudMaximaDefinicion);
                contadorDefinicion.setText(formatoFinal);
                if (longitudRestanteDefinicion == 0){
                    color = ContextCompat.getColor(InsertarActivity.this, R.color.color_rojo);
                    contadorDefinicion.setTextColor(color);
                } else {
                    color = ContextCompat.getColor(InsertarActivity.this, R.color.black);
                    contadorDefinicion.setTextColor(color);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //Metodo que limpia los campos de este activity y realiza la funcion del boton hacia atras
    private void limpiarYRegresar(){
        textoDefinicion.setText("");
        textoTitulo.setText("");
        onBackPressed();
    }

    //----------------------------------------------------------------------------------------------
    //Metodo que se realiza al presionar el boton de atras, refresca el activity padre
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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

    //----------------------------------------------------------------------------------------------
}