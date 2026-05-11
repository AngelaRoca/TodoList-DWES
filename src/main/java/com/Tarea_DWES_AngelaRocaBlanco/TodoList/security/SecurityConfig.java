package com.Tarea_DWES_AngelaRocaBlanco.TodoList.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth

                // Publicos
                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/api-docs",
                    "/v3/api-docs/**",
                    "/v3/api-docs",
                    "/webjars/**"
                ).permitAll()

                // Solo ADMIN
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                // Solo GESTOR (y ADMIN por si acaso)
                .requestMatchers("/api/manager/**").hasAnyAuthority("ADMIN", "GESTOR")

                // Categorias: GET libre para autenticados, escritura solo ADMIN/GESTOR
                .requestMatchers(HttpMethod.GET, "/api/categories/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/categories/**").hasAnyAuthority("ADMIN", "GESTOR")
                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAnyAuthority("ADMIN", "GESTOR")
                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAnyAuthority("ADMIN", "GESTOR")

                // El resto requiere autenticacion
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("""
                            {"status": 401, "error": "No autenticado", "message": "Debes iniciar sesion para acceder a este recurso"}
                            """);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("""
                            {"status": 403, "error": "Acceso denegado", "message": "No tienes permiso para realizar esta accion"}
                            """);
                })
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Define el proveedor de autenticacion que usara Spring Security.
    // DaoAuthenticationProvider es el proveedor estandar que autentica
    // usuarios consultando la base de datos a traves del UserDetailsService.
    // Recibe el UserDetailsService para cargar el usuario por username.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        // Indica que las contraseñas se comparan usando BCrypt.
        // Cuando el usuario hace login, Spring encripta la contrasena introducida
        // y la compara con el hash guardado en la base de datos.
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Define el encriptador de contraseñas usando el algoritmo BCrypt.
    // BCrypt es un algoritmo de hashing seguro que añade un "salt" aleatorio
    // a cada contraseña, por lo que dos contraseñas iguales generan
    // hashes diferentes. Es el estandar recomendado para contraseñas.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    // Expone el AuthenticationManager como un bean de Spring.
    // El AuthenticationManager es el componente central de Spring Security
    // que gestiona el proceso de autenticacion.
    // Lo necesitamos inyectado en el AuthService para autenticar
    // al usuario cuando hace login con usuario y contrasena.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
