package com.openclassrooms.dataShare_api.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DSFileDTO {
    Long id;
    String path;
    Long ownerId;
    String name;
    LocalDateTime uploadDate;
    LocalDateTime expirationDate;
    String type;
    Long size;

    public DSFileDTO(Long id, String path, Long ownerId, String name, 
        LocalDateTime uploadDate, LocalDateTime expirationDate, String type, Long size
    ) {
        this.id = id;
        this.path = path;
        this.ownerId = ownerId;
        this.name = name;
        this.uploadDate = uploadDate;
        this.expirationDate = expirationDate;
        this.type = type;
        this.size = size;
    }
}
