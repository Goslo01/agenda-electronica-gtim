package com.example.agendaelectronicagtim;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class PapeleraActivity extends AppCompatActivity {

    private ListView listViewPapelera;
    private ArrayList<Tarea> listaTareasEliminadas;
    private PapeleraAdapter adaptador;
    private DatabaseHelper dbHelper;
    private long idEmpleadoActual;
    private String nombreEmpleadoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_papelera);

        // Cambiar el título de la barra superior
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Papelera de Tareas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar la base de datos
        dbHelper = new DatabaseHelper(this);

        // Obtener datos del Intent
        idEmpleadoActual = getIntent().getLongExtra("idEmpleado", -1);
        nombreEmpleadoActual = getIntent().getStringExtra("nombreEmpleado");

        // Inicializar componentes
        listViewPapelera = findViewById(R.id.listViewPapelera);
        listaTareasEliminadas = new ArrayList<>();

        // Configurar adaptador
        configurarLista();

        // Cargar tareas eliminadas
        cargarTareasEliminadas();
    }

    private void configurarLista() {
        adaptador = new PapeleraAdapter(
                this,
                listaTareasEliminadas,
                dbHelper,
                new PapeleraAdapter.OnTareaActionListener() {
                    @Override
                    public void onRecuperar(Tarea tarea) {
                        recuperarTarea(tarea);
                    }

                    @Override
                    public void onEliminarPermanentemente(Tarea tarea) {
                        eliminarPermanentemente(tarea);
                    }
                }
        );
        listViewPapelera.setAdapter(adaptador);
    }

    private void cargarTareasEliminadas() {
        listaTareasEliminadas.clear();

        Cursor cursor = dbHelper.obtenerTareasEliminadas(idEmpleadoActual);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("idTarea"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inicio"));
                String horaInicio = cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio"));
                String fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fecha_fin"));
                String horaFin = cursor.getString(cursor.getColumnIndexOrThrow("hora_fin"));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));

                Tarea tarea = new Tarea(id, titulo, fechaInicio, horaInicio,
                        fechaFin, horaFin, nombreEmpleadoActual, descripcion);
                listaTareasEliminadas.add(tarea);
            } while (cursor.moveToNext());
        }

        cursor.close();
        adaptador.notifyDataSetChanged();
    }

    private void recuperarTarea(Tarea tarea) {
        boolean recuperado = dbHelper.recuperarTarea(tarea.getId());

        if (recuperado) {
            Toast.makeText(this, "Tarea recuperada exitosamente", Toast.LENGTH_SHORT).show();
            cargarTareasEliminadas(); // Recargar lista
        } else {
            Toast.makeText(this, "Error al recuperar tarea", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarPermanentemente(Tarea tarea) {
        boolean eliminado = dbHelper.eliminarTareaPermanentemente(tarea.getId());

        if (eliminado) {
            Toast.makeText(this, "Tarea eliminada permanentemente", Toast.LENGTH_SHORT).show();
            cargarTareasEliminadas(); // Recargar lista
        } else {
            Toast.makeText(this, "Error al eliminar tarea", Toast.LENGTH_SHORT).show();
        }
    }

    // Manejar el botón de regresar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}