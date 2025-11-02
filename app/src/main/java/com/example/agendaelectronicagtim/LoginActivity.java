package com.example.agendaelectronicagtim;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    // Variables para los campos de texto
    private EditText editUsuario, editContrasena;
    private Button btnLogin, btnRegistro;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar la base de datos
        dbHelper = new DatabaseHelper(this);

        // Conectar las variables con los elementos del XML
        inicializarComponentes();

        // Configurar botones
        configurarBotonLogin();
        configurarBotonRegistro();
    }

    // Método para conectar variables con XML
    private void inicializarComponentes() {
        editUsuario = findViewById(R.id.editUsuario);
        editContrasena = findViewById(R.id.editContrasena);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);
    }

    // Método para configurar qué pasa cuando presionas el botón de login
    private void configurarBotonLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCredenciales();
            }
        });
    }

    // Método para configurar el botón de registro
    private void configurarBotonRegistro() {
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    // Método para validar usuario y contraseña contra la base de datos
    private void validarCredenciales() {
        // Obtener el texto de los campos
        String usuario = editUsuario.getText().toString().trim();
        String contrasena = editContrasena.getText().toString().trim();

        // Verificar que los campos no estén vacíos
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar contra la base de datos (devuelve idEmpleado)
        long idEmpleado = dbHelper.validarLogin(usuario, contrasena);

        if (idEmpleado != -1) {
            // Login exitoso - obtener nombre del empleado
            String nombreEmpleado = dbHelper.obtenerNombreEmpleado(idEmpleado);

            Toast.makeText(this, "Bienvenido " + nombreEmpleado + "!",
                    Toast.LENGTH_SHORT).show();

            // Crear un Intent para ir a MainActivity y pasar el ID del empleado
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("idEmpleado", idEmpleado);
            intent.putExtra("nombreEmpleado", nombreEmpleado);
            startActivity(intent);

            // Cerrar esta actividad
            finish();
        } else {
            // Login fallido
            Toast.makeText(this, "Usuario o contraseña incorrectos",
                    Toast.LENGTH_SHORT).show();
        }
    }
}