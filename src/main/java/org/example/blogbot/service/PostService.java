package org.example.blogbot.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.blogbot.entity.PostLog;
import org.example.blogbot.repository.PostLogRepository;
import org.example.blogbot.util.HtmlUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final GptService gptService;
    private final PostLogRepository postLogRepository;
    private final DriveUploadService driveUploadService;

    public PostLog generateAndPublish(String blogName, String topic) {
        LocalDateTime now = LocalDateTime.now();
        String html = gptService.generateHtmlPost(topic);
        String safeHtml = HtmlUtil.ensureHtml(html);
        String title = gptService.suggestTitle(topic, safeHtml);

        PostLog log = PostLog.builder()
                .title(title)
                .content(safeHtml)
                .status("INIT")
                .blogName(blogName)
                .createdAt(now)
                .build();
        log = postLogRepository.save(log);

        try {
            File savedFile = saveAsHtmlFile(blogName, title, safeHtml);

            // ✅ Google Drive 업로드
            String uploadedUrl = driveUploadService.uploadFile(savedFile);

            log.setStatus(uploadedUrl != null ? "UPLOADED" : "LOCAL_ONLY");
            log.setPostUrl(uploadedUrl);
            log.setPostedAt(LocalDateTime.now());
        } catch (Exception e) {
            log.setStatus("FAIL");
            log.setErrorMessage(e.getMessage());
        }
        return postLogRepository.save(log);
    }

    private File saveAsHtmlFile(String blogName, String title, String htmlContent) throws Exception {
        String safeTitle = title.replaceAll("[^a-zA-Z0-9가-힣\\s]", "").replace(" ", "_");
        File dir = new File("posts/" + blogName);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, safeTitle + ".html");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("<h1>" + title + "</h1>\n");
            writer.write(htmlContent);
        }
        System.out.println("✅ [" + blogName + "] 글 저장 완료: " + file.getAbsolutePath());
        return file;
    }
}
