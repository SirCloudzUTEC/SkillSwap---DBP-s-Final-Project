package com.onlyswapx.session.infrastructure;

import com.onlyswapx.session.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByTeacherId(Long teacherId);
    List<Session> findByStudentId(Long studentId);
}