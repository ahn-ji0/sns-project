package com.spring.snsproject.repository;

import com.spring.snsproject.domain.entity.Following;
import com.spring.snsproject.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingRepository extends JpaRepository<Following, Long> {

    Optional<Following> findByFromUserAndToUser(User fromUser, User toUser);

}
