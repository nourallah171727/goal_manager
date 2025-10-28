package com.example.dto.goal;

import com.example.dto.task.TaskFeedDTO;
import com.example.dto.task.TaskResponseDTO;
import com.example.goal.common.GoalCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record GoalFeedDTO (String nameOfHost,
                           LocalDate dueDate,
                           GoalCategory category,
                           LocalDateTime createdAt,
                           int votesToBeMarkedDone,
                           List<TaskResponseDTO>tasks){
}
