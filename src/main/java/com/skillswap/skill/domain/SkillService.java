package com.skillswap.skill.domain;

import com.skillswap.skill.dto.SkillRequest;
import com.skillswap.skill.dto.SkillResponse;
import com.skillswap.skill.infrastructure.SkillRepository;
import com.skillswap.user.domain.User;
import com.skillswap.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;

    @Transactional
    public SkillResponse create(String userEmail, SkillRequest request) {
        User user = loadUserByEmail(userEmail);
        Skill saved = skillRepository.save(toEntity(user, request));

        geminiService.analyzeSkillAsync(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getCategory()
        );

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> getMySkills(String userEmail) {
        User user = loadUserByEmail(userEmail);
        return skillRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long skillId, String userEmail) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
        User user = loadUserByEmail(userEmail);
        if (!skill.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this skill");
        }
        skillRepository.delete(skill);
    }

    private User loadUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    private Skill toEntity(User user, SkillRequest request) {
        return Skill.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .skillType(request.getSkillType())
                .level(request.getLevel())
                .build();
    }

    private SkillResponse toResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .userId(skill.getUser().getId())
                .name(skill.getName())
                .description(skill.getDescription())
                .category(skill.getCategory())
                .skillType(skill.getSkillType())
                .level(skill.getLevel())
                .createdAt(skill.getCreatedAt())
                .build();
    }
}
