package com.spring.snsproject.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE post SET deleted_at = current_timestamp where id = ?")
@Where(clause = "deleted_at is NULL")
public class Post extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String body;

    public void editPost(String title, String body){
        this.title = title;
        this.body = body;
    }
}
