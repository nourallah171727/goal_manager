package com.example.dto.task;

import com.example.task.common.TaskDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskCreateDTO(@NotBlank String name, @NotNull TaskDifficulty difficulty) {
}
