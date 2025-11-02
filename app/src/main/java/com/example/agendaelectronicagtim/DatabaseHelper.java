package com.example.agendaelectronicagtim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_android.db";
    private static final int DATABASE_VERSION = 4; //

    // Nombres de las tablas
    private static final String TABLE_EMPLEADO = "empleado";
    private static final String TABLE_USUARIO = "usuario";
    private static final String TABLE_TAREA = "tarea";
    private static final String TABLE_ALMACENAMIENTO = "almacenamiento"; //

    // Columnas de EMPLEADO
    private static final String COL_EMPLEADO_ID = "idEmpleado";
    private static final String COL_EMPLEADO_NOMBRE = "nombre";
    private static final String COL_EMPLEADO_APELLIDO = "apellido";
    private static final String COL_EMPLEADO_SEXO = "sexo";

    // Columnas de USUARIO
    private static final String COL_USUARIO_ID_EMPLEADO = "idEmpleado";
    private static final String COL_USUARIO_NOMBRE = "nombre_usuario";
    private static final String COL_USUARIO_CONTRASENA = "contrasena";
    private static final String COL_USUARIO_EMAIL = "email";

    // Columnas de TAREA
    private static final String COL_TAREA_ID = "idTarea";
    private static final String COL_TAREA_TITULO = "titulo";
    private static final String COL_TAREA_FECHA_INICIO = "fecha_inicio";
    private static final String COL_TAREA_HORA_INICIO = "hora_inicio";
    private static final String COL_TAREA_FECHA_FIN = "fecha_fin";
    private static final String COL_TAREA_HORA_FIN = "hora_fin";
    private static final String COL_TAREA_DESCRIPCION = "descripcion";
    private static final String COL_TAREA_ID_EMPLEADO = "idEmpleado_responsable";
    private static final String COL_TAREA_ELIMINADO = "eliminado";

    // Columnas almacenamiento
    private static final String COL_ALMACENAMIENTO_ID = "idAlmacenamiento";
    private static final String COL_ALMACENAMIENTO_TIPO = "tipo_formato";
    private static final String COL_ALMACENAMIENTO_FECHA = "fecha_guardado";
    private static final String COL_ALMACENAMIENTO_ID_TAREA = "idTarea";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // tabla EMPLEADO
        String createEmpleado = "CREATE TABLE " + TABLE_EMPLEADO + " (" +
                COL_EMPLEADO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMPLEADO_NOMBRE + " TEXT NOT NULL, " +
                COL_EMPLEADO_APELLIDO + " TEXT, " +
                COL_EMPLEADO_SEXO + " TEXT)";
        db.execSQL(createEmpleado);

        // tabla USUARIO
        String createUsuario = "CREATE TABLE " + TABLE_USUARIO + " (" +
                COL_USUARIO_ID_EMPLEADO + " INTEGER PRIMARY KEY, " +
                COL_USUARIO_NOMBRE + " TEXT NOT NULL UNIQUE, " +
                COL_USUARIO_CONTRASENA + " TEXT NOT NULL, " +
                COL_USUARIO_EMAIL + " TEXT NOT NULL UNIQUE, " +
                "FOREIGN KEY(" + COL_USUARIO_ID_EMPLEADO + ") REFERENCES " +
                TABLE_EMPLEADO + "(" + COL_EMPLEADO_ID + ") ON DELETE CASCADE)";
        db.execSQL(createUsuario);

        // tabla TAREA
        String createTarea = "CREATE TABLE " + TABLE_TAREA + " (" +
                COL_TAREA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TAREA_TITULO + " TEXT NOT NULL, " +
                COL_TAREA_FECHA_INICIO + " TEXT NOT NULL, " +
                COL_TAREA_HORA_INICIO + " TEXT NOT NULL, " +
                COL_TAREA_FECHA_FIN + " TEXT, " +
                COL_TAREA_HORA_FIN + " TEXT, " +
                COL_TAREA_DESCRIPCION + " TEXT, " +
                COL_TAREA_ID_EMPLEADO + " INTEGER, " +
                COL_TAREA_ELIMINADO + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COL_TAREA_ID_EMPLEADO + ") REFERENCES " +
                TABLE_EMPLEADO + "(" + COL_EMPLEADO_ID + "))";
        db.execSQL(createTarea);

        // ALMACENAMIENTO
        String createAlmacenamiento = "CREATE TABLE " + TABLE_ALMACENAMIENTO + " (" +
                COL_ALMACENAMIENTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ALMACENAMIENTO_TIPO + " TEXT NOT NULL, " +
                COL_ALMACENAMIENTO_FECHA + " DATETIME NOT NULL, " +
                COL_ALMACENAMIENTO_ID_TAREA + " INTEGER, " +
                "FOREIGN KEY(" + COL_ALMACENAMIENTO_ID_TAREA + ") REFERENCES " +
                TABLE_TAREA + "(" + COL_TAREA_ID + "))";
        db.execSQL(createAlmacenamiento);

        insertarDatosPrueba(db);
    }

    private void insertarDatosPrueba(SQLiteDatabase db) {
        ContentValues empleado = new ContentValues();
        empleado.put(COL_EMPLEADO_NOMBRE, "Admin");
        empleado.put(COL_EMPLEADO_APELLIDO, "Sistema");
        empleado.put(COL_EMPLEADO_SEXO, "Masculino");
        long idEmpleado = db.insert(TABLE_EMPLEADO, null, empleado);

        ContentValues usuario = new ContentValues();
        usuario.put(COL_USUARIO_ID_EMPLEADO, idEmpleado);
        usuario.put(COL_USUARIO_NOMBRE, "admin");
        usuario.put(COL_USUARIO_CONTRASENA, "1234");
        usuario.put(COL_USUARIO_EMAIL, "admin@sistema.com");
        db.insert(TABLE_USUARIO, null, usuario);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALMACENAMIENTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLEADO);
        onCreate(db);
    }

    // USUARIO

    public long validarLogin(String nombreUsuario, String contrasena) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_USUARIO_ID_EMPLEADO + " FROM " + TABLE_USUARIO +
                " WHERE " + COL_USUARIO_NOMBRE + " = ? AND " + COL_USUARIO_CONTRASENA + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{nombreUsuario, contrasena});
        long idEmpleado = -1;
        if (cursor.moveToFirst()) {
            idEmpleado = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return idEmpleado;
    }

    public String obtenerNombreEmpleado(long idEmpleado) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_EMPLEADO_NOMBRE + ", " + COL_EMPLEADO_APELLIDO +
                " FROM " + TABLE_EMPLEADO + " WHERE " + COL_EMPLEADO_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idEmpleado)});
        String nombreCompleto = "";
        if (cursor.moveToFirst()) {
            nombreCompleto = cursor.getString(0) + " " + cursor.getString(1);
        }
        cursor.close();
        db.close();
        return nombreCompleto;
    }

    public long registrarUsuario(String nombre, String apellido, String sexo,
                                 String email, String nombreUsuario, String contrasena) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursorUsuario = db.rawQuery(
                    "SELECT * FROM " + TABLE_USUARIO + " WHERE " + COL_USUARIO_NOMBRE + " = ?",
                    new String[]{nombreUsuario});
            if (cursorUsuario.getCount() > 0) {
                cursorUsuario.close();
                db.close();
                return -2;
            }
            cursorUsuario.close();

            Cursor cursorEmail = db.rawQuery(
                    "SELECT * FROM " + TABLE_USUARIO + " WHERE " + COL_USUARIO_EMAIL + " = ?",
                    new String[]{email});
            if (cursorEmail.getCount() > 0) {
                cursorEmail.close();
                db.close();
                return -3;
            }
            cursorEmail.close();

            ContentValues empleado = new ContentValues();
            empleado.put(COL_EMPLEADO_NOMBRE, nombre);
            empleado.put(COL_EMPLEADO_APELLIDO, apellido);
            empleado.put(COL_EMPLEADO_SEXO, sexo);
            long idEmpleado = db.insert(TABLE_EMPLEADO, null, empleado);

            if (idEmpleado == -1) {
                db.close();
                return -1;
            }

            ContentValues usuario = new ContentValues();
            usuario.put(COL_USUARIO_ID_EMPLEADO, idEmpleado);
            usuario.put(COL_USUARIO_NOMBRE, nombreUsuario);
            usuario.put(COL_USUARIO_CONTRASENA, contrasena);
            usuario.put(COL_USUARIO_EMAIL, email);

            long resultado = db.insert(TABLE_USUARIO, null, usuario);
            db.close();
            return resultado;

        } catch (Exception e) {
            db.close();
            return -1;
        }
    }

    // ==================== MÉTODOS PARA TAREA ====================

    public long insertarTarea(String titulo, String fechaInicio, String horaInicio,
                              String fechaFin, String horaFin, String descripcion, long idEmpleado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TAREA_TITULO, titulo);
        values.put(COL_TAREA_FECHA_INICIO, fechaInicio);
        values.put(COL_TAREA_HORA_INICIO, horaInicio);
        values.put(COL_TAREA_FECHA_FIN, fechaFin);
        values.put(COL_TAREA_HORA_FIN, horaFin);
        values.put(COL_TAREA_DESCRIPCION, descripcion);
        values.put(COL_TAREA_ID_EMPLEADO, idEmpleado);
        values.put(COL_TAREA_ELIMINADO, 0);
        long resultado = db.insert(TABLE_TAREA, null, values);
        db.close();
        return resultado;
    }

    public Cursor obtenerTareasEmpleado(long idEmpleado) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TAREA +
                " WHERE " + COL_TAREA_ID_EMPLEADO + " = ? AND " +
                COL_TAREA_ELIMINADO + " = 0 " +
                "ORDER BY " + COL_TAREA_ID + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(idEmpleado)});
    }

    public Cursor obtenerTareasEliminadas(long idEmpleado) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TAREA +
                " WHERE " + COL_TAREA_ID_EMPLEADO + " = ? AND " +
                COL_TAREA_ELIMINADO + " = 1 " +
                "ORDER BY " + COL_TAREA_ID + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(idEmpleado)});
    }

    public boolean eliminarTarea(long idTarea) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TAREA_ELIMINADO, 1);
        int resultado = db.update(TABLE_TAREA, values,
                COL_TAREA_ID + " = ?", new String[]{String.valueOf(idTarea)});
        db.close();
        return resultado > 0;
    }

    public boolean recuperarTarea(long idTarea) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TAREA_ELIMINADO, 0);
        int resultado = db.update(TABLE_TAREA, values,
                COL_TAREA_ID + " = ?", new String[]{String.valueOf(idTarea)});
        db.close();
        return resultado > 0;
    }

    public boolean eliminarTareaPermanentemente(long idTarea) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete(TABLE_TAREA, COL_TAREA_ID + " = ?",
                new String[]{String.valueOf(idTarea)});
        db.close();
        return resultado > 0;
    }



    // Registrar una exportación en la tabla ALMACENAMIENTO
    public long registrarExportacion(String tipoFormato, long idTarea) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Obtener fecha y hora actual
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fechaActual = sdf.format(new Date());

        ContentValues values = new ContentValues();
        values.put(COL_ALMACENAMIENTO_TIPO, tipoFormato);
        values.put(COL_ALMACENAMIENTO_FECHA, fechaActual);
        values.put(COL_ALMACENAMIENTO_ID_TAREA, idTarea);

        long resultado = db.insert(TABLE_ALMACENAMIENTO, null, values);
        db.close();
        return resultado;
    }

    // Obtener historial de exportaciones
    public Cursor obtenerHistorialExportaciones() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT a.*, t.titulo FROM " + TABLE_ALMACENAMIENTO + " a " +
                "LEFT JOIN " + TABLE_TAREA + " t ON a." + COL_ALMACENAMIENTO_ID_TAREA +
                " = t." + COL_TAREA_ID +
                " ORDER BY a." + COL_ALMACENAMIENTO_FECHA + " DESC";
        return db.rawQuery(query, null);
    }

    // Obtener todas las tareas activas (para exportar todas)
    public Cursor obtenerTodasTareasActivas(long idEmpleado) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TAREA +
                " WHERE " + COL_TAREA_ID_EMPLEADO + " = ? AND " +
                COL_TAREA_ELIMINADO + " = 0";
        return db.rawQuery(query, new String[]{String.valueOf(idEmpleado)});
    }

    // *** NUEVO: Obtener una tarea específica por su ID ***
    public Cursor obtenerTareaPorId(long idTarea) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TAREA +
                " WHERE " + COL_TAREA_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(idTarea)});
    }
}