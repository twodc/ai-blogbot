package org.example.blogbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlogBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogBotApplication.class, args);
    }

}
