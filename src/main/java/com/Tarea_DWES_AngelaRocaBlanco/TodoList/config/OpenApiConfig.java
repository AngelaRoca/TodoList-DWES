package com.Tarea_DWES_AngelaRocaBlanco.TodoList.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Introduce el token JWT obtenido en /api/auth/login"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lista de Tareas API REST")
                        .version("1.0")
                        .description("""
                                API REST para gestión de tareas personales.
                                
                                Roles disponibles: ADMIN (acceso total), GESTOR (CRUD categorías), USER (gestión de sus tareas).
                                
                                Autenticación: JWT. Regístrate en /api/auth/register, obtén el token en /api/auth/login y úsalo en el botón Authorize.
                                """)
                        .contact(new Contact().name("Ángela Roca Blanco - DWES")));
    }
}
