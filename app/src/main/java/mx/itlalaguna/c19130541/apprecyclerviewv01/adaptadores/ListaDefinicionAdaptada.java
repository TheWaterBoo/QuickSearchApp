/*------------------------------------------------------------------------------------------
:*                         TECNOLOGICO NACIONAL DE MEXICO
:*                                CAMPUS LA LAGUNA
:*                     INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                             DESARROLLO EN ANDROID "A"
:*
:*                   SEMESTRE: ENE-JUN/2023    HORA: 08-09 HRS
:*
:*              Clase que adapta y muestra los items del recyclerView
:*
:*  Archivo     : ListaDefinicionAdaptada.java
:*  Autor       : Pedro Lopez Ramirez   19130541
:*  Fecha       : 26/04/2023
:*  Compilador  : Android Studio Electric Eel 2022.1
:*  Descripcion : Esta clase incluye metodos como por ejemplo para mostrar cada uno de los items
:*                de la lista con su respectiva informacion, ademas de que infla cada item
:*                tambien ayuda al filtrado de busqueda, mostrando asi los items que solo coinciden
:*  Ultima modif:
:*  Fecha       Modifico             Motivo
:*==========================================================================================
:*
:*------------------------------------------------------------------------------------------*/

package mx.itlalaguna.c19130541.apprecyclerviewv01.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import mx.itlalaguna.c19130541.apprecyclerviewv01.MostrarDefinicionActivity;
import mx.itlalaguna.c19130541.apprecyclerviewv01.R;
import mx.itlalaguna.c19130541.apprecyclerviewv01.entidades.Definiciones;

public class ListaDefinicionAdaptada extends RecyclerView.Adapter<ListaDefinicionAdaptada.DefinicionViewHolder> {

    ArrayList<Definiciones> listaDefiniciones;
    ArrayList<Definiciones> listaOriginalDefiniciones;
    String ruta;

    //----------------------------------------------------------------------------------------------
    //Constructor de la clase
    public ListaDefinicionAdaptada(ArrayList<Definiciones> listaDefiniciones){
        this.listaDefiniciones = listaDefiniciones;
        listaOriginalDefiniciones = new ArrayList<>();
        listaOriginalDefiniciones.addAll(listaDefiniciones);
    }

    //----------------------------------------------------------------------------------------------
    @NonNull
    @Override
    //Ayuda a asignar el diseño de cada elemento de la vista
    public DefinicionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_definicion, null, false);
        Context context = parent.getContext();
        ruta = context.getExternalFilesDir(null) + File.separator;
        Collections.sort(listaDefiniciones, new Comparator<Definiciones>() {
            @Override
            public int compare(Definiciones definicion1, Definiciones definicion2) {
                return definicion1.getTitulo().compareTo(definicion2.getTitulo());
            }
        });
        return new DefinicionViewHolder(view);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onBindViewHolder(@NonNull DefinicionViewHolder holder, int position) {
        String fileName = listaDefiniciones.get(position).getId() + listaDefiniciones.get(position).getTitulo();
        String fichero = ruta + fileName + ".3gp";
        File archivo = new File(fichero);
        //Si existe un archivo de audio, indica en el titulo que este tiene un archivo de audio
        if (archivo.exists()){
            holder.viewTitulo.setText(listaDefiniciones.get(position).getTitulo() + " - \uD83C\uDFB6");
        } else {
            holder.viewTitulo.setText(listaDefiniciones.get(position).getTitulo());
        }
        holder.viewDefinicion.setText(listaDefiniciones.get(position).getDefinicion());
    }

    //----------------------------------------------------------------------------------------------
    //Metodo que trabaja en conjunto con el SearchView
    public boolean fitrarBusquedas(String textoBuscar){
        boolean notfound = false;
        int longitud = textoBuscar.length();

        if(longitud == 0){
            Collections.sort(listaOriginalDefiniciones, new Comparator<Definiciones>() {
                @Override
                public int compare(Definiciones definicion1, Definiciones definicion2) {
                    return definicion1.getTitulo().compareTo(definicion2.getTitulo());
                }
            });

            listaDefiniciones.clear();
            listaDefiniciones.addAll(listaOriginalDefiniciones);
            notfound = false;
        } else {
            List<Definiciones> coleccion = listaOriginalDefiniciones.stream()
                    .filter(i -> i.getTitulo().toLowerCase().contains(textoBuscar.toLowerCase()))
                    .collect(Collectors.toList());

            Collections.sort(coleccion, new Comparator<Definiciones>() {
                @Override
                public int compare(Definiciones definicion1, Definiciones definicion2) {
                    return definicion1.getTitulo().compareTo(definicion2.getTitulo());
                }
            });

            listaDefiniciones.clear();
            listaDefiniciones.addAll(coleccion);
            if(coleccion.size() == 0)
                notfound = true;
            else
                notfound = false;
            
        }
        notifyDataSetChanged();
        return notfound;
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public int getItemCount() {
        return listaDefiniciones.size();
    }

    //----------------------------------------------------------------------------------------------
    //Metodo que define cada item del RecyclerView y le da una accion al ser presionado
    public class DefinicionViewHolder extends RecyclerView.ViewHolder {
        TextView viewTitulo, viewDefinicion;

        public DefinicionViewHolder(@NonNull View itemView) {
            super(itemView);
            viewTitulo = itemView.findViewById(R.id.txtViewTitulo);
            viewDefinicion = itemView.findViewById(R.id.txtViewDefinicion);

            //En caso de presionar algun item de la lista, este nos mandara a MostrarDefinicion
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, MostrarDefinicionActivity.class);
                    intent.putExtra("ID", listaDefiniciones.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
