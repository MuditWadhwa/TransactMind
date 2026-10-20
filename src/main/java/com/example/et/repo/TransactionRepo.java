package com.example.et.repo;

import com.example.et.model.core.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepo extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {
    Optional<Transaction> findByIdAndAppUserId(UUID transactionId, UUID userUuid);

    List<Transaction> findAllByTransferIdAndAppUserId(UUID transferId, UUID userUuid);
    @Query(value = "SELECT * FROM transaction WHERE embedding IS NULL ORDER BY created_at ASC LIMIT :limit",
            nativeQuery = true)
    List<Transaction> findTransactionsWithoutEmbedding(@Param("limit") int limit);

    @Modifying
    @Query(value = "UPDATE transaction SET embedding = CAST(:embedding AS vector) WHERE id = :id",
            nativeQuery = true)
    void updateEmbedding(@Param("id") UUID id, @Param("embedding") String embedding);

    @Query(value = """
      SELECT * FROM transaction
      WHERE app_user_id = :userId AND embedding IS NOT NULL
      ORDER BY embedding <=> CAST(:queryEmbedding AS vector)
      LIMIT :limit
      """, nativeQuery = true)
    List<Transaction> findSimilarTransactions(
            @Param("userId") UUID userId,
            @Param("queryEmbedding") String queryEmbedding,
            @Param("limit") int limit);
}