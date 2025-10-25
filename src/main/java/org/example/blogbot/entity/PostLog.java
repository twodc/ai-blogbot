package org.example.blogbot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    private String status; // INIT, LOCAL_ONLY, FAIL 등

    private String blogName; // ✅ 추가됨 (블로그 이름 or 카테고리 구분용)

    private String postUrl;

    private LocalDateTime createdAt;

    private LocalDateTime postedAt;

    private String errorMessage;
}
