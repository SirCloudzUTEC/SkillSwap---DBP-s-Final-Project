package com.onlyswapx.credit.infrastructure;

import com.onlyswapx.credit.domain.CreditTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, Long> {
    List<CreditTransaction> findBySessionId(Long sessionId);
}