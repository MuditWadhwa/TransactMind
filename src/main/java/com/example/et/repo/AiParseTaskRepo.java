package com.example.et.repo;

import com.example.et.model.ai.AiParsingTask;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.UUID;

public interface AiParseTaskRepo extends JpaRepository<AiParsingTask, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")}) // SKIP LOCKED, not NOWAIT
    @Query("SELECT t FROM AiParsingTask t JOIN FETCH t.appUser WHERE t.status = :status ORDER BY t.createdAt ASC")
    List<AiParsingTask> findByStatusWithAppUserForUpdate(AiParsingTask.Status status);

    @Query("SELECT t FROM AiParsingTask t JOIN FETCH t.appUser WHERE t.status = :status ORDER BY t.createdAt ASC")
    List<AiParsingTask> findByStatusWithAppUser(AiParsingTask.Status status);

    List<AiParsingTask> findByStatus(AiParsingTask.Status status);

    @Modifying
    @Query("UPDATE AiParsingTask a SET a.transaction = null WHERE a.transaction.id = :transactionId")
    void unlinkTransaction(UUID transactionId);
}