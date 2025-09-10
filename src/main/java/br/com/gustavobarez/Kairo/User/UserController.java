package br.com.gustavobarez.Kairo.User;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavobarez.Kairo.Appointment.AppointmentDTO;
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

    @GetMapping("/{userId}/appointments")
    public ApiResponseDTO<Map<String, List<AppointmentDTO>>> getAppointments(@PathVariable Long userId) {
        var appointments = service.listAllUserAppointments(userId);
        var response = new ApiResponseDTO<>(appointments, "list-all-user-appointments");
        return response;
    }
}
