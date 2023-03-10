package com.spring.snsproject.repository;

import com.spring.snsproject.domain.entity.Alarm;
import com.spring.snsproject.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm,Long> {
    Page<Alarm> findByUser(User user, Pageable pageable);
}
