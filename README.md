# TodoList API REST - Proyecto Final DWES

**Autor:** Angela Roca Blanco
**Modulo:** Desarrollo Web en Entorno Servidor (DWES)
**Centro:** CIFP La Laboral



## Descripcion

API REST para la gestion de tareas personales desarrollada con Spring Boot 4.
Permite gestionar tareas, categorizarlas, etiquetarlas y controlar el acceso
mediante roles de usuario (ADMIN, GESTOR, USER).



## Tecnologias utilizadas

|Tecnologia|Version|
|-|-|
|Java|17|
|Spring Boot|4.0.6|
|Spring Security|JWT|
|Spring Data JPA|Hibernate|
|MySQL|8+|
|Lombok|1.18.46|
|SpringDoc OpenAPI|2.8.8 (Swagger)|
|JJWT|0.11.5|



## Modelo de datos

### Entidades

**User**

* id, username, password, email, fullname, role (ADMIN/GESTOR/USER)

**Task** (atributos base + 2 extras justificados)

* id, title, description, completed, createdAt
* **deadline** (LocalDate): fecha limite de la tarea. Permite buscar tareas vencidas y alimenta el dashboard con tareas proximas a vencer.
* **priority** (enum LOW/MEDIUM/HIGH): nivel de urgencia de la tarea. Permite filtrar y priorizar tareas por importancia.

**Category**

* id, title

**Tag**

* id, name

### Relaciones

* User 1 a N Task
* Category 1 a N Task
* Task N a N Tag



## Arquitectura

El proyecto sigue una arquitectura MVC en capas:

```
Cliente -> Controller -> Service -> Repository -> Base de datos
                     <->
                    DTO
```

### Justificacion de DTOs

|DTO|Justificacion|
|-|-|
|TaskRequest|Recibe los datos de entrada para crear/editar una tarea. Evita exponer la entidad JPA directamente.|
|TaskResponse|Devuelve solo los campos relevantes de la tarea, sin datos internos de JPA ni contrasenas del autor.|
|AuthResponse|Devuelve unicamente el token JWT y el rol, sin datos sensibles del usuario.|
|UserResponse|Expone datos del usuario sin incluir la contrasena hasheada.|
|DashboardResponse|Agrupa estadisticas calculadas sin exponer entidades JPA directamente.|
|CategoryRequest/Response|Separa la entrada y salida de categorias para mayor control.|
|TagRequest/Response|Separa la entrada y salida de tags.|



## Seguridad

La API utiliza **JWT (JSON Web Token)** con Spring Security en modo **Stateless**.

### Roles

|Rol|Permisos|
|-|-|
|ADMIN|Acceso total. CRUD usuarios, CRUD categorias en /api/admin/categories, promocionar/degradar gestores|
|GESTOR|CRUD categorias en /api/manager/categories|
|USER|CRUD tareas propias, CRUD tags, listar categorias, dashboard, modificar perfil|

### Endpoints publicos (sin autenticacion)

* POST /api/auth/register
* POST /api/auth/login
* Swagger UI (/swagger-ui.html, /v3/api-docs/\*\*)

### Flujo de autenticacion

1. Registrarse en /api/auth/register o usar usuario inicial
2. Hacer login en /api/auth/login y obtener token JWT
3. Incluir el token en las peticiones: Authorization: Bearer <token>

### Usuarios iniciales (cargados al arrancar por primera vez)

|Usuario|Contrasena|Rol|
|-|-|-|
|admin|admin123|ADMIN|
|gestor|gestor123|GESTOR|
|angela|angela123|USER|


## Endpoints

### Autenticacion /api/auth (publico)

|Metodo|Ruta|Descripcion|
|-|-|-|
|POST|/api/auth/register|Registrar nuevo usuario (rol USER)|
|POST|/api/auth/login|Iniciar sesion y obtener JWT|

### Admin /api/admin (solo ADMIN)

|Metodo|Ruta|Descripcion|
|-|-|-|
|GET|/api/admin/users|Listar todos los usuarios|
|GET|/api/admin/users/{id}|Obtener usuario por id|
|PUT|/api/admin/users/{id}|Actualizar usuario|
|DELETE|/api/admin/users/{id}|Eliminar usuario|
|PATCH|/api/admin/users/{id}/promote|Promocionar USER a GESTOR|
|PATCH|/api/admin/users/{id}/demote|Degradar GESTOR a USER|
|GET|/api/admin/categories|Listar categorias|
|POST|/api/admin/categories|Crear categoria|
|PUT|/api/admin/categories/{id}|Actualizar categoria|
|DELETE|/api/admin/categories/{id}|Eliminar categoria|

### Gestor /api/manager/categories (ADMIN y GESTOR)

|Metodo|Ruta|Descripcion|
|-|-|-|
|GET|/api/manager/categories|Listar categorias|
|GET|/api/manager/categories/{id}|Obtener categoria por id|
|POST|/api/manager/categories|Crear categoria|
|PUT|/api/manager/categories/{id}|Actualizar categoria|
|DELETE|/api/manager/categories/{id}|Eliminar categoria|

### Categorias /api/categories (cualquier usuario autenticado, solo lectura)

|Metodo|Ruta|Descripcion|
|-|-|-|
|GET|/api/categories|Listar categorias disponibles|
|GET|/api/categories/{id}|Obtener categoria por id|

### Tags /api/tags (usuario autenticado)

|Metodo|Ruta|Descripcion|
|-|-|-|
|GET|/api/tags|Listar todos los tags|
|GET|/api/tags/{id}|Obtener tag por id|
|POST|/api/tags|Crear tag|
|PUT|/api/tags/{id}|Actualizar tag|
|DELETE|/api/tags/{id}|Eliminar tag|

### Tareas /api/tasks (usuario autenticado, solo sus tareas)

|Metodo|Ruta|Descripcion|
|-|-|-|
|GET|/api/tasks|Listar mis tareas|
|GET|/api/tasks/{id}|Obtener tarea por id|
|POST|/api/tasks|Crear tarea|
|PUT|/api/tasks/{id}|Actualizar tarea|
|DELETE|/api/tasks/{id}|Eliminar tarea|
|GET|/api/tasks/filter/completed|Filtrar por completadas/pendientes|
|GET|/api/tasks/search/title|Buscar por titulo|
|GET|/api/tasks/search/description|Buscar por descripcion|
|GET|/api/tasks/filter/category/{id}|Filtrar por categoria|
|GET|/api/tasks/filter/tags|Filtrar por tags|
|GET|/api/tasks/filter/priority|Filtrar por prioridad (atributo extra)|
|GET|/api/tasks/filter/deadline|Filtrar por deadline (atributo extra)|
|POST|/api/tasks/{taskId}/tags/{tagId}|Asignar tag a tarea|
|DELETE|/api/tasks/{taskId}/tags/{tagId}|Eliminar tag de tarea|
|GET|/api/tasks/dashboard|Dashboard de estadisticas|

### Perfil /api/users (usuario autenticado)

|Metodo|Ruta|Descripcion|
|-|-|-|
|GET|/api/users/me|Ver mi perfil|
|PUT|/api/users/me|Actualizar mi perfil|


## Codigos de respuesta HTTP

|Codigo|Significado|
|-|-|
|200|OK - Operacion correcta|
|201|Created - Recurso creado|
|204|No Content - Eliminado correctamente|
|400|Bad Request - Datos invalidos o faltantes|
|401|Unauthorized - No autenticado o token invalido|
|403|Forbidden - Sin permisos para esta operacion|
|404|Not Found - Recurso no encontrado|
|409|Conflict - Conflicto (ej: usuario ya existe)|
|500|Internal Server Error - Error interno del servidor|


## Documentacion Swagger

Una vez arrancada la aplicacion, accede a:

```
http://localhost:8080/swagger-ui.html
```

Para endpoints protegidos pulsa el boton **Authorize** e introduce:

```
Bearer <token obtenido en login>
```


## Configuracion y arranque

### Requisitos previos

* Java 17
* MySQL 8+
* Eclipse con Lombok instalado

### Base de datos

Ejecuta este script en MySQL antes de arrancar:

```sql
CREATE DATABASE IF NOT EXISTS todoList
  CHARACTER SET utf8mb4
  COLLATE utf8mb4\_unicode\_ci;

CREATE USER IF NOT EXISTS 'todo\_user'@'localhost'
IDENTIFIED BY 'todo\_pass';

GRANT ALL PRIVILEGES ON todoList.\* TO 'todo\_user'@'localhost';

FLUSH PRIVILEGES;
```

### application.properties

```properties
spring.application.name=TodoList

spring.datasource.url=jdbc:mysql://localhost:3306/todoList
spring.datasource.username=todo\_user
spring.datasource.password=todo\_pass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format\_sql=true

app.jwt.secret=dGhpcyBpcyBhIHZlcnkgbG9uZyBzZWNyZXQga2V5IGZvciBqd3QgdG9rZW5zIGluIHNwcmluZyBib290IGFwcGxpY2F0aW9u
app.jwt.expiration=86400000

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs

server.port=8080
```

### Pasos para arrancar

1. Importa o abrir carpeta del proyecto en Eclipse como Maven Project
2. Ejecuta el script SQL en MySQL
3. Configura el application.properties con tus credenciales
4. Clic derecho en el proyecto -> Maven -> Update Project
5. Clic derecho -> Run As -> Java Application
6. Accede a http://localhost:8080/swagger-ui.html

## Estructura del proyecto

```
src/main/java/com/Tarea\_DWES\_AngelaRocaBlanco/TodoList/
|-- config/
|   |-- DataInitializer.java         Carga usuarios iniciales al arrancar
|   |-- OpenApiConfig.java           Configuracion Swagger/OpenAPI
|-- controller/
|   |-- AuthController.java          Login y registro (publico)
|   |-- AdminController.java         Operaciones de administrador + categorias admin
|   |-- ManagerCategoryController.java  CRUD categorias para gestor
|   |-- CategoryController.java      Listado de categorias (cualquier usuario)
|   |-- TagController.java           CRUD tags
|   |-- TaskController.java          CRUD y busquedas de tareas
|   |-- UserController.java          Perfil de usuario
|-- dto/
|   |-- auth/AuthDTOs.java
|   |-- category/CategoryDTOs.java
|   |-- tag/TagDTOs.java
|   |-- task/TaskDTOs.java
|   |-- user/UserDTOs.java
|-- exception/
|   |-- GlobalExceptionHandler.java  Manejo global de errores HTTP
|-- model/
|   |-- Category.java
|   |-- Priority.java
|   |-- Tag.java
|   |-- Task.java
|   |-- User.java
|   |-- UserRole.java
|-- repository/
|   |-- CategoryRepository.java
|   |-- TagRepository.java
|   |-- TaskRepository.java
|   |-- UserRepository.java
|-- security/
|   |-- JwtAuthenticationFilter.java
|   |-- JwtService.java
|   |-- SecurityConfig.java
|   |-- UserDetailsServiceImpl.java
|-- service/
|   |-- AuthService.java
|   |-- CategoryService.java
|   |-- TagService.java
|   |-- TaskService.java
|   |-- UserService.java
```

