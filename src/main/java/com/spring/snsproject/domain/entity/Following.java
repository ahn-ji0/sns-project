package com.spring.snsproject.domain.entity;

import com.spring.snsproject.domain.dto.following.FollowingDto;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE post SET deleted_at = current_timestamp where id = ?")
@Where(clause = "deleted_at is NULL")
public class Following extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "from_id")
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_id")
    private User toUser;

    public static FollowingDto of(Following following) {
        return new FollowingDto(User.of(following.fromUser), User.of(following.toUser));
    }
}
