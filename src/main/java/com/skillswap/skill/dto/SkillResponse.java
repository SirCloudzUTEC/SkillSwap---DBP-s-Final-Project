package com.skillswap.skill.dto;

import com.skillswap.skill.domain.Skill.SkillType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SkillResponse {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String category;
    private SkillType skillType;
    private String level;
    private LocalDateTime createdAt;
}
