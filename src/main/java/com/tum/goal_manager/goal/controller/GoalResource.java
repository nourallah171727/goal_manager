package com.tum.goal_manager.goal.controller;

import com.tum.goal_manager.dto.DTOMapper;
import com.tum.goal_manager.dto.goal.GoalCreateDTO;
import com.tum.goal_manager.dto.goal.GoalResponseDTO;
import com.tum.goal_manager.goal.entity.Goal;
import com.tum.goal_manager.goal.common.GoalType;
import com.tum.goal_manager.goal.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goal")
public class GoalResource {
    private final GoalService goalService;
    private final DTOMapper dtoMapper;

    @Autowired
    public GoalResource(GoalService goalService,DTOMapper mapper) {
        this.goalService = goalService;
        this.dtoMapper=mapper;
    }
    //any user
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponseDTO> getGoal(@PathVariable("goalId") Long goalId) {
            Goal goal = goalService.getGoalById(goalId);
            return ResponseEntity.ok(dtoMapper.goalToResponseDTO(goal));
    }
    /*
    //any user
    @GetMapping("/all")
    public ResponseEntity<List<Goal>> getAllGoals() {
        return ResponseEntity.ok(goalService.getGoals());
    }
    */


    //any user
    @PostMapping("/{userId}")
    public ResponseEntity<GoalResponseDTO> createGoal(@PathVariable("userId") Long userId,
                                                      @RequestBody @Valid GoalCreateDTO goal) {
            Goal createdGoal = goalService.createGoal(dtoMapper.createDtoToGoal(goal), userId);
            return ResponseEntity.ok(dtoMapper.goalToResponseDTO(createdGoal));
    }
    //only host or admin
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable("id") Long id, @RequestBody Goal goalDetails) {
        try {
            return ResponseEntity.ok(goalService.updateGoal(id, goalDetails));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    //only host or admin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable("id") Long id) {
        try {
            goalService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    //any user if not host and not already joined
    @PostMapping("/{goalId}/join/{userId}")
    public ResponseEntity<Void> joinGoal(@PathVariable Long goalId, @PathVariable Long userId) {
            goalService.joinGoal(goalId, userId);
            return ResponseEntity.ok().build();
    }
    //only if already joined in
    @DeleteMapping("/{goalId}/leave/{userId}")
    public ResponseEntity<Void> leaveGoal(@PathVariable Long goalId, @PathVariable Long userId) {
        try {
            goalService.leaveGoal(goalId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    /*
    // any user
    @PostMapping("/{goalId}/star/{userId}")
    public ResponseEntity<Void> addStar(@PathVariable Long goalId, @PathVariable Long userId) {
        try {
            goalService.addStar(goalId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    //any user
    @DeleteMapping("/{goalId}/star/{userId}")
    public ResponseEntity<Void> removeStar(@PathVariable Long goalId, @PathVariable Long userId) {
        try {
            goalService.removeStar(goalId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }*/
    //those I don't need for now

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Goal>> findGoalsByCategory(@PathVariable String category) {
        try {
            return ResponseEntity.ok(goalService.findGoalsByCategory(category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Goal>> findGoalsByType(@PathVariable GoalType type) {
        try {
            return ResponseEntity.ok(goalService.findGoalsByType(type));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<Goal>> findGoalsByHost(@PathVariable Long hostId) {
        try {
            return ResponseEntity.ok(goalService.findGoalsByHost(hostId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/member/{userId}")
    public ResponseEntity<List<Goal>> findGoalsByMember(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(goalService.findGoalsByMember(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}