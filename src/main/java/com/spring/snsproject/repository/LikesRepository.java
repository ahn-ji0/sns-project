package com.spring.snsproject.repository;

import com.spring.snsproject.domain.entity.Likes;
import com.spring.snsproject.domain.entity.Post;
import com.spring.snsproject.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes,Long> {
    Optional<Likes> findByUserAndPost(User user, Post post);
    int countByPost(Post post);
    @Transactional
    void deleteAllByPost(Post post);
}
