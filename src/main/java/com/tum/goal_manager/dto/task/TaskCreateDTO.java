package com.tum.goal_manager.dto.task;

import com.tum.goal_manager.task.common.TaskDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskCreateDTO(@NotBlank String name, @NotNull TaskDifficulty difficulty) {
}
