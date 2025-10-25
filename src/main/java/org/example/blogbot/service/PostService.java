package org.example.blogbot.service;

import lombok.RequiredArgsConstructor;
import org.example.blogbot.entity.PostLog;
import org.example.blogbot.repository.PostLogRepository;
import org.example.blogbot.util.HtmlUtil;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final GptService gptService;
    private final PostLogRepository postLogRepository;
    private final DriveUploadService driveUploadService;

    public PostLog generateAndSave(String topic) {
        LocalDateTime now = LocalDateTime.now();

        String html = gptService.generateHtmlPost(topic);
        String safeHtml = HtmlUtil.ensureHtml(html);
        String title = gptService.suggestTitle(topic, safeHtml);

        // 파일 저장
        String date = LocalDate.now().toString();
        String fileName = HtmlUtil.safeFileName(date + "_" + title) + ".html";
        java.io.File dir = new java.io.File("posts");
        if (!dir.exists()) dir.mkdirs();
        java.io.File file = new java.io.File(dir, fileName);

        try (BufferedWriter w = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            w.write("<meta charset='UTF-8'>\n");
            w.write("<h1>" + title + "</h1>\n");
            w.write(safeHtml);
        } catch (Exception e) {
            PostLog fail = PostLog.builder()
                    .title(title)
                    .content(safeHtml)
                    .status("FAIL")
                    .errorMessage("파일 저장 실패: " + e.getMessage())
                    .createdAt(now)
                    .build();
            return postLogRepository.save(fail);
        }

        // 구글 드라이브 업로드 (옵션)
        String webLink = driveUploadService.uploadHtml(file);

        PostLog ok = PostLog.builder()
                .title(title)
                .content(safeHtml)
                .status("LOCAL_ONLY")
                .postUrl(webLink) // 드라이브 링크 저장(있으면)
                .createdAt(now)
                .postedAt(LocalDateTime.now())
                .build();

        return postLogRepository.save(ok);
    }
}
