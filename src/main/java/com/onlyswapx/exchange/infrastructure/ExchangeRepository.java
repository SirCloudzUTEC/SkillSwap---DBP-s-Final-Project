package com.onlyswapx.exchange.infrastructure;

import com.onlyswapx.exchange.domain.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeRequest, Long> {
    List<ExchangeRequest> findByRequesterId(Long requesterId);
    List<ExchangeRequest> findByReceiverId(Long receiverId);
    boolean existsByRequesterIdAndReceiverId(Long requesterId, Long receiverId);
}