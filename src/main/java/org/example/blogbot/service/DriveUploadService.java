package org.example.blogbot.service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriveUploadService {

    private final Drive drive; // GoogleDriveConfig에서 주입 (없으면 null)

    @Value("${gdrive.folder-id:}")
    private String folderId;

    public String uploadHtml(java.io.File htmlFile) {
        try {
            if (drive == null) {
                // 드라이브 비활성 모드
                return null;
            }
            File metadata = new File();
            metadata.setName(htmlFile.getName());
            if (folderId != null && !folderId.isBlank()) {
                metadata.setParents(java.util.List.of(folderId));
            }
            FileContent mediaContent = new FileContent("text/html; charset=UTF-8", htmlFile);
            File result = drive.files()
                    .create(metadata, mediaContent)
                    .setFields("id, name, webViewLink")
                    .execute();
            return result.getWebViewLink();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
