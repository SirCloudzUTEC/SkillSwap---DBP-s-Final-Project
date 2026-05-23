package com.skillswap.skill.domain;

import com.skillswap.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "skills",
        indexes = {
                @Index(name = "idx_skill_user", columnList = "user_id"),
                @Index(name = "idx_skill_type", columnList = "skill_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_skill_user"))
    private User user;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "category", length = 80)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false, length = 16)
    private SkillType skillType;

    @Column(name = "level", length = 40)
    private String level;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum SkillType { OFFER, WANT }
}
