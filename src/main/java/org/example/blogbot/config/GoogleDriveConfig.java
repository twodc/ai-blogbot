//package org.example.blogbot.config;
//
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.drive.Drive;
//import com.google.auth.http.HttpCredentialsAdapter;
//import com.google.auth.oauth2.ServiceAccountCredentials;
//import java.io.ByteArrayInputStream;
//import java.nio.charset.StandardCharsets;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GoogleDriveConfig {
//
//    @Value("${gdrive.service-account-json:}")
//    private String serviceAccountJson; // 환경변수로 JSON 전체를 전달
//
//    @Bean
//    public Drive googleDrive() throws Exception {
//        if (serviceAccountJson == null || serviceAccountJson.isBlank()) {
//            // 드라이브 업로드 비활성화 모드(Bean은 생성하되 인증 없이)
//            return null;
//        }
//
//        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//        var jsonFactory = GsonFactory.getDefaultInstance();
//
//        var credentials = ServiceAccountCredentials
//                .fromStream(new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8)))
//                .createScoped("https://www.googleapis.com/auth/drive.file"); // 업로드 권한
//
//        return new Drive.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
//                .setApplicationName("AI Blog Bot")
//                .build();
//    }
//}
