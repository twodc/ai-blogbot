package org.example.blogbot.service;

import lombok.RequiredArgsConstructor;
import org.example.blogbot.dto.GptDtos.ChatMessage;
import org.example.blogbot.dto.GptDtos.ChatRequest;
import org.example.blogbot.dto.GptDtos.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GptService {

    private final RestClient restClient;

    @Value("${openai.api-key}")
    private String openAiApiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    public String generateHtmlPost(String topic) {
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"));

        ChatRequest req = ChatRequest.builder()
                .model(model)
                .temperature(0.7)
                .max_tokens(3000)
                .messages(List.of(
                        ChatMessage.builder()
                                .role("system")
                                .content(
                                        "너는 실무 중심의 AI 활용 블로그 작가야. " +
                                                "AI, 생산성 툴, 개발 트렌드, 스타트업의 기술 활용 사례를 기반으로 " +
                                                "자연스럽고 현실적인 시각으로 글을 써라. " +
                                                "문체는 사람이 쓴 뉴스·칼럼처럼 매끄럽고, 불필요한 이모지나 과한 꾸밈은 사용하지 마. " +
                                                "모든 결과는 완전한 HTML 형식으로 출력해."
                                ).build(),
                        ChatMessage.builder()
                                .role("user")
                                .content("""
                                        오늘 날짜는 %s입니다.
                                        오늘의 주제는 "%s"입니다.
                                        
                                        아래 형식으로 HTML 글을 작성해 주세요:
                                        
                                        <h2>%s, 오늘의 AI 활용 트렌드</h2>
                                        
                                        <h3>1. 새롭게 주목받는 AI 툴</h3>
                                        <p>최근 발표되거나 인기를 얻고 있는 AI 도구, 기능, 또는 기술 중
                                        실제로 실무에 도움이 될 만한 내용을 4~6문장으로 서술해 주세요.</p>
                                        
                                        <h3>2. 실제 활용 사례</h3>
                                        <p>기업, 개발자, 혹은 개인이 AI를 활용해 성과를 낸 사례를
                                        5문장 이상으로 구체적으로 작성해 주세요.</p>
                                        
                                        <h3>3. 오늘의 인사이트</h3>
                                        <p>실무자나 개발자가 참고할 만한 시사점이나 조언을 4문장 이상으로 작성해 주세요.</p>
                                        
                                        전체 글은 약 1000~1500단어 분량으로 작성해 주세요.
                                        """.formatted(todayStr, topic, todayStr)
                                ).build()
                ))
                .build();

        ChatResponse res = restClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + openAiApiKey)
                .body(req)
                .retrieve()
                .body(ChatResponse.class);

        if (res == null || res.getChoices() == null || res.getChoices().isEmpty()) {
            throw new IllegalStateException("GPT 응답이 비었습니다.");
        }

        Object content = res.getChoices().get(0).getMessage().get("content");
        return content == null ? "" : content.toString().trim();
    }

    public String suggestTitle(String topic, String html) {
        ChatRequest req = ChatRequest.builder()
                .model(model)
                .temperature(0.6)
                .max_tokens(50)
                .messages(List.of(
                        ChatMessage.builder().role("system")
                                .content("너는 글 제목을 40자 이내로 간결하게 뽑는 편집자야. 한국어로만 답해.").build(),
                        ChatMessage.builder().role("user")
                                .content("다음 본문에 어울리는 제목을 1개만 출력해줘.\n주제 힌트: " + topic + "\n본문(HTML):\n" + html).build()
                ))
                .build();

        ChatResponse res = restClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + openAiApiKey)
                .body(req)
                .retrieve()
                .body(ChatResponse.class);

        if (res == null || res.getChoices() == null || res.getChoices().isEmpty()) {
            return "오늘의 기술 트렌드";
        }
        Object content = res.getChoices().get(0).getMessage().get("content");
        return content == null ? "오늘의 기술 트렌드"
                : content.toString().replaceAll("[\\n\\r]+", " ").trim();
    }
}
