/*------------------------------------------------------------------------------------------
:*                         TECNOLOGICO NACIONAL DE MEXICO
:*                                CAMPUS LA LAGUNA
:*                     INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                             DESARROLLO EN ANDROID "A"
:*
:*                   SEMESTRE: ENE-JUN/2023    HORA: 08-09 HRS
:*
:*              Activity que despliega el activity para editar las definiciones
:*
:*  Archivo     : EditarDefinicionActivity.java
:*  Autor       : Pedro Lopez Ramirez   19130541
:*  Fecha       : 26/04/2023
:*  Compilador  : Android Studio Electric Eel 2022.1
:*  Descripcion : Este activity muestra el mismo layout que el que usa InsertarActivity,
:*                pero con algunos cambios, ademas tomando el titulo y definicion actual del
:*                activity padre que viene, con el hecho de hacer cambios y luego actualizar
:*                la base de datos
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

import java.io.File;
import java.util.ArrayList;

import mx.itlalaguna.c19130541.apprecyclerviewv01.database.DBDefiniciones;
import mx.itlalaguna.c19130541.apprecyclerviewv01.entidades.Definiciones;

public class EditarDefinicionActivity extends AppCompatActivity {

    EditText textTitulo, textDefinicion;
    Definiciones definicion;
    Button botonEditar, botonCancelar;
    Boolean bandera = false;
    //Para los contadores que estan debajo de los editText
    String formatoContador = "%d / %d", formatoFinal = "";
    TextView contadorTitulo, contadorDefinicion;
    int longitudMaximaTitulo = 25, longitudMaximaDefinicion = 500;
    int longitudRestanteTitulo = longitudMaximaTitulo;
    int longitudRestanteDefinicion = longitudMaximaDefinicion;
    int id = 0;
    String fileName;

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String ruta = getApplicationContext().getExternalFilesDir(null) + File.separator;
        //Se establece el tema de la app, antes de la creacion de los componentes
        setDayNight();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_definicion);

        //Se obtiene referencia de los componentes que se usaran
        textTitulo = findViewById(R.id.editTxtTitutlo);
        textDefinicion = findViewById(R.id.editTxtDefinicion);
        botonEditar = findViewById(R.id.botonGuardarDefinicion);
        botonCancelar = findViewById(R.id.botonCancelar);
        contadorTitulo = findViewById(R.id.contadorCaracteresTitulo);
        contadorDefinicion = findViewById(R.id.contadorCaracteresDefinicion);

        //Se obtiene el id actual del dato en la base de datos
        if (savedInstanceState == null){
            Bundle extra = getIntent().getExtras();
            if (extra == null){
                id = Integer.parseInt(null);
            } else {
                id = extra.getInt("ID");
            }
        } else {
            id = (int) savedInstanceState.getSerializable("ID");
        }

        //Con el id obtenido, se puede llamar a ver definicion y mostrar su contenido
        final DBDefiniciones dbDefiniciones = new DBDefiniciones(EditarDefinicionActivity.this);
        definicion = dbDefiniciones.verDefinicion(id);
        if(definicion != null){
            textTitulo.setText(definicion.getTitulo());
            textDefinicion.setText(definicion.getDefinicion());
        }

        //Una vez obtenido los dos campos de arriba, se establece su longitud actual y restante
        longitudRestanteTitulo = longitudMaximaTitulo - textTitulo.length();
        formatoFinal = String.format(formatoContador, longitudRestanteTitulo, longitudMaximaTitulo);
        contadorTitulo.setText(formatoFinal);

        longitudRestanteDefinicion = longitudMaximaDefinicion - textDefinicion.length();
        formatoFinal = String.format(formatoContador, longitudRestanteDefinicion, longitudMaximaDefinicion);
        contadorDefinicion.setText(formatoFinal);

        fileName = definicion.getId() + definicion.getTitulo() + ".3gp";
        File pastName = new File(ruta, fileName);

        //----------------------------------------------------------------------------------------------
        //Listener para el boton editar
        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!textTitulo.getText().toString().equals("") && !textDefinicion.getText().toString().equals("")){
                    bandera = dbDefiniciones.editarDefinicion(id, textTitulo.getText().toString(), textDefinicion.getText().toString());
                    if (bandera){
                        if (pastName.exists()) {
                            String tmpName = definicion.getId() + textTitulo.getText().toString();
                            File newName = new File(ruta, tmpName + ".3gp");
                            boolean rename = pastName.renameTo(newName);
                            if (!rename) {
                                Toast.makeText(EditarDefinicionActivity.this, "Algo salio mal!\nEl audio se perdio", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Toast.makeText(EditarDefinicionActivity.this, "Definicion modificada", Toast.LENGTH_SHORT).show();
                        verDefinicion();
                    } else {
                        Toast.makeText( EditarDefinicionActivity.this, "Error al modificar la definicion!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditarDefinicionActivity.this, "Los campos no pueden permanecer vacios", Toast.LENGTH_LONG).show();
                }
            }
        });

        //----------------------------------------------------------------------------------------------
        //Listener para el boton cancelar, solo realiza el metodo del boton atras
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Listener para cuando el texto cambia del EditText
        //Solo modifica los caracteres restantes del campo de texto
        textTitulo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Nothing...
            }

            //Cuando el texto cambia, se resta o suman los caracteres actuales al TextView que esta
            //debajo del EditText. esto para ver que el campo tiene un limite de caracteres
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int color;
                longitudRestanteTitulo = longitudMaximaTitulo - charSequence.length();
                formatoFinal = String.format(formatoContador, longitudRestanteTitulo, longitudMaximaTitulo);
                contadorTitulo.setText(formatoFinal);
                if (longitudRestanteTitulo == 0){
                    color = ContextCompat.getColor(EditarDefinicionActivity.this, R.color.color_rojo);
                    contadorTitulo.setTextColor(color);
                } else {
                    color = ContextCompat.getColor(EditarDefinicionActivity.this, R.color.black);
                    contadorTitulo.setTextColor(color);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //No hace nada...
            }
        });

        //Listener para cuando el texto cambia del EditText
        //Solo modifica los caracteres restantes del campo de texto
        textDefinicion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Nothing...
            }

            //Lo mismo del metodo de arriba del listener anterior
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int color;
                longitudRestanteDefinicion = longitudMaximaDefinicion - charSequence.length();
                formatoFinal = String.format(formatoContador, longitudRestanteDefinicion, longitudMaximaDefinicion);
                contadorDefinicion.setText(formatoFinal);
                if (longitudRestanteDefinicion == 0){
                    color = ContextCompat.getColor(EditarDefinicionActivity.this, R.color.color_rojo);
                    contadorDefinicion.setTextColor(color);
                } else {
                    color = ContextCompat.getColor(EditarDefinicionActivity.this, R.color.black);
                    contadorDefinicion.setTextColor(color);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //No hace nada...
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //Metodo que devuelve al activity anterior, el cual mostraba el titulo y concepto, se finaliza este activity
    private void verDefinicion(){
        Intent intent = new Intent(this, MostrarDefinicionActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
        finish();
    }

    //----------------------------------------------------------------------------------------------
    //Metodo que se realiza al presionar el boton de atras
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        verDefinicion();
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
}