# AprendeApp

Aplicación Android desarrollada en **Kotlin** que funciona como un **Sistema de Gestión de Recursos de Aprendizaje** (libros, videos, artículos y tutoriales de Ingeniería de Sistemas). Utiliza la arquitectura **MVC** y consume una **API REST dinámica** (MockAPI.io) con operaciones CRUD completas, autenticación con roles y persistencia de datos de usuario.

> Proyecto del **Tercer Desafío Práctico** de la asignatura **Desarrollo de Software para Móviles (DSM)**, Escuela de Ingeniería en Computación, Universidad Don Bosco, ciclo II-2026.

**Autor:** [TU NOMBRE COMPLETO] · **Carnet:** [TU CARNET]

---

## Tabla de contenido

1. [Funcionalidades](#funcionalidades)
2. [Tecnologías y librerías](#tecnologías-y-librerías)
3. [Arquitectura MVC](#arquitectura-mvc)
4. [API (MockAPI.io)](#api-mockapiio)
5. [Roles y permisos](#roles-y-permisos)
6. [Validación de contraseña](#validación-de-contraseña)
7. [Decisiones de diseño](#decisiones-de-diseño)
8. [Cómo ejecutar el proyecto](#cómo-ejecutar-el-proyecto)
9. [Convención de commits](#convención-de-commits)
10. [Limitaciones conocidas](#limitaciones-conocidas)
11. [Uso de inteligencia artificial](#uso-de-inteligencia-artificial)

---

## Funcionalidades

### Autenticación
- **Registro** de usuario con validación segura de contraseña y selección de rol (Estudiante o Docente).
- **Login** con verificación contra la API y **Logout** con confirmación.
- **Sesión persistente**: al reabrir la app, el usuario entra directo a la pantalla principal.

### Gestión de recursos
- **Visualización** en tarjetas con imagen, título, descripción corta, tipo, ID, enlace y calificación promedio.
- **Filtro por tipo** mediante chips (Todos, Libro, Video, Artículo, Tutorial).
- **Ordenamiento** por título (A-Z y Z-A), por tipo y por mejor calificados.
- **Búsqueda avanzada** por ID, título o tipo (o en todos los campos), sin distinguir mayúsculas ni tildes.
- **CRUD completo (Docente)**:
  - Agregar recurso (`POST`) con formulario validado.
  - Modificar recurso (`PUT`) con formulario prellenado.
  - Eliminar recurso (`DELETE`) con confirmación previa.

### Interacción de datos (Estudiante)
- **Favoritos**: marcar o desmarcar recursos y verlos en un filtro exclusivo. Persisten localmente, por usuario.
- **Calificación de 1 a 5 estrellas**: se actualiza en el backend (`PUT`) y el promedio se muestra a todos los usuarios. Un estudiante puede corregir su voto sin duplicarlo.

### UI / UX
- Material Design (tarjetas, chips, campos con validación, diálogos, botón flotante).
- Feedback visual: loaders, toasts, diálogos de confirmación, Snackbar con opción de reintento, deslizar para actualizar y animación de entrada de las tarjetas.
- Todos los textos están **centralizados en `strings.xml`**.

---

## Tecnologías y librerías

| Tecnología | Uso |
|---|---|
| Kotlin + Android Studio | Lenguaje y entorno de desarrollo |
| Retrofit 2.9.0 + Gson | Consumo de la API REST y conversión JSON |
| OkHttp Logging Interceptor 4.12.0 | Registro de peticiones HTTP para depuración |
| Kotlin Coroutines 1.7.3 | Llamadas asíncronas sin bloquear la interfaz |
| Glide 4.16.0 | Carga de imágenes desde URL |
| Material Components 1.11.0 | Componentes de interfaz |
| RecyclerView 1.3.2 | Lista de tarjetas |
| SwipeRefreshLayout 1.1.0 | Deslizar para actualizar |
| ViewBinding | Acceso seguro a las vistas |
| SharedPreferences | Sesión, favoritos y votos locales |

**Versión mínima de Android:** API 24.

---

## Arquitectura MVC

```
com.example.aprendeapp
├── model/                       # MODELO: datos y acceso a la API
│   ├── Recurso.kt               # Clase de datos del recurso (incluye promedio calculado)
│   ├── Usuario.kt               # Clase de datos del usuario
│   ├── ApiService.kt            # Endpoints de Retrofit (GET, POST, PUT, DELETE)
│   └── RetrofitClient.kt        # Cliente HTTP configurado (singleton)
├── controller/                  # CONTROLADOR: lógica de la aplicación
│   ├── AuthController.kt        # Registro y login
│   └── RecursoController.kt     # Carga, búsqueda, filtro, orden, CRUD y calificación
├── view/                        # VISTA: pantallas y adaptadores
│   ├── LoginActivity.kt
│   ├── RegistroActivity.kt
│   ├── FormularioRecursoActivity.kt
│   └── RecursoAdapter.kt
├── utils/                       # Utilidades compartidas
│   ├── Constantes.kt            # Roles
│   ├── HashUtil.kt              # SHA-256 para contraseñas
│   ├── ValidadorPassword.kt     # Reglas de contraseña
│   ├── SesionManager.kt         # Sesión del usuario
│   ├── InteraccionManager.kt    # Favoritos y votos locales
│   └── TiposRecurso.kt          # Tipos de recurso y sus etiquetas
└── MainActivity.kt              # Pantalla principal (vista de la lista de recursos)
```

**Responsabilidades:**
- **Modelo:** define los datos y se comunica con la API. No conoce la interfaz.
- **Vista:** muestra datos y captura eventos del usuario. No contiene reglas de negocio.
- **Controlador:** recibe lo que pide la vista, aplica la lógica (validaciones, filtros, reglas de votación) y habla con el modelo.

---

## API (MockAPI.io)

**URL base:** `https://6ae17d23be3bc8fead988df6.mockapi.io/api/v1/`

### Recurso `recursos`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | String | Identificador generado por MockAPI |
| `titulo` | String | Título del recurso |
| `descripcion` | String | Descripción detallada |
| `tipo` | String | `libro`, `video`, `articulo` o `tutorial` |
| `enlace` | String | URL del recurso completo |
| `imagen` | String | URL de la imagen ilustrativa |
| `sumaCalificaciones` | Number | Suma de todas las estrellas recibidas |
| `numeroVotos` | Number | Cantidad de votos recibidos |

El promedio se calcula en la app: `sumaCalificaciones / numeroVotos`.

### Recurso `usuarios`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | String | Identificador generado por MockAPI |
| `nombre` | String | Nombre del usuario |
| `correo` | String | Correo (se usa para el login) |
| `password` | String | Hash SHA-256 de la contraseña |
| `rol` | String | `estudiante` o `docente` |

### Endpoints utilizados

| Acción | Método | Ruta |
|---|---|---|
| Listar recursos | GET | `/recursos` |
| Obtener un recurso | GET | `/recursos/{id}` |
| Crear recurso (docente) | POST | `/recursos` |
| Editar recurso o calificar | PUT | `/recursos/{id}` |
| Eliminar recurso (docente) | DELETE | `/recursos/{id}` |
| Registrar usuario | POST | `/usuarios` |
| Login (buscar por correo) | GET | `/usuarios?correo=...` |

---

## Roles y permisos

| Acción | Estudiante | Docente |
|---|:---:|:---:|
| Ver, buscar, filtrar y ordenar recursos | Sí | Sí |
| Marcar favoritos | Sí | No |
| Calificar recursos | Sí | No |
| Crear, editar y eliminar recursos | No | Sí |

La pantalla de formulario del docente también verifica el rol al abrirse, no solo oculta los botones.

---

## Validación de contraseña

Al registrarse, la contraseña debe cumplir **todas** estas reglas (se muestran las que faltan):

- Mínimo **12 caracteres**.
- Al menos una **letra mayúscula**.
- Al menos una **letra minúscula**.
- Al menos un **número**.
- Al menos un **carácter especial** (`! @ # $ % ^ & *`).

Ejemplo válido: `Docente#2026ok`

---

## Decisiones de diseño

- **Contraseñas con hash SHA-256:** nunca se guarda ni se envía la contraseña en texto plano a la API. Para un entorno de producción se usaría un algoritmo con sal (bcrypt o Argon2) en un servidor propio con autenticación real.
- **Mensaje de error genérico en el login:** el mismo mensaje se muestra si el correo no existe o la contraseña es incorrecta, para no revelar qué cuentas están registradas.
- **Calificación con lectura previa:** antes de actualizar el promedio, la app vuelve a pedir el recurso a la API (`GET`) y luego guarda el cambio (`PUT`), para no pisar los votos de otros usuarios.
- **Favoritos locales por usuario:** se guardan en `SharedPreferences`, con un archivo distinto para cada usuario.
- **Búsqueda sin tildes:** `articulo` y `artículo` devuelven el mismo resultado.
- **MockAPI y los filtros:** MockAPI responde `404` cuando un filtro no encuentra coincidencias y su filtro es por coincidencia parcial; el controlador lo maneja comparando el correo exacto.

---

## Cómo ejecutar el proyecto

### Requisitos
- Android Studio (versión reciente) con Android SDK.
- Un emulador o un dispositivo con Android 7.0 (API 24) o superior.
- Conexión a internet (la app consume la API en línea).

### Pasos
1. Clona el repositorio:
   ```bash
   git clone https://github.com/[TU_USUARIO]/AprendeApp.git
   ```
2. Abre la carpeta en Android Studio y espera a que termine la sincronización de Gradle.
3. Ejecuta la app con **Run ▶** en un emulador o dispositivo.

### Usar tu propia API (opcional)
1. Crea un proyecto en [MockAPI.io](https://mockapi.io) con los recursos `recursos` y `usuarios` y los campos de la sección [API](#api-mockapiio).
2. Cambia la constante `BASE_URL` en `model/RetrofitClient.kt` por la URL de tu proyecto (debe terminar en `/`).

### Cuentas de prueba
Puedes crear tus propias cuentas desde la pantalla de registro, eligiendo el rol Estudiante o Docente.

---

## Convención de commits

Todos los commits comienzan con un verbo en español, como pide el desafío:

`agregar` · `corregir` · `actualizar` · `eliminar` · `mejorar` · `implementar` · `optimizar` · `refactorizar`

Ejemplos: `implementar controlador de autenticación`, `agregar tarjetas de recursos con imagen y calificación`.

---

## Limitaciones conocidas

- MockAPI no ofrece autenticación real: el login se resuelve en la app comparando hashes, por lo que no sustituye a un backend seguro.
- El rol se elige al registrarse porque así lo pide el desafío; en una aplicación real, el rol docente lo asignaría un administrador.
- El voto previo de cada usuario se guarda solo en el dispositivo; si inicia sesión en otro equipo, un nuevo voto se contaría como adicional.
- Los favoritos son locales y no se sincronizan entre dispositivos.

---

## Uso de inteligencia artificial

Se utilizó una herramienta de IA como **apoyo para aprender y avanzar** en el desarrollo (explicaciones paso a paso, revisión de errores y propuestas de estructura). El proyecto fue construido, probado y comprendido por el autor, quien explica su funcionamiento en el video de defensa.
