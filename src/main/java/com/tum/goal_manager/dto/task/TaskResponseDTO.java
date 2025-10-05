package com.tum.goal_manager.dto.task;

import com.tum.goal_manager.task.common.TaskDifficulty;

public record TaskResponseDTO (Long id , String name , TaskDifficulty difficulty){
}
