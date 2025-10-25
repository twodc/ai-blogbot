package org.example.blogbot.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blogbot.entity.PostLog;
import org.example.blogbot.service.PostService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoPostScheduler {

    private final PostService postService;

    @Value("${bot.blog-name:ai-blogbot}")
    private String blogName;

    @Value("${bot.topics:AI로 업무 효율 높이기,최근 떠오른 생산성 툴,개발자가 알아야 할 AI 도구,스타트업의 AI 도입 사례,오늘의 AI 기술 트렌드}")
    private String topics;

    @Scheduled(cron = "${bot.cron:0 0 9 * * *}")
    public void runDaily() {
        try {
            List<String> topicList = List.of(topics.split(","));
            String randomTopic = topicList.get(new Random().nextInt(topicList.size())).trim();

            PostLog postLog = postService.generateAndSave(blogName, randomTopic);
            log.info("✅ AutoPost done [{}]: {}", blogName, postLog.getTitle());
        } catch (Exception e) {
            log.error("❌ AutoPost error", e);
        }
    }
}
