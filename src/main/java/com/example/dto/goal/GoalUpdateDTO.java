package com.example.dto.goal;

import com.example.dto.task.TaskCreateDTO;
import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.Set;

public record GoalUpdateDTO(@NotBlank(message = "name is required")
                            String name,

                            @NotNull(message = "category should be provided")
                            GoalCategory category
) {
}
