package com.tum.goal_manager.dto.goal;

import com.tum.goal_manager.dto.task.TaskCreateDTO;
import com.tum.goal_manager.goal.common.GoalCategory;
import com.tum.goal_manager.goal.common.GoalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
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
