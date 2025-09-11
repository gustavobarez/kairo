package br.com.gustavobarez.Kairo.Auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.gustavobarez.Kairo.util.ApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Auth management endpoints")
public class AuthController {

    AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // @BasePath /api/v1
    // @Summary User authentication
    // @Description Authenticate user and return JWT token
    // @Tags Auth
    // @Accept json
    // @Produce json
    // @Param request body AuthUserRequestDTO true "Request body"
    // @Success 200 {object} ApiResponseDTO[AuthUserResponseDTO]
    // @Failure 400 {object} ErrorResponse
    // @Failure 401 {object} ErrorResponse
    // @Failure 500 {object} ErrorResponse
    // @Router /auth/login [post]
    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Função responsável por autenticar um usuário e retornar token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso", content = {
                    @Content(schema = @Schema(implementation = ApiResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ApiResponseDTO<?>> auth(
            @Valid @RequestBody AuthUserRequestDTO authUserRequestDTO) {
        try {
            var token = authService.execute(authUserRequestDTO);
            var response = new ApiResponseDTO<>(token, "auth-user");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            var response = new ApiResponseDTO<>(null, "error");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}