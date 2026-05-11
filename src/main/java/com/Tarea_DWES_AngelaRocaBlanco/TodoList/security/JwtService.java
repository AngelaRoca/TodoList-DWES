package com.Tarea_DWES_AngelaRocaBlanco.TodoList.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//Servicio que gestiona toda la logica relacionada con los tokens JWT:
//generacion, validacion y extraccion de datos del token.
@Service
public class JwtService {

 // Clave secreta para firmar y verificar los tokens JWT.
 // Se lee desde application.properties (app.jwt.secret).
 // Viene en formato Base64 y debe tener minimo 256 bits.
 @Value("${app.jwt.secret}")
 private String secretKey;

 // Tiempo de expiracion del token en milisegundos.
 // Se lee desde application.properties (app.jwt.expiration).
 // Por defecto 86400000 ms = 24 horas.
 @Value("${app.jwt.expiration}")
 private long jwtExpiration;

 // Extrae el nombre de usuario (subject) del token JWT.
 // El subject es el campo donde guardamos el username al generar el token.
 public String extractUsername(String token) {
     return extractClaim(token, Claims::getSubject);
 }

 // Metodo generico para extraer cualquier campo (claim) del token JWT.
 // Recibe una funcion que indica que campo extraer de los claims.
 public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
     final Claims claims = extractAllClaims(token);
     return claimsResolver.apply(claims);
 }

 // Genera un token JWT para el usuario dado sin claims adicionales.
 // Llama al metodo sobrecargado con un mapa vacio de claims extra.
 public String generateToken(UserDetails userDetails) {
     return generateToken(new HashMap<>(), userDetails);
 }

 // Genera un token JWT con claims adicionales opcionales.
 // El token contiene:
 // - extraClaims: informacion adicional que queramos incluir
 // - subject: el username del usuario
 // - issuedAt: fecha y hora de creacion del token
 // - expiration: fecha y hora de expiracion (ahora + jwtExpiration)
 // - firma: con la clave secreta usando el algoritmo HS256
 public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
     return Jwts.builder()
             .setClaims(extraClaims)
             .setSubject(userDetails.getUsername())
             .setIssuedAt(new Date(System.currentTimeMillis()))
             .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
             .signWith(getSignInKey(), SignatureAlgorithm.HS256)
             .compact();
 }

 // Valida si un token JWT es valido para un usuario concreto.
 // Comprueba dos cosas:
 // 1. Que el username del token coincide con el del usuario
 // 2. Que el token no ha expirado
 public boolean isTokenValid(String token, UserDetails userDetails) {
     final String username = extractUsername(token);
     return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
 }

 // Comprueba si el token ha expirado comparando
 // su fecha de expiracion con la fecha actual.
 private boolean isTokenExpired(String token) {
     return extractExpiration(token).before(new Date());
 }

 // Extrae la fecha de expiracion del token JWT.
 private Date extractExpiration(String token) {
     return extractClaim(token, Claims::getExpiration);
 }

 // Extrae y devuelve todos los claims (datos) contenidos en el token JWT.
 // Verifica la firma del token con la clave secreta.
 // Si la firma no es valida o el token esta manipulado lanza una excepcion.
 private Claims extractAllClaims(String token) {
     return Jwts.parserBuilder()
             .setSigningKey(getSignInKey())
             .build()
             .parseClaimsJws(token)
             .getBody();
 }

 // Genera la clave criptografica a partir del secreto en Base64.
 // Decodifica el Base64 y crea una clave HMAC-SHA compatible con HS256.
 // Esta clave se usa para firmar y verificar los tokens JWT.
 private Key getSignInKey() {
     byte[] keyBytes = Base64.getDecoder().decode(secretKey);
     return Keys.hmacShaKeyFor(keyBytes);
 }
}