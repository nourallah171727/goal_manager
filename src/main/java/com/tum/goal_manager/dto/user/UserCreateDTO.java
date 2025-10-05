package com.tum.goal_manager.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateDTO(
        @NotBlank(message = "username is required")
        String username,

        @NotBlank(message = "email is required")
        @Email(message = "must be a valid email")
        String email,
        @NotBlank(message="a password must be given")
        String password
) {}
