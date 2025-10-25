package org.example.blogbot.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Service
public class DriveUploadService {

    @Value("${GDRIVE_SERVICE_ACCOUNT_JSON}")
    private String serviceAccountJson;

    @Value("${GDRIVE_FOLDER_ID}")
    private String folderId;

    private Drive driveService;

    private Drive getDriveService() throws IOException, GeneralSecurityException {
        if (driveService != null) return driveService;

        ByteArrayInputStream jsonStream = new ByteArrayInputStream(serviceAccountJson.getBytes());
        ServiceAccountCredentials credentials = ServiceAccountCredentials
                .fromStream(jsonStream)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive.file"));

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        driveService = new Drive.Builder(httpTransport, GsonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials))
                .setApplicationName("AI BlogBot")
                .build();

        return driveService;
    }

    public String uploadFile(java.io.File localFile) {
        try {
            Drive service = getDriveService();

            File fileMetadata = new File();
            fileMetadata.setName(localFile.getName());
            fileMetadata.setParents(Collections.singletonList(folderId));

            FileContent mediaContent = new FileContent("text/html", localFile);
            File uploadedFile = service.files().create(fileMetadata, mediaContent)
                    .setFields("id, webViewLink")
                    .execute();

            String link = uploadedFile.getWebViewLink();
            log.info("✅ Google Drive 업로드 완료: {}", link);
            return link;

        } catch (Exception e) {
            log.error("❌ Drive 업로드 실패: {}", e.getMessage(), e);
            return null;
        }
    }
}
