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

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        // Solo inicializa si no hay usuarios (primera vez que arranca)
        if (userRepository.count() > 0) {
            log.info("Base de datos ya inicializada, omitiendo carga inicial");
            return;
        }

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@listatareas.com")
                .fullname("Administrador")
                .role(UserRole.ADMIN)
                .build();

        User gestor = User.builder()
                .username("gestor")
                .password(passwordEncoder.encode("gestor123"))
                .email("gestor@listatareas.com")
                .fullname("Gestor Principal")
                .role(UserRole.GESTOR)
                .build();

        User usuario = User.builder()
                .username("angela")
                .password(passwordEncoder.encode("angela123"))
                .email("angela@listatareas.com")
                .fullname("Ángela Roca Blanco")
                .role(UserRole.USER)
                .build();

        userRepository.saveAll(List.of(admin, gestor, usuario));

        log.info(">>> Usuarios iniciales cargados:");
        log.info("    ADMIN  -> admin / admin123");
        log.info("    GESTOR -> gestor / gestor123");
        log.info("    USER   -> angela / angela123");
    }
}
