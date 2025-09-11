package br.com.gustavobarez.Kairo.User;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavobarez.Kairo.Appointment.AppointmentDTO;
import br.com.gustavobarez.Kairo.util.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseDTO<CreateUserResponseDTO>> createUser(
            @Valid @RequestBody CreateUserRequestDTO dto) {
        var request = service.createUser(dto);
        var response = new ApiResponseDTO<>(request, "create-user");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/appointments")
    public ResponseEntity<ApiResponseDTO<Map<String, List<AppointmentDTO>>>> getAppointments(
            @PathVariable Long userId) {
        var appointments = service.listAllUserAppointments(userId);
        var response = new ApiResponseDTO<>(appointments, "list-all-user-appointments");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(HttpServletRequest request) {
        Object userIdAttribute = request.getAttribute("user_id");
        if (userIdAttribute == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userIdStr = userIdAttribute.toString();
        Long userId = Long.parseLong(userIdStr);

        service.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<ApiResponseDTO<UpdateUserResponseDTO>> updateUser(
            @Valid @RequestBody UpdateUserRequestDTO dto,
            HttpServletRequest request) {

        Object userIdAttribute = request.getAttribute("user_id");
        if (userIdAttribute == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userIdStr = userIdAttribute.toString();
        Long userId = Long.parseLong(userIdStr);

        var user = service.update(dto, userId);
        var response = new ApiResponseDTO<>(user, "update-user");
        return ResponseEntity.ok(response);
    }
}