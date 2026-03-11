# Proyecto Intermodular – InmobiliaControl

## Estado actual del proyecto
En esta fase del proyecto se ha implementado una base de datos local utilizando **Room (SQLite)** para gestionar el acceso de usuarios desde la pantalla de login de la aplicación.

El objetivo de esta primera integración es disponer de una estructura mínima de persistencia que permita almacenar y validar usuarios dentro de la aplicación.

---

# Reparto de tareas del equipo
El trabajo del proyecto se está desarrollando de la siguiente forma:

- **Federico** → Desarrollo de la **interfaz de usuario (UI)** de la aplicación.
- **Juanjo** → Implementación de **table views / listados de datos**.
- **Bryant** → Implementación de la **base de datos con Room** e integración con el sistema de login.

---

# Base de datos implementada

Se ha creado una base de datos local llamada:

**inmobilia_db**

Dentro de esta base de datos se ha implementado la tabla:

### users

Campos:

- `id` → INTEGER (Primary Key autogenerada)
- `email` → TEXT
- `password` → TEXT

---

# Arquitectura añadida

Para implementar Room se han añadido los siguientes paquetes y archivos:

### entity
Contiene las entidades de la base de datos.

- `User.kt` → Define la entidad User.

### dao
Contiene las operaciones de acceso a datos.

- `UserDao.kt` → Métodos para insertar y consultar usuarios.

### database
Configuración de la base de datos.

- `InmobiliaDatabase.kt` → Configuración de Room y acceso a la base de datos.

## repository
Capa intermedia entre la base de datos y la interfaz.

- `UserRepository.kt` → Gestiona el acceso a los datos desde la aplicación.

---

# Integración con la pantalla de login

Se ha conectado la base de datos con la pantalla inicial de login.

La pantalla permite introducir:

- Tipo de usuario
- Email / Usuario
- Contraseña

Cuando el usuario se registra o inicia sesión, la información queda almacenada en la base de datos.

---

# Desarrollo de la interfaz de usuario

La interfaz inicial de la aplicación ha sido desarrollada utilizando **Jetpack Compose**.

Se ha implementado una **pantalla de login funcional**, que incluye:

- Visualización del **logo de la aplicación**.
- Selección del **tipo de usuario** mediante un desplegable (Inquilino, Agencia o Dueño).
- Campos de entrada para **email/usuario** y **contraseña**.
- Validación básica de los datos introducidos por el usuario antes de iniciar sesión.

La pantalla de login se encuentra conectada con el **LoginViewModel**, que gestiona el estado de la interfaz y la lógica de autenticación.

También se ha implementado la **navegación básica de la aplicación**, permitiendo acceder a una pantalla inicial (**HomeScreen**) una vez completado el proceso de login.

Esta pantalla sirve como base para el desarrollo de las siguientes vistas de la aplicación, donde se implementarán las funcionalidades de gestión de incidencias.

---

# Verificación mediante Database Inspector

Para comprobar que los datos se almacenan correctamente:

1. Ejecutar la aplicación en el emulador.
2. Abrir **App Inspection** en Android Studio.
3. Acceder a **Database Inspector**.
4. Seleccionar la base de datos **inmobilia_db**.
5. Consultar la tabla **users**.

De esta forma se pueden visualizar los registros almacenados.

---

# Próximos pasos del proyecto

Las siguientes fases del desarrollo serán:

- Implementar las siguientes pantallas de la aplicación.
- Crear el sistema de **tickets**.
- Registrar los **tickets** en la base de datos.
- Asociar a cada ticket sus **incidencias correspondientes**.

Esto permitirá gestionar incidencias dentro de la aplicación de forma estructurada.

---

# Tecnologías utilizadas

- Kotlin
- Jetpack Compose
- Room Database
- Android Studio
- SQLite
