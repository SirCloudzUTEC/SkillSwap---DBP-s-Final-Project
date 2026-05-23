package com.onlyswapx.message.dto;

import com.onlyswapx.message.domain.Message;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class MessageResponse {
    private Long id;
    private Long exchangeId;
    private Long senderId;
    private String senderName;
    private String content;
    private Message.MessageType messageType;
    private LocalDateTime createdAt;
}