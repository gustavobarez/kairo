package br.com.gustavobarez.Kairo.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDTO {
    
    private Long id;

    private String username;

    public UserResponseDTO(CreateUserRequestDTO request) {
        this.username = request.username();
    }

}
