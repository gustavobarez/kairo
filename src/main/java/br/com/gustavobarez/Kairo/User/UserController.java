package br.com.gustavobarez.Kairo.User;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavobarez.Kairo.util.ApiResponseDTO;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseDTO<CreateUserResponseDTO>> createUser(@RequestBody CreateUserRequestDTO dto) {
        var request = service.createUser(dto);
        var response = new ApiResponseDTO<>(request, "create-user");
        return ResponseEntity.ok(response);
    }

}
