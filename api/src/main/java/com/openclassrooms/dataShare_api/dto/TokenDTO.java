package com.openclassrooms.dataShare_api.dto;

import lombok.Data;

@Data
public class TokenDTO {
    private String token;
    private Long userId;

    public TokenDTO() {

    }

    public TokenDTO(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }
}
