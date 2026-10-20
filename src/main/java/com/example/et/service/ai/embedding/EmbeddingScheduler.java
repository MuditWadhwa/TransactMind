package com.example.et.service.ai.embedding;

import com.example.et.repo.TransactionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmbeddingScheduler {
    private final TransactionRepo transactionRepo;
    private final EmbeddingService embeddingService;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void embedPendingTransactions() {
        final var pending = transactionRepo.findTransactionsWithoutEmbedding(20);
        if (pending.isEmpty()) return;

        log.info("Embedding {} pending transactions", pending.size());

        for (var txn : pending) {
            try {
                final var text = String.format("%s %s %s amount %.2f",
                        txn.getType(),
                        txn.getTransactionCategory() != null ? txn.getTransactionCategory().getName() : "",
                        txn.getDescription() != null ? txn.getDescription() : "",
                        Math.abs(txn.getAmount()));

                final var vector = embeddingService.embed(text);
                transactionRepo.updateEmbedding(txn.getId(), embeddingService.toVectorLiteral(vector));
            } catch (Exception e) {
                log.error("Failed to embed transaction {}", txn.getId(), e);
                // left with embedding=NULL, picked up again next sweep — naturally retried
            }
        }
    }
}