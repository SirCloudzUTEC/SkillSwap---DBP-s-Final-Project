package com.onlyswapx.skill.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    private static final String MODEL_PATH = "/models/gemini-1.5-flash:generateContent";

    private final WebClient webClient;

    @Value("${gemini.api-key:}")
    private String apiKey;

    public GeminiService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    @Async("taskExecutor")
    public void analyzeSkillAsync(Long skillId, String skillName, String description, String category) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[GEMINI] api-key not configured; skipping analysis for skill {}", skillId);
            return;
        }

        String prompt = buildPrompt(skillName, description, category);
        Map<String, Object> body = buildRequestBody(prompt);

        try {
            String text = webClient.post()
                    .uri(uri -> uri.path(MODEL_PATH).queryParam("key", apiKey).build())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(this::extractText)
                    .timeout(Duration.ofSeconds(15))
                    .onErrorResume(WebClientResponseException.class, this::handleHttpError)
                    .onErrorResume(ex -> {
                        log.error("[GEMINI] network failure for skill {}: {}", skillId, ex.getMessage());
                        return Mono.just("");
                    })
                    .block();

            log.info("[GEMINI][skill={}][thread={}] analysis: {}",
                    skillId, Thread.currentThread().getName(), text);
        } catch (Exception ex) {
            log.error("[GEMINI] unexpected error for skill {}: {}", skillId, ex.getMessage());
        }
    }

    private String buildPrompt(String name, String description, String category) {
        return """
                You are a semantic matching assistant for SkillSwap, a peer-to-peer skill exchange platform.
                Given the following skill, produce a short normalized profile in JSON with the keys:
                "canonical_name", "tags" (array of 3-7 lowercase keywords), "complementary_skills" (array of 3 skills
                that pair well for an exchange). Respond with valid JSON only, no prose.

                Skill name: %s
                Description: %s
                Category: %s
                """.formatted(safe(name), safe(description), safe(category));
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        return Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) return "";
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            if (content == null) return "";
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) return "";
            Object text = parts.get(0).get("text");
            return text == null ? "" : text.toString();
        } catch (ClassCastException ex) {
            log.warn("[GEMINI] unexpected response shape: {}", ex.getMessage());
            return "";
        }
    }

    private Mono<String> handleHttpError(WebClientResponseException ex) {
        log.error("[GEMINI] HTTP {} from API: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        return Mono.just("");
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("\"", "'");
    }
}
