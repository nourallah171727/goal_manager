package com.example.demo.service;

import com.example.demo.model.Goal;
import com.example.demo.model.User;
import com.example.demo.repository.GoalRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalService goalService;
    private static Goal goal;
    private static Goal goal2;
    @BeforeAll
    static void setup(){
        goal = new Goal("win the tennis Tournament", new User("mockUser", "mockemail@yahoo.de"));
        goal2 = new Goal("achieve 36 credits", new User("mockUser2", "mockuser2@Gmail.com"));
    }
    @Test
    void testGetGoalByExistingId(){
        when(goalRepository.findById(123546L)).thenReturn(Optional.of(goal));
        Goal givenGoal = goalService.getGoalById(123546L);
        assertNotNull(givenGoal);
        assertEquals(goal.getName(), givenGoal.getName());
        assertNotNull(givenGoal.getUser());
        assertEquals(goal.getUser().getUsername(), givenGoal.getUser().getUsername());
        assertEquals(goal.getUser().getEmail(), givenGoal.getUser().getEmail());
        verify(goalRepository).findById(123546L);
    }

    @Test
    void testGetGoalByNonExistingId(){
        when(goalRepository.findById(146587L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()->goalService.getGoalById(146587L));
        assertEquals("no goal with such ID",ex.getMessage());
        verify(goalRepository).findById(146587L);
    }

    @Test
    void testGetGoalNullId(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()->goalService.getGoalById(null));
        assertEquals("goalId is null", ex.getMessage());
    }

    @Test
    void testGetGoalsEmpty(){
        when(goalRepository.findAll()).thenReturn(new ArrayList<>());
        List<Goal> goalList = goalService.getGoals();
        assertNotNull(goalList);
        assertEquals(0, goalList.size());
        verify(goalRepository).findAll();
    }

    @Test
    void testGetGoalsMulti(){
        List<Goal> goals = new LinkedList<>();
        goals.add(goal);
        goals.add(goal2);
        when(goalRepository.findAll()).thenReturn(goals);
        List<Goal> goalList = goalService.getGoals();
        assertNotNull(goalList);
        assertEquals(2, goals.size());
        Goal goalList1 = goalList.get(0);
        Goal goalList2 = goalList.get(1);
        assertNotNull(goalList1);
        assertNotNull(goalList2);
        assertTrue(goal.getName().equals(goalList1.getName())^goal.getName().equals(goalList2.getName()));
        assertTrue(goal2.getName().equals(goalList1.getName())^goal2.getName().equals(goalList2.getName()));
        assertNotNull(goalList1.getUser());
        assertNotNull(goalList2.getUser());
        assertTrue(goal.getUser().getUsername().equals(goalList1.getUser().getUsername()) ^ goal.getUser().getUsername().equals(goalList2.getUser().getUsername()));
        assertTrue(goal.getUser().getEmail().equals(goalList1.getUser().getEmail()) ^ goal.getUser().getEmail().equals(goalList2.getUser().getEmail()));
        assertTrue(goal2.getUser().getUsername().equals(goalList1.getUser().getUsername()) ^ goal2.getUser().getUsername().equals(goalList2.getUser().getUsername()));
        assertTrue(goal2.getUser().getEmail().equals(goalList1.getUser().getEmail()) ^ goal2.getUser().getEmail().equals(goalList2.getUser().getEmail()));
    }
    @Test
    void testCreateGoalNullId(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()->goalService.createGoal(new Goal("win the tennis Tournament", new User("mockUser", "mockemail@yahoo.de")), null ));
        assertEquals("userId should not be null", ex.getMessage());
    }
    @Test
    void testCreateGaolNonExistingId(){
        when(userRepository.findById(145687L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> goalService.createGoal(new Goal("win the tennis Tournament", new User("mockUser", "mockemail@yahoo.de")), 145687L ));
        assertEquals("userId should be a valid Id", ex.getMessage());
        verify(userRepository).findById(145687L);
        verifyNoInteractions(goalRepository);
    }
    @Test
    void testCreateNullGoal(){
        when(userRepository.findById(145687L)).thenReturn(Optional.of(new User("username", "useremail@gmail.com")));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> goalService.createGoal(null, 145687L ));
        assertEquals("goal should not be null", ex.getMessage());
        verify(userRepository).findById(145687L);
        verifyNoInteractions(goalRepository);
    }
    @Test
    void testCreateInvalidGoal(){
        when(userRepository.findById(145687L)).thenReturn(Optional.of(new User("username", "useremail@gmail.com")));
        goal.setId(45896L);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> goalService.createGoal(goal, 145687L ));
        assertEquals("goal should not already have an ID!", ex.getMessage());
        verify(userRepository).findById(145687L);
        verifyNoInteractions(goalRepository);
    }

    @Test
    void testCreateGoalSuccessful(){
        when(userRepository.findById(145687L)).thenReturn(Optional.of(new User("username", "useremail@gmail.com")));
        when(goalRepository.save(goal)).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            g.setId(1254L); // simulate DB-generated ID
            return g;
        });
        assertNull(goal.getId());
        Goal savedGoal = goalService.createGoal(goal, 145687L);
        assertNotNull(savedGoal);
        assertNotNull(savedGoal.getId());
        assertEquals(goal.getName(), savedGoal.getName());
        assertNotNull(savedGoal.getUser());
        assertEquals("username", savedGoal.getUser().getUsername());
        assertEquals("useremail@gmail.com", savedGoal.getUser().getEmail());
        verify(userRepository).findById(145687L);
        verify(goalRepository).save(goal);
    }
    @Test
    void testUpdateGoalNullId(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()->goalService.updateGoal(null, goal));
        assertEquals("id must not be null", ex.getMessage());
        verifyNoInteractions(goalRepository);
    }
    @Test
    void testUpdateNullGoal(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> goalService.updateGoal(1245L, null));
        assertEquals( "goal should not be null", ex.getMessage());
        verifyNoInteractions(goalRepository);
    }
    @Test
    void testUpdateGoalNonExistingId(){
        when(goalRepository.findById(12453L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> goalService.updateGoal(12453L, goal));
        assertEquals( "goal must already be in the db", ex.getMessage());
        verify(goalRepository).findById(12453L);
    }
    /*
    @Test
    void testUpdateGoalNotNullId(){
        goal.setId(821478L);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> goalService.updateGoal(821478L, goal));
        assertEquals("goalId must not be null", ex.getMessage());
        verifyNoInteractions(goalRepository);
    }

     */
    @Test
    void testUpdateGoalSuccessful(){
        when(goalRepository.findById(4605242L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(goal2)).thenReturn(goal2);
        Goal updatedGoal = goalService.updateGoal(4605242L, goal2);
        assertNotNull(updatedGoal);
        assertEquals(goal2.getName(), updatedGoal.getName());
        assertEquals(4605242L, updatedGoal.getId());
        verify(goalRepository).findById(4605242L);
        verify(goalRepository).save(any());
    }
    @Test
    void testDeleteGoalNullId(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> goalService.deleteById(null));
        assertEquals("goal must already be in the db", ex.getMessage());
        verifyNoInteractions(goalRepository);
    }
    @Test
    void testDeleteGoalNonExistingId(){
        when(goalRepository.findById(87564L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> goalService.deleteById(87564L));
        assertEquals("goal must already be in the db", ex.getMessage());
        verify(goalRepository).findById(87564L);
    }
    @Test
    void testDeleteGoalSuccessful(){
        when(goalRepository.findById(457898L)).thenReturn(Optional.of(goal));
        goalService.deleteById(457898L);
        verify(goalRepository).findById(457898L);
        verify(goalRepository).deleteById(457898L);
        verifyNoMoreInteractions(goalRepository);
    }
}
