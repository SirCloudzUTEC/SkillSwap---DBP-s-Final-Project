package is25.onlyswapx.skill.infraestructure;

import is25.onlyswapx.skill.domain.Skill;
import is25.onlyswapx.skill.domain.Skill.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByUserId(Long userId);

    List<Skill> findBySkillType(SkillType skillType);

    List<Skill> findByUserIdAndSkillType(Long userId, SkillType skillType);
}
