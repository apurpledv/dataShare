package com.openclassrooms.dataShare_api.dto;

import lombok.Data;

@Data
public class TokenDTO {
    private String token;

    public TokenDTO() {

    }

    public TokenDTO(String token) {
        this.token = token;
    }
}
