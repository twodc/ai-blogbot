package org.example.blogbot.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
@Service
public class DriveUploadService {

    @Value("${gdrive.service-account-json:}")
    private String serviceAccountJson;

    @Value("${gdrive.folder-id:}")
    private String folderId;

    private Drive drive; // 캐싱용

    /** Google Drive 클라이언트 초기화 */
    private Drive getDrive() throws Exception {
        if (drive != null) return drive;
        if (serviceAccountJson == null || serviceAccountJson.isBlank()) {
            log.warn("⚠️ Google Drive 비활성화 모드 (환경변수 없음)");
            return null;
        }

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var jsonFactory = GsonFactory.getDefaultInstance();

        var credentials = ServiceAccountCredentials
                .fromStream(new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8)))
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/drive.file"));

        drive = new Drive.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName("AI Blog Bot")
                .build();

        return drive;
    }

    /** Google Drive 업로드 */
    public String uploadHtml(java.io.File file) {
        try {
            Drive drive = getDrive();
            if (drive == null) {
                log.warn("⚠️ Drive 연결 안 됨 — 업로드 스킵됨");
                return null;
            }

            File fileMetadata = new File();
            fileMetadata.setName(file.getName());
            if (folderId != null && !folderId.isBlank()) {
                fileMetadata.setParents(Collections.singletonList(folderId));
            }

            FileContent mediaContent = new FileContent("text/html", file);
            File uploadedFile = drive.files().create(fileMetadata, mediaContent)
                    .setFields("id, webViewLink, name")
                    .execute();

            String url = "https://drive.google.com/file/d/" + uploadedFile.getId() + "/view";
            log.info("✅ Google Drive 업로드 완료: {}", url);
            return url;
        } catch (IOException e) {
            log.error("❌ Drive 업로드 실패: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("❌ Drive 인증 실패: {}", e.getMessage(), e);
            return null;
        }
    }
}
