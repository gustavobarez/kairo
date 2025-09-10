package br.com.gustavobarez.Kairo.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.gustavobarez.Kairo.Appointment.AppointmentDTO;

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
            throw new IllegalArgumentException("Email already in use");
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
        var user = repository.findById(userId).get();
        var createdAppointments = user.getCreatedAppointments();
        var participatingAppointments = user.getParticipatingAppointments();

        List<AppointmentDTO> createdAppointmentsDTO = createdAppointments.stream()
                .map(dto -> new AppointmentDTO(dto))
                .collect(Collectors.toList());

        List<AppointmentDTO> participatingAppointmentsDTO = participatingAppointments.stream()
                .map(dto -> new AppointmentDTO(dto))
                .collect(Collectors.toList());

        Map<String, List<AppointmentDTO>> result = Map.of(
                "createdAppointments", createdAppointmentsDTO,
                "participatingAppointments", participatingAppointmentsDTO);

        return result;
    }

}
