package org.example.blogbot.util;

public class HtmlUtil {
    private HtmlUtil() {}

    public static String ensureHtml(String html) {
        if (html == null || html.isBlank()) return "<p>내용이 비어 있습니다.</p>";
        // 최소한의 감싸기 (이미 태그가 있으면 그대로)
        return html.trim();
    }

    public static String safeFileName(String title) {
        String t = title == null ? "post" : title;
        t = t.replaceAll("[\\\\/:*?\"<>|]", " "); // 파일명 불가문자 제거
        t = t.replaceAll("\\s+", "_").trim();
        if (t.isBlank()) t = "post";
        return t;
    }
}
