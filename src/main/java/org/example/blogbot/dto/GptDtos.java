package org.example.blogbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

public class GptDtos {

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ChatMessage {
        private String role;    // "system" | "user" | "assistant"
        private String content;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ChatRequest {
        private String model;
        private Double temperature;
        private Integer max_tokens;
        private List<ChatMessage> messages;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ChatResponse {
        private List<Choice> choices;

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class Choice {
            private Map<String, Object> message; // {role: "...", content: "..."}
        }
    }
}
