package com.example.upload.repo;

import com.example.upload.entity.Upload;
import com.example.upload.entity.UploadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UploadRepository extends JpaRepository< Upload , UploadId> {
    @Modifying
    @Query("""
    UPDATE Upload u
    SET u.currentVotes = u.currentVotes + 1
    WHERE u.userId = :userId
    AND u.taskId = :taskId
    AND u.currentVotes < :required
    """)
    int incrementVotesIfBelowThreshold(@Param("userId") Long userId,
                                  @Param("taskId") Long taskId,
                                  @Param("required") int required);
    @Modifying
    @Query("""
    DELETE FROM Upload u
    WHERE u.userId = :userId AND u.taskId = :taskId
    AND u.currentVotes = :requiredVotes
""")
    int deleteIfAtThreshold(@Param("userId") Long userId,
                            @Param("taskId") Long taskId,
                            @Param("requiredVotes") int requiredVotes);

}
