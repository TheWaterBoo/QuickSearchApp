/*------------------------------------------------------------------------------------------
:*                         TECNOLOGICO NACIONAL DE MEXICO
:*                                CAMPUS LA LAGUNA
:*                     INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                             DESARROLLO EN ANDROID "A"
:*
:*                   SEMESTRE: ENE-JUN/2023    HORA: 08-09 HRS
:*
:*              Activity que muestra una sola definicion a detalle
:*
:*  Archivo     : MostrarDefinicionActivity.java
:*  Autor       : Pedro Lopez Ramirez   19130541
:*  Fecha       : 26/04/2023
:*  Compilador  : Android Studio Electric Eel 2022.1
:*  Descripcion : Este activity despliega un activity con el Titulo y la definicion mas
:*                extendida, la cual incluye dos botones, uno para editar y otro para eliminar
:*                la definicion, tambien cuenta con un scrollView en caso de que el texto se pase
:*  Ultima modif:
:*  Fecha       Modifico             Motivo
:*==========================================================================================
:*
:*------------------------------------------------------------------------------------------*/

package mx.itlalaguna.c19130541.apprecyclerviewv01;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.TooltipCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

import mx.itlalaguna.c19130541.apprecyclerviewv01.database.DBDefiniciones;
import mx.itlalaguna.c19130541.apprecyclerviewv01.entidades.Definiciones;

public class MostrarDefinicionActivity extends AppCompatActivity {

    TextView textTitulo, textDefinicion, textStatus;
    Definiciones definicion;
    FloatingActionButton botonFloatanteEditar, botonFlotanteEliminar, buttonEraseAudio,
            buttonRecordAudio, buttonStopAudio, buttonPlayAudio, buttonPauseAudio, buttonResumeAudio;
    int id = 0;
    //Obtiene la ruta donde se almacenara el audio
    //private final String ruta = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private String fichero, fileName;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String ruta = getApplicationContext().getExternalFilesDir(null) + File.separator;
        //Se establece primero el tema de la app, antes de crear los componentes
        setDayNight();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_definicion);

        //Se toma referencia a los componentes que se usaran
        textTitulo = findViewById(R.id.editTxtTitutlo);
        textDefinicion = findViewById(R.id.editTxtDefinicion);
        botonFloatanteEditar = findViewById(R.id.botonFloatanteEditar);
        botonFlotanteEliminar = findViewById(R.id.botonFloatanteEliminar);
        buttonEraseAudio = findViewById(R.id.buttonEraseAudio);
        buttonRecordAudio = findViewById(R.id.buttonRecordAudio);
        buttonStopAudio = findViewById(R.id.buttonStopAudio);
        buttonPlayAudio = findViewById(R.id.buttonPlayAudio);
        buttonPauseAudio = findViewById(R.id.buttonPauseAudio);
        buttonResumeAudio = findViewById(R.id.buttonResumeAudio);
        textStatus = findViewById(R.id.textRecordStatus);

        //Se añaden tooltips al los botones flotantes
        TooltipCompat.setTooltipText(botonFloatanteEditar, getString(R.string.tooltip_edicion_flotante));
        TooltipCompat.setTooltipText(botonFlotanteEliminar, getString(R.string.tooltip_eliminar_flotante));
        TooltipCompat.setTooltipText(buttonRecordAudio, getString(R.string.tooltip_grabar));
        TooltipCompat.setTooltipText(buttonEraseAudio, getString(R.string.tooltip_eliminar_audio));
        TooltipCompat.setTooltipText(buttonPlayAudio, getString(R.string.tooltip_Reproducir));
        TooltipCompat.setTooltipText(buttonStopAudio, getString(R.string.tooltip_parar));

        textStatus.setText("");

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

        //se llama a verDefinicion con el valor de la variable id, para de ahi tomar los dos campos
        //que se van a mostrar en este activity
        DBDefiniciones dbDefiniciones = new DBDefiniciones(MostrarDefinicionActivity.this);
        definicion = dbDefiniciones.verDefinicion(id);
        if(definicion != null){
            textTitulo.setText(definicion.getTitulo());
            textDefinicion.setText(definicion.getDefinicion());
        }

        //Se toma el nombre y la ruta en la que ira el archivo de audio
        fileName = definicion.getId() + definicion.getTitulo();
        fichero = ruta + fileName + ".3gp";
        File archivo = new File(fichero);

        //En caso de que ese archivo ya exista, entonces solo se muestra para su reproduccion
        if (archivo.exists()){
            buttonPlayAudio.setVisibility(View.VISIBLE);
            buttonEraseAudio.setVisibility(View.VISIBLE);
            buttonRecordAudio.setVisibility(View.GONE);
        }

        //----------------------------------------------------------------------------------------------
        //Listener para el boton flotante editar
        botonFloatanteEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MostrarDefinicionActivity.this, EditarDefinicionActivity.class);
                intent.putExtra("ID", id);
                startActivity(intent);
                finish();
            }
        });

        //----------------------------------------------------------------------------------------------
        //Listener para el boton flotante eliminar
        botonFlotanteEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MostrarDefinicionActivity.this);
                builder.setMessage("¿Esta seguro de eliminar esta definicion?\nNOTA: Si esta definicion incluye una nota de audio, esta tambien sera eliminada")
                        .setTitle("Eliminar definicion")
                        .setIcon(R.drawable.warn_icon)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (dbDefiniciones.suprimirDefinicion(id)) {
                                    boolean erased = archivo.delete();
                                    if(erased) {
                                        volverInicio();
                                    } else {
                                        volverInicio();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //No hacer nada...
                            }
                        })
                        .setCancelable(false).show();
            }
        });

        //Listener para el boton grabar audio
        buttonRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
                mediaRecorder.setOutputFile(fichero);
                try{
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    buttonRecordAudio.setVisibility(View.GONE);
                    buttonStopAudio.setVisibility(View.VISIBLE);
                    botonFloatanteEditar.setEnabled(false);
                    botonFlotanteEliminar.setEnabled(false);
                    textStatus.setText(R.string.record_text);
                    textStatus.setTextColor(getColor(R.color.color_rojo));
                } catch(IOException ex){
                    Toast.makeText(MostrarDefinicionActivity.this,"Ocurrio un error al grabar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Listener para detener el audio al momento de estarse grabando
        buttonStopAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                mediaRecorder.release();
                buttonStopAudio.setVisibility(View.GONE);
                buttonPlayAudio.setVisibility(View.VISIBLE);
                buttonEraseAudio.setVisibility(View.VISIBLE);
                botonFloatanteEditar.setEnabled(true);
                botonFlotanteEliminar.setEnabled(true);
                textStatus.setText("");
                mediaRecorder = null;
            }
        });

        //Listener para reproducir el audio almacenado internamente
        buttonPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = new MediaPlayer();
                try{
                    mediaPlayer.setDataSource( fichero );
                    mediaPlayer.prepare();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                            buttonPlayAudio.setEnabled(false);
                            buttonEraseAudio.setEnabled(false);
                            botonFloatanteEditar.setEnabled(false);
                            botonFlotanteEliminar.setEnabled(false);
                            buttonPlayAudio.setVisibility(View.GONE);
                            buttonPauseAudio.setVisibility(View.VISIBLE);
                            textStatus.setText(R.string.play_text);
                            textStatus.setTextColor(getColor(R.color.separator_color));
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.release();
                            buttonPlayAudio.setEnabled(true);
                            buttonEraseAudio.setEnabled(true);
                            botonFloatanteEditar.setEnabled(true);
                            botonFlotanteEliminar.setEnabled(true);
                            buttonPlayAudio.setVisibility(View.VISIBLE);
                            buttonPauseAudio.setVisibility(View.GONE);
                            buttonResumeAudio.setVisibility(View.GONE);
                            textStatus.setText("");
                        }
                    });
                } catch (IOException ex){
                    Toast.makeText(MostrarDefinicionActivity.this,"No se ha podido\nreproducir la nota...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonPauseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    buttonResumeAudio.setVisibility(View.VISIBLE);
                    buttonPauseAudio.setVisibility(View.GONE);
                    botonFloatanteEditar.setEnabled(true);
                    botonFlotanteEliminar.setEnabled(true);
                    textStatus.setText(R.string.pause_text);
                    textStatus.setTextColor(getColor(R.color.menu_bar));
                    mediaPlayer.pause();
                }
            }
        });

        buttonResumeAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    buttonResumeAudio.setVisibility(View.GONE);
                    buttonPauseAudio.setVisibility(View.VISIBLE);
                    botonFloatanteEditar.setEnabled(false);
                    botonFlotanteEliminar.setEnabled(false);
                    textStatus.setText(R.string.play_text);
                    textStatus.setTextColor(getColor(R.color.separator_color));
                    mediaPlayer.start();
                }
            }
        });

        //Listener para eliminar el archivo de audio
        buttonEraseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertEraseAudio = new AlertDialog.Builder(MostrarDefinicionActivity.this);
                alertEraseAudio.setIcon(R.drawable.warn_icon)
                        .setTitle("Eliminar nota de audio")
                        .setMessage("Esta seguro de querer eliminar esta\nnota de audio ligada a esta definicion?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean erased = archivo.delete();
                                if(erased){
                                    buttonEraseAudio.setVisibility(View.INVISIBLE);
                                    buttonRecordAudio.setVisibility(View.VISIBLE);
                                    buttonPlayAudio.setVisibility(View.GONE);
                                    mediaPlayer = null;
                                    mediaRecorder = null;
                                }
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Nothing to do.....
                            }
                        })
                        .setCancelable(false).show();
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //Metodo para cuando se presiona hacia atras del sistema operativo
    @Override
    public void onBackPressed() {
        if (mediaRecorder != null){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //----------------------------------------------------------------------------------------------
    //Metodo usado solamente cuando se borra una definicion, este finaliza el activity actual y
    //vuelve y refresca al activity padre
    private void volverInicio(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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