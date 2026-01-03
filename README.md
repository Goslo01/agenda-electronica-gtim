# Agenda Electrónica GTIM

![Android](https://img.shields.io/badge/Android-8.0%2B-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![SQLite](https://img.shields.io/badge/SQLite-3-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

Una aplicación Android completa para la gestión profesional de tareas con sistema de borrado lógico, exportación de datos y almacenamiento local usando SQLite

## 📋 Descripción

Agenda Electrónica GTIM es una aplicación móvil diseñada para facilitar la organización y seguimiento de tareas personales. Permite a los usuarios crear, gestionar, exportar y guardar sus actividades de manera eficiente. Incluye un sistema de borrado lógico que previene cualquier borrado de información accidental.

## ✨ Características Principales

### 🔐 Gestión de Usuarios
- Sistema de registro de nuevos usuarios
- Autenticación segura con validación de credenciales

### ✏️ Gestión de Tareas
- Creación de tareas con información completa (título, fechas, horarios, descripción)
- Selectores visuales de fecha y hora
- Visualización de todas las tareas activas
- Filtrado de tareas por usuario

### 🗑️ Borrado Lógico y Papelera
- Sistema de borrado lógico que previene el borrado accidental de datos
- Papelera para revisar tareas eliminadas
- Recuperación de tareas eliminadas de forma accidental
- Eliminación permanente de tareas

### 📤 Exportación de Datos
- Exportación de todas las tareas
- Exportación individual de tareas
- Tres formatos disponibles de exportación: TXT, CSV, JSON
- Archivos guardados en la carpeta de descargas del teléfono Android

### 💾 Historial de Exportaciones
- Registro completo de todas las exportaciones realizadas
- Consulta de fecha y formato de cada exportación

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java
- **Plataforma:** Android SDK
- **Base de Datos:** SQLite
- **IDE:** Android Studio
- **Mínimo API Level:** 26 (Android 8.0 Oreo)

## 🗄️ Estructura de Base de Datos

### Tablas Principales

#### EMPLEADO
```sql
CREATE TABLE empleado (
    idEmpleado INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50),
    sexo VARCHAR(10)
);
```

#### USUARIO
```sql
CREATE TABLE usuario (
    idEmpleado INTEGER PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    FOREIGN KEY(idEmpleado) REFERENCES empleado(idEmpleado) ON DELETE CASCADE
);
```

#### TAREA
```sql
CREATE TABLE tarea (
    idTarea INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo VARCHAR(100) NOT NULL,
    fecha_inicio TEXT NOT NULL,
    hora_inicio TEXT NOT NULL,
    fecha_fin TEXT,
    hora_fin TEXT,
    descripcion TEXT,
    idEmpleado_responsable INTEGER,
    eliminado INTEGER DEFAULT 0,
    FOREIGN KEY(idEmpleado_responsable) REFERENCES empleado(idEmpleado)
);
```

#### ALMACENAMIENTO
```sql
CREATE TABLE almacenamiento (
    idAlmacenamiento INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo_formato VARCHAR(10) NOT NULL,
    fecha_guardado DATETIME NOT NULL,
    idTarea INTEGER,
    FOREIGN KEY(idTarea) REFERENCES tarea(idTarea)
);
```

## 📦 Estructura del Proyecto
```
app/
└── src/
    └── main/
        ├── java/com/example/agendaelectronicagtim/
        │   ├── DatabaseHelper.java          # Gestor de base de datos SQLite
        │   ├── ExportManager.java           # Manejo de exportaciones
        │   ├── HistorialExportacionesActivity.java
        │   ├── LoginActivity.java           # Pantalla de login
        │   ├── MainActivity.java            # Pantalla principal
        │   ├── PapeleraActivity.java        # Pantalla de papelera
        │   ├── PapeleraAdapter.java         # Adaptador de papelera
        │   ├── RegistroActivity.java        # Pantalla de registro
        │   ├── Tarea.java                   # Modelo de datos
        │   └── TareaAdapter.java            # Adaptador de lista de tareas
        ├── res/
        │   └── layout/
        │       ├── activity_login.xml
        │       ├── activity_main.xml
        │       ├── activity_papelera.xml
        │       ├── activity_registro.xml
        │       ├── activity_historial_exportaciones.xml
        │       ├── item_tarea.xml
        │       └── item_papelera.xml
        └── AndroidManifest.xml
```

## 🚀 Instalación y Uso

### Requisitos Previos
- Android Studio (última versión)
- Dispositivo Android o Emulador con API 26+ (Android 8.0 Oreo o superior)
- JDK 11 o superior

### Permisos Necesarios
La aplicación requiere los siguientes permisos:
- `WRITE_EXTERNAL_STORAGE` - Para exportar archivos
- `READ_EXTERNAL_STORAGE` - Para acceder a archivos exportados

### Pasos de Instalación

1. Clona el repositorio:
```bash
   git clone https://github.com/Goslo01/agenda-electronica-gtim.git
```

2. Abre el proyecto en Android Studio

3. Sincroniza el proyecto con Gradle:
   - `File > Sync Project with Gradle Files`

4. Conecta un dispositivo Android o inicia un emulador

5. Ejecuta la aplicación:
   - Haz clic en el botón ▶️ Run

### Credenciales de Prueba
La aplicación incluye un usuario de prueba:
- **Usuario:** admin
- **Contraseña:** 1234

## 📄 Licencia
Este proyecto es de código abierto y está disponible bajo la Licencia MIT.

## 👨‍💻 Autor
**Luis Gzz**
- GitHub: [@Goslo01](https://github.com/Goslo01)

---
