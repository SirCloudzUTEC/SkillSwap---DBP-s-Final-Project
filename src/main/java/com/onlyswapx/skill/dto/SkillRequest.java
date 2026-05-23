package com.onlyswapx.skill.dto;

import com.onlyswapx.skill.domain.Skill.SkillType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillRequest {

    @NotBlank(message = "name is required")
    @Size(min = 2, max = 120, message = "name must be between 2 and 120 characters")
    private String name;

    @Size(max = 1000, message = "description must not exceed 1000 characters")
    private String description;

    @Size(max = 80, message = "category must not exceed 80 characters")
    private String category;

    @NotNull(message = "skillType is required (OFFER or WANT)")
    private SkillType skillType;

    @Size(max = 40, message = "level must not exceed 40 characters")
    private String level;
}
