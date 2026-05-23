package com.onlyswapx.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequest {
    @NotNull
    private Long exchangeId;
    @NotBlank
    private String content;
    private String messageType = "TEXT";
}