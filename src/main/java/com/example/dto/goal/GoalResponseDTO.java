package com.example.dto.goal;

import com.example.goal.common.GoalCategory;

public record GoalResponseDTO(Long id, String name , GoalCategory category) {
}
