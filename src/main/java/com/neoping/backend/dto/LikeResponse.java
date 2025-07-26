package com.neoping.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponse {
    private boolean liked;
    private int likesCount;
    private boolean success;
    private String message;
    private String error;
}
