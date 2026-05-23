package com.onlyswapx.exchange.dto;

import com.onlyswapx.exchange.domain.ExchangeRequest;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class ExchangeResponse {
    private Long id;
    private Long requesterId;
    private String requesterName;
    private Long receiverId;
    private String receiverName;
    private ExchangeRequest.ExchangeStatus status;
    private String message;
    private LocalDateTime createdAt;
}