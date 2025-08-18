package com.teamalgo.algo.domain.user;

import com.teamalgo.algo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "user",
        uniqueConstraints = {
//                @UniqueConstraint(columnNames = "handle"),
                @UniqueConstraint(columnNames = {"provider", "providerId"})
        }
)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false, unique = true)
//    private String handle;

    @Column(nullable = false, length = 50)
    private String nickname;

//    private String avatarUrl;

    @Column(nullable = false, length = 20)
    private String provider;  // google, kakao, github

    @Column(nullable = false, unique = true, length = 100)
    private String providerId;

}