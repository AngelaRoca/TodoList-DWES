package com.Tarea_DWES_AngelaRocaBlanco.TodoList.security;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.User;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


//Implementacion del contrato UserDetailsService de Spring Security.
//Spring Security necesita esta clase para cargar los datos del usuario
//desde la base de datos durante el proceso de autenticacion.
//Sin esta clase Spring no sabria como buscar un usuario por su username.
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

 // Repositorio para consultar los usuarios en la base de datos
 private final UserRepository userRepository;

 // Metodo que Spring Security llama automaticamente cuando necesita
 // autenticar a un usuario, por ejemplo durante el login o al
 // validar el token JWT en cada peticion.
 // Recibe el username y devuelve un objeto UserDetails con los
 // datos necesarios para la autenticacion y autorizacion.
 @Override
 public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

     // Busca el usuario en la base de datos por su username.
     // Si no existe lanza UsernameNotFoundException que Spring Security
     // captura y devuelve un 401 al cliente.
     User user = userRepository.findByUsername(username)
             .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

     // Convierte nuestro User (entidad JPA) al User de Spring Security.
     // Spring Security necesita su propio objeto User que contiene:
     // - username: nombre de usuario
     // - password: contrasena hasheada para compararla en el login
     // - authorities: lista de roles/permisos del usuario
     // SimpleGrantedAuthority convierte el rol (ADMIN, GESTOR, USER)
     // al formato que entiende Spring Security para la autorizacion.
     return new org.springframework.security.core.userdetails.User(
             user.getUsername(),
             user.getPassword(),
             List.of(new SimpleGrantedAuthority(user.getRole().name()))
     );
 }
}
