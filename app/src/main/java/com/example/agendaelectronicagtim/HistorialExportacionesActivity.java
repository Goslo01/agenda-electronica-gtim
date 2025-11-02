package com.example.agendaelectronicagtim;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class HistorialExportacionesActivity extends AppCompatActivity {

    private ListView listViewHistorial;
    private TextView txtTotalExportaciones;
    private DatabaseHelper dbHelper;
    private ArrayList<String> listaHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_exportaciones);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Historial de Exportaciones");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        listViewHistorial = findViewById(R.id.listViewHistorial);
        txtTotalExportaciones = findViewById(R.id.txtTotalExportaciones);
        listaHistorial = new ArrayList<>();

        cargarHistorial();
    }

    private void cargarHistorial() {
        listaHistorial.clear();

        Cursor cursor = dbHelper.obtenerHistorialExportaciones();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No hay exportaciones registradas", Toast.LENGTH_SHORT).show();
            txtTotalExportaciones.setText("Total de exportaciones: 0");
        } else {
            txtTotalExportaciones.setText("Total de exportaciones: " + cursor.getCount());

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("idAlmacenamiento"));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo_formato"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_guardado"));

                // Verificar si tiene idTarea asociado
                int idTareaIndex = cursor.getColumnIndex("idTarea");
                String tituloTarea = "Todas las tareas";

                if (idTareaIndex != -1) {
                    int idTarea = cursor.getInt(idTareaIndex);
                    if (idTarea > 0) {
                        int tituloIndex = cursor.getColumnIndex("titulo");
                        if (tituloIndex != -1 && !cursor.isNull(tituloIndex)) {
                            tituloTarea = cursor.getString(tituloIndex);
                        } else {
                            tituloTarea = "Tarea ID: " + idTarea;
                        }
                    }
                }

                String registro = String.format("#%d - %s\nFormato: %s\nFecha: %s\n%s",
                        id, tituloTarea, tipo, fecha, "------------------------");

                listaHistorial.add(registro);
            }
        }

        cursor.close();

        android.widget.ArrayAdapter<String> adaptador = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listaHistorial);
        listViewHistorial.setAdapter(adaptador);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}