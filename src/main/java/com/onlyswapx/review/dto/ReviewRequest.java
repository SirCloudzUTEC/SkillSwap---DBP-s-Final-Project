package com.onlyswapx.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewRequest {
    private Long reviewedId;
    private Integer rating;
    private String comment;
}