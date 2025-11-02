package com.example.agendaelectronicagtim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class PapeleraAdapter extends ArrayAdapter<Tarea> {

    private Context contexto;
    private ArrayList<Tarea> listaTareas;
    private DatabaseHelper dbHelper;
    private OnTareaActionListener listener;

    // Interface para manejar los clicks
    public interface OnTareaActionListener {
        void onRecuperar(Tarea tarea);
        void onEliminarPermanentemente(Tarea tarea);
    }

    // Constructor
    public PapeleraAdapter(Context context, ArrayList<Tarea> tareas,
                           DatabaseHelper dbHelper, OnTareaActionListener listener) {
        super(context, R.layout.item_papelera, tareas);
        this.contexto = context;
        this.listaTareas = tareas;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Tarea tareaActual = listaTareas.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(contexto);
            convertView = inflater.inflate(R.layout.item_papelera, parent, false);
        }

        // Conectar elementos del layout
        TextView txtTarea = convertView.findViewById(R.id.txtTareaPapelera);
        Button btnRecuperar = convertView.findViewById(R.id.btnRecuperar);
        Button btnEliminarPermanente = convertView.findViewById(R.id.btnEliminarPermanente);

        // Mostrar información de la tarea
        txtTarea.setText(tareaActual.toString());

        // Configurar botón de recuperar
        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRecuperar(tareaActual);
                }
            }
        });

        // Configurar botón de eliminar permanentemente
        btnEliminarPermanente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEliminarPermanentemente(tareaActual);
                }
            }
        });

        return convertView;
    }
}