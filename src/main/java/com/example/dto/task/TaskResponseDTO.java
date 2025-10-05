package com.example.dto.task;

import com.example.task.common.TaskDifficulty;

public record TaskResponseDTO (Long id , String name , TaskDifficulty difficulty){
}
