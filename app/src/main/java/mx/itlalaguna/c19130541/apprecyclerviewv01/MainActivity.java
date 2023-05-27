/*------------------------------------------------------------------------------------------
:*                         TECNOLOGICO NACIONAL DE MEXICO
:*                                CAMPUS LA LAGUNA
:*                     INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                             DESARROLLO EN ANDROID "A"
:*
:*                   SEMESTRE: ENE-JUN/2023    HORA: 08-09 HRS
:*
:*              Activity que despliega el menu principal de la aplicacion
:*
:*  Archivo     : MainActivity.java
:*  Autor       : Pedro Lopez Ramirez   19130541
:*  Fecha       : 26/04/2023
:*  Compilador  : Android Studio Electric Eel 2022.1
:*  Descripcion : Este activity despliega la pantalla principal la cual muestra las definiciones
:*                en un recyclerView, en caso de no tener nada, este procede a mostrar un
:*                label el cual indica que se deben agregar primero definiciones
:*  Ultima modif:
:*  Fecha       Modifico             Motivo
:*==========================================================================================
:*
:*------------------------------------------------------------------------------------------*/

package mx.itlalaguna.c19130541.apprecyclerviewv01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import mx.itlalaguna.c19130541.apprecyclerviewv01.adaptadores.ListaDefinicionAdaptada;
import mx.itlalaguna.c19130541.apprecyclerviewv01.database.DBDefiniciones;
import mx.itlalaguna.c19130541.apprecyclerviewv01.entidades.Definiciones;
import util.permisos.ChecadorDePermisos;
import util.permisos.PermisoApp;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_IMPORT_FILE_SELECTED = 19;
    private static final int CODE_EXPORT_FILE_SELECTED = 64;
    RecyclerView listaDefiniciones;
    ArrayList<Definiciones> listaArregloDefiniciones;
    FloatingActionButton botonFlotanteAgregar, buttonMenu, buttonExport, buttonImport;
    ListaDefinicionAdaptada adaptador;
    MenuItem menuSearch, itemOpciones, itemAcercaDe;
    LinearLayout ll, linearLayoutScrollDefinicion;
    TextView textoLayout;
    Boolean isOpened = false;
    EditText selectedFile, selectedPath, fileExportedName;
    private String fileName, pathName, fileAndPath, tmpName, tmpPath;
    Button positiveButtonExport, positiveButtonImport;
    Uri importUri, exportUri;


    //----------------------------------------------------------------------------------------------

    private PermisoApp[] permisosReq = new PermisoApp [] {
            new PermisoApp(Manifest.permission.RECORD_AUDIO, "Microfono", true),
            new PermisoApp(Manifest.permission.READ_EXTERNAL_STORAGE, "Almacenamiento", true)
    };

    //----------------------------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == ChecadorDePermisos.CODIGO_PEDIR_PERMISOS){
            ChecadorDePermisos.verificarPermisosSolicitados( this, permisosReq, permissions, grantResults);
        }
    }

    //----------------------------------------------------------------------------------------------

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String ruta = getApplicationContext().getExternalFilesDir(null) + File.separator;
        //Comprobamos el status del tema de la app, antes de iniciar lo demas
        setDayNight();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Referenciamos los componentes que usaremos con su ID
        listaDefiniciones = findViewById(R.id.listaDefiniciones);
        textoLayout = findViewById(R.id.emptyText);
        ll = findViewById(R.id.withoutDefinitions);
        linearLayoutScrollDefinicion = findViewById(R.id.containerScroll);
        //Referencia del menu de botones...
        botonFlotanteAgregar = findViewById(R.id.botonAgregarDefinicion);
        buttonExport = findViewById(R.id.exportButton);
        buttonImport = findViewById(R.id.importButton);
        buttonMenu = findViewById(R.id.buttonMenu);

        listaDefiniciones.setLayoutManager(new LinearLayoutManager(this));
        DBDefiniciones dbDefiniciones = new DBDefiniciones(MainActivity.this);
        listaArregloDefiniciones = new ArrayList<>();

        //Se inicializa y establece adaptador para el RecyclerView
        adaptador = new ListaDefinicionAdaptada(dbDefiniciones.mostrarDefiniciones());
        listaDefiniciones.setAdapter(adaptador);

        //Se inicializa y agregan separadores de los items del recyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                this,new LinearLayoutManager(this).getOrientation());
        listaDefiniciones.addItemDecoration(dividerItemDecoration);

        //------------------------------------------------------------------------------------------

        //Animaciones y eventos para el boton menu, el cual muestra otros 3 botones
        TooltipCompat.setTooltipText(buttonMenu, "Expandir");
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Al dar click en el boton, muestra los botones, dependiendo del estado del boton menu
                if (!isOpened) {
                    buttonExport.setVisibility(View.VISIBLE);
                    buttonExport.startAnimation(fadeInAnimation);
                    buttonImport.setVisibility(View.VISIBLE);
                    buttonImport.startAnimation(fadeInAnimation);
                    botonFlotanteAgregar.setVisibility(View.VISIBLE);
                    botonFlotanteAgregar.startAnimation(fadeInAnimation);
                    buttonMenu.setImageResource(R.drawable.close_menu_ic);
                    TooltipCompat.setTooltipText(buttonMenu, "Contraer");
                    isOpened = true;
                } else {
                    buttonExport.startAnimation(fadeOutAnimation);
                    buttonExport.setVisibility(View.GONE);
                    buttonImport.startAnimation(fadeOutAnimation);
                    buttonImport.setVisibility(View.GONE);
                    botonFlotanteAgregar.startAnimation(fadeOutAnimation);
                    botonFlotanteAgregar.setVisibility(View.GONE);
                    buttonMenu.setImageResource(R.drawable.menu_bars_ic);
                    TooltipCompat.setTooltipText(buttonMenu, "Expandir");
                    isOpened = false;
                }
            }
        });

        //------------------------------------------------------------------------------------------

        //Accion y tooltip del boton flotante en el activity main
        TooltipCompat.setTooltipText(botonFlotanteAgregar, "Agregar nueva definicion");
        botonFlotanteAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevaDefinicion();
            }
        });

        //------------------------------------------------------------------------------------------

        //Accion y tooltip para exportar un archivo con definiciones (definiciones que ya se tengan)
        TooltipCompat.setTooltipText(buttonExport, "Exportar Definiciones");
        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Se comprueba si tiene un padre y si es asi se remueve primero el hijo del padre
                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_exportdialog, null);
                ViewGroup parent = (ViewGroup) dialogView.getParent();
                if (parent != null){
                    parent.removeView(dialogView);
                }

                AlertDialog.Builder layoutExportacion = new AlertDialog.Builder(MainActivity.this);
                layoutExportacion.setTitle("Exportar archivo")
                        .setMessage("Asignale un nombre al archivo y selecciona la ruta donde se guardara\n")
                        .setIcon(R.drawable.export_alert_ic)
                        .setView(dialogView)
                        .setPositiveButton("Exportar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                fileName = fileExportedName.getText().toString();
                                if (!fileName.equals(tmpName)){
                                    //Metodo para renombrar el archivo, respetando el nombre que el
                                    //usuario defina en el alertDialog
                                    File oldFile = new File(tmpPath + "/" + tmpName);
                                    File newFile = new File(tmpPath + "/" + fileName);

                                    if (oldFile.exists()){
                                        oldFile.renameTo(newFile);
                                    }
                                }

                                File file = new File(tmpPath + "/" + fileName);
                                //Exportacion de archivo, se le pasa la ruta del archivo file
                                dbDefiniciones.exportDataToTextFile(file);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Nada por el momento
                            }
                        })
                        .setCancelable(false);

                AlertDialog exportarAlerta = layoutExportacion.create();
                exportarAlerta.show();

                //Referenciamos al boton exportar del AlertDialog, este se habilitara cuando esten llenos los campos
                positiveButtonExport = exportarAlerta.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButtonExport.setEnabled(false);

                //Se Referencia al campo de texto para la ruta del archivo, este solo mostrara la ruta
                selectedPath = dialogView.findViewById(R.id.textFieldPath);
                selectedPath.setEnabled(false);

                fileExportedName = dialogView.findViewById(R.id.editTextFileName);

                Button searchPath = dialogView.findViewById(R.id.buttonSelectSavePath);
                searchPath.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fileName = fileExportedName.getText().toString();
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            startActivityForResult ( Intent.createChooser ( intent, "Seleccione la ubicacion y nombre:" ),
                                    CODE_EXPORT_FILE_SELECTED );
                        } catch ( ActivityNotFoundException e ) {
                            Toast.makeText(MainActivity.this, "Explorador de archivos no disponible\nAsegurese de tener instalado uno", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //Watcher para el campo de texto del nombre, no dejara exportrar archivos sin nombre
                fileExportedName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        //Nothing
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //Nothing
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        actualizarBotonExportar();
                    }
                });
            }
        });

        //------------------------------------------------------------------------------------------

        //Accion y tooltip para importar archivos, trae definiciones el archivo (deberia...)
        TooltipCompat.setTooltipText(buttonImport, "Importar Definiciones");
        buttonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Se comprueba si tiene un padre y si es asi se remueve primero el hijo del padre
                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_alertdialog, null);
                ViewGroup parent = (ViewGroup) dialogView.getParent();
                if (parent != null){
                    parent.removeView(dialogView);
                }

                AlertDialog.Builder layoutImportacion = new AlertDialog.Builder(MainActivity.this);
                layoutImportacion.setTitle("Importar archivo")
                        .setMessage("Selecciona el archivo que desea importar: ")
                        .setView(dialogView)
                        .setIcon(R.drawable.import_alert_ic)
                        .setPositiveButton("Importar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                File file = new File(fileAndPath);
                                //Comienza la importacion, se le pasa la ruta completa con file
                                dbDefiniciones.importDataFromTextFile(file);
                                //Actualiza la actividad, para que muestre los datos importados
                                recreate();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Do nothing...
                            }
                        })
                        .setCancelable(false);

                AlertDialog importarAlerta = layoutImportacion.create();
                importarAlerta.show();

                //Referenciamos al boton del alertDialog y lo inhabilitamos
                positiveButtonImport = importarAlerta.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButtonImport.setEnabled(false);

                //Referenciamos al campo de texto para inhabilitar la entrada de datos
                selectedFile = dialogView.findViewById(R.id.textFieldFile);
                selectedFile.setEnabled(false);

                //Se referencia al boton que se muestra desde otro layout incrustado en el alertDialog
                Button searchFile = dialogView.findViewById(R.id.buttonExploreFiles);
                searchFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            startActivityForResult ( Intent.createChooser ( intent, "Seleccione un archivo" ),
                                    CODE_IMPORT_FILE_SELECTED );
                        } catch ( ActivityNotFoundException e ) {
                            Toast.makeText(MainActivity.this, "Explorador de archivos no disponible\nAsegurese de tener instalado uno", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        //------------------------------------------------------------------------------------------
        //Condiciones por si no hay ninguna definicion en la BD
        //De ser asi se oculta el RecyclerView y se muestra la leyenda de no hay definiciones
        if(adaptador.getItemCount() == 0){
            listaDefiniciones.setVisibility(View.GONE);
            ll.setVisibility(View.VISIBLE);
        } else {
            listaDefiniciones.setVisibility(View.VISIBLE);
            ll.setVisibility(View.GONE);
        }

        ChecadorDePermisos.checarPermisos( this, permisosReq );
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Condicion para importacion
        if (requestCode == CODE_IMPORT_FILE_SELECTED && resultCode == RESULT_OK) {
            importUri = data.getData();
            //Se normaliza la ruta uri en caso de que tenga otro formato
            fileAndPath = replaceUriPath(importUri.getPath());
            //Se muestra la ruta y el archivo en el campo de texto del AlertDialog
            selectedFile.setText(fileAndPath);
            selectedFile.setSelection(fileAndPath.length());
            positiveButtonImport.setEnabled(true);
        }
        //Condicion para exportacion
        if (requestCode == CODE_EXPORT_FILE_SELECTED && resultCode == RESULT_OK) {
            exportUri = data.getData();
            pathName = replaceUriPath(exportUri.getPath()); //Ruta del archivo que se genero
            File file = new File(pathName);
            tmpPath = file.getParent();  //Incluye SOLO la ruta
            tmpName = file.getName();    //Incluye SOLO el nombre del archivo que se genero

            //En caso de que el campo de texto este vacio, se le asigna el nombre del archivo que se crea
            if (fileName.equals("")){
                fileExportedName.setText(tmpName);
            }

            selectedPath.setText(tmpPath);
            selectedPath.setSelection(tmpPath.length());
            positiveButtonExport.setEnabled(true);
        }
    }

    //----------------------------------------------------------------------------------------------

    public String replaceUriPath(String uriString) {
        String sdCardSegment = null;

        //Condicion para almacenamiento interno, generalmente tienen esas sintaxis, de lo contrario solo se ignoran
        if (uriString.contains("/document/primary:")) {
            // Reemplaza "/document/primary:" por "/storage/0/"
            uriString = uriString.replace("/document/primary:", "/storage/emulated/0/");
        } else {
            int start = uriString.indexOf("/document/") + "/document/".length();
            int end = uriString.indexOf(":", start);
            //Condicion para cuando se requiere una ruta en almacenamiento externo
            if (end != -1) {
                sdCardSegment = uriString.substring(start, end);
                // Reemplaza "/document/xxxx:" por "/storage/xxxx/" (xxxxx = identificador o segemento de memoria SD)
                uriString = uriString.replace("/document/" + sdCardSegment + ":", "/storage/" + sdCardSegment + "/");
            }
        }

        // Remueve la barra diagonal al final en caso de que exista (poco probable...)
        if (uriString.endsWith("/")) {
            uriString = uriString.substring(0, uriString.length() - 1);
        }

        //Regresa la cadena con nuevo formato, solo en caso de que aplique
        return uriString;
    }

    //----------------------------------------------------------------------------------------------

    private void actualizarBotonExportar() {
        String nombreText = fileExportedName.getText().toString();
        String rutaText = selectedPath.getText().toString();
        boolean isEmpty = !TextUtils.isEmpty(nombreText) && !TextUtils.isEmpty(rutaText);
        positiveButtonExport.setEnabled(isEmpty);
    }

    //----------------------------------------------------------------------------------------------
    //Metodo donde se crean las opciones del menuBar, junto con sus items
    public boolean onCreateOptionsMenu (Menu menu){
        //Se infla el menuBar, referenciando su layout
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);

        //Se referencian los items que se encuentren en el menuBar
        menuSearch = menu.findItem(R.id.accionBuscar);
        itemOpciones = menu.findItem(R.id.itemOpciones);
        itemAcercaDe = menu.findItem(R.id.itemAcercaDe);

        androidx.appcompat.widget.SearchView searchView =
                (androidx.appcompat.widget.SearchView) menuSearch.getActionView();
        searchView.setQueryHint("Buscar...");

        //Listener para el SearchView implementado en el menuBar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            //Cuando el texto cambie a tiempo real, los resultados de la busqueda cambiaran
            @Override
            public boolean onQueryTextChange(String newText) {
                boolean notfound;
                notfound = adaptador.fitrarBusquedas(newText);
                if(notfound){
                    listaDefiniciones.setVisibility(View.GONE);
                    ll.setVisibility(View.VISIBLE);
                    textoLayout.setText(R.string.not_found);
                } else {
                    if(adaptador.getItemCount() == 0){
                        listaDefiniciones.setVisibility(View.GONE);
                        ll.setVisibility(View.VISIBLE);
                    } else {
                        listaDefiniciones.setVisibility(View.VISIBLE);
                        ll.setVisibility(View.GONE);
                    }
                    textoLayout.setText(R.string.vista_vacia);
                }
                return false;
            }
        });

        //Listener para cuando el enfoque del searchView cambie
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean estaEnfocado) {
                //si esta enfocado, entonces oculta los demas items del MenuBar
                if(estaEnfocado){
                    itemOpciones.setVisible(false);
                    itemAcercaDe.setVisible(false);
                    menuSearch.setVisible(false);
                } else {    //si no, entonces los muestra y ademas colapsa el searchview
                    itemOpciones.setVisible(true);
                    itemAcercaDe.setVisible(true);
                    menuSearch.collapseActionView();
                    menuSearch.setVisible(true);
                }
            }
        });

        return true;
    }

    //----------------------------------------------------------------------------------------------
    //Metodo para las acciones de los items del MenuBar
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.itemOpciones:
                goToSettings();
                return true;
            case R.id.itemAcercaDe:
                acercaDe();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //----------------------------------------------------------------------------------------------
    //Metodo para iniciar Activiy de Agregar definicion
    private void nuevaDefinicion(){
        Intent intent = new Intent (this, InsertarActivity.class);
        startActivity(intent);
    }

    //----------------------------------------------------------------------------------------------
    //Metodo para iniciar activity de los ajustes de la app
    private void goToSettings(){
        Intent intent = new Intent (this, SettingsActivity.class);
        startActivity(intent);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //----------------------------------------------------------------------------------------------
    //Metodo para ocultar el teclado, en caso de presionar algun otro lugar fuera del teclado
    public void ocultarTeclado(View w) {
        View vista = getCurrentFocus();
        if (vista != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(vista.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //----------------------------------------------------------------------------------------------
    //Metodo que muestra un alertDialog con el Acerca De..
    public void acercaDe(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setIcon(R.drawable.logo_tec)
                .setTitle("Acerca de...")
                .setMessage("Creado por:\nPedro Lopez Ramirez\n19130541" +
                        "\n\nCarlos Antonio Madrigal Trejo\n20130053" +
                        "\n\nEdson Alexis Valadez Davalos\nn19130575")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do nothing -.-
            }
        }).show();
    }

    //----------------------------------------------------------------------------------------------
    //Metodo que define el tema de la app
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