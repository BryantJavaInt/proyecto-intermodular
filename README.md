<div align="center">

<br/>

<img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
<img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
<img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>
<img src="https://img.shields.io/badge/Architecture-MVVM-FF6F00?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Database-Room%20%2F%20SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white"/>
<img src="https://img.shields.io/badge/Status-Production%20Ready-success?style=for-the-badge"/>

<br/><br/>

# 🏠 InmobiliaControl

### Enterprise Mobile Solution for Real Estate Incident Management

*Eliminando la fragmentación en la gestión de incidencias inmobiliarias*

<br/>

[![Made with Kotlin](https://img.shields.io/badge/Made%20with-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android SDK](https://img.shields.io/badge/Target%20SDK-35-3DDC84?logo=android)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-Academic%20Project-blue)]()
[![Universidad Europea](https://img.shields.io/badge/Universidad-Europea%20de%20Madrid-red)]()

</div>

---

## 📋 Tabla de Contenidos

- [Visión General](#-visión-general)
- [Arquitectura del Sistema](#-arquitectura-del-sistema)
- [Stack Tecnológico](#-stack-tecnológico)
- [Modelo de Roles](#-modelo-de-roles)
- [Estructura de la Base de Datos](#-estructura-de-la-base-de-datos)
- [Flujo de Estados](#-flujo-de-estados-de-una-incidencia)
- [Módulos Principales](#-módulos-principales)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Control de Calidad](#-control-de-calidad--pruebas)
- [Roadmap](#-roadmap--futuras-mejoras)
- [Equipo](#-equipo-de-desarrollo)

---

## 🎯 Visión General

**InmobiliaControl** es una solución móvil nativa Android diseñada para digitalizar y centralizar la gestión de incidencias en activos inmobiliarios. La plataforma actúa como nexo de comunicación entre los tres actores fundamentales del ecosistema inmobiliario, eliminando la dependencia de canales informales (llamadas telefónicas, mensajería instantánea, correo electrónico) que derivan en pérdida crítica de información y falta de trazabilidad.

```
Problema:  Comunicación fragmentada → Pérdida de información → Incidencias sin resolver
Solución:  Plataforma centralizada → Trazabilidad total → Ciclo de vida completo del ticket
```

### Propuesta de Valor

| Antes de InmobiliaControl | Con InmobiliaControl |
|---|---|
| Llamadas sin registro ni seguimiento | Tickets documentados con historial completo |
| Sin asignación formal de responsables | Roles definidos con permisos específicos |
| Desconocimiento del estado de la avería | Estado en tiempo real con código de colores |
| Comunicación dispersa en múltiples canales | Canal de mensajería interno por incidencia |
| Datos perdidos al cerrar la app | Persistencia local garantizada con Room |

---

## 🏛️ Arquitectura del Sistema

El proyecto implementa el patrón **Model-View-ViewModel (MVVM)**, recomendado por Google como estándar para el desarrollo Android moderno, garantizando una separación estricta de responsabilidades y facilitando la escalabilidad a largo plazo.

```
┌─────────────────────────────────────────────────────────────┐
│                        UI LAYER                             │
│              Jetpack Compose @Composable                    │
│         (Login · Dashboard · Tickets · Detail · Chat)       │
└──────────────────────┬──────────────────────────────────────┘
                       │  observes state
┌──────────────────────▼──────────────────────────────────────┐
│                    VIEWMODEL LAYER                          │
│   LoginViewModel · TicketViewModel · RegisterViewModel      │
│              StateFlow · SavedStateHandle                   │
└──────────────────────┬──────────────────────────────────────┘
                       │  calls
┌──────────────────────▼──────────────────────────────────────┐
│                   REPOSITORY LAYER                          │
│  UserRepository · TicketRepository · CommentRepository     │
│                  PropertyRepository                         │
└──────────────────────┬──────────────────────────────────────┘
                       │  queries
┌──────────────────────▼──────────────────────────────────────┐
│                     DATA LAYER                              │
│     Room Database (inmobilia_db)  ·  DAOs  ·  Entities      │
│              SQLite — Singleton Pattern                     │
└─────────────────────────────────────────────────────────────┘
```

### Principios de diseño aplicados

- **Singleton Pattern** — Instancia única de la base de datos para garantizar la integridad de los datos
- **Repository Pattern** — Abstracción de la fuente de datos, desacoplando la lógica de negocio
- **Reactive UI** — La interfaz se actualiza automáticamente ante cambios en el estado mediante `StateFlow`
- **Cascade Deletion** — Integridad referencial garantizada mediante `ForeignKey.CASCADE`
- **Data Seeding** — Inyección de datos iniciales en el primer arranque para garantizar UX óptima

---

## 🛠️ Stack Tecnológico

| Capa | Tecnología | Versión / Detalle |
|---|---|---|
| **Lenguaje** | Kotlin | Coroutines + Flow |
| **UI Framework** | Jetpack Compose | Declarativo y reactivo |
| **Arquitectura** | MVVM | Google Architecture Guidelines |
| **Base de datos** | Room (SQLite) | `inmobilia_db` — Singleton |
| **Navegación** | Navigation Compose | NavHost centralizado con paso seguro de argumentos |
| **Build System** | Gradle | Kotlin DSL |
| **IDE** | Android Studio | Ladybug o superior |
| **Target SDK** | Android SDK 35 | Compatible con últimas APIs del SO |
| **Control de versiones** | GitHub | Integración continua del equipo |
| **Gestión de proyecto** | Scrum | Sprints de 2 semanas |
| **Diagramas** | draw.io | UML · Casos de uso · Navegación |

---

## 👥 Modelo de Roles

La aplicación implementa un sistema de **autenticación con bifurcación de lógica** basado en el rol seleccionado en el inicio de sesión. Cada perfil accede a una interfaz y permisos completamente diferenciados.

```
                    ┌─────────────────┐
                    │   Pantalla de   │
                    │     Login       │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
    ┌─────────▼──────┐ ┌─────▼──────┐ ┌────▼───────────┐
    │    TENANT      │ │   AGENCY   │ │  MAINTENANCE   │
    │  (Inquilino)   │ │  (Agencia) │ │  (Técnico)     │
    └────────────────┘ └────────────┘ └────────────────┘
```

### Permisos por rol

| Acción | TENANT | AGENCY | MAINTENANCE |
|---|:---:|:---:|:---:|
| Crear incidencia | ✅ | ✅ | ❌ |
| Ver sus propias incidencias | ✅ | — | — |
| Ver todas las incidencias | ❌ | ✅ | ❌ |
| Ver incidencias asignadas | ❌ | ❌ | ✅ |
| Cambiar estado del ticket | ❌ | ✅ | ✅ |
| Cambiar prioridad | ❌ | ✅ | ❌ |
| Enviar comentarios internos | ✅ | ✅ | ✅ |

---

## 🗄️ Estructura de la Base de Datos

Base de datos local `inmobilia_db` implementada con **Room (SQLite)**, inicializada bajo patrón Singleton.

```sql
┌──────────────────────┐        ┌──────────────────────┐
│        User          │        │       Property       │
├──────────────────────┤        ├──────────────────────┤
│ PK  user_id          │        │ PK  property_id      │
│     name             │        │     address          │
│     email (UNIQUE)   │        │     reference        │
│     password_hash    │        └──────────┬───────────┘
│     role             │                   │ (1)
│     is_active        │                   │
└──────────┬───────────┘                   │
           │ (1)                           │ (N)
           │ (N)                 ┌─────────▼───────────┐
           └─────────────────────►       Ticket        │
                                 ├─────────────────────┤
                                 │ PK  ticket_id       │
                                 │ FK  property_id     │
                                 │ FK  created_by_user │
                                 │     title           │
                                 │     description     │
                                 │     category        │
                                 │     priority        │
                                 │     status          │
                                 │     assigned_to     │
                                 │     created_at      │
                                 │     updated_at      │
                                 └──────────┬──────────┘
                                            │ (1)
                                            │ (N)
                                 ┌──────────▼──────────┐
                                 │       Comment       │
                                 ├─────────────────────┤
                                 │ PK  comment_id      │
                                 │ FK  ticket_id       │
                                 │ FK  author_user_id  │
                                 │     message         │
                                 │     created_at      │
                                 └─────────────────────┘
```

---

## 🔄 Flujo de Estados de una Incidencia

```
  ┌─────────────┐    Agencia asigna    ┌─────────────────┐    Técnico cierra    ┌─────────────┐
  │   ABIERTA   │ ──────────────────►  │   EN PROCESO    │ ─────────────────►  │   RESUELTA  │
  │     🔴      │                      │       🟡        │                      │     🔵      │
  └─────────────┘                      └─────────────────┘                      └─────────────┘

  Estado inicial        Técnico asignado,          Reparación completada,
  tras el reporte       trabajo en curso            ciclo cerrado
```

---

## 📦 Módulos Principales

### `auth` — Autenticación y Registro
Gestión de credenciales, selección de rol y bifurcación de lógica de negocio según perfil.

### `tickets` — Gestión de Incidencias
Módulo central. CRUD completo de tickets con filtrado por rol, actualización de estados y sistema de prioridades.

### `comments` — Comunicación Interna
Canal de mensajería interno por incidencia. Cada mensaje queda registrado con autor y timestamp para garantizar trazabilidad total.

### `properties` — Gestión de Inmuebles
Registro y vinculación de viviendas con sus incidencias asociadas.

### `navigation` — Enrutamiento
`NavHost` centralizado que transfiere de forma segura el rol y el `userId` entre pantallas mediante argumentos estructurados, sin uso de variables globales.

---

## ⚙️ Instalación y Configuración

### Prerrequisitos

```
Android Studio Ladybug (o superior)
Android SDK — compileSdk 35 / targetSdk 35
Kotlin 1.9+
Gradle 8.x (Kotlin DSL)
```

### Clonar el repositorio

```bash
git clone https://github.com/[org]/InmobiliaControl.git
cd InmobiliaControl
```

### Abrir en Android Studio

```
File → Open → Seleccionar carpeta raíz del proyecto
```

### Ejecutar la aplicación

```
Run → Run 'app'   (Shift + F10)
```

> La base de datos se inicializa automáticamente en el primer arranque.  
> El sistema de **seeding** (`seedPropertiesIfNeeded`) inyecta datos de muestra si la base está vacía.

### Credenciales de prueba por defecto

| Rol | Email | Contraseña |
|---|---|---|
| Inquilino | tenant@test.com | 1234 |
| Agencia | agency@test.com | 1234 |
| Mantenimiento | maintenance@test.com | 1234 |

---

## 🧪 Control de Calidad & Pruebas

| Tipo de prueba | Descripción | Herramienta |
|---|---|---|
| **Caja negra** | Validación de flujos de usuario por rol (login, creación, cambio de estado) | Manual |
| **Inspección de BD** | Monitorización en tiempo real de inserciones, actualizaciones y eliminaciones | Android Studio Database Inspector |
| **Regresión** | Verificación de estabilidad tras cada corrección técnica | Manual — ciclo completo de ticket |

### Problemas técnicos resueltos

- ✅ Migración de datos volátiles (`TicketMock`) a persistencia real con Room
- ✅ Pérdida de sesión al navegar entre pantallas → resuelto con `SavedStateHandle`
- ✅ Datos huérfanos en BD → resuelto con `ForeignKey.CASCADE`
- ✅ Pantallas vacías en primer uso → resuelto con función de seeding automático

---

## 🗺️ Roadmap — Futuras Mejoras

| Prioridad | Mejora | Descripción |
|---|---|---|
| 🔴 Alta | **Evidencia multimedia** | Captura y adjunto de fotografías en el formulario de creación de incidencias |
| 🟡 Media | **Cloud Sync** | Migración de Room a Firebase / AWS con notificaciones push |
| 🟡 Media | **Módulo de facturación** | Emisión de facturas en PDF asociadas a tickets resueltos |
| 🟢 Baja | **Dashboard Analytics** | Panel de métricas para la agencia: tiempos de respuesta, eficiencia por técnico, recurrencia por inmueble |

---

## 👨‍💻 Equipo de Desarrollo

<br/>

| | Desarrollador | Área de Responsabilidad |
|---|---|---|
| 🎨 | **Federico Velasco** | Interfaz de usuario — Jetpack Compose, diseño de pantallas y experiencia de usuario |
| 🗄️ | **Bryant Giorgini Vasquez** | Base de datos — Room, MVVM, repositorios y lógica de negocio |
| 📋 | **Juan José Martín-Serrano** | TableView — gestión y visualización de listas de incidencias |

<br/>

> **Tutor académico:** D. Francisco Javier Navazo Fernández  
> **Centro:** Universidad Europea de Madrid  
> **Ciclo:** Desarrollo de Aplicaciones Multiplataforma (DAM)  
> **Curso académico:** 2025 – 2026

---

## 📚 Referencias Técnicas

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [Guide to App Architecture — MVVM](https://developer.android.com/topic/architecture)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Navigation Compose](https://developer.android.com/guide/navigation/navigation-compose)

---

<div align="center">

<br/>

**InmobiliaControl** · Proyecto Intermodular DAM · Universidad Europea · 2025–2026

*Desarrollado por Federico, Bryant y Juan José**

</div>
