package com.Tarea_DWES_AngelaRocaBlanco.TodoList.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.User;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.UserRole;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.repository.UserRepository;

//Indica a Spring que esta clase es un componente que se ejecuta automaticamente
@Component

//Genera el constructor con los campos final (inyeccion de dependencias)
@RequiredArgsConstructor

//Genera el logger 'log' para mostrar mensajes por consola
@Slf4j
public class DataInitializer implements ApplicationRunner {

 // Repositorio para acceder a la tabla de usuarios en la base de datos
 private final UserRepository userRepository;

 // Encriptador de contrasenyas con BCrypt
 private final PasswordEncoder passwordEncoder;

 // Este metodo se ejecuta automaticamente cada vez que arranca la aplicacion
 @Override
 public void run(ApplicationArguments args) {

     // Si ya hay usuarios en la base de datos no hace nada.
     // Esto evita duplicar los usuarios en cada reinicio.
     if (userRepository.count() > 0) {
         log.info("Base de datos ya inicializada, omitiendo carga inicial");
         return;
     }

     // Crea el usuario ADMIN con contrasena encriptada
     // El ADMIN tiene acceso total a la API
     User admin = User.builder()
             .username("admin")
             .password(passwordEncoder.encode("admin123"))
             .email("admin@listatareas.com")
             .fullname("Administrador")
             .role(UserRole.ADMIN)
             .build();

     // Crea el usuario GESTOR con contrasena encriptada
     // El GESTOR puede hacer CRUD de categorias
     User gestor = User.builder()
             .username("gestor")
             .password(passwordEncoder.encode("gestor123"))
             .email("gestor@listatareas.com")
             .fullname("Gestor")
             .role(UserRole.GESTOR)
             .build();

     // Crea el usuario USER con contrasena encriptada
     // El USER puede gestionar sus propias tareas
     User usuario = User.builder()
             .username("angela")
             .password(passwordEncoder.encode("angela123"))
             .email("angela@listatareas.com")
             .fullname("Angela Roca Blanco")
             .role(UserRole.USER)
             .build();

     // Guarda los tres usuarios en la base de datos en una sola operacion
     userRepository.saveAll(List.of(admin, gestor, usuario));

     // Muestra por consola las credenciales de los usuarios creados
     log.info("Usuarios iniciales cargados:");
     log.info("    ADMIN  -> admin / admin123");
     log.info("    GESTOR -> gestor / gestor123");
     log.info("    USER   -> angela / angela123");
 }
}
