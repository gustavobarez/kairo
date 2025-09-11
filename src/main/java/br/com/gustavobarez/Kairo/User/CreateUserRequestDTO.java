package br.com.gustavobarez.Kairo.User;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequestDTO(@NotBlank(message = "Username cannot be null or empty") String username,
                @NotBlank(message = "Email cannot be null or empty") String email,
                @NotBlank(message = "Password cannot be null or empty") String password) {
}