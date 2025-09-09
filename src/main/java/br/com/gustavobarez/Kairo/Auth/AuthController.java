package br.com.gustavobarez.Kairo.Auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavobarez.Kairo.util.ApiResponseDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
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
