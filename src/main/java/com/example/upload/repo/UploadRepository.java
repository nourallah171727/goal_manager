package com.example.upload.repo;

import com.example.upload.entity.Upload;
import com.example.upload.entity.UploadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UploadRepository extends JpaRepository< Upload , UploadId> {
    @Modifying
    @Query("UPDATE Upload u " +
            "SET u.currentVotes = u.currentVotes + 1 " +
            "WHERE u.user.id = :userId AND u.task.id = :taskId")
    void incrementVotes(@Param("userId") Long userId,
                       @Param("taskId") Long taskId);

}
