package com.Tarea_DWES_AngelaRocaBlanco.TodoList.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Indica a Spring que esta clase contiene configuracion de la aplicacion
@Configuration

//Define el esquema de seguridad que aparece en Swagger UI.
//Esto es lo que hace que aparezca el boton "Authorize" en Swagger
//donde el usuario puede introducir su token JWT para probar los endpoints protegidos.
//name = "bearerAuth" -> nombre que usamos en los controladores con @SecurityRequirement(name = "bearerAuth")
//type = HTTP -> tipo de autenticacion HTTP
//scheme = "bearer" -> indica que es un token Bearer
//bearerFormat = "JWT" -> indica que el token es JWT (solo informativo para Swagger)
@SecurityScheme(
 name = "bearerAuth",
 type = SecuritySchemeType.HTTP,
 scheme = "bearer",
 bearerFormat = "JWT",
 description = "Introduce el token JWT obtenido en /api/auth/login"
)
public class OpenApiConfig {

 // Crea y configura el objeto OpenAPI que define la informacion
 // general de la API que se muestra en la cabecera de Swagger UI
 @Bean
 public OpenAPI customOpenAPI() {
     return new OpenAPI()
             .info(new Info()
                     // Titulo que aparece en la cabecera de Swagger UI
                     .title("Lista de Tareas API REST")

                     // Version de la API
                     .version("1.0")

                     // Descripcion general que aparece debajo del titulo en Swagger.
                     // Explica como usar la API: roles disponibles y como autenticarse.
                     .description("""
                             API REST para gestion de tareas personales.

                             Roles disponibles: ADMIN (acceso total), GESTOR (CRUD categorias), USER (gestion de sus tareas).

                             Autenticacion: JWT. Registrate en /api/auth/register, obtén el token en /api/auth/login y usalo en el boton Authorize.
                             """)

                     // Informacion de contacto del autor que aparece en Swagger UI
                     .contact(new Contact().name("Angela Roca Blanco")));
 }
}