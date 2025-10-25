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
    @Column(columnDefinition = "CLOB")
    private String content;

    private String status;          // LOCAL_ONLY / FAIL
    private String postUrl;         // (티스토리 사용 안함 - 항상 null)
    private String errorMessage;

    private LocalDateTime createdAt;
    private LocalDateTime postedAt; // 파일 저장/업로드 시각
}
