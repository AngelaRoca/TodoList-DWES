package com.Tarea_DWES_AngelaRocaBlanco.TodoList.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.AuthDTOs.AuthResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.AuthDTOs.LoginRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.AuthDTOs.RegisterRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro e inicio de sesión. Endpoints públicos, no requieren token.")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario con rol USER y devuelve un token JWT"
    )
    @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class),
                    examples = @ExampleObject("""
                            {
                                "token": "eyJhbGciOiJIUzI1NiJ9...",
                                "username": "angela",
                                "role": "USER"
                            }
                            """)))
    @ApiResponse(responseCode = "400", description = "El usuario o email ya existe",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject("""
                            {
                                "status": 400,
                                "error": "Bad Request",
                                "message": "El nombre de usuario ya existe"
                            }
                            """)))
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject("""
                                    {
                                        "username": "angela",
                                        "password": "1234",
                                        "email": "angela@correo.com",
                                        "fullname": "Ángela Roca Blanco"
                                    }
                                    """)))
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica las credenciales y devuelve un token JWT para usar en el resto de endpoints"
    )
    @ApiResponse(responseCode = "200", description = "Login correcto",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class),
                    examples = @ExampleObject("""
                            {
                                "token": "eyJhbGciOiJIUzI1NiJ9...",
                                "username": "angela",
                                "role": "USER"
                            }
                            """)))
    @ApiResponse(responseCode = "401", description = "Credenciales incorrectas",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject("""
                            {
                                "status": 401,
                                "error": "Unauthorized",
                                "message": "Credenciales incorrectas"
                            }
                            """)))
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales del usuario", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject("""
                                    {
                                        "username": "angela",
                                        "password": "1234"
                                    }
                                    """)))
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
