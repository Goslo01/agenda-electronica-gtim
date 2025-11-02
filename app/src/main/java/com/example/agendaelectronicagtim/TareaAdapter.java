package com.example.agendaelectronicagtim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

// Adaptador personalizado para mostrar las tareas con botones de eliminar y exportar
public class TareaAdapter extends ArrayAdapter<Tarea> {

    private Context contexto;
    private ArrayList<Tarea> listaTareas;
    private DatabaseHelper dbHelper;
    private String nombreEmpleado;

    // Constructor del adaptador
    public TareaAdapter(Context context, ArrayList<Tarea> tareas, DatabaseHelper dbHelper, String nombreEmpleado) {
        super(context, R.layout.item_tarea, tareas);
        this.contexto = context;
        this.listaTareas = tareas;
        this.dbHelper = dbHelper;
        this.nombreEmpleado = nombreEmpleado;
    }

    // Método que se ejecuta para cada item de la lista
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Obtener la tarea en esta posición
        final Tarea tareaActual = listaTareas.get(position);

        // Inflar el layout personalizado si no existe
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(contexto);
            convertView = inflater.inflate(R.layout.item_tarea, parent, false);
        }

        // Conectar los elementos del layout
        TextView txtTarea = convertView.findViewById(R.id.txtTarea);
        Button btnExportarIndividual = convertView.findViewById(R.id.btnExportarIndividual);
        ImageButton btnEliminar = convertView.findViewById(R.id.btnEliminar);

        // Mostrar la información de la tarea en el TextView
        txtTarea.setText(tareaActual.toString());

        // Configurar el botón de exportar individual
        btnExportarIndividual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportManager exportManager = new ExportManager(contexto, dbHelper);
                String rutaArchivo = exportManager.exportarTareaIndividualTXT(tareaActual.getId(), nombreEmpleado);

                if (rutaArchivo != null) {
                    Toast.makeText(contexto, "Tarea exportada a:\n" + rutaArchivo, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(contexto, "Error al exportar tarea", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el botón de eliminar
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Eliminar de la base de datos (borrado lógico)
                boolean eliminado = dbHelper.eliminarTarea(tareaActual.getId());

                if (eliminado) {
                    // Eliminar de la lista visual
                    listaTareas.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(contexto, "Tarea movida a la papelera", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(contexto, "Error al eliminar tarea", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }
}