package com.spring.snsproject.repository;

import com.spring.snsproject.domain.entity.Post;
import com.spring.snsproject.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByUser(User user, Pageable pageable);
}
