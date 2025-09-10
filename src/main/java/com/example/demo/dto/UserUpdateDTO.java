package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(

        @NotBlank(message = "username is required")
        String username,
        @NotBlank(message = "email is required")
        @Email(message = "must be a valid email")
        String email) {
}
