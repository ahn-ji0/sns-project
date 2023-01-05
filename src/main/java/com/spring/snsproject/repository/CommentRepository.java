package com.spring.snsproject.repository;

import com.spring.snsproject.domain.entity.Comment;
import com.spring.snsproject.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Page<Comment> findByPost(Post post, Pageable pageable);
    void deleteAllByPost(Post savedPost);
}
