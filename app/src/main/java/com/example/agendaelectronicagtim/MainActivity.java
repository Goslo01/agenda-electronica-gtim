package com.example.agendaelectronicagtim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Variables para los campos de texto y botones
    private EditText editTitulo, editFechaInicio, editHoraInicio,
            editFechaFin, editHoraFin, editResponsable, editDescripcion;
    private Button btnCrearTarea, btnPapelera, btnExportar, btnHistorial;
    private ListView listViewTareas;

    // Lista para guardar todas las tareas
    private ArrayList<Tarea> listaTareas;
    private TareaAdapter adaptador;

    // Base de datos y datos del empleado logueado
    private DatabaseHelper dbHelper;
    private long idEmpleadoActual;
    private String nombreEmpleadoActual;

    // Código de solicitud de permisos
    private static final int REQUEST_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar la base de datos
        dbHelper = new DatabaseHelper(this);

        // Obtener datos del Intent (enviados desde LoginActivity)
        idEmpleadoActual = getIntent().getLongExtra("idEmpleado", -1);
        nombreEmpleadoActual = getIntent().getStringExtra("nombreEmpleado");

        // Conectar variables con XML
        inicializarComponentes();

        // Pre-llenar el campo responsable con el nombre del empleado logueado
        editResponsable.setText(nombreEmpleadoActual);

        // Configurar la lista de tareas
        configurarLista();

        // Cargar tareas desde la base de datos
        cargarTareasDesdeDB();

        // Configurar los pickers de fecha y hora
        configurarPickers();

        // Configurar los botones
        configurarBotonCrear();
        configurarBotonPapelera();
        configurarBotonExportar();
        configurarBotonHistorial();

        // *** NUEVO: Solicitar permisos de almacenamiento ***
        solicitarPermisos();
    }

    // *** NUEVO: Método para solicitar permisos ***
    private void solicitarPermisos() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            }
        }
    }

    // *** NUEVO: Manejar respuesta de permisos ***
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permisos denegados. No se podrá exportar archivos",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTareasDesdeDB();
    }

    private void inicializarComponentes() {
        editTitulo = findViewById(R.id.editTitulo);
        editFechaInicio = findViewById(R.id.editFechaInicio);
        editHoraInicio = findViewById(R.id.editHoraInicio);
        editFechaFin = findViewById(R.id.editFechaFin);
        editHoraFin = findViewById(R.id.editHoraFin);
        editResponsable = findViewById(R.id.editResponsable);
        editDescripcion = findViewById(R.id.editDescripcion);
        btnCrearTarea = findViewById(R.id.btnCrearTarea);
        btnPapelera = findViewById(R.id.btnPapelera);
        btnExportar = findViewById(R.id.btnExportar);
        btnHistorial = findViewById(R.id.btnHistorial);
        listViewTareas = findViewById(R.id.listViewTareas);

        listaTareas = new ArrayList<>();
    }

    private void configurarLista() {
        adaptador = new TareaAdapter(this, listaTareas, dbHelper, nombreEmpleadoActual);
        listViewTareas.setAdapter(adaptador);
    }

    private void cargarTareasDesdeDB() {
        listaTareas.clear();

        Cursor cursor = dbHelper.obtenerTareasEmpleado(idEmpleadoActual);

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
                listaTareas.add(tarea);
            } while (cursor.moveToNext());
        }

        cursor.close();
        adaptador.notifyDataSetChanged();
    }

    private void configurarPickers() {
        editFechaInicio.setFocusable(false);
        editFechaInicio.setClickable(true);
        editHoraInicio.setFocusable(false);
        editHoraInicio.setClickable(true);
        editFechaFin.setFocusable(false);
        editFechaFin.setClickable(true);
        editHoraFin.setFocusable(false);
        editHoraFin.setClickable(true);

        editFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker(editFechaInicio);
            }
        });

        editHoraInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarTimePicker(editHoraInicio);
            }
        });

        editFechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker(editFechaFin);
            }
        });

        editHoraFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarTimePicker(editHoraFin);
            }
        });
    }

    private void mostrarDatePicker(final EditText campo) {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String fechaSeleccionada = String.format(Locale.getDefault(),
                                "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        campo.setText(fechaSeleccionada);
                    }
                },
                anio, mes, dia);

        datePickerDialog.show();
    }

    private void mostrarTimePicker(final EditText campo) {
        final Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String horaSeleccionada = String.format(Locale.getDefault(),
                                "%02d:%02d", hourOfDay, minute);
                        campo.setText(horaSeleccionada);
                    }
                },
                hora, minuto, true);

        timePickerDialog.show();
    }

    private void configurarBotonCrear() {
        btnCrearTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearNuevaTarea();
            }
        });
    }

    private void configurarBotonPapelera() {
        btnPapelera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PapeleraActivity.class);
                intent.putExtra("idEmpleado", idEmpleadoActual);
                intent.putExtra("nombreEmpleado", nombreEmpleadoActual);
                startActivity(intent);
            }
        });
    }

    private void configurarBotonExportar() {
        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoExportacion();
            }
        });
    }

    private void mostrarDialogoExportacion() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Exportar Tareas");
        builder.setMessage("Selecciona el formato de exportación:");

        builder.setPositiveButton("TXT", (dialog, which) -> exportarTareas("TXT"));
        builder.setNeutralButton("CSV", (dialog, which) -> exportarTareas("CSV"));
        builder.setNegativeButton("JSON", (dialog, which) -> exportarTareas("JSON"));

        builder.show();
    }

    private void exportarTareas(String formato) {
        ExportManager exportManager = new ExportManager(this, dbHelper);
        String rutaArchivo = null;

        switch (formato) {
            case "TXT":
                rutaArchivo = exportManager.exportarTXT(idEmpleadoActual, nombreEmpleadoActual);
                break;
            case "CSV":
                rutaArchivo = exportManager.exportarCSV(idEmpleadoActual, nombreEmpleadoActual);
                break;
            case "JSON":
                rutaArchivo = exportManager.exportarJSON(idEmpleadoActual, nombreEmpleadoActual);
                break;
        }

        if (rutaArchivo != null) {
            Toast.makeText(this, "Tareas exportadas a:\n" + rutaArchivo, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No hay tareas para exportar o error al guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarBotonHistorial() {
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistorialExportacionesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void crearNuevaTarea() {
        String titulo = editTitulo.getText().toString().trim();
        String fechaInicio = editFechaInicio.getText().toString().trim();
        String horaInicio = editHoraInicio.getText().toString().trim();
        String fechaFin = editFechaFin.getText().toString().trim();
        String horaFin = editHoraFin.getText().toString().trim();
        String responsable = editResponsable.getText().toString().trim();
        String descripcion = editDescripcion.getText().toString().trim();

        if (titulo.isEmpty() || fechaInicio.isEmpty() || horaInicio.isEmpty() ||
                fechaFin.isEmpty() || horaFin.isEmpty() || responsable.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos obligatorios!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        long idTarea = dbHelper.insertarTarea(titulo, fechaInicio, horaInicio,
                fechaFin, horaFin, descripcion, idEmpleadoActual);

        if (idTarea != -1) {
            Toast.makeText(this, "Tarea creada exitosamente", Toast.LENGTH_SHORT).show();
            cargarTareasDesdeDB();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Error al crear la tarea", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        editTitulo.setText("");
        editFechaInicio.setText("");
        editHoraInicio.setText("");
        editFechaFin.setText("");
        editHoraFin.setText("");
        editDescripcion.setText("");
    }
}