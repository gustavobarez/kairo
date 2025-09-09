package br.com.gustavobarez.Kairo.User;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public CreateUserDTO createUser(CreateUserDTO dto) {

        if (repository.findUserByEmail(dto.email()) != null) {
            throw new IllegalArgumentException("Email already in use");
        }
        
        User user = User.builder()
        .username(dto.username())
        .email(dto.email())
        .password(dto.password())
        .build();

        repository.save(user);

        return dto;
    }

}
