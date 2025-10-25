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

    // ✅ 글 자동 생성 (GET/POST 둘 다 허용)
    @RequestMapping(value = "/publish", method = {RequestMethod.GET, RequestMethod.POST})
    public PostLog publish(
            @RequestParam(defaultValue = "ai-blogbot") String blogName,
            @RequestParam(defaultValue = "오늘의 AI 트렌드 요약") String topic
    ) {
        return postService.generateAndSave(blogName, topic);
    }

    // ✅ 특정 ID로 글 조회
    @GetMapping("/{id}")
    public PostLog get(@PathVariable Long id) {
        return postLogRepository.findById(id).orElseThrow();
    }
}
