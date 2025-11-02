package com.example.agendaelectronicagtim;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter; // Importación necesaria
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;      // Importación necesaria
import android.widget.Toast;

public class RegistroActivity extends AppCompatActivity {


    private EditText editNombre, editApellido, editEmail,
            editUsuario, editContrasena, editConfirmarContrasena;

    private Spinner spinnerSexo;
    private Button btnRegistrarse, btnYaTengoCuenta;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        dbHelper = new DatabaseHelper(this);
        inicializarComponentes();
        configurarBotones();
    }


    private void inicializarComponentes() {
        editNombre = findViewById(R.id.editNombre);
        editApellido = findViewById(R.id.editApellido);
        editEmail = findViewById(R.id.editEmail);
        editUsuario = findViewById(R.id.editUsuario);
        editContrasena = findViewById(R.id.editContrasena);
        editConfirmarContrasena = findViewById(R.id.editConfirmarContrasena);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        btnYaTengoCuenta = findViewById(R.id.btnYaTengoCuenta);

        // --- Configuración completa del Spinner ---
        spinnerSexo = findViewById(R.id.spinnerSexo);

        // 1. Crea un array con las opciones que quieres mostrar
        String[] opciones = {"Masculino", "Femenino", "Otro"};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opciones);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerSexo.setAdapter(adapter);
    }

    private void configurarBotones() {
        btnRegistrarse.setOnClickListener(v -> registrarUsuario());
        btnYaTengoCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }


    private void registrarUsuario() {
        String nombre = editNombre.getText().toString().trim();
        String apellido = editApellido.getText().toString().trim();


        String sexo = spinnerSexo.getSelectedItem().toString();

        String email = editEmail.getText().toString().trim();
        String usuario = editUsuario.getText().toString().trim();
        String contrasena = editContrasena.getText().toString().trim();
        String confirmarContrasena = editConfirmarContrasena.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() ||
                usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contrasena.length() < 4) {
            Toast.makeText(this, "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        long resultado = dbHelper.registrarUsuario(nombre, apellido, sexo, email, usuario, contrasena);

        if (resultado > 0) {
            Toast.makeText(this, "¡Registro exitoso! Ahora puedes iniciar sesión", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (resultado == -2) {
            Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
        } else if (resultado == -3) {
            Toast.makeText(this, "El correo electrónico ya está registrado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al registrar. Intenta de nuevo", Toast.LENGTH_SHORT).show();
        }
    }
}
