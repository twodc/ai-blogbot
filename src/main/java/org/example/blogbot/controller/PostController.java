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

    @PostMapping("/generate")
    public PostLog generate(@RequestParam(defaultValue = "AI 실무 활용 트렌드") String topic) {
        return postService.generateAndSave(topic);
    }

    @GetMapping("/{id}")
    public PostLog get(@PathVariable Long id) {
        return postLogRepository.findById(id).orElseThrow();
    }
}
