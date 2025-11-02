package com.example.agendaelectronicagtim;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExportManager {

    private Context context;
    private DatabaseHelper dbHelper;

    public ExportManager(Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    // Exportar todas las tareas activas en formato TXT
    public String exportarTXT(long idEmpleado, String nombreEmpleado) {
        Cursor cursor = dbHelper.obtenerTodasTareasActivas(idEmpleado);

        if (cursor.getCount() == 0) {
            cursor.close();
            return null; // No hay tareas
        }

        StringBuilder contenido = new StringBuilder();
        contenido.append("==============================================\n");
        contenido.append("     AGENDA ELECTRÓNICA GTIM\n");
        contenido.append("     Tareas de: ").append(nombreEmpleado).append("\n");
        contenido.append("     Fecha: ").append(obtenerFechaActual()).append("\n");
        contenido.append("==============================================\n\n");

        int contador = 1;
        while (cursor.moveToNext()) {
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            String fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inicio"));
            String horaInicio = cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio"));
            String fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fecha_fin"));
            String horaFin = cursor.getString(cursor.getColumnIndexOrThrow("hora_fin"));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));

            contenido.append("TAREA #").append(contador).append("\n");
            contenido.append("----------------------------------\n");
            contenido.append("Título: ").append(titulo).append("\n");
            contenido.append("Inicio: ").append(fechaInicio).append(" ").append(horaInicio).append("\n");
            contenido.append("Fin: ").append(fechaFin).append(" ").append(horaFin).append("\n");
            contenido.append("Responsable: ").append(nombreEmpleado).append("\n");
            if (descripcion != null && !descripcion.isEmpty()) {
                contenido.append("Descripción: ").append(descripcion).append("\n");
            }
            contenido.append("\n");
            contador++;
        }

        cursor.close();

        // Guardar archivo
        String rutaArchivo = guardarArchivo(contenido.toString(), "tareas_" + System.currentTimeMillis() + ".txt");


        if (rutaArchivo != null) {
            dbHelper.registrarExportacion("TXT", 0); // idTarea = 0 significa "todas las tareas"
        }

        return rutaArchivo;
    }

    // Exportar en formato CSV
    public String exportarCSV(long idEmpleado, String nombreEmpleado) {
        Cursor cursor = dbHelper.obtenerTodasTareasActivas(idEmpleado);

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        StringBuilder contenido = new StringBuilder();
        // Encabezados CSV
        contenido.append("ID,Título,Fecha Inicio,Hora Inicio,Fecha Fin,Hora Fin,Responsable,Descripción\n");

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("idTarea"));
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            String fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inicio"));
            String horaInicio = cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio"));
            String fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fecha_fin"));
            String horaFin = cursor.getString(cursor.getColumnIndexOrThrow("hora_fin"));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));

            // Escapar comillas en CSV
            titulo = escaparCSV(titulo);
            descripcion = escaparCSV(descripcion);

            contenido.append(id).append(",");
            contenido.append(titulo).append(",");
            contenido.append(fechaInicio).append(",");
            contenido.append(horaInicio).append(",");
            contenido.append(fechaFin).append(",");
            contenido.append(horaFin).append(",");
            contenido.append(nombreEmpleado).append(",");
            contenido.append(descripcion).append("\n");
        }

        cursor.close();

        String rutaArchivo = guardarArchivo(contenido.toString(), "tareas_" + System.currentTimeMillis() + ".csv");

        //
        if (rutaArchivo != null) {
            dbHelper.registrarExportacion("CSV", 0);
        }

        return rutaArchivo;
    }

    // Exportar en formato JSON
    public String exportarJSON(long idEmpleado, String nombreEmpleado) {
        Cursor cursor = dbHelper.obtenerTodasTareasActivas(idEmpleado);

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        StringBuilder contenido = new StringBuilder();
        contenido.append("{\n");
        contenido.append("  \"usuario\": \"").append(nombreEmpleado).append("\",\n");
        contenido.append("  \"fecha_exportacion\": \"").append(obtenerFechaActual()).append("\",\n");
        contenido.append("  \"tareas\": [\n");

        boolean primero = true;
        while (cursor.moveToNext()) {
            if (!primero) {
                contenido.append(",\n");
            }

            long id = cursor.getLong(cursor.getColumnIndexOrThrow("idTarea"));
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            String fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inicio"));
            String horaInicio = cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio"));
            String fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fecha_fin"));
            String horaFin = cursor.getString(cursor.getColumnIndexOrThrow("hora_fin"));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));

            contenido.append("    {\n");
            contenido.append("      \"id\": ").append(id).append(",\n");
            contenido.append("      \"titulo\": \"").append(escaparJSON(titulo)).append("\",\n");
            contenido.append("      \"fecha_inicio\": \"").append(fechaInicio).append("\",\n");
            contenido.append("      \"hora_inicio\": \"").append(horaInicio).append("\",\n");
            contenido.append("      \"fecha_fin\": \"").append(fechaFin).append("\",\n");
            contenido.append("      \"hora_fin\": \"").append(horaFin).append("\",\n");
            contenido.append("      \"responsable\": \"").append(nombreEmpleado).append("\",\n");
            contenido.append("      \"descripcion\": \"").append(escaparJSON(descripcion)).append("\"\n");
            contenido.append("    }");

            primero = false;
        }

        contenido.append("\n  ]\n");
        contenido.append("}\n");

        cursor.close();

        String rutaArchivo = guardarArchivo(contenido.toString(), "tareas_" + System.currentTimeMillis() + ".json");


        if (rutaArchivo != null) {
            dbHelper.registrarExportacion("JSON", 0);
        }

        return rutaArchivo;
    }

    // Guardar archivo en almacenamiento
    private String guardarArchivo(String contenido, String nombreArchivo) {
        try {
            // Directorio de descargas del dispositivo
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File archivo = new File(downloadsDir, nombreArchivo);

            FileWriter writer = new FileWriter(archivo);
            writer.write(contenido);
            writer.close();

            return archivo.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Métodos auxiliares
    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String escaparCSV(String texto) {
        if (texto == null) return "";
        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }

    private String escaparJSON(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }



    // Exportar UNA tarea específica en formato TXT
    public String exportarTareaIndividualTXT(long idTarea, String nombreEmpleado) {
        Cursor cursor = dbHelper.obtenerTareaPorId(idTarea);

        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
        String fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inicio"));
        String horaInicio = cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio"));
        String fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fecha_fin"));
        String horaFin = cursor.getString(cursor.getColumnIndexOrThrow("hora_fin"));
        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
        cursor.close();

        StringBuilder contenido = new StringBuilder();
        contenido.append("==============================================\n");
        contenido.append("     AGENDA ELECTRÓNICA GTIM\n");
        contenido.append("     TAREA EXPORTADA\n");
        contenido.append("     Fecha: ").append(obtenerFechaActual()).append("\n");
        contenido.append("==============================================\n\n");
        contenido.append("Título: ").append(titulo).append("\n");
        contenido.append("Inicio: ").append(fechaInicio).append(" ").append(horaInicio).append("\n");
        contenido.append("Fin: ").append(fechaFin).append(" ").append(horaFin).append("\n");
        contenido.append("Responsable: ").append(nombreEmpleado).append("\n");
        if (descripcion != null && !descripcion.isEmpty()) {
            contenido.append("Descripción: ").append(descripcion).append("\n");
        }

        String rutaArchivo = guardarArchivo(contenido.toString(),
                "tarea_" + idTarea + "_" + System.currentTimeMillis() + ".txt");

        if (rutaArchivo != null) {
            dbHelper.registrarExportacion("TXT", idTarea);
        }

        return rutaArchivo;
    }
}