package com.example.et.service.ai.parsetask;

import com.example.et.model.ai.AiParsingTask;
import com.example.et.repo.AiParseTaskRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiParseTaskServiceImpl implements AiParseTaskService {
    private final AiParseTaskRepo aiParseTaskRepo;

    @Override
    public AiParsingTask save(AiParsingTask task) {
        return aiParseTaskRepo.save(task);
    }

    @Override
    public List<AiParsingTask> getPendingTasksWithAppUser(AiParsingTask.Status status) {
        return aiParseTaskRepo.findByStatusWithAppUser(status);
    }
    @Override
    public AiParsingTask getById(UUID id) {
        return aiParseTaskRepo.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void unlinkTransaction(UUID transactionId) {
        aiParseTaskRepo.unlinkTransaction(transactionId);
    }
    @Override
    @Transactional
    public List<AiParsingTask> claimPendingTasks() {
        final var tasks = aiParseTaskRepo.findByStatusWithAppUserForUpdate(AiParsingTask.Status.PENDING);
        tasks.forEach(t -> t.setStatus(AiParsingTask.Status.PROCESSING));
        return aiParseTaskRepo.saveAll(tasks);
        // transaction commits here — lock released, rows now visibly PROCESSING
    }
}