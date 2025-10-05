package com.tum.goal_manager.dto.goal;

import com.tum.goal_manager.goal.common.GoalCategory;

public record GoalResponseDTO(Long id, String name , GoalCategory category) {
}
