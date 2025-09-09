package br.com.gustavobarez.Kairo.Auth;

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
public class AuthUserResponseDTO {
    
    private String access_token;

    private Long expires_in;

}
