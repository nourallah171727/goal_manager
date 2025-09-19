package com.example.user.common;

import com.example.user.entity.User;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserEntityListener {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PrePersist
    @PreUpdate
    public void hashPassword(User user) {
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));

    }
}