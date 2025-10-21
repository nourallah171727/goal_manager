package com.example.dto.goal;

import com.example.dto.task.TaskCreateDTO;
import com.example.goal.common.GoalCategory;
import com.example.goal.common.GoalType;
import com.example.task.entity.Task;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record GoalCreateDTO(@NotBlank(message = "name is required")
                            String name,
                            LocalDate dueDate,
                            @NotNull(message = "category should be provided")
                            GoalCategory category,
                            @NotNull(message = "type must be provided")
                            GoalType type,

                            String privateCode,
                            @Positive
                            int votesToMarkCompleted,
                            @NotEmpty
                            Set<@Valid TaskCreateDTO> tasks){
}
