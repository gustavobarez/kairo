package br.com.gustavobarez.Kairo.Auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.gustavobarez.Kairo.User.UserRepository;

@Service
public class AuthService {

    @Value("${security.token.secret}")
    private String secretKey;

    UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthUserResponseDTO execute(AuthUserRequestDTO dto) throws AuthenticationException {
        var user = this.userRepository.findUserByEmail(dto.email())
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("Email/password incorrect");
                });
        var passwordMatches = this.passwordEncoder
                .matches(dto.password(), user.getPassword());

        if (!passwordMatches) {
            throw new AuthenticationException();
        }

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        var expiresIn = Instant.now().plus(Duration.ofMinutes(10));
        var token = JWT.create()
                .withIssuer("kairo")
                .withSubject(user.getId().toString())
                .withClaim("roles", Arrays.asList("USER"))
                .withExpiresAt(expiresIn)
                .sign(algorithm);

        var authUserResponseDTO = AuthUserResponseDTO.builder()
                .access_token(token)
                .expires_in(expiresIn.toEpochMilli())
                .build();

        return authUserResponseDTO;

    }

}
