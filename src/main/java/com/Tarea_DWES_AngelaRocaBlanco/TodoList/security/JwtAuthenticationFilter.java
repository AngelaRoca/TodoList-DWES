package com.Tarea_DWES_AngelaRocaBlanco.TodoList.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
//OncePerRequestFilter garantiza que este filtro se ejecuta
//UNA SOLA VEZ por cada peticion HTTP que llega a la API
public class JwtAuthenticationFilter extends OncePerRequestFilter {

 // Servicio que gestiona la generacion y validacion de tokens JWT
 private final JwtService jwtService;

 // Servicio que carga los datos del usuario desde la base de datos
 private final UserDetailsService userDetailsService;

 // Este metodo se ejecuta en cada peticion HTTP antes de que llegue al controlador.
 // Su funcion es interceptar el token JWT, validarlo e identificar al usuario.
 @Override
 protected void doFilterInternal(
         @NonNull HttpServletRequest request,   // Peticion HTTP entrante
         @NonNull HttpServletResponse response, // Respuesta HTTP saliente
         @NonNull FilterChain filterChain)      // Cadena de filtros de Spring Security
         throws ServletException, IOException {

     // Obtiene el valor de la cabecera "Authorization" de la peticion.
     // Si el cliente esta autenticado deberia venir con formato: "Bearer <token>"
     final String authHeader = request.getHeader("Authorization");

     // Si no hay cabecera Authorization o no empieza por "Bearer "
     // significa que la peticion no lleva token.
     // Se deja pasar al siguiente filtro sin autenticar al usuario.
     // Spring Security decidira luego si el endpoint es publico o no.
     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
         filterChain.doFilter(request, response);
         return;
     }

     // Extrae el token JWT eliminando el prefijo "Bearer " (7 caracteres)
     final String jwt = authHeader.substring(7);

     // Extrae el nombre de usuario que viene dentro del token JWT
     final String username = jwtService.extractUsername(jwt);

     // Si se pudo extraer el username y el usuario aun no esta autenticado
     // en el contexto de seguridad de Spring (evita autenticar dos veces)
     if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

         // Carga los datos del usuario desde la base de datos
         UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

         // Verifica que el token es valido: firma correcta y no expirado
         if (jwtService.isTokenValid(jwt, userDetails)) {

             // Crea el objeto de autenticacion con el usuario y sus roles
             // El segundo parametro (null) es la credencial, no necesaria con JWT
             UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                     userDetails, null, userDetails.getAuthorities());

             // Añade detalles adicionales de la peticion al token de autenticacion
             // (IP del cliente, session ID, etc.)
             authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

             // Registra al usuario como autenticado en el contexto de seguridad de Spring.
             // A partir de aqui Spring sabe quien es el usuario en esta peticion.
             SecurityContextHolder.getContext().setAuthentication(authToken);
         }
     }

     // Pasa la peticion al siguiente filtro de la cadena
     filterChain.doFilter(request, response);
 }
}