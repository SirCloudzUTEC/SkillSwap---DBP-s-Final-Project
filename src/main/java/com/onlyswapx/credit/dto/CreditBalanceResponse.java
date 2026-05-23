package com.onlyswapx.credit.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CreditBalanceResponse {
    private Long userId;
    private Integer balance;
}