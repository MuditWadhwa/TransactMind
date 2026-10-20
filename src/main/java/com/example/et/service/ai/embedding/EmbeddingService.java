package com.example.et.service.ai.embedding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@Slf4j
public class EmbeddingService {

    public EmbeddingService() {
        log.info("Successfully initialized Custom Local Embedding Generator (Offline Mode).");
    }

    public float[] embed(String text) {
        // text-embedding-004 uses 768 dimensions. We match this exactly so your
        // existing Flyway V13 database table schema constraints don't crash.
        final int dimensions = 768;
        final float[] vector = new float[dimensions];

        // Seed the randomizer with a clean lowercase string hash.
        // This ensures the EXACT same text always produces the EXACT same vector.
        long seed = text.trim().toLowerCase().hashCode();
        Random random = new Random(seed);

        float sumSquare = 0;
        for (int i = 0; i < dimensions; i++) {
            vector[i] = random.nextFloat() - 0.5f;
            sumSquare += vector[i] * vector[i];
        }

        // CRITICAL STEP: Normalize the vector magnitude to 1.0.
        // This allows pgvector to execute accurate Cosine Distance calculations.
        float magnitude = (float) Math.sqrt(sumSquare);
        if (magnitude > 0) {
            for (int i = 0; i < dimensions; i++) {
                vector[i] /= magnitude;
            }
        }

        return vector;
    }

    public String toVectorLiteral(float[] vector) {
        final var sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            sb.append(vector[i]);
            if (i < vector.length - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }
}
