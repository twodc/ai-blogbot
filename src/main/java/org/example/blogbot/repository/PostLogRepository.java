package org.example.blogbot.repository;

import org.example.blogbot.entity.PostLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLogRepository extends JpaRepository<PostLog, Long> {
}
