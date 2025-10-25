package org.example.blogbot.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blogbot.entity.PostLog;
import org.example.blogbot.service.PostService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoPostScheduler {

    private final PostService postService;

    @Value("${bot.topics:AI로 업무 효율 높이기,최근 떠오른 생산성 툴,개발자가 알아야 할 AI 도구,스타트업의 AI 도입 사례,오늘의 AI 기술 트렌드}")
    private String topicsCsv;

    // 매일 9시 (서울)
    @Scheduled(cron = "${bot.cron:0 0 9 * * *}", zone = "Asia/Seoul")
    public void runDaily() {
        List<String> topics = List.of(topicsCsv.split("\\s*,\\s*"));
        String topic = topics.get((int) (Math.random() * topics.size()));

        try {
            PostLog logEntry = postService.generateAndSave(topic);
            log.info("✅ AutoPost: title='{}', status={}, link={}",
                    logEntry.getTitle(), logEntry.getStatus(), logEntry.getPostUrl());
        } catch (Exception e) {
            log.error("❌ AutoPost error", e);
        }
    }
}
