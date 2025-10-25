package org.example.blogbot.controller;

import lombok.RequiredArgsConstructor;
import org.example.blogbot.entity.PostLog;
import org.example.blogbot.repository.PostLogRepository;
import org.example.blogbot.service.PostService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLogRepository postLogRepository;

    // ✅ GET/POST 모두 허용 (Render 브라우저 테스트 호환)
    @RequestMapping(value = "/publish", method = {RequestMethod.GET, RequestMethod.POST})
    public PostLog publish(
            @RequestParam(defaultValue = "ai-blogbot") String blogName,
            @RequestParam(defaultValue = "오늘의 AI 트렌드 요약") String topic
    ) {
        return postService.generateAndPublish(blogName, topic);
    }

    // ✅ 특정 글 ID 조회
    @GetMapping("/{id}")
    public PostLog get(@PathVariable Long id) {
        return postLogRepository.findById(id).orElseThrow();
    }
}
