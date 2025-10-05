package com.tum.goal_manager.user.repository;

import com.tum.goal_manager.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface  UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    @Modifying
    @Query(value = """
        INSERT INTO user_finished_tasks (user_id, task_id, finished_at)
        VALUES (:userId, :taskId, CURRENT_TIMESTAMP)
        """, nativeQuery = true)
    void insertFinishedTask(@Param("userId") Long userId, @Param("taskId") Long taskId);
}
