package br.com.gustavobarez.Kairo.User;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

}
