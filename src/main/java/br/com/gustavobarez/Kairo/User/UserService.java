package br.com.gustavobarez.Kairo.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.gustavobarez.Kairo.Appointment.AppointmentDTO;
import br.com.gustavobarez.Kairo.exceptions.ResourceAlreadyExistsException;
import br.com.gustavobarez.Kairo.exceptions.ResourceNotFoundException;

@Service
public class UserService {

    UserRepository repository;
    PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public CreateUserResponseDTO createUser(CreateUserRequestDTO dto) {
        if (repository.findUserByEmail(dto.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email already in use", "email");
        }

        User user = User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(user);

        CreateUserResponseDTO response = new CreateUserResponseDTO(dto.username(), dto.email());
        return response;
    }

    public Map<String, List<AppointmentDTO>> listAllUserAppointments(Long userId) {
        var user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        var createdAppointments = user.getCreatedAppointments();
        var participatingAppointments = user.getParticipatingAppointments();

        List<AppointmentDTO> createdAppointmentsDTO = createdAppointments.stream()
                .filter(appointment -> appointment.getDeletedAt() == null)
                .map(appointment -> new AppointmentDTO(appointment))
                .collect(Collectors.toList());

        List<AppointmentDTO> participatingAppointmentsDTO = participatingAppointments.stream()
                .filter(appointment -> appointment.getDeletedAt() == null)
                .map(appointment -> new AppointmentDTO(appointment))
                .collect(Collectors.toList());

        Map<String, List<AppointmentDTO>> result = Map.of(
                "createdAppointments", createdAppointmentsDTO,
                "participatingAppointments", participatingAppointmentsDTO);

        return result;
    }

    public void delete(Long userId) {
        var user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setDeletedAt(LocalDateTime.now());
        repository.save(user);
    }

    public UpdateUserResponseDTO update(UpdateUserRequestDTO dto, Long userId) {
        if (dto.username() == null && dto.email() == null) {
            throw new IllegalArgumentException("At least username or email must be provided");
        }

        var user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            if (repository.findUserByEmail(dto.email()).isPresent()) {
                throw new ResourceAlreadyExistsException("Email already in use", "email");
            }
        }

        if (dto.username() != null) {
            user.setUsername(dto.username());
        }

        if (dto.email() != null) {
            user.setEmail(dto.email());
        }

        user.setUpdatedAt(LocalDateTime.now());
        repository.save(user);

        UpdateUserResponseDTO response = new UpdateUserResponseDTO(userId, user.getUsername(), user.getEmail());
        return response;
    }
}