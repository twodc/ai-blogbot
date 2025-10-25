package org.example.blogbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blogbot.entity.PostLog;
import org.example.blogbot.repository.PostLogRepository;
import org.example.blogbot.util.HtmlUtil;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final GptService gptService;
    private final PostLogRepository postLogRepository;
    private final DriveUploadService driveUploadService;

    /**
     * GPT로 글 생성 + HTML 저장 + (옵션) 구글 드라이브 업로드
     */
    public PostLog generateAndPublish(String blogName, String topic) {
        LocalDateTime now = LocalDateTime.now();
        log.info("🧠 [{}] 주제 '{}' 로 포스트 생성 시작", blogName, topic);

        String html = gptService.generateHtmlPost(topic);
        String safeHtml = HtmlUtil.ensureHtml(html);
        String title = gptService.suggestTitle(topic, safeHtml);

        // 초기 로그 저장
        PostLog logEntry = PostLog.builder()
                .title(title)
                .content(safeHtml)
                .status("INIT")
                .blogName(blogName)
                .createdAt(now)
                .build();
        logEntry = postLogRepository.save(logEntry);

        try {
            // 1️⃣ 로컬 파일 저장
            File savedFile = saveAsHtmlFile(blogName, title, safeHtml);

            // 2️⃣ Google Drive 업로드 시도
            String driveUrl = driveUploadService.uploadHtml(savedFile);
            if (driveUrl != null) {
                logEntry.setStatus("UPLOADED");
                logEntry.setPostUrl(driveUrl);
                log.info("✅ [{}] 업로드 완료: {}", blogName, driveUrl);
            } else {
                logEntry.setStatus("LOCAL_ONLY");
                log.warn("⚠️ [{}] 업로드 실패 (로컬 저장만 완료)", blogName);
            }

            logEntry.setPostedAt(LocalDateTime.now());
        } catch (Exception e) {
            logEntry.setStatus("FAIL");
            logEntry.setErrorMessage(e.getMessage());
            log.error("❌ [{}] 포스트 생성 중 오류: {}", blogName, e.getMessage(), e);
        }

        return postLogRepository.save(logEntry);
    }

    /**
     * HTML 파일로 저장
     */
    private File saveAsHtmlFile(String blogName, String title, String htmlContent) throws Exception {
        String safeTitle = title
                .replaceAll("[^a-zA-Z0-9가-힣\\s]", "")
                .trim()
                .replace(" ", "_");

        File dir = new File("posts/" + blogName);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("디렉토리 생성 실패: " + dir.getAbsolutePath());
        }

        File file = new File(dir, safeTitle + ".html");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("<h1>" + title + "</h1>\n");
            writer.write(htmlContent);
        }

        log.info("✅ [{}] 글 저장 완료: {}", blogName, file.getAbsolutePath());
        return file;
    }
}
